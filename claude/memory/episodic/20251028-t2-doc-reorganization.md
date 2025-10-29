---
id: ep-003
title: Task 2 文档整改 - Query API 与图层协同分析
type: episodic
date: 2025-10-28
task: 2.constant-eval-and-reachability.md (continuation)
tags: [task-completion, documentation, query-api, cpg-analysis, graph-infrastructure]
links:
  - /claude/result/2/2.graph-and-query-analysis.md
  - /claude/memory/episodic/20251028-t2-constant-eval-analysis.md (ep-002)
  - /claude/memory/semantic/query-api-dsl.md (sem-004)
---

# Task 2 文档整改 - Session Summary

## 背景

在 Task 2 的初始完成后（见 ep-002），用户指出文档存在重大架构理解偏差：

**用户反馈** (2025-10-28):
> "不是补充，而是在现有的文档上全面的整改。Query API 和 Graph 是同级别且不可或缺的，本质上 Query API 是一个查询 DSL，最终它们在分析流程中是同级别且不可或缺的。"

**问题诊断**:
- 原文档将 Query API 视为图层的辅助工具
- Query API 分析深度不足（仅占文档 5%）
- 未体现 Query API 作为 DSL 的核心地位
- 未展示图层与查询层的协同关系

## 任务目标

对 Task 2 的 4 个输出文档进行**全面整改**（整改，非补充）：
1. `2.graph-and-query-analysis.md` - 图结构与查询 DSL 协同分析
2. `2.evaluation-infrastructure.md` - 常量求值基础设施（需整合 Query 使用）
3. `2.feasibility-and-roadmap.md` - 可行性与路线图（需整合 Query API 流程）
4. `2.examples-and-diagrams.md` - 示例与图表（需增加 Query DSL 示例）

**核心要求**:
- 将 Query API 从"辅助工具"提升为"核心组件"
- 展示图层与查询层的**协同设计**，而非主从关系
- 体现 Sensitivity 系统作为两层桥梁的作用
- 强调 Query API 是一个完整的 DSL，不是简单的查询接口

## 工作进展

### Phase 1: 深度分析 Query API（已完成）

**文件分析** (2025-10-28):
1. `/home/dai/code/cpg/cpg-analysis/src/main/kotlin/de/fraunhofer/aisec/cpg/query/QueryTree.kt` (920 行)
   - QueryTree 核心设计：value, children, operator, assumptions
   - DSL 操作符：AND/OR/NOT/IMPLIES/EQ/NE/GT/GE/LT/LE/IN/IS
   - mergeWithAll/mergeWithAny 聚合函数
   - printNicely() 调试功能

2. `/home/dai/code/cpg/cpg-analysis/src/main/kotlin/de/fraunhofer/aisec/cpg/query/FlowQueries.kt` (654 行)
   - executionPath: 默认使用 `FilterUnreachableEOG + ContextSensitive` (行 244)
   - dataFlow: 默认使用 `FieldSensitive + ContextSensitive` (行 175)
   - alwaysFlowsTo: 数据流必经点分析
   - Must/May 分析类型

3. `/home/dai/code/cpg/cpg-core/src/main/kotlin/de/fraunhofer/aisec/cpg/graph/AnalysisConfiguration.kt` (716 行)
   - FilterUnreachableEOG: 行 594-605，过滤 `unreachable == true` 的边
   - ContextSensitive: 行 622-655，调用栈管理
   - FieldSensitive: 行 661-697，索引栈管理
   - Forward/Backward/Bidirectional: 遍历方向控制

4. `/home/dai/code/cpg/cpg-core/src/main/kotlin/de/fraunhofer/aisec/cpg/graph/Extensions.kt` (1549 行)
   - followXUntilHit: 行 854-960，核心工作列表算法
   - followEOGEdgesUntilHit: 行 379-404，EOG 遍历包装
   - followDFGEdgesUntilHit: 行 415-442，DFG 遍历包装

**关键发现**:

1. **QueryTree 是结果记录系统**:
   - 不仅返回 true/false，还记录完整求值路径
   - 每个 QueryTree 节点代表一步求值
   - 支持 assumptions 追踪不确定性
   - 支持 suppression 用户手动修正

2. **Sensitivity 是图层与查询层的桥梁**:
   - 输入：图层的 Edge 和 Node 属性
   - 输出：布尔值（是否遍历该边）
   - 效果：动态改变遍历算法行为

3. **executionPath 自动集成常量求值结果**:
   - 默认使用 `FilterUnreachableEOG`
   - 读取 `EvaluationOrder.unreachable` 属性（由 UnreachableEOGPass 设置）
   - 用户无需手动配置，自动过滤死代码

### Phase 2: 重组第一份文档（已完成）

**文件**: `/home/dai/code/cpg/claude/result/2/2.graph-and-query-analysis.md`

**原结构** (30 KB):
```
1. 概述
2. 核心节点模型 (Node.kt)
3. 分支节点接口与实现
4. EOG 构建 Pass 分析
5. DFG 构建 Pass 分析
6. 查询引擎分析 (简单提及)
7. FilterUnreachableEOG 的实现位置
8. 图结构总结
9. 关键结论
```

**新结构** (100 KB):
```
1. 概述 - 强调图层与 Query DSL 协同设计
2. 核心图结构：代码的多维度表示
   2.1 基础节点模型 (Node.kt)
   2.2 EOG 边的详细结构：支持不可达性标记
   2.3 DFG 边的详细结构：支持粒度与上下文
   2.4 分支节点接口与实现
   2.5 图构建 Pass：从 AST 到多维图
3. 查询 DSL 层：图遍历与分析的统一接口 ← 新增 45 KB
   3.1 QueryTree：路径追踪与结果聚合的核心
   3.2 高级查询函数：executionPath 与 dataFlow
   3.3 分析配置系统：Sensitivity 与 AnalysisDirection
   3.4 路径遍历核心算法：followXUntilHit
4. 协同机制：常量求值与分支剪枝的完整流程
   4.1 图层贡献：UnreachableEOGPass
   4.2 查询层贡献：FilterUnreachableEOG
   4.3 完整流程示意图
   4.4 常量求值能力矩阵
5. 协同设计的优势与扩展性
   5.1 分离关注点
   5.2 可配置性
   5.3 QueryTree 的可扩展性
   5.4 图层的扩展能力
6. 图结构总结
7. 关键结论
   7.1 图层与查询层的同级地位
   7.2 Sensitivity 系统是桥梁
   7.3 常量求值是图层与查询层的共同成果
   7.4 设计优势
   7.5 已有基础设施支持常量求值+分支剪枝
8. 附录：文件清单
```

**关键改进**:

1. **标题变更**:
   - 原："CPG 核心图基础设施深度分析"
   - 新："**CPG 核心分析基础设施：图结构与查询 DSL 的协同设计**"

2. **第 3 章（全新）：查询 DSL 层 - 45 KB**:
   - 3.1: QueryTree 设计理念、DSL 操作符、mergeWithAll/Any
   - 3.2: executionPath/dataFlow/alwaysFlowsTo 完整分析
   - 3.3: FilterUnreachableEOG/ContextSensitive/FieldSensitive 机制
   - 3.4: followXUntilHit 核心算法逐行解析

3. **第 4 章：协同机制**:
   - 用序列图展示图层和查询层的交互
   - 明确标注各步骤属于哪一层
   - 强调用户体验的无缝集成

4. **第 7 章：关键结论**:
   - 7.1: 明确"不是主从关系，而是协同关系"
   - 7.2: Sensitivity 系统是两层的桥梁
   - 7.3: 常量求值是两层的共同成果

**内容占比变化**:
- 原文档：图层 95%，Query API 5%
- 新文档：图层 45%，Query DSL 45%，协同机制 10%

### Phase 3: 待完成工作

**剩余文档**:
1. `2.evaluation-infrastructure.md` (24 KB)
   - 需要：整合 Query API 如何使用 ValueEvaluator 结果
   - 需要：展示 executionPath 与 ValueEvaluator 的协同

2. `2.feasibility-and-roadmap.md` (34 KB)
   - 需要：路线图中加入 Query API 层面的工作
   - 需要：覆盖率估算考虑 Query DSL 的影响

3. `2.examples-and-diagrams.md` (22 KB)
   - 需要：增加 Query DSL 使用示例
   - 需要：展示 executionPath/dataFlow 的实际用法
   - 需要：Mermaid 图表展示查询流程

**内存系统更新**:
- 创建新的语义记忆：`query-api-dsl.md` (sem-004)
- 更新 episodic 记忆：本文档 (ep-003)
- 更新 topics.json 和 tags.json

## 架构洞察

### 协同设计的本质

**图层职责**:
- 定义数据结构（Node, Edge, 属性）
- 构建图（Pass 系统）
- 提供静态基础（what）

**查询层职责**:
- 提供 DSL 接口（executionPath, dataFlow）
- 实现遍历算法（followXUntilHit）
- 提供动态行为（how）

**连接机制**:
- **Sensitivity 系统**：读取图层属性（Edge.unreachable, Edge.granularity），控制查询层遍历
- **Context 对象**：查询层维护状态（callStack, indexStack），在遍历过程中传递
- **QueryTree**：查询层记录路径，引用图层节点（Node）

### 常量求值的完整流程

```
图层：EvaluationOrderGraphPass
  └─ 构建 EOG 边，设置 branch 属性（true/false/null）

图层：DFGPass
  └─ 构建 DFG 边，为常量传播提供基础

图层：UnreachableEOGPass（依赖 DFG）
  ├─ 使用 ValueEvaluator 求值条件
  ├─ 根据结果设置 EOG.unreachable = true
  └─ 输出：图中标记了不可达边

查询层：executionPath（默认集成）
  ├─ 使用 FilterUnreachableEOG sensitivity
  ├─ followEOGEdgesUntilHit 检查 edge.unreachable
  └─ 自动跳过不可达边

用户：调用 executionPath(start, predicate)
  └─ 无需手动配置，自动获得死代码过滤
```

**关键点**:
1. 图层负责"标记"（设置属性）
2. 查询层负责"使用"（读取并过滤）
3. 用户体验无缝（默认集成）

## 经验总结

### 文档重组策略

1. **标题即立场**：标题必须体现核心架构理念
2. **比例即重要性**：两个同级组件应占据相似篇幅
3. **顺序体现关系**：先介绍数据（图层），再介绍操作（查询层），最后介绍协同
4. **结论强化理解**：在结论章节明确架构关系

### 技术文档写作

1. **证据为王**：所有结论必须有文件路径 + 行号支持
2. **代码为证**：关键逻辑必须引用源码
3. **图表辅助**：复杂流程用序列图/流程图展示
4. **示例说明**：抽象概念配具体代码示例

### 架构理解误区

**误区 1**：将 Query API 视为图的"查询接口"
- **正确**：Query DSL 是与图层同级的分析基础设施

**误区 2**：认为 Query API 只是简单的遍历函数
- **正确**：Query DSL 包含 QueryTree、Sensitivity、AnalysisType 等完整系统

**误区 3**：将 Sensitivity 视为查询层的配置参数
- **正确**：Sensitivity 是连接图层和查询层的桥梁

## 下一步行动

1. **创建语义记忆**: `query-api-dsl.md` (sem-004)
2. **整改文档 2**: `2.evaluation-infrastructure.md`
3. **整改文档 3**: `2.feasibility-and-roadmap.md`
4. **整改文档 4**: `2.examples-and-diagrams.md`
5. **更新索引**: topics.json, tags.json

## 链接

- **输出文件**: `/home/dai/code/cpg/claude/result/2/2.graph-and-query-analysis.md` (已完成)
- **源码分析**: QueryTree.kt, FlowQueries.kt, AnalysisConfiguration.kt, Extensions.kt
- **前置工作**: ep-002 (Task 2 初始完成)
