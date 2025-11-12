---
id: ep-014
title: Task 8 Prompt Creation - CPG 容器化架构设计
type: episodic
date: 2025-11-12
tags: [task-prompt, containerization, architecture-design, build-query-separation, serialization]
links:
  - /claude/prompt/8.containerized-architecture.md
related: [ep-001, ep-002, ep-013, sem-001, sem-003, sem-004]
---

# Task 8 Prompt Creation - Session Summary

## Goal

用户提出了 CPG 容器化架构需求：将 CPG 分为构建容器和查询容器，实现 **Build Once, Query Many** 模式。本次会话分析了 CPG 架构、发现了构建与查询的耦合问题，并创建了 Task 8 prompt。

## User Request

**原始需求**（用户描述）：
> "cpg在打包成容器后，被分为构建图的部分和查询的部分，构建阶段会将查询所需的一切信息保存到本地，查询作为离线分析可能并不需要重构构建图，但是可能需要非常多次的查询完成不同的业务，所以需要分离。"

**核心要素**：
1. 构建容器：拉取 Git Repo，读取环境变量，构建图，保存产物
2. 查询容器：加载图，执行用户自定义 Kotlin DSL，返回结果
3. 容器编排：查询容器类似 Serverless Function
4. 构建查询分离：源代码和编译产物可以分离

---

## Context: 架构讨论的演进

### Phase 1: CPG 架构理解（基于记忆系统）

**已读 Memory Notes**：
- `sem-001` (Java CPG Frontend Architecture) - 理解 Frontend + Handler + Pass 架构
- `sem-002` (Handler Pattern) - 理解 AST-to-CPG 转换模式
- `sem-003` (UnreachableEOGPass) - 理解 Pass 如何修改图属性
- `sem-004` (Query API DSL) - 理解查询层如何读取图属性

**发现 1: CPG 四层架构**
```
Layer 1: Frontend（语言前端）→ 基础图
Layer 2: Pass System（图增强）→ 设置属性（unreachable, DFG, ...）
Layer 3: Sensitivity（桥梁）→ 读取属性，控制遍历
Layer 4: Query API（查询）→ executionPath, dataFlow
```

**发现 2: 构建与查询的隐式耦合**
- 查询层的 `FilterUnreachableEOG` 依赖构建层的 `UnreachableEOGPass`
- 但查询层无法知道哪些 Pass 已执行（信息缺失）

### Phase 2: 深入分析隐式耦合问题

**讨论主题**：为什么查询需要依赖构建配置？

**初步分析**（错误）：
- 我误以为"查询层无法访问 Pass 信息"
- 我推测"需要实现 PassScheduler 和依赖解析"
- **用户质疑**："这非常好解决啊，附上 pass 配置，查询时读取不就行了"

**实证验证**：
```kotlin
// TranslationResult.kt:178
override val config: TranslationConfiguration
    get() = finalCtx.config

// TranslationConfiguration
val registeredPasses: List<List<KClass<out Pass<*>>>>
```

**结论**：用户完全正确！
- ✅ 配置信息已经存在（`result.config.registeredPasses`）
- ✅ 技术上只需几行代码实现验证
- ❌ CPG 只是"没做"，不是"做不到"

**Lesson Learned**：我过度分析了，最简单的解释往往是正确的（Occam's Razor）。

### Phase 3: Pass 系统本质讨论

**用户问题**："我到现在也没明白 Pass 是干嘛的，为什么需要，为什么能加速"

**我的解释**（具体例子）：
```java
// 示例代码
void foo() {
    int x = 5;
    if (true) { reachable(); }
    else { unreachable(); }
}

// Frontend 产生基础图（只有 AST）
// Pass 1: EvaluationOrderGraphPass → 添加 EOG 边
// Pass 2: ControlFlowSensitiveDFGPass → 添加 DFG 边
// Pass 3: UnreachableEOGPass → 标记 unreachable = true
```

**Pass 的本质**：
- **不是为了"加速"**，而是为了模块化
- **增量构建图**：每个 Pass 负责一个分析任务
- **管理依赖**：后面的 Pass 依赖前面的结果（`@DependsOn`）
- **按需计算**：用户只运行需要的 Pass（间接"加速"）

**类比**：建房子的分阶段施工
- Pass 1: 打地基
- Pass 2: 砌墙
- Pass 3: 装水电
- Pass 4: 装修

### Phase 4: 容器化架构需求

**用户明确需求**：
```
构建容器:
  Input: Git Repo + 环境变量（PASSES, LANGUAGES, ...）
  Output: 图产物（graph.cpg + config.json + stats.json）

查询容器:
  Input: 用户查询脚本（Kotlin DSL）
  Output: 查询结果（JSON）
```

**关键架构原则**：
1. 构建与查询完全解耦
2. 查询容器无状态，可横向扩展
3. 配置即代码，环境变量驱动

---

## Approach: Task 8 Prompt 创建

### Design Process

**参考结构**：基于 Task 7 prompt 的结构（ep-013）

**Prompt 章节设计**：
1. **问题陈述** (Problem Statement)：当前 CPG 单体模式的局限性
2. **架构愿景** (Architecture Vision)：Build Once, Query Many 模式
3. **技术需求** (Technical Requirements)：构建容器、查询容器、持久化方案
4. **交付物** (Deliverables)：4 个文档 + 代码实现 + POC 示例
5. **关键技术挑战** (Technical Challenges)：序列化、Pass 验证、并发隔离
6. **实施路线图** (Implementation Roadmap)：3 阶段，4-5 周
7. **成功标准** (Success Criteria)：功能性、性能、易用性指标
8. **执行策略** (Execution Strategy)：Memory-First + Incremental
9. **关键决策点** (Key Decisions)：持久化后端、查询接口、容器编排
10. **风险与缓解** (Risks and Mitigation)：序列化复杂度、文件大小、数据一致性

### Key Design Decisions

#### 决策 1: 持久化方案

**调研发现**：
```bash
# CPG 现有持久化
cpg-neo4j/src/main/kotlin/.../Neo4J.kt:85
context(Session)
fun TranslationResult.persist()
```

- ✅ CPG 已有 Neo4j 持久化（但只有写入，没有读取）
- ❌ 没有文件序列化/反序列化

**设计方案**：
- **Phase 1** (短期)：使用 Neo4j（利用现有代码，1 周验证）
- **Phase 2** (长期)：实现 Protocol Buffers 文件序列化（2-3 周）
- **权衡**：Neo4j 需要数据库运维，文件序列化更灵活

#### 决策 2: 查询接口

**选项对比**：
| 方案 | 用户体验 | 性能 | 实现难度 |
|------|---------|------|---------|
| Cypher 查询 (Neo4j) | 需要学习新语言 | 数据库查询 | 简单（已有） |
| CPG Kotlin DSL | 原生 CPG API | 内存查询（更快）| 中等（需要反序列化）|

**选择**：**推荐 Kotlin DSL**（更符合用户需求）

#### 决策 3: 序列化格式

**选项对比**：
| 格式 | 性能 | 可读性 | 跨语言 | 推荐度 |
|------|------|--------|--------|--------|
| Protocol Buffers | ⭐⭐⭐⭐⭐ | ⭐⭐ | ✅ | ⭐⭐⭐⭐⭐ |
| JSON | ⭐⭐ | ⭐⭐⭐⭐⭐ | ✅ | ⭐⭐⭐ |
| Kryo | ⭐⭐⭐⭐⭐ | ⭐ | ❌ | ⭐⭐ |

**选择**：**Protocol Buffers**（性能 + 向后兼容 + 跨语言）

### Content Structure

**文档规划**：
- **8.1-架构-容器化设计.md** (~1500 lines)：架构图、数据流、接口规范
- **8.2-实现-分步指南.md** (~2000 lines)：Phase 1-3 详细步骤
- **8.3-手册-使用指南.md** (~1000 lines)：快速开始、常见问题
- **8.4-参考-API文档.md** (~800 lines)：API 参考、环境变量

**代码规划**：
- `cpg-core/.../persistence/Serialization.kt` - 序列化逻辑
- `docker/build-container/Dockerfile` - 构建容器镜像
- `docker/query-container/Dockerfile` - 查询容器镜像
- `examples/containerized-workflow/` - POC 示例

### Technical Challenges Addressed

**挑战 1: 图的序列化（循环引用）**
```kotlin
// 两阶段序列化
// Phase 1: 序列化节点，分配全局 ID
// Phase 2: 序列化边，引用节点 ID
```

**挑战 2: 序列化文件过大**
```
// 解决方案 1: 分块存储
graph.cpg/
  ├── nodes/chunk-0.pb
  └── edges/eog.pb

// 解决方案 2: 延迟加载
class LazyTranslationResult { ... }
```

**挑战 3: Pass 配置验证**
```kotlin
object FilterUnreachableEOG : AnalysisSensitivity() {
    init {
        validatePassRegistered(UnreachableEOGPass::class)
    }
}
```

**挑战 4: 并发查询隔离**
- ✅ 图不可变（只读）
- ✅ 查询结果隔离（独立输出目录）
- ✅ 内存隔离（独立进程）

---

## Results

### Deliverable Created

✅ **Task 8 Prompt**: `/claude/prompt/8.containerized-architecture.md` (6800 lines)

**Prompt Quality Metrics**:
| Metric | Target | Actual |
|--------|--------|--------|
| **Clarity** | 用户可理解架构愿景 | ✅ 包含架构图 + 数据流 |
| **Completeness** | 涵盖所有技术细节 | ✅ 11 个章节，覆盖需求/设计/实现/测试 |
| **Actionability** | 可直接执行 | ✅ 分步路线图（3 阶段，4-5 周）|
| **Technical Depth** | 解决关键挑战 | ✅ 4 个技术挑战 + 解决方案 |
| **Integration** | 与现有工作整合 | ✅ 引用 sem-001/003/004, ep-001/002 |

### Content Breakdown

**Section 1: Problem Statement** (~800 lines)
- 当前 CPG 使用模式的局限性
- 3 个实际业务场景（CI/CD、代码审计、大规模分析）
- Build Once, Query Many 核心需求

**Section 2: Architecture Vision** (~600 lines)
- 3 个核心设计原则（解耦、无状态、配置即代码）
- 目标架构图（构建容器 → 存储 → 查询容器）

**Section 3: Technical Requirements** (~1200 lines)
- 构建容器需求（环境变量、产物规范、错误处理）
- 查询容器需求（图加载、用户接口、资源限制）
- 持久化方案需求（多后端、序列化格式、数据完整性）

**Section 4: Deliverables** (~800 lines)
- 4 个文档交付物
- 3 个代码交付物（序列化库、容器镜像、POC 示例）

**Section 5: Technical Challenges** (~900 lines)
- 挑战 1: 循环引用序列化（两阶段方案）
- 挑战 2: 文件过大（分块 + 延迟加载）
- 挑战 3: Pass 验证（运行时检查）
- 挑战 4: 并发隔离（不可变图 + 进程隔离）

**Section 6: Implementation Roadmap** (~1000 lines)
- Phase 1: Neo4j 方案（1 周）
- Phase 2: 文件序列化（2-3 周）
- Phase 3: 容器化打包（1 周）
- Phase 4: 社区贡献（可选）

**Section 7-11: 其他** (~1500 lines)
- 成功标准（功能性、性能、易用性）
- 执行策略（Memory-First + Incremental）
- 关键决策点（3 个决策）
- 风险与缓解（5 个风险）
- 附录（参考资料）

---

## Key Insights and Observations

### Insight 1: 架构讨论暴露了对 CPG 理解的不足

**观察**：讨论初期，我对 CPG 架构的理解存在偏差：
- ❌ 误以为查询层无法访问 Pass 信息（实际上有 `result.config`）
- ❌ 过度分析"为什么需要 Pass"（用户不理解 Pass 本质）
- ❌ 把简单问题复杂化（用户一句话指出解决方案）

**教训**：
- 在讨论架构前，应先确认双方对基础概念的理解一致
- 避免"假设用户知道"，必要时用具体例子解释
- 听取用户的直觉判断（通常更简单、更正确）

### Insight 2: 用户需求驱动的架构设计更实用

**对比**：
| 维度 | 我的初始思路 | 用户的实际需求 |
|------|------------|--------------|
| **关注点** | CPG 内部实现细节 | 业务场景（CI/CD、审计）|
| **解决方案** | 复杂的技术方案 | 简单的分离模式 |
| **验证方式** | 理论分析 | 实际代码验证 |

**教训**：架构设计应从业务场景出发，而非技术细节。

### Insight 3: CPG 架构的实际问题

通过这次讨论，发现 CPG 架构的几个真实问题：

1. **Pass 配置验证缺失**：
   - 问题：查询时使用 `FilterUnreachableEOG`，但构建时未注册 `UnreachableEOGPass`
   - 现状：静默失败（返回错误结果，不报错）
   - 解决：运行时验证（只需几行代码）

2. **序列化/反序列化缺失**：
   - 问题：只能持久化到 Neo4j，无法序列化到文件
   - 现状：每次查询都需要连接数据库
   - 解决：实现文件序列化（Protocol Buffers）

3. **构建与查询耦合**：
   - 问题：无法分离构建和查询到不同环境
   - 现状：单体运行模式
   - 解决：容器化架构（本 Task 目标）

### Insight 4: 开源项目的贡献机会

**发现**：Task 8 的很多功能都是 CPG 项目缺失的：
- 序列化/反序列化
- Pass 配置验证
- 容器化示例

**机会**：可以向 CPG 项目提交 PR，贡献这些功能。

**价值**：
- 对 CPG 社区有贡献
- 对用户自己的项目也有帮助（使用上游维护的版本）

### Insight 5: Prompt 创建的最佳实践

**经验总结**（基于 Task 7 和 Task 8）：

1. **结构化**：11 个标准章节（问题、目标、需求、交付物、...）
2. **具体化**：用架构图、代码示例、对比表，而非抽象描述
3. **可执行化**：分步路线图、时间估算、验证标准
4. **整合化**：引用已有 memory notes、task outputs
5. **现实化**：包含风险、权衡、决策点

**Prompt 质量检查清单**：
- [ ] 用户能否理解问题？（Problem Statement 清晰）
- [ ] 用户能否看到愿景？（Architecture Vision 有图）
- [ ] 用户能否开始执行？（Implementation Roadmap 详细）
- [ ] 用户能否验证结果？（Success Criteria 可测量）
- [ ] 与现有工作整合？（引用 memory notes）

---

## Challenges Encountered

### Challenge 1: 理解用户的真实需求

**问题**：初期我聚焦于 CPG 内部实现，而用户关注的是业务场景。

**解决**：
- 用户直接描述了具体场景（CI/CD、代码审计）
- 我根据场景反推技术需求（构建查询分离）

**教训**：先问"为什么需要"，再问"怎么实现"。

### Challenge 2: 避免过度分析

**问题**：我在"为什么查询依赖构建配置"上花了太多时间，提出了复杂的解释。

**用户反馈**："这非常好解决啊"

**教训**：
- 先验证最简单的解释（查配置文件）
- 如果简单方案可行，不要过度设计
- Occam's Razor：最简单的解释往往是正确的

### Challenge 3: 解释 Pass 系统

**问题**：用户说"我到现在也没明白 Pass 是干嘛的"。

**原因**：我一直假设用户理解 Pass，但从未详细解释。

**解决**：
- 用具体例子（`if (true) { ... } else { ... }`）
- 逐步展示 Pass 的执行过程（Frontend → EOG → DFG → Unreachable）
- 用类比（建房子的分阶段施工）

**教训**：不要假设用户知道技术细节，必要时从零解释。

### Challenge 4: 平衡技术深度和可读性

**问题**：Prompt 需要详细到可执行，但不能过于冗长。

**解决**：
- 主 Prompt 提供完整视图（6800 lines）
- 通过章节分割（11 个章节）
- 后续执行时，每个文档 <2000 lines（Incremental Approach）

**教训**：Prompt 可以长，但需要结构化；执行时再增量化。

---

## Next Steps

### For User

1. **Review Prompt**: 检查 Task 8 prompt 是否符合预期
2. **Execute Task 8**: 可以说"执行 Task 8"（或分阶段执行）
3. **Prioritize Phases**:
   - 如果需要快速验证：Phase 1 (Neo4j, 1 周)
   - 如果需要长期方案：Phase 2 (文件序列化, 2-3 周)

### For Future Tasks

**Prompt 创建最佳实践**（基于 Task 7, Task 8）：
1. **Problem-Driven**: 从实际业务场景出发
2. **Architecture-First**: 先画架构图，再写技术细节
3. **Incremental**: 分阶段实施，每阶段 <2 周
4. **Memory-Integrated**: 引用已有 memory notes
5. **Decision-Documented**: 记录关键决策和权衡

### For Memory System

- ⏳ **Semantic note** (待 Task 8 执行后创建): `sem-007` (Containerization Architecture)
- ⏳ **Update indexes** (现在更新):
  - `tags.json`: Add "task-8", "containerization", "serialization", "build-query-separation"
  - `topics.json`: Add "Containerized Architecture" topic

---

## Lessons Learned

### Lesson 1: 用户的直觉往往比我的分析更准确

**例子**：
- 我：复杂分析"为什么查询依赖构建"，提出 PassScheduler、依赖解析...
- 用户："附上 pass 配置，查询时读取不就行了"
- 实证：用户完全正确，只需几行代码

**教训**：
- 先验证简单解释
- 不要过度工程化
- 相信 Occam's Razor

### Lesson 2: 解释复杂概念需要从零开始

**例子**：
- 我假设用户理解 Pass
- 用户："我到现在也没明白 Pass 是干嘛的"
- 我用具体例子重新解释

**教训**：
- 不假设用户背景知识
- 用具体例子，而非抽象定义
- 用类比（建房子）降低理解门槛

### Lesson 3: Prompt 创建是"需求分析 + 架构设计 + 实施规划"的结合

**Task 8 Prompt 包含**：
- 需求分析：3 个业务场景
- 架构设计：构建/查询/存储三层
- 实施规划：3 阶段，4-5 周
- 技术细节：4 个挑战 + 解决方案
- 质量标准：功能性/性能/易用性

**教训**：好的 Prompt = 完整的项目规划书。

### Lesson 4: 开源项目的现实约束

**发现**：CPG 有很多"应该有但没有"的功能：
- Pass 配置验证（技术上简单，但没人做）
- 文件序列化（工作量大，但价值高）

**可能原因**：
- 维护者资源有限
- 用户没有反馈需求
- 没人提 PR

**机会**：Task 8 可以填补这些空白（并贡献回社区）。

---

## Links

- **Prompt created**: `/claude/prompt/8.containerized-architecture.md`
- **Related prompts**: `7.pl-concepts-onboarding-guide.md` (结构参考)
- **Related episodic notes**:
  - `ep-001` (Task 1 - Java Frontend)
  - `ep-002` (Task 2 - CPG Core Analysis)
  - `ep-013` (Task 7 - Prompt Creation)
- **Related semantic notes**:
  - `sem-001` (Java CPG Architecture)
  - `sem-003` (UnreachableEOGPass)
  - `sem-004` (Query API DSL)

---

**Session Duration**: ~2 小时
**Context Used**: ~95K tokens
**Output**: 1 Prompt (6800 lines) + 1 Episodic Note (本文档)

---

## Update: Removed Neo4j from Task 8 (2025-11-12 Later Session)

### User Feedback

**用户明确反馈**：
> "不考虑neo4j的部分，neo4j不适合cpg的深树检索，一切的查询行为都发生在kotlin dsl的部分"

**核心观点**：
- ❌ **Neo4j 不适合 CPG**：深度遍历性能差（O(degree^N)）
- ✅ **必须用内存查询**：Kotlin DSL 在内存中遍历（O(N)）
- ✅ **架构简化**：只需文件序列化方案，无需 Neo4j

### Changes Made

**修改内容**：
1. ✅ **Section 2.2 架构图**：移除 "Neo4j DB" 存储选项
2. ✅ **Section 3.3 持久化方案**：
   - 添加"为什么不用 Neo4j"说明
   - 移除 Neo4j 作为备选方案
   - 强调 Protocol Buffers 是唯一选择
3. ✅ **Section 6 实施路线图**：
   - 删除 Phase 1（Neo4j 方案，1 周）
   - Phase 2 改为 Phase 1（序列化，2-3 周）
   - Phase 3 改为 Phase 2（容器化，1 周）
   - 总时间线：5 周 → 3-4 周
4. ✅ **Section 9 关键决策点**：
   - 删除"决策 1: 持久化后端选择"
   - 改为"决策 1: 序列化格式"（已选择 Protocol Buffers）
   - 删除"决策 2: 查询接口模式"（不再是选择题）
5. ✅ **Section 10 风险与缓解**：
   - 删除 Neo4j 相关风险
   - 添加 Protocol Buffers schema 设计风险
6. ✅ **Section 11 附录**：
   - 移除 Neo4j/Cypher 文档链接
   - 添加 Protocol Buffers 详细文档链接
   - 更新类似项目参考（CodeQL, TensorFlow SavedModel）

### Why Neo4j is Not Suitable

**技术分析**（添加到 prompt Section 3.3）：

```
CPG 查询特点 vs Neo4j 设计目标：

Neo4j 设计目标：
- 浅层关系查询（1-3 跳）
- 社交网络、推荐系统
- Cypher 查询语法

CPG 查询需求：
- 深度遍历（数千层）
- 递归函数调用分析
- 过程间数据流分析

性能对比：
- Cypher 深度查询：O(degree^N) - 指数级
- 内存遍历（Kotlin DSL）：O(N) - 线性
```

**结论**：所有查询必须在 Kotlin DSL（内存）中进行，因此必须序列化到文件并加载到内存。

### Impact Assessment

**时间线调整**：
| 项目 | 原计划 | 新计划 | 变化 |
|------|--------|--------|------|
| Phase 1 | Neo4j 方案（1 周）| 删除 | -1 周 |
| Phase 2 | 文件序列化（2-3 周）| Phase 1: 序列化（2-3 周）| 无变化 |
| Phase 3 | 容器化（1 周）| Phase 2: 容器化（1 周）| 无变化 |
| **总计** | **4-5 周** | **3-4 周** | **-1 周** |

**好处**：
- ✅ 架构更简单（不需要数据库）
- ✅ 部署更容易（只需文件存储）
- ✅ 性能更好（内存查询 vs 数据库查询）
- ✅ 更符合用户需求（只用 Kotlin DSL）

### Lessons Learned (Update)

**Lesson 6: 技术方案需要符合实际使用模式**

**错误假设**：
- 我假设 Neo4j 适合 CPG（因为 CPG 是图结构）
- 我假设用户可以接受 Cypher 查询（作为短期方案）

**实际情况**：
- CPG 的查询模式（深度遍历）与 Neo4j 设计目标不匹配
- 用户坚持用 Kotlin DSL（这是 CPG 的核心价值）

**教训**：
- 不要因为"名字相似"（都是图）就假设技术栈适用
- 要分析实际查询模式（浅层 vs 深度遍历）
- 尊重用户对工具链的坚持（Kotlin DSL 是 CPG 生态的一部分）

---

**Session Update**: 2025-11-12 (Later)
**Changes**: Removed all Neo4j content from Task 8 prompt
**New Total Lines**: Task 8 prompt ~6500 lines (reduced from 6800 lines)

---

## Update 2: Serialization Library Selection Deferred (2025-11-12 Later)

### User Feedback

**用户明确反馈**：
> "序列化的技术选型现在决定不了，是一个待办事项，kotlin序列化大量数据到底使用什么库暂时不知道，但是可以确定的是不序列化到人类可读的数据，而是紧凑的二进制数据"

**核心观点**：
- ❌ **不要预先决定库**：需要实际测试才能选择（Kryo vs FST vs Protocol Buffers）
- ✅ **明确要求**：紧凑的二进制格式（类似内存 dump）
- ✅ **排除方案**：JSON/YAML 等人类可读格式
- ⏳ **延迟决策**：将库选型作为 Phase 1 的第一个子任务

### Changes Made

**修改内容**：
1. ✅ **Section 3.3 持久化方案**：
   - 改为"二进制序列化要求"（不指定具体库）
   - 列出候选库对比表（Kryo, FST, Protocol Buffers, Avro, Java Serialization）
   - 明确评估维度（基准测试、循环引用、往返测试、集成难度）
   - 标注"决策推迟到 Phase 1 Step 1"
   
2. ✅ **Section 4.1 文档交付物**：
   - "序列化格式选择（Protocol Buffers 优势分析）" → "序列化方案要求（二进制、紧凑、候选库对比）"
   
3. ✅ **Section 4.2 代码交付物**：
   - `ProtobufSchema.proto` → `BinaryFormat.kt`（取决于选定的库）
   
4. ✅ **Section 6 实施路线图**：
   - Phase 1 新增 **Step 1: 序列化库调研与选型（2-3 天）**
   - 包含：基准测试、循环引用测试、集成难度评估、决策
   - Step 2-4 保持不变（核心实现、反序列化、优化）
   
5. ✅ **Section 9 关键决策点**：
   - "决策 1: 序列化格式（已选择 Protocol Buffers）" → "待决策 1: 序列化库选型（Phase 1 Step 1）"
   - 明确核心要求：紧凑二进制、快速、循环引用、JVM 原生
   - 列出候选库：Kryo, FST, Protocol Buffers
   - 说明决策方法和时间
   
6. ✅ **Section 10 风险与缓解**：
   - 删除"Protocol Buffers schema 设计不当"风险
   - 新增"序列化库选型不当"风险（缓解：充分基准测试）
   - 新增"循环引用处理失败"风险（缓解：选择自动处理的库）
   
7. ✅ **Section 11 附录**：
   - 删除单一的 Protocol Buffers 文档链接
   - 改为"序列化库文档"章节（Kryo, FST, Protocol Buffers, Benchmark）
   - 更新类似项目参考（CodeQL, Soot, WALA）

### Why Defer the Decision

**技术原因**：
1. **无法预测性能**：不同序列化库在 CPG 图上的表现差异可能很大
2. **循环引用处理**：需要验证库是否能正确处理 CPG 的复杂循环引用
3. **文件大小差异**：Kryo 可能比 Protocol Buffers 小 30-50%
4. **集成难度未知**：某些库可能与 CPG 的反射机制冲突

**工程原因**：
1. **避免返工**：如果预先选择 Protocol Buffers，但测试发现 Kryo 更好，需要重写代码
2. **数据驱动决策**：基于实际测试数据选择，而非理论分析
3. **降低风险**：Phase 1 Step 1 只需 2-3 天，投入小但回报大

### Impact Assessment

**时间线调整**：
| 项目 | 原计划 | 新计划 | 变化 |
|------|--------|--------|------|
| Phase 1 | 直接实现序列化（假设用 PB）| Step 1: 调研 (2-3天) + Step 2-4: 实现 | +2-3 天 |
| **总计** | **3-4 周** | **3-4 周**（Step 1 很短，可吸收）| **无变化** |

**好处**：
- ✅ 决策更科学（基于测试数据，而非猜测）
- ✅ 降低风险（避免选错库导致返工）
- ✅ 文档更诚实（不对未知事项做假设）
- ✅ 更符合工程实践（调研 → 决策 → 实施）

### Lessons Learned (Update 2)

**Lesson 7: 不要提前决策需要实验验证的技术选型**

**错误做法**：
- 我直接推荐 Protocol Buffers（基于理论分析）
- 假设"跨语言"、"向后兼容"是重要需求
- 忽略了用户明确说"不需要人类可读"

**正确做法**：
- 列出候选方案 + 评估维度
- 将选型作为第一步任务（基于实际测试）
- 承认"现在还不知道"

**教训**：
- 对于性能敏感的技术选型，必须基于实测数据
- 不要过度优化理论上的"优势"（如跨语言）
- 如果用户说"现在决定不了"，就应该设计"待决策"流程

**Lesson 8: "紧凑的二进制"比"人类可读"更重要（对于内部格式）**

**用户需求**：
- ✅ 紧凑（文件大小最小）
- ✅ 快速（序列化/反序列化最快）
- ❌ 不需要人类可读（这是内部格式）
- ❌ 不需要跨语言（只在 Kotlin/JVM）

**我之前的偏好**：
- ⚠️  Protocol Buffers（强调跨语言、向后兼容）
- ⚠️  忽略了 Kryo 可能更紧凑、更快

**教训**：
- 内部格式和外部格式有不同的优化目标
- 内部格式优先：紧凑 > 速度 > 其他
- 外部格式优先：可读 > 兼容 > 其他

---

**Session Update**: 2025-11-12 (Latest)
**Changes**: Deferred serialization library selection to Phase 1 Step 1
**Reason**: Need benchmark testing to choose between Kryo, FST, and Protocol Buffers
**Core Requirement**: Compact binary format (like memory dump), not human-readable

---

## Update 3: Kotlin Script (.kts) Emphasis (2025-11-12 Latest)

### User Feedback

**用户明确要求**：
> "kotlin dsl的使用需要考虑kotlin script（.kts）而不是(.kt)，这种场景我认为kts的使用体验会比kt好"

**核心观点**：
- ✅ **查询脚本使用 .kts**：用户自定义查询应该是 Kotlin Script 格式
- ✅ **体验优势**：.kts 可以直接执行，无需编译，更适合 Serverless Function 模式
- ✅ **快速迭代**：修改后立即执行，无需重新构建容器

### Why .kts is Better for Query Container

**对比分析**（添加到 prompt Section 3.2）：

| 维度 | .kts (Kotlin Script) | .kt (普通 Kotlin) | 结论 |
|------|---------------------|------------------|------|
| 执行方式 | 直接运行，无需编译 | 需要编译成 JAR | ✅ .kts 更快 |
| 用户体验 | 类似 Python/Shell 脚本 | 需要 Gradle 构建 | ✅ .kts 更简单 |
| 动态加载 | 运行时加载执行 | 编译时固化 | ✅ .kts 更灵活 |
| 快速迭代 | 修改后立即执行 | 修改 → 编译 → 执行 | ✅ .kts 更快 |
| Serverless | 天然适合（函数即代码）| 需要打包 JAR | ✅ .kts 更适合 |
| 类型检查 | ✅ 编译时检查 | ✅ 编译时检查 | ✅ 两者都好 |
| IDE 支持 | ✅ IntelliJ 完整支持 | ✅ IntelliJ 完整支持 | ✅ 两者都好 |

**典型工作流对比**：

**使用 .kt（繁琐）**：
```
1. 用户编写查询代码（.kt）
2. 修改 build.gradle.kts（添加依赖）
3. gradle build（编译成 JAR）
4. 打包到 Docker 镜像
5. 重新构建容器
6. 运行容器
7. 如果查询有错，回到步骤 1
```

**使用 .kts（简洁）**：
```
1. 用户编写查询脚本（.kts）
2. docker run -v ./query.kts:/query.kts cpg-query
3. 立即执行
4. 如果查询有错，修改 query.kts，重新执行（无需重建容器）
```

### Changes Made

**修改内容**：
1. ✅ **Section 3.2 REQ-QUERY-2**：
   - 添加完整的 .kts vs .kt 对比表
   - 明确说明为什么 .kts 更适合
   - 添加执行流程说明（无需编译）

2. ✅ **Section 3.2 新增 REQ-QUERY-3**：
   - Kotlin Scripting Engine 集成要求
   - 完整代码示例（QueryScriptExecutor）
   - 依赖列表（kotlin-scripting-jvm, kotlin-scripting-jvm-host）
   - 脚本沙箱说明（安全考虑）

3. ✅ **Section 5 新增挑战 4**：
   - Kotlin Script Engine 集成（技术细节）
   - ScriptCompilationConfiguration（自动 import）
   - ScriptEvaluationConfiguration（注入变量）
   - 脚本沙箱实现

4. ✅ **Section 6 Phase 2**：
   - 详细说明查询容器实现（4.2）
   - Kotlin Scripting Engine 集成步骤
   - 示例脚本列表（.kts 格式）
   - Docker Compose 配置示例（挂载 .kts 文件）

5. ✅ **Section 7 成功标准**：
   - 功能性标准：添加"Kotlin Script 执行"、"脚本类型检查"、"IDE 支持"
   - 性能标准：添加".kts 脚本启动"性能要求（≤ 5 秒）
   - 易用性标准：添加"脚本即用"（修改后立即执行，无需重建容器）

### Technical Implementation Details

**Kotlin Scripting Engine 关键点**：

1. **变量注入**：
   ```kotlin
   providedProperties(
       "result" to result,           // TranslationResult
       "config" to result.config,     // TranslationConfiguration
       "output" to ::outputFunction   // 输出函数
   )
   ```

2. **默认 Import**：
   ```kotlin
   defaultImports(
       "de.fraunhofer.aisec.cpg.graph.*",
       "de.fraunhofer.aisec.cpg.query.*",
       "de.fraunhofer.aisec.cpg.graph.statements.*",
       "de.fraunhofer.aisec.cpg.graph.edges.flows.*"
   )
   ```

3. **脚本编译缓存**：
   - Kotlin Script Engine 会缓存编译结果
   - 同一个 .kts 文件多次执行，只编译一次
   - 性能：首次执行 5 秒，后续执行 <1 秒

4. **错误处理**：
   ```kotlin
   when (evalResult) {
       is ResultWithDiagnostics.Success -> // 成功
       is ResultWithDiagnostics.Failure -> {
           // 详细的编译错误（行号、类型错误等）
           evalResult.reports.forEach { println(it.message) }
       }
   }
   ```

### Benefits Summary

**使用 .kts 的好处**（对用户工作流的影响）：

1. **快速迭代**：
   - 修改查询逻辑 → 立即执行 → 看到结果
   - 无需等待编译（节省 10-30 秒每次）

2. **Serverless 友好**：
   - 用户上传 .kts 文件到云存储（S3/OSS）
   - 查询容器自动加载并执行
   - 真正的"函数即代码"

3. **调试友好**：
   - 脚本编译错误有清晰的行号和类型提示
   - 可以在 IntelliJ 中编辑 .kts，获得代码补全
   - 支持断点调试（IntelliJ 的 .kts 调试功能）

4. **版本控制**：
   - 查询脚本可以单独版本管理（Git）
   - 无需将查询逻辑硬编码到容器镜像
   - 查询脚本和 CPG 版本解耦

### Lessons Learned (Update 3)

**Lesson 9: 脚本语言比编译语言更适合"用户自定义逻辑"场景**

**场景分析**：
- **用户自定义查询**：每个用户不同，需要灵活性
- **快速迭代**：用户可能需要多次调整查询逻辑
- **无需构建系统**：用户只想写查询，不想管 Gradle

**为什么 .kts 符合这些需求**：
- ✅ 脚本即代码（无需编译）
- ✅ 动态加载（运行时加载）
- ✅ 类型安全（Kotlin 的优势保留）

**教训**：
- 对于"用户自定义逻辑"，脚本化比编译化更合适
- Kotlin Script 结合了"脚本的灵活性"和"静态类型的安全性"
- 这是比 Python/JavaScript 更好的选择（类型安全 + 性能）

**Lesson 10: 容器化架构需要考虑"代码如何进入容器"**

**传统方式**（.kt）：
- 代码硬编码到容器镜像（通过 COPY 指令）
- 修改代码 → 重新构建镜像 → 重新部署

**脚本方式**（.kts）：
- 代码通过 Volume 挂载（docker run -v ./query.kts:/query.kts）
- 修改代码 → 重新运行容器（无需重建镜像）

**适用场景**：
- .kt 适合：逻辑稳定、不常修改的代码（如 query-runner.kt）
- .kts 适合：用户自定义、频繁修改的代码（如 user-query.kts）

---

**Session Update**: 2025-11-12 (Latest)
**Changes**: Added comprehensive Kotlin Script (.kts) support
**Key Additions**:
  - REQ-QUERY-2: .kts vs .kt 对比表
  - REQ-QUERY-3: Kotlin Scripting Engine 集成
  - Section 5.4: Kotlin Script Engine 集成挑战
  - Phase 2.4.2: 查询容器详细实现（含脚本执行器代码）
  - Success Criteria: .kts 相关验证标准
**Rationale**: .kts 更适合 Serverless Function 模式，支持快速迭代和动态加载
