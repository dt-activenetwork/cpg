---
id: ep-016
title: Task 9 Prompt Creation - 研究密集型缺陷分析
type: episodic
date: 2025-11-13
tags: [task-prompt, research, academic-papers, static-analysis-theory, interprocedural-analysis, call-graph, alias-analysis, symbolic-execution]
links:
  - /claude/prompt/9.research-intensive-analysis.md
related: [ep-005, ep-007, ep-009, ep-010, ep-014]
---

# Task 9 Prompt Creation - Session Summary

## Goal

用户提出需求：综合过去的研究，识别**未实现的部分中需要详细调研论文来研究实现方法的领域**，这些部分**通常不被大众和 AI 所知**。

本次会话的目标是创建 Task 9 prompt，专注于识别**需要深入学术研究**才能修复的 CPG 缺陷。

## User Request

**原始需求**：
> "我需要你创建一个新的任务的prompt，综合过去的研究，我们为了达成目标，未实现的部分中，有哪些是需要详细调研论文来研究实现方法的，这部分通常不被大众和ai所知"

**核心要求**：
1. 综合过去的研究（Task 1-8）
2. 识别**未实现的部分**
3. 找出**需要详细调研论文**的部分
4. 这些部分**不被大众和 AI 所知**

---

## Context: 已完成的工作

### 从 Memory System 了解到的背景

**已读 Memory Notes**：
- `tags.json`, `topics.json` - 理解已完成的任务和知识领域
- `ep-014` (Task 8 Prompt Creation) - 容器化架构
- `ep-015` (Task 8 Execution) - 容器化实施
- Task 4 prompt (Gap Analysis) - 识别了 30 个缺陷
- Task 5 prompt (Resource Analysis) - 人力资源分析

**已完成的任务**：
1. **Task 1**: Java CPG Frontend 分析和文档
2. **Task 2**: 常量求值和不可达代码分析
3. **Task 3**: 可达性分析教学展示（4 个真实场景）
4. **Task 4**: Gap Analysis - 识别了 **30 个缺陷**（D1-D26, M1-M4）
5. **Task 5**: Resource Analysis - 团队结构和技能分析
6. **Task 6**: Document Localization - 文档本地化
7. **Task 7**: PL Concepts Onboarding Guide - 编译原理入门指南
8. **Task 8**: Containerized Architecture - Build Once, Query Many 架构

### Task 4 识别的 30 个缺陷

**Category A (P0)**: 6 个 - 直接阻塞 Task 3 场景
- D1: Static Final Field DFG Missing
- D2: String.equals() Not in ValueEvaluator
- D3: Interprocedural DFG Missing
- D4: Call Graph Infrastructure Missing
- D5: Integer Constant Evaluation Incomplete
- D6: Enum Constants Not Resolved

**Category B (P1-P2)**: 11 个 - 真实部署必备
- D7-D17, D22-D24（包括性能、错误恢复、测试、字节码分析等）

**Category C (P2-P3)**: 7 个 - 精度提升
- D9: No Alias Analysis
- D13: Path Condition Missing
- D19: Call Graph Precision (CHA only)
- D25: Field-Sensitive DFG Missing
- D26: Context-Sensitive Interprocedural Analysis Missing
- etc.

**Category D (P3-P4)**: 3 个 - 易用性工具
**Category M (P2-P3)**: 4 个 - 多语言抽象代价

---

## Approach: 识别研究密集型缺陷

### Phase 1: 分析缺陷的研究难度

**关键洞察**：并非所有缺陷都需要深入论文研究。

**研究难度分级**：
- **Level 1 (常规工程)**: 可通过常规工程知识实现（~10 个缺陷）
  - D1, D2, D5, D11, D17, D23（工程集成、简单扩展）
- **Level 2 (中等研究)**: 需要 1-3 篇经典论文 + 参考工具（~8 个缺陷）
  - D6 (Enum), D8 (SSA), D10 (Parallel), D14 (Exception), D15 (Lambda), D22 (Incremental)
- **Level 3 (深度研究)**: 需要 5-10 篇论文，算法演进理解（~8 个缺陷）
  - **D3 (Interprocedural DFG)**: 过程间数据流分析
  - **D4 (Call Graph)**: 调用图构建
  - **D9 (Alias Analysis)**: 别名分析
  - **D13 (Path Condition)**: 路径敏感分析、符号执行
  - **D19 (Call Graph Precision)**: k-CFA, object-sensitive
  - **D25 (Field-Sensitive DFG)**: 字段敏感指针分析
  - **D26 (Context-Sensitive)**: 上下文敏感过程间分析
- **Level 4 (前沿研究)**: 学术界尚未完全解决（~4 个缺陷）
  - M1 (Generic Type Erasure)
  - Task 8 新挑战（增量序列化）

### Phase 2: 整合为 5 大核心研究主题

**发现**：Level 3 的 8 个缺陷可以整合为 5 大研究主题：

1. **主题 1: 过程间数据流分析** (Interprocedural DFA)
   - 相关缺陷: D3, D26
   - 核心论文: IFDS framework, context-sensitivity
   - 研究时间: 2-3 周

2. **主题 2: 调用图构建与精化** (Call Graph)
   - 相关缺陷: D4, D19
   - 核心论文: CHA, RTA, k-CFA, object-sensitive
   - 研究时间: 2 周

3. **主题 3: 别名分析** (Alias Analysis)
   - 相关缺陷: D9, D25
   - 核心论文: Andersen's, Steensgaard's, field-sensitive
   - 研究时间: 3-4 周

4. **主题 4: 路径敏感分析与符号执行** (Symbolic Execution)
   - 相关缺陷: D13
   - 核心论文: KLEE, path explosion, SMT solver
   - 研究时间: 4 周

5. **主题 5: SSA 与流敏感分析** (SSA)
   - 相关缺陷: D8
   - 核心论文: Cytron's algorithm
   - 研究时间: 1 周

### Phase 3: 论文引用收集

**论文数量统计**：
- **Level 3 核心论文**: ~50 篇
- **Level 2 参考论文**: ~10 篇
- **总计**: ~60 篇论文

**论文分类**：
- **基础理论** (1970s-1990s): IFDS, SSA, 符号执行基础
- **经典算法** (1990s-2000s): Andersen's, Steensgaard's, CHA, RTA, k-CFA
- **OOP 特化** (2000s): Object-sensitive, Java pointer analysis
- **现代优化** (2010s-2020s): Scalability, demand-driven, incremental

**关键论文示例**：
- [9] Reps et al., "Precise Interprocedural Dataflow Analysis via Graph Reachability" (POPL 1995)
- [23] Andersen, "Program Analysis and Specialization for the C Programming Language" (PhD thesis, 1994)
- [31] King, "Symbolic Execution and Program Testing" (CACM 1976)
- [4] Cytron et al., "Efficiently Computing SSA Form" (TOPLAS 1991)

---

## Results

### Deliverable Created

✅ **Task 9 Prompt**: `/claude/prompt/9.research-intensive-analysis.md` (923 lines)

**Prompt Quality Metrics**:
| Metric | Target | Actual |
|--------|--------|--------|
| **Clarity** | 用户可理解研究需求 | ✅ 明确分级 (Level 1-4) |
| **Completeness** | 覆盖所有研究密集型缺陷 | ✅ 8 个 Level 3 缺陷，5 大主题 |
| **Actionability** | 可直接执行研究 | ✅ 论文列表（54 篇）+ 研究路线图 |
| **Research Depth** | 论文引用充分 | ✅ 50+ 篇必读论文 |
| **Integration** | 与 Task 4/5 整合 | ✅ 引用 Task 4 缺陷，Task 5 团队结构 |

### Content Breakdown

**Section 1: 问题陈述** (~200 lines)
- 为什么需要深入论文研究
- 为什么这些知识不被大众所知（专业壁垒、实现细节、AI 边界）
- 典型例子：D3 (Interprocedural DFG)

**Section 2: 研究需求分类** (~400 lines)
- Level 1-4 分级标准
- 每个级别的示例缺陷
- Level 3 深度研究缺陷的详细分析（D3, D4, D9, D13, D19, D25, D26）
- 每个缺陷的必读论文列表（5-10 篇）
- 研究需求汇总表

**Section 3: 核心研究主题** (~150 lines)
- 5 大研究主题整合
- 每个主题的研究内容、核心论文、关键概念、研究产出

**Section 4: 研究路线图** (~100 lines)
- 并行研究策略（5 名工程师，4 周完成）
- 研究顺序建议（依赖关系）
- 交付物清单（6 个研究报告 + 论文库）

**Section 5: 成功标准** (~30 lines)
- 研究质量标准（论文摘要、算法理解、对比分析、CPG 集成）
- 可操作性标准（工程师可直接实施）
- 完整性标准（所有 Level 3 缺陷覆盖）

**Section 6-9: 其他** (~40 lines)
- 执行策略（Memory-First + Incremental）
- 关键决策点（算法选型）
- 风险与缓解（论文理解困难、实施难度、性能）
- 附录（BibTeX 引用、工具链接）

---

## Key Insights and Observations

### Insight 1: 30% 的缺陷需要深入学术研究

**观察**：
- Task 4 识别了 30 个缺陷
- 其中 **8 个缺陷**（27%）需要 Level 3 深度研究
- 这 8 个缺陷需要阅读 **50+ 篇论文**
- **研究时间占比**: 60-70%（虽然只占 30% 的缺陷数量）

**结论**：
- 并非所有缺陷都需要论文研究
- **研究密集型缺陷是修复的主要瓶颈**
- 需要专门的研究阶段（Task 9）来解决知识gap

### Insight 2: 5 大核心研究主题可以整合缺陷

**观察**：
- Level 3 的 8 个缺陷可以整合为 **5 大研究主题**
- 主题之间有**依赖关系**（Call Graph → Alias → Interprocedural DFA）
- 主题可以**并行研究**（5 名工程师同时研究）

**结论**：
- **整合研究主题**可以减少重复工作
- **识别依赖关系**可以优化研究顺序
- **并行策略**可以缩短研究时间（6 周 → 4 周）

### Insight 3: 论文理解是最大的挑战

**观察**：
- 学术论文使用**数学符号和理论术语**
- AI 模型（GPT-4/Claude）对学术论文的理解有**边界**
- 开源工具（Soot, WALA）的**实现和论文算法有差异**

**缓解策略**：
1. 优先阅读**综述论文** (survey papers) - 更易懂
2. 参考**工具实现**（Soot, WALA 源码）- 工程化的算法
3. 使用 AI 辅助解释（但需验证）
4. 寻求**外部顾问**（学术界专家）

**结论**：
- 论文阅读需要**专门的技能**（编译原理、PL 理论）
- 不能完全依赖 AI 模型（需要人工验证）
- **工具参考**是理解论文算法的最佳途径

### Insight 4: 研究驱动 vs 工程驱动的缺陷修复

**对比**：
| 维度 | 工程驱动缺陷 (Level 1) | 研究驱动缺陷 (Level 3) |
|------|---------------------|---------------------|
| **知识来源** | 公开文档、博客、Stack Overflow | 学术论文、专著、前沿研究 |
| **实施方式** | 代码复用、API 调用 | 算法理解 → 自行实现 |
| **时间预估** | 1-2 周 | 2-4 周（含研究） |
| **风险** | 低（有成熟方案） | 高（可能实施失败）|

**结论**：
- Task 9 专注于**研究驱动缺陷**（Level 3）
- 工程驱动缺陷（Level 1）不需要 Task 9（直接实施即可）
- **研究先行**可以降低实施风险

### Insight 5: 开源工具是理解算法的最佳参考

**观察**：
- Soot, WALA, Doop 都是**成熟的静态分析框架**
- 它们实现了**大部分 Level 3 算法**
- 但实现细节**不在文档中**（需要读源码）

**策略**：
- **论文 + 工具源码**结合研究
- 论文提供**算法理论**
- 工具提供**工程实践**（优化、边界情况处理）

**结论**：
- 研究报告必须包含**工具参考**（不只是论文）
- 工程师可以**复用工具代码**（减少重复开发）

---

## Challenges Encountered

### Challenge 1: 识别哪些缺陷需要论文研究

**问题**：
- Task 4 识别了 30 个缺陷，但并非所有缺陷都需要论文研究
- 如何区分"工程问题"和"研究问题"？

**解决方法**：
1. **评估标准**：
   - 是否有成熟的公开实现？（有 → Level 1, 无 → Level 3）
   - 是否需要理解算法原理？（需要 → Level 2/3, 不需要 → Level 1）
   - AI 模型能否提供足够细节？（能 → Level 1, 不能 → Level 3）
2. **分级系统**：Level 1-4，清晰区分研究深度
3. **示例分析**：为每个级别提供具体缺陷示例

**结果**：
- Level 1: ~10 个缺陷（常规工程）
- Level 2: ~8 个缺陷（中等研究）
- Level 3: ~8 个缺陷（深度研究）← **Task 9 的重点**
- Level 4: ~4 个缺陷（前沿研究）

### Challenge 2: 平衡论文数量和可读性

**问题**：
- Level 3 缺陷可能涉及 10+ 篇论文
- 如果列出所有论文，Prompt 会过于冗长
- 如何筛选"必读论文"？

**解决方法**：
1. **历史演进视角**：按时间顺序组织论文
   - 基础理论（1970s-1990s）
   - 经典算法（1990s-2000s）
   - OOP 特化（2000s）
   - 现代优化（2010s-2020s）
2. **优先级标注**：Must-read vs Nice-to-read
3. **综述论文优先**：优先推荐 survey papers

**结果**：
- 每个 Level 3 缺陷：5-10 篇必读论文
- 总计 ~54 篇论文（可管理）
- 按主题组织（不按缺陷）

### Challenge 3: 研究路线图的并行化

**问题**：
- 5 大研究主题，如果串行需要 **20+ 周**
- 如何并行化研究？
- 依赖关系如何处理？

**解决方法**：
1. **识别依赖关系**：
   - Alias Analysis 依赖 Call Graph
   - Interprocedural DFA 依赖 Call Graph
   - SSA 独立
2. **并行策略**：5 名工程师并行研究（与 Task 5 团队一致）
3. **时间优化**：4-6 周（vs 20+ 周串行）

**结果**：
- 并行研究可以**缩短 75% 的时间**
- 依赖关系通过**研究顺序**解决（先 Call Graph，后 Alias）

### Challenge 4: 论文引用的格式

**问题**：
- 如何提供论文引用？（标题、作者、会议、年份）
- 是否需要提供 BibTeX？

**解决方法**：
1. **Prompt 中**：简化引用（[编号] 作者, 标题, 会议/期刊, 年份）
2. **附录中**：提供 BibTeX 格式（工程师可直接复制）
3. **交付物中**：专门的论文引用列表文档（9.7-参考-论文引用列表.md）

**结果**：
- Prompt 可读性好（简化引用）
- 工程师可复用（BibTeX 格式）

---

## Next Steps

### For User

1. **Review Prompt**: 检查 Task 9 prompt 是否符合预期
2. **Execute Task 9** (可选): 开始研究阶段（或委托团队执行）
3. **Prioritize Research Topics**: 决定哪些主题先研究（建议：主题 2 (Call Graph) → 主题 3 (Alias) → 主题 1 (Interprocedural DFA)）

### For Future Tasks

**研究阶段完成后**：
- **Task 10** (可能): 缺陷修复实施（基于 Task 9 的研究结果）
- 每个研究主题产出：
  - 技术报告（研究结果）
  - 算法选型（推荐算法）
  - CPG 集成方案（具体实施步骤）

### For Memory System

- ⏳ **Semantic notes** (待 Task 9 执行后创建):
  - `sem-006`: Interprocedural DFA
  - `sem-007`: Call Graph
  - `sem-008`: Alias Analysis
  - `sem-009`: Symbolic Execution
  - `sem-010`: SSA
- ⏳ **Update indexes** (现在更新):
  - `tags.json`: Add "research", "academic-papers", "interprocedural-dfa", "call-graph", "alias-analysis", "symbolic-execution", "ssa"
  - `topics.json`: Add "Static Analysis Algorithms" topic

---

## Lessons Learned

### Lesson 1: 并非所有缺陷都需要论文研究

**错误假设**：
- 最初认为所有 30 个缺陷都需要深入研究
- 实际上只有 **30%** 需要 Level 3 深度研究

**教训**：
- 先**分级**（Level 1-4），再**聚焦**（Level 3）
- 不要浪费时间在已有成熟方案的缺陷上
- **工程问题用工程方法，研究问题用研究方法**

### Lesson 2: 整合研究主题可以优化研究效率

**策略**：
- 8 个缺陷 → 5 大主题
- 相关缺陷整合到同一主题（D3 + D26 → Interprocedural DFA）

**好处**：
- 减少重复研究（论文共享）
- 统一算法选型（一致性）
- 便于团队协作（明确分工）

**教训**：
- 研究任务需要**自上而下**设计（主题 → 缺陷）
- 而非**自下而上**枚举（缺陷 → 主题）

### Lesson 3: 论文理解需要多种辅助手段

**挑战**：
- 学术论文难懂（数学符号、理论术语）
- AI 模型有局限（可能产生幻觉）

**缓解策略**（多管齐下）：
1. 综述论文（入门）
2. 经典论文（理论）
3. 工具源码（实践）
4. AI 辅助（解释）
5. 外部顾问（验证）

**教训**：
- 不能只依赖论文（需要工具参考）
- 不能只依赖 AI（需要人工验证）
- **多种手段结合**才是最佳策略

### Lesson 4: 研究驱动任务需要明确的交付物

**观察**：
- Task 9 不是实施任务（不写代码）
- Task 9 是研究任务（产出知识）

**交付物设计**：
- **每个主题**：技术报告 + 算法选型 + 集成方案
- **整体**：论文引用列表 + 工具参考
- **可操作性**：工程师可直接实施（无需再次阅读论文）

**教训**：
- 研究任务的交付物是**知识产品**（报告、方案）
- 不是代码（代码在后续实施任务）
- **可操作性**是关键（研究报告必须足够详细）

### Lesson 5: 论文研究是缺陷修复的主要瓶颈

**时间分配**：
- Level 3 缺陷：30% 的数量，60-70% 的时间
- Level 1 缺陷：30% 的数量，10-20% 的时间

**结论**：
- **研究阶段**是整个项目的**关键路径**
- Task 9 必须先于实施（知识先行）
- 如果跳过研究，实施会遇到大量困难

**教训**：
- 不要急于实施（先理解算法）
- **投资研究时间**可以**节省实施时间**
- Research-driven development > Code-first development

---

## Links

- **Prompt created**: `/claude/prompt/9.research-intensive-analysis.md`
- **Related prompts**:
  - `4.gap-analysis-and-fork-roadmap.md` (缺陷识别)
  - `5.resource-analysis-and-staffing.md` (团队结构)
  - `7.pl-concepts-onboarding-guide.md` (编译原理入门)
- **Related episodic notes**:
  - `ep-005` (Task 4 Prompt Creation)
  - `ep-007` (Task 4 Execution)
  - `ep-009` (Task 5 Prompt Creation)
  - `ep-010` (Task 5 Execution)
  - `ep-014` (Task 8 Prompt Creation)
- **Related semantic notes**:
  - (待创建): `sem-006` (Interprocedural DFA), `sem-007` (Call Graph), `sem-008` (Alias), `sem-009` (Symbolic Execution), `sem-010` (SSA)

---

**Session Duration**: ~1 小时
**Context Used**: ~87K tokens
**Output**: 1 Prompt (923 lines) + 1 Episodic Note (本文档)

---

## Appendix: Task 9 Prompt Structure

### 主要章节

1. **问题陈述** (200 lines)
   - 为什么需要论文研究
   - 为什么不被大众所知
   - 典型例子

2. **研究需求分类** (400 lines)
   - Level 1-4 分级
   - Level 3 缺陷详细分析（8 个缺陷）
   - 每个缺陷的必读论文列表（54 篇）
   - 研究需求汇总表

3. **核心研究主题** (150 lines)
   - 5 大主题整合
   - 每个主题的研究内容、核心论文、关键概念

4. **研究路线图** (100 lines)
   - 并行研究策略（4 周）
   - 研究顺序建议
   - 交付物清单（6 个报告）

5. **成功标准** (30 lines)
6. **执行策略** (20 lines)
7. **关键决策点** (20 lines)
8. **风险与缓解** (20 lines)
9. **附录** (BibTeX 引用、工具链接)

**Total**: 923 lines
