---
id: ep-018
title: Task 9 Prompt Major Revision - 从研究课题识别改为源码审计
type: episodic
date: 2025-11-13
tags: [task-9, task-prompt, prompt-revision, source-code-audit, defect-verification, critical-correction]
links:
  - /claude/prompt/9.research-intensive-analysis.md
  - /claude/result/9/研究课题清单-OBSOLETE.md
related: [ep-016, ep-017]
---

# Task 9 Prompt Major Revision - Session Summary

## Goal

用户发现了 Task 9 prompt 的**根本性缺陷**，要求完全重写 prompt，从"识别研究课题"改为"源码审计与缺陷验证"。

## Critical Discovery by User

### 用户的质疑

**用户提出的关键问题**：
> "课题1和课题2不是cpg自己实现的吗，这种级别的cpg没有实现一部分和定义好吗"

**背景**：
- **课题 1**: 过程间数据流分析（Interprocedural DFA）
- **课题 2**: 调用图构建（Call Graph Construction）
- Task 9（旧版）声称这两个功能**完全缺失**，需要深度研究

**用户的洞察**：
- ✅ CPG 作为一个成熟的静态分析框架，**不太可能完全缺失这些基础功能**
- ✅ 更可能的情况是：**已有实现，但不完善**
- ✅ Task 4 和 Task 9 可能基于**未经验证的假设**

---

## Evidence Discovery (AI Agent)

### 立即源码验证

**我立即检查 CPG 源码，发现了 3 个关键证据**：

#### 证据 1: CPG 已有调用图基础

```kotlin
// cpg-core/src/test/kotlin/.../InvokeTest.kt:42-44
call.invokes += func  // CallExpression → FunctionDeclaration
assertEquals(1, func.calledByEdges.size)  // 镜像边存在
```

**发现**：
- ✅ CPG **有** `invokes` 边（CallExpression → FunctionDeclaration）
- ✅ CPG **有** 镜像边 `calledBy` (FunctionDeclaration ← CallExpression)
- ✅ 这是调用图的基本构件
- ❌ **D4 (Call Graph Infrastructure Missing) 可能是误判**

#### 证据 2: CPG 已有过程间 DFG

```kotlin
// cpg-core/src/main/kotlin/.../DFGPass.kt:82-93
arg.prevDFGEdges.addContextSensitive(
    param,
    callingContext = CallingContextOut(call),  // 上下文敏感！
)
```

**发现**：
- ✅ CPG **有** 参数到实参的 DFG 边（argument → parameter）
- ✅ CPG **有** 上下文敏感分析（`CallingContextOut`）
- ✅ 这是过程间数据流分析的核心机制
- ❌ **D3 (Interprocedural DFG Missing) 可能是误判**

#### 证据 3: CPG 已有 Function Summaries

```kotlin
// cpg-core/src/main/kotlin/.../DFGPass.kt:51-52
log.info("Function summaries database has {} entries",
    config.functionSummaries.functionToDFGEntryMap.size)
```

**发现**：
- ✅ CPG **有** Function Summaries 机制（处理不可用源码的函数）
- ✅ 这是 Summary-based 过程间分析的基础
- ❌ **Task 4 可能遗漏了这个重要功能**

---

## Problem Analysis

### Task 4 和 Task 9 的根本问题

**问题 1: 未经源码验证的假设**
- ❌ Task 4 识别了 30 个"缺陷"，但这些分析**基于假设，未经源码验证**
- ❌ Task 4 可能只读了高层架构和文档，**没有深入 Pass 实现**
- ❌ 混淆了"功能不存在"和"功能不完善"

**问题 2: Task 9 继承了 Task 4 的错误假设**
- ❌ 旧版 Task 9 基于 Task 4 的缺陷分析，识别了 10 个研究课题
- ❌ 假设 D3 和 D4 完全缺失，需要深度研究
- ❌ 但实际上 CPG **已有基础实现**，可能只需要扩展

**问题 3: 可能的误导**
- ❌ 如果执行旧版 Task 9，会浪费资源去研究**已经实现**的功能
- ❌ 可能把"简单扩展"误判为"深度研究"
- ❌ 研究课题数量（10 个）可能被**严重高估**

### 预估的影响

**如果不修正 Task 9**：
- ❌ **误判率可能 > 50%**（10 个研究课题中，5+ 个可能是误判）
- ❌ **资源浪费**：可能花费数周研究已经实现的功能
- ❌ **错误的优先级**：真正需要研究的功能可能被忽略

---

## User's Correct Insight

### 用户提出的正确方向

**用户的建议**：
> "如果是这样，我认为task9就不应该是总结研究方向了，而应该是深度调研过去所有的未开发和缺失内容，分析source中是否有实现，实现到什么程度，对task4中提出的场景是否满足"

**核心转变**：
- ❌ **旧目标**: 识别研究课题（基于未验证的假设）
- ✅ **新目标**: 审计 CPG 源码，验证 Task 4 的缺陷

**新任务的四个阶段**：
1. **Phase 1: 源码审计** - 逐一审计 Task 4 的 30 个缺陷，阅读 CPG 源码，确认功能是否存在
2. **Phase 2: 场景验证** - 验证 Task 3 的 4 个场景在当前 CPG 下能否工作
3. **Phase 3: 真实 Gap 分析** - 将 30 个缺陷重新分类（完全缺失/部分实现/完全实现/有 bug）
4. **Phase 4: 研究课题修正** - 只为**真正缺失**的功能提出研究课题

---

## Actions Taken

### Prompt 完全重写

**旧版 Prompt** (685 lines):
- 任务类型: 研究课题识别与范围定义
- 输出: 10 个研究课题清单
- 方法: 基于 Task 4 的缺陷分析

**新版 Prompt** (722 lines):
- 任务类型: **源码审计与实现验证**
- 输出:
  - CPG 功能审计报告（30 个缺陷的真实状态）
  - 场景验证报告（4 个场景能否工作）
  - 真实 Gap 分析（重新分类缺陷）
  - 修正的研究课题清单（只包含真正需要研究的内容）
- 方法: **深度阅读 CPG 源码，提供代码证据**

### 关键变更

**1. 问题陈述（Section 1）**
- ✅ 添加了 3 个关键证据（invokes 边、过程间 DFG、Function Summaries）
- ✅ 明确指出 Task 4 可能存在重大偏差
- ✅ 定义了新的目标（源码审计，而非研究课题识别）

**2. 审计方法论（Section 2）**
- ✅ 定义了验证流程（理解缺陷 → 定位源码 → 阅读实现 → 评估程度 → 提供证据 → 分类状态）
- ✅ 提供了实现程度评估标准（0% → 100%）
- ✅ 列出了必读源码文件清单（DFGPass, SymbolResolver, FlowQueries, InvokeTest 等）

**3. 缺陷重新分类（Section 5）**
- ✅ Category A: 完全缺失 (0-25%) → 需要深度研究
- ✅ Category B: 部分实现 (26-75%) → 需要工程扩展
- ✅ Category C: 完全实现但有 bug (76-95%) → 需要修复 bug
- ✅ Category D: 完全实现且正常 (96-100%) → 无需行动（Task 4 误判）

**4. 执行策略（Section 7）**
- ✅ Phase 1: 核心缺陷优先审计 (D1-D4, 5 天)
- ✅ Phase 2: 场景验证 (Task 3 的 4 个场景, 2 天)
- ✅ Phase 3: 剩余缺陷审计 (D5-D30, 5 天)
- ✅ Phase 4: 综合分析与修正 (5 天)

**5. 交付物（Section 8）**
- ✅ CPG 功能审计报告（3000-5000 lines）
- ✅ 场景验证报告（1000-1500 lines）
- ✅ 真实 Gap 分析报告（800-1200 lines）
- ✅ 修正的研究课题清单（500-1000 lines）

**6. 预期结果**
- ✅ 研究课题数量：**从 10 个减少到 2-5 个**（大部分可能是工程扩展，不需要深度研究）
- ✅ Task 4 误判率：预计 20-40%（部分缺陷可能是误判）

### 旧结果归档

**操作**：
- ✅ 将旧版 Task 9 结果归档为 `/claude/result/9/研究课题清单-OBSOLETE.md`
- ✅ 保留以供参考，但标记为过时

---

## Key Insights and Observations

### Insight 1: 源码验证是缺陷分析的基础

**错误方法**（Task 4）:
- ❌ 基于高层架构和文档分析
- ❌ 假设功能缺失，未验证源码
- ❌ 导致误判率可能 > 50%

**正确方法**（新版 Task 9）:
- ✅ 深度阅读源码实现
- ✅ 提供代码证据（文件路径:行号 + 代码片段）
- ✅ 评估实现程度（0-100%）
- ✅ 区分"完全缺失"vs"部分实现"vs"有 bug"

**教训**：
- **Never trust assumptions, always verify with source code**
- 高层架构分析只能识别问题的"可能性"，不能替代源码验证
- 静态分析框架的核心功能（调用图、过程间分析）几乎不可能完全缺失

---

### Insight 2: "功能不存在" vs "功能不完善"

**关键区别**：
- **功能不存在** (0-25% 实现): 需要从头实现，可能需要深度研究
- **功能不完善** (26-75% 实现): 已有基础实现，只需扩展

**典型例子**（基于初步证据）：
- **D3 (Interprocedural DFG)**: 可能是"功能不完善"（已有 argument→parameter DFG，可能缺少返回值传播或多层调用支持）
- **D4 (Call Graph)**: 可能是"功能不完善"（已有基础调用图，可能缺少高级算法如 k-CFA）

**影响**：
- ❌ 旧版 Task 9 把 D3 和 D4 归类为"完全缺失"，需要 2-3 周深度研究
- ✅ 新版 Task 9 可能发现它们是"部分实现"，只需 3-5 天工程扩展

**工作量差异**：
- 深度研究: 2-3 周（理解算法理论 + 选型 + 设计 + 实现）
- 工程扩展: 3-5 天（理解现有实现 + 扩展功能）
- **时间节省**: ~75-85%

---

### Insight 3: 研究课题数量可能被严重高估

**旧版 Task 9 的估计**：
- 10 个研究课题（Level 2 + Level 3）
- 预计总研究时间：15-23 周（串行）或 6-8 周（并行）

**新版 Task 9 的预期**（审计后）：
- 真正需要深度研究的课题：**2-5 个**（而非 10 个）
- 预计总研究时间：**4-10 周**（串行）或 **2-4 周**（并行）
- **时间节省**: ~50-75%

**原因**：
- 大部分缺陷可能是"部分实现"（需要工程扩展，不需要深度研究）
- 部分缺陷可能是"完全实现但有 bug"（只需修复，不需要研究）
- 部分缺陷可能是"完全实现"（Task 4 误判，无需任何行动）

---

### Insight 4: 用户领域知识的价值

**用户的洞察力**：
- ✅ 用户基于对静态分析框架的理解，质疑 Task 9 的合理性
- ✅ 用户指出"这种级别的 CPG 不可能完全缺失调用图和过程间分析"
- ✅ 用户建议应该先审计源码，而非直接假设功能缺失

**AI Agent 的局限**：
- ❌ AI Agent 容易基于 Task 4 的输出，盲目信任其准确性
- ❌ AI Agent 倾向于"过度工作"（提供太详细的研究课题清单）
- ❌ AI Agent 缺少对静态分析框架的"常识性判断"

**教训**：
- **User's domain knowledge is invaluable** - 应该鼓励用户质疑 AI 的输出
- **AI should verify assumptions, not propagate them** - AI 应该验证假设，而非传播错误
- **Critical thinking > Blind execution** - 批判性思维比盲目执行更重要

---

### Insight 5: 任务依赖的脆弱性

**问题链**：
```
Task 4 (缺陷分析) - 可能有误判
    ↓
Task 9 (旧版：研究课题识别) - 继承 Task 4 的错误
    ↓
Task 10 (研究执行) - 基于错误的研究课题
    ↓
Task 11 (缺陷修复) - 基于错误的研究结果
```

**修正后的链**：
```
Task 4 (缺陷分析) - 提供初步线索
    ↓
Task 9 (新版：源码审计) - 验证 Task 4，修正错误
    ↓
Task 10 (缺陷修复) - 基于真实 Gap 分析
    ↓
Task 11 (研究执行) - 只对真正需要研究的课题（如果有）
```

**教训**：
- **验证链条的第一环**：如果 Task 4 有误，后续所有任务都会错误
- **引入验证步骤**：Task 9 作为"验证关卡"，阻止错误传播
- **可追溯性**：每个结论都应有代码证据，便于验证

---

## Lessons Learned

### Lesson 1: 永远不要盲目信任前置任务的输出

**问题**：
- Task 9（旧版）盲目信任 Task 4 的缺陷分析
- 没有质疑"D3 和 D4 真的完全缺失吗？"

**教训**：
- ✅ **Always verify critical assumptions** - 关键假设必须验证
- ✅ **Source code is the ground truth** - 源码是唯一的真相
- ✅ **Document-based analysis is insufficient** - 基于文档的分析不充分

---

### Lesson 2: 用户质疑是修正 AI 错误的最佳机会

**关键时刻**：
- 用户质疑："课题1和课题2不是cpg自己实现的吗？"
- AI Agent 立即验证，发现用户是对的

**教训**：
- ✅ **Encourage user to question AI outputs** - 鼓励用户质疑 AI 的输出
- ✅ **User's domain knowledge > AI's assumptions** - 用户的领域知识 > AI 的假设
- ✅ **Quick verification is key** - 快速验证是关键（立即读取源码，而非辩解）

---

### Lesson 3: Prompt 的目标定义必须精确

**旧版 Task 9 的问题**：
- 任务名称："研究课题识别"（模糊）
- 隐含假设：Task 4 的缺陷是正确的
- 导致：错误的研究课题清单

**新版 Task 9 的修正**：
- 任务名称："源码审计与缺陷验证"（精确）
- 明确目标：验证 Task 4，而非盲目接受
- 导致：真实的 Gap 分析

**教训**：
- ✅ **Task name matters** - 任务名称很重要（"识别" vs "验证"）
- ✅ **Make assumptions explicit** - 明确假设（而非隐含）
- ✅ **Define "not in scope" clearly** - 明确定义"不包括什么"

---

### Lesson 4: 增量验证 > 一次性分析

**旧方法**（Task 4 + 旧版 Task 9）：
1. Task 4: 一次性分析 30 个缺陷（未验证源码）
2. 旧版 Task 9: 一次性识别 10 个研究课题（基于未验证的缺陷）
3. 问题：错误累积，难以修正

**新方法**（新版 Task 9）：
1. 逐一审计 30 个缺陷（提供代码证据）
2. 增量修正（每发现一个误判，立即更新结论）
3. 最终汇总（真实 Gap 分析）
4. 优点：错误及时发现，易于修正

**教训**：
- ✅ **Incremental verification reduces error propagation** - 增量验证减少错误传播
- ✅ **Provide evidence for each claim** - 为每个声明提供证据
- ✅ **Allow mid-course corrections** - 允许中途修正

---

### Lesson 5: 研究 vs 工程的区分非常重要

**错误分类的代价**：
- 如果把"工程扩展"误判为"深度研究"：
  - ❌ 浪费 2-3 周研究已知算法
  - ❌ 延迟实际修复时间
  - ❌ 资源分配错误

**正确分类的价值**：
- 如果正确区分"研究" vs "工程"：
  - ✅ 工程扩展：3-5 天（快速修复）
  - ✅ 深度研究：2-3 周（只对真正需要的课题）
  - ✅ 总时间缩短 50-75%

**教训**：
- ✅ **Distinguish "missing" vs "incomplete"** - 区分"缺失"vs"不完善"
- ✅ **Research should be the last resort** - 研究应该是最后的手段（先尝试工程方法）
- ✅ **Accurate classification saves time** - 准确分类节省时间

---

## Next Steps

### Immediate Actions

**1. 执行新版 Task 9** (2-3 周)
- Phase 1: 核心缺陷优先审计 (D1-D4)
- Phase 2: 场景验证 (Task 3 的 4 个场景)
- Phase 3: 剩余缺陷审计 (D5-D30)
- Phase 4: 综合分析与修正

**2. 更新 Memory System** (本会话)
- ✅ 创建 ep-018（本文档）
- ⏳ 更新 tags.json 和 topics.json
- ⏳ 标记 ep-016 和 ep-017 为过时

**3. 考虑是否修正 Task 4** (待定)
- 如果 Task 9 发现 Task 4 的误判率 > 50%，建议：
  - 重新审视 Task 4 的方法论
  - 更新 Task 5 的资源分析（修复工作量可能大幅减少）

---

## Impact Assessment

### 对项目的影响

**时间影响**：
- **预期节省**: 6-12 周（研究课题从 10 个减少到 2-5 个）
- **预期加速**: 修复工作可能提前 1-2 个月开始（减少不必要的研究）

**资源影响**：
- **人力**: 5 名工程师的工作量可能减少 50-75%（部分缺陷只需工程扩展）
- **成本**: 研究成本可能降低 60-80%

**质量影响**：
- ✅ **更准确的 Gap 分析**（基于源码验证，而非假设）
- ✅ **更合理的修复优先级**（区分研究 vs 工程 vs 修复）
- ✅ **更快的修复周期**（工程扩展比深度研究快 5-10 倍）

---

## Links

- **Prompt modified**: `/claude/prompt/9.research-intensive-analysis.md` (722 lines, v3.0.0)
- **Obsolete result**: `/claude/result/9/研究课题清单-OBSOLETE.md` (旧版研究课题清单)
- **Related episodic notes**:
  - `ep-016` (Task 9 Prompt Creation - 初始版本，有偏差) - 需标记为过时
  - `ep-017` (Task 9 Execution - 旧版执行) - 需标记为过时
- **Related prompts**:
  - `4.gap-analysis-and-fork-roadmap.md` (Task 4: 缺陷识别，可能需要修正)

---

**Session Duration**: ~45 分钟
**Context Used**: ~25K tokens
**Critical Discovery**: CPG 已有调用图和过程间 DFG 的基础实现
**Outcome**: Task 9 prompt 完全重写（685 lines → 722 lines），从"研究课题识别"改为"源码审计"

---

## Appendix: Prompt 对比

### 旧版 Prompt 的核心问题

**Section 1 (问题陈述)**：
- ❌ 假设 Task 4 的缺陷分析是正确的
- ❌ 没有质疑"这些功能真的不存在吗？"
- ❌ 声称"AI 模型和公开资料无法提供足够深度的技术细节"（但没有验证 CPG 源码）

**Section 3 (研究课题清单)**：
- ❌ 直接列出 10 个研究课题（未验证缺陷是否真实存在）
- ❌ 课题 1 和课题 2 假设调用图和过程间 DFG 完全缺失
- ❌ 提供了详细的研究方向和要求（但基于错误的假设）

**Section 6 (执行策略)**：
- ❌ "Task 9 只是识别研究课题，实际的研究工作需要在后续任务中执行"
- ❌ 没有验证步骤，直接假设 Task 4 是正确的

### 新版 Prompt 的核心改进

**Section 1 (问题陈述)**：
- ✅ **明确指出 Task 4 可能存在重大偏差**
- ✅ 提供了 3 个关键证据（invokes 边、过程间 DFG、Function Summaries）
- ✅ 质疑："D3 和 D4 真的完全缺失吗？还是只是不完善？"

**Section 2 (审计方法论)**：
- ✅ 定义了验证流程（6 个步骤）
- ✅ 提供了实现程度评估标准（0-100%）
- ✅ 列出了必读源码文件清单

**Section 3 (审计清单)**：
- ✅ 优先审计核心缺陷（D1-D4），这些是最可能被误判的
- ✅ 为每个缺陷提供了审计报告格式（包含代码证据）

**Section 5 (真实 Gap 分析)**：
- ✅ 将缺陷重新分类为 4 类（完全缺失/部分实现/完全实现但有 bug/完全实现且正常）
- ✅ 评估 Task 4 的准确性（准确率、误报率、漏报率）

**Section 6 (修正的研究课题清单)**：
- ✅ **只有满足以下条件的缺陷才需要"研究课题"**：
  1. 确认为完全缺失 (0-25% 实现)
  2. 解决方案需要深入学术理论
  3. 现有工具实现复杂或不完整
- ✅ 预期：研究课题数量可能从 10 个减少到 2-5 个

---

**Key Takeaway**: 这次 prompt 修正是一个**范式转变**，从"基于假设的规划"改为"基于证据的验证"。这是用户领域知识和 AI Agent 快速验证能力的完美结合。
