---
id: ep-020
title: Task 9 Batch Architecture Design - 动态批处理与可中断执行
type: episodic
date: 2025-11-13
tags: [task-9, task-prompt, batch-processing, dynamic-todolist, interruptible-execution, planning-execution-separation]
links:
  - /claude/prompt/9.research-intensive-analysis.md
related: [ep-018, ep-019]
---

# Task 9 Batch Architecture Design - Session Summary

## Goal

用户提出了关键的架构修正，要求将 Agent-based 架构改为**批处理架构**，解决账户限制和可中断性问题。

## User's Critical Corrections

### 修正 1: 批处理执行（避免账户限制）

**用户指出**：
> "task9因为太多，我认为就算拆分subagent，也不要一口气做完，可能遇见账户限制，我认为应该是批处理的形式做，每五个五个的做，用todolist的形式"

**核心问题**：
- ❌ v4.0.0 设计：一次性启动 30 个 subagents（分 4 批，但仍可能触发限制）
- ❌ 账户可能有 API 调用频率限制
- ❌ 无法中断和恢复（如果中途失败，需要重新执行）

**解决方案**：
- ✅ **每批只启动 5 个 subagents**
- ✅ 每批完成后**更新 todolist**（记录进度）
- ✅ **可中断**：每批之间可以暂停
- ✅ **可恢复**：下次从下一批继续，无需重新执行

### 修正 2: 动态 Todolist（由 Subagent 创建）

**用户指出**：
> "todolist是subagent来执行，然后对于报告的数量和subagent不要限制...这个创建todolist本身也是动态的，由subagent创建而不是task9的prompt创建"

**核心问题**：
- ❌ v4.0.0 设计：Prompt 预定义 30 个验证任务（静态）
- ❌ 无法适应实际需求（可能需要 50 个或 80 个）
- ❌ 缺乏灵活性

**解决方案**：
- ✅ **Planning Subagent 动态创建 todolist**
- ✅ 根据实际报告内容，自主决定任务数量（50-100 个）
- ✅ Prompt 只提供方法论，不预定义具体任务

### 修正 3: 验证范围扩大（所有报告和 Memory）

**用户指出**：
> "要验证的不止task4，要验证全部和记忆系统里面所有的记录"

**核心问题**：
- ❌ v4.0.0 设计：主要验证 Task 4 的 30 个缺陷
- ❌ 可能遗漏其他报告的问题
- ❌ 可能遗漏 Memory System 中的偏差

**解决方案**：
- ✅ 验证**所有历史报告**（Task 1-8）的所有技术声明
- ✅ 验证**Memory System** 的所有记录（semantic/episodic/procedural notes）
- ✅ 探索报告中未提到的所有领域

### 修正 4: 两类 Subagent（规划 vs 执行）

**用户指出**：
> "subagent实际上分为两类，大体上一类是做任务规划，一类是做任务执行"

**核心洞察**：
- ✅ **Planning Subagent**（规划型）：创建 todolist，拆解任务
- ✅ **Execution Subagent**（执行型）：执行具体的验证/探索任务
- ✅ 职责分离：规划 vs 执行

**Prompt 实现**：
- Section 2.2: 明确定义两类 Subagent
- Section 3: Planning Subagent 的详细任务
- Section 4: Execution Subagent 的详细任务

---

## Architecture Design (v5.0.0)

### 核心架构

```
Phase 1: Planning Subagent 创建动态 todolist
├─ 读取所有报告 + Memory System
├─ 提取所有需要验证的声明（50-80 个）
├─ 识别所有需要探索的领域（10-30 个）
├─ 创建动态 todolist（60-110 个任务）
└─ 输出: task-breakdown.md + todolist.json

Phase 2: Main Agent 批处理管理
├─ Loop:
│  ├─ 读取 todolist，获取下一批 5 个任务
│  ├─ 启动 5 个 Execution Subagents（并行）
│  ├─ 等待完成，收集 5 个小报告
│  ├─ 更新 todolist（标记已完成）
│  ├─ Checkpoint（记录进度）
│  └─ 继续下一批...
└─ 直到 todolist 全部完成

Phase 3: Synthesis Subagent 汇总
├─ 增量读取所有小报告（每批 10 个）
└─ 产出 4 个最终报告
```

### 关键特性

**1. 动态 Todolist**
- ✅ 由 Planning Subagent 创建（不是 prompt 预定义）
- ✅ 任务数量不限制（50-100 个）
- ✅ JSON 格式，机器可读

**2. 批处理执行**
- ✅ 每批 5 个任务
- ✅ 批次之间可暂停
- ✅ Todolist 记录进度

**3. 可中断恢复**
- ✅ 每批完成后更新 todolist
- ✅ 遇到限制时可暂停
- ✅ 下次从下一批继续

**4. 全面审计**
- ✅ 验证所有报告（Task 1-8）
- ✅ 验证所有 Memory（semantic/episodic/procedural）
- ✅ 探索未提到的领域

**5. 两类 Subagent**
- ✅ Planning（规划）：1 个
- ✅ Execution（执行）：50-100 个
- ✅ Synthesis（汇总）：1 个

---

## Key Design Decisions

### Decision 1: 为什么每批 5 个？

**考虑因素**：
1. **账户限制**: 不确定限制是多少，5 个是保守值
2. **可追踪**: 5 个任务的进度易于追踪
3. **可 Review**: 每批完成后可以 review 5 个报告（不会太多）
4. **灵活**: 可以根据实际情况调整（3 个或 8 个）

**为什么不是 10 个**？
- ❌ 10 个可能触发限制
- ❌ Review 负担过重

**为什么不是 3 个**？
- ❌ 太保守，执行时间过长
- ❌ 批次数过多（65 个任务 → 22 批）

**5 个是平衡点**：
- ✅ 安全（不太可能触发限制）
- ✅ 高效（批次数合理，13 批）
- ✅ 可调整（可以根据实际情况增减）

---

### Decision 2: Todolist 为什么由 Subagent 创建？

**用户的洞察**：
> "这个创建todolist本身也是动态的，由subagent创建而不是task9的prompt创建"

**静态 Todolist 的问题**（Prompt 预定义）:
- ❌ Prompt 预定义 30 个任务 → 无法适应实际需求
- ❌ 发现新问题时，无法动态添加任务
- ❌ 缺乏灵活性

**动态 Todolist 的优势**（Subagent 创建）:
- ✅ Planning Subagent **自主决定**任务数量
- ✅ 根据实际报告内容，提取所有需要验证的声明
- ✅ 可能发现 50 个、80 个、甚至 100 个任务
- ✅ 灵活和可扩展

**实现方式**：
1. Prompt 只提供**方法论**（如何提取验证任务？如何识别探索任务？）
2. Planning Subagent 执行方法论，产出**具体的 todolist**
3. Main Agent 读取 todolist，按批次执行

---

### Decision 3: 为什么验证所有报告和 Memory？

**用户的洞察**：
> "要验证的不止task4，要验证全部和记忆系统里面所有的记录"

**只验证 Task 4 的问题**:
- ❌ Task 1-3 也可能有偏差（例如："CPG 使用 Handler 模式" 是否完全正确？）
- ❌ Memory System 中的 semantic notes 可能有错误
- ❌ 遗漏了大量需要验证的技术声明

**全面审计的价值**:
- ✅ 验证**所有技术声明**（不只是 Task 4 的缺陷）
- ✅ 修正**所有错误知识**（包括 Memory System）
- ✅ 建立**可信的知识基础**（审计后的知识才是可靠的）

**预计任务数量**:
- Task 4 的缺陷: 30 个验证任务
- Task 1-3 的声明: 20-30 个验证任务
- Memory System 的记录: 10-20 个验证任务
- 未提到的领域: 10-30 个探索任务
- **总计**: 70-110 个任务

---

### Decision 4: 为什么不限制报告数量？

**用户的洞察**：
> "对于报告的数量和subagent不要限制"

**限制报告数量的问题**:
- ❌ 如果预设"最多 30 个报告" → Planning Subagent 可能遗漏重要任务
- ❌ 如果预设"每个缺陷一个报告" → 可能无法处理复杂缺陷（需要多个子任务）

**不限制的优势**:
- ✅ Planning Subagent **根据实际需要**决定任务数量
- ✅ 某些复杂缺陷可能拆分为多个子任务
- ✅ 灵活应对实际发现

**实际预期**:
- 最少: 40-50 个任务（如果报告质量高，声明少）
- 最多: 100-120 个任务（如果报告质量低，声明多）
- Planning Subagent 自主决定

---

### Decision 5: 两类 Subagent 的职责分离

**用户的洞察**：
> "subagent实际上分为两类，大体上一类是做任务规划，一类是做任务执行"

**职责分离的价值**:

**Planning Subagent**（规划型）:
- ✅ 专注于**全局视角**
- ✅ 读取所有报告和 Memory
- ✅ 提取所有需要验证的声明
- ✅ 识别所有需要探索的领域
- ✅ 创建**全面的、优先级排序的** todolist
- **技能**: 综合分析、优先级排序、任务拆解

**Execution Subagent**（执行型）:
- ✅ 专注于**单个任务**
- ✅ 定位源码、阅读实现、评估程度
- ✅ 产出单个小报告
- **技能**: 源码阅读、代码审计、证据提取

**为什么分离**？
- ✅ 不同的技能要求（规划 vs 执行）
- ✅ 不同的 context 需求（全局 vs 局部）
- ✅ 可并行化（1 个 Planning + 多个 Execution）

---

## Prompt Changes (v4.0.0 → v5.0.0)

### 核心变更

**1. 架构图更新** (Section 2.1)
- ✅ 明确显示批处理循环
- ✅ Main Agent 作为 Batch Controller
- ✅ 每批 5 个 Execution Subagents

**2. 两类 Subagent 明确定义** (Section 2.2)
- ✅ Planning Subagent: 1 个（规划型）
- ✅ Execution Subagent: 50-100 个（执行型）
- ✅ Synthesis Subagent: 1 个（汇总型）

**3. 审计范围扩大** (Section 1.2)
- ✅ 不只是 Task 4 的 30 个缺陷
- ✅ 所有报告的所有技术声明
- ✅ 所有 Memory System 的所有记录
- ✅ 所有未被提到的领域

**4. 批处理执行流程** (Section 4.1)
- ✅ 提供伪代码示例
- ✅ 明确 Main Agent 的循环逻辑
- ✅ 如何处理账户限制（暂停和恢复）

**5. 关键设计原则** (Section 11)
- ✅ 原则 1: 动态 vs 静态
- ✅ 原则 2: 批处理 vs 一次性
- ✅ 原则 3: 全面审计 vs 局部验证
- ✅ 原则 4: Subagent 创建 Todolist vs Prompt 创建

---

## Architecture Benefits

### Benefit 1: 完全避免账户限制

**批处理设计**:
- 每批 5 个 subagents
- 批次之间可以间隔（例如：每批间隔 5 分钟）
- 如果触发限制，暂停后恢复

**预期效果**:
- ✅ 65 个任务 → 13 批 → 每批 5 个
- ✅ 即使每批间隔 10 分钟，总时间仍 < 3 小时（纯执行时间）
- ✅ 完全避免账户限制风险

### Benefit 2: 可中断和恢复

**场景**:
```
Day 1:
- 执行 Batch 1-5（25 个任务完成）
- 遇到限制或用户需要暂停
- 更新 todolist: 25/65 完成（38%）
- 更新 episodic note: 记录进度

Day 2:
- 继续从 Batch 6 开始
- 读取 todolist: 识别下一批 5 个任务
- 无需重新执行 Batch 1-5
```

**价值**:
- ✅ 用户可以随时暂停（每批之间）
- ✅ 进度不丢失（todolist + episodic note）
- ✅ 可以分多天执行（每天 2-3 批）

### Benefit 3: 全面审计

**验证范围扩大**:
| 对象 | v4.0.0 | v5.0.0 |
|------|--------|--------|
| **Task 4 缺陷** | ✅ 30 个 | ✅ 30 个 |
| **Task 1-3 声明** | ❌ 未覆盖 | ✅ 20-30 个 |
| **Memory System** | ❌ 未覆盖 | ✅ 10-20 个 |
| **未提到领域** | ✅ 10-20 个 | ✅ 10-30 个 |
| **总任务数** | ~60 个 | **70-110 个** |

**价值**:
- ✅ 更全面的审计
- ✅ 修正所有错误知识（不只是 Task 4）
- ✅ 建立可信的知识基础

### Benefit 4: 动态灵活性

**静态设计 (v4.0.0)**:
- Prompt 预定义 30 个验证任务
- 无法适应实际发现

**动态设计 (v5.0.0)**:
- Planning Subagent 根据实际内容，动态创建 todolist
- 任务数量可能是 50 个、80 个、甚至 100 个
- 灵活应对实际需求

---

## Implementation Details

### Todolist JSON Schema

```json
{
  "generated_at": "2025-11-13T18:00:00",
  "total_tasks": 65,
  "batches": 13,
  "batch_size": 5,
  "completed": 0,
  "pending": 65,
  "tasks": [
    {
      "id": "task-1",
      "type": "verification",
      "description": "验证 D1: Static Final Field DFG Missing",
      "source": "Task 4",
      "claim": "CPG 缺少 static final 字段的 DFG 边",
      "files": ["FieldDeclarationHandler.kt", "DFGPass.kt"],
      "priority": "P0",
      "batch": 1,
      "status": "pending",
      "report": null
    },
    ...
  ]
}
```

**字段说明**:
- `id`: 任务唯一标识
- `type`: verification 或 exploration
- `description`: 任务描述
- `source`: 来自哪个报告/note
- `claim`: 需要验证的声明（仅 verification）
- `files`: 预计需要读取的源码文件
- `priority`: P0/P1/P2
- `batch`: 所属批次
- `status`: pending/in_progress/completed
- `report`: 产出的报告文件路径（完成后填充）

### Batch Execution Workflow

**Main Agent 的执行循环**:

```markdown
## Batch 1

1. 读取 todolist.json
2. 提取 batch=1, status="pending" 的任务（5 个）
3. 为每个任务生成 Execution Subagent prompt
4. 启动 5 个 subagents（使用单个消息，5 个 Task tool calls）
5. 等待完成
6. 收集报告: report-task-1.md to report-task-5.md
7. 更新 todolist:
   - task-1 to task-5: status="completed", report="report-task-1.md", ...
   - completed=5, pending=60
8. 更新 episodic note:
   ```markdown
   ## Progress
   - Batch 1 (✅): task-1 to task-5 完成（5/65, 8%）
   - Batch 2 (⏳): 待执行
   ```
9. 继续 Batch 2

## Batch 2

（重复步骤 1-9）

...

## Batch 13

（重复步骤 1-9）

最终: todolist.json 显示 completed=65, pending=0
```

### Progress Tracking

**Todolist 进度**:
```json
{
  "completed": 25,
  "pending": 40,
  "progress": "38%"
}
```

**Episodic Note 进度**:
```markdown
## Progress (Updated: 2025-11-13 Day 2)

- Batch 1 (✅): task-1 to task-5 完成
- Batch 2 (✅): task-6 to task-10 完成
- Batch 3 (✅): task-11 to task-15 完成
- Batch 4 (✅): task-16 to task-20 完成
- Batch 5 (✅): task-21 to task-25 完成
- Batch 6 (⏳): 待执行（下次继续）

**总进度**: 25/65 完成（38%）
```

---

## Expected Outcomes

### 任务数量预估

**验证任务**（来自报告和 Memory）:
- Task 4 缺陷: 30 个
- Task 1 声明（Frontend）: 10-15 个
- Task 2 声明（Core）: 15-20 个
- Task 3 声明（Scenarios）: 5 个
- Task 5-8 声明: 10 个
- Memory semantic notes: 5-10 个
- **小计**: 75-90 个验证任务

**探索任务**（未提到的领域）:
- 未分析的 Pass: 10-15 个
- 未分析的 Handler: 5-10 个
- 未分析的边类型: 5-10 个
- 测试覆盖率: 1 个
- **小计**: 21-36 个探索任务

**总计**: **96-126 个任务**

**批次数**: 19-25 批（每批 5 个）

### 执行时间预估

**Phase 1** (Planning):
- Planning Subagent 执行: 2-4 小时
- Review 任务清单: 30 分钟
- **小计**: 3-5 小时

**Phase 2** (Batch Execution):
- 每批 5 个 subagents: 30-60 分钟/批
- 19-25 批: 9.5-25 小时（纯执行时间）
- 如果每天执行 2-3 批: **7-12 天**

**Phase 3** (Synthesis):
- Synthesis Subagent 执行: 3-5 小时
- Review 最终报告: 1-2 小时
- **小计**: 4-7 小时

**总时间**: **8-14 天**（按批次执行，可中断）

---

## Key Insights and Observations

### Insight 1: 批处理是大规模审计的唯一可行方法

**观察**:
- 100+ 个任务，如果一次性执行 → 账户限制
- 批处理（每批 5 个）→ 完全可控

**教训**:
- **For >50 tasks, batch processing is mandatory**
- 批处理 = 避免限制 + 可中断 + 可追踪

---

### Insight 2: 动态 Todolist 是灵活性的关键

**观察**:
- 报告内容不同 → 任务数量不同
- 静态预定义 → 无法适应
- 动态生成 → 完全灵活

**教训**:
- **Let planning agent decide task count, not prompt**
- Planning Subagent 比 Prompt 更了解实际需求

---

### Insight 3: 全面审计的价值超过局部验证

**观察**:
- 只验证 Task 4 → 可能遗漏其他报告的问题
- 全面审计 → 修正所有错误知识

**教训**:
- **Audit everything, not just suspected areas**
- Memory System 也需要验证（可能有错误）

---

### Insight 4: 可中断执行是长期任务的必备特性

**观察**:
- 100+ 个任务 → 需要 1-2 周
- 用户可能无法一次性完成
- 必须支持暂停和恢复

**教训**:
- **For multi-day tasks, interruptibility is essential**
- Todolist + Episodic note = 进度持久化

---

### Insight 5: 用户的架构设计能力

**观察**:
- 用户在几句话内识别了 v4.0.0 的问题
- 提出了完整的解决方案（批处理 + 动态 todolist + 两类 subagent）
- 这是经过深思熟虑的设计

**教训**:
- **User's architectural feedback is invaluable**
- AI Agent 应该快速理解并实施用户的设计
- 用户可能有更深的实践经验

---

## Lessons Learned

### Lesson 1: 账户限制是真实约束

**问题**: v4.0.0 忽略了账户限制
**教训**: 必须设计时考虑 API 限制，批处理是唯一解

### Lesson 2: 可中断性是长期任务的核心需求

**问题**: v4.0.0 没有考虑中断和恢复
**教训**: Todolist 不只是进度跟踪，更是恢复机制

### Lesson 3: Planning 和 Execution 应该分离

**问题**: v4.0.0 混淆了规划和执行
**教训**: 不同的技能，应该由不同的 subagents 处理

### Lesson 4: 不要限制灵活性

**问题**: v4.0.0 预设了任务数量（30 个）
**教训**: 让 Planning Subagent 自主决定，不要预设限制

### Lesson 5: 用户的实践经验很重要

**观察**: 用户提出的批处理和动态 todolist 设计，体现了实践经验
**教训**: 应该信任和快速实施用户的架构建议

---

## Next Steps

### For Main Agent (Executing Task 9)

**Phase 1: Planning** (现在开始)
1. 启动 Planning Subagent
2. Planning Subagent 创建动态 todolist
3. Review 任务清单
4. 准备进入 Phase 2

**Phase 2: Batch Execution** (分多天执行)
- Day 1: Batch 1-3（15 个任务）
- Day 2: Batch 4-6（15 个任务）
- Day 3: Batch 7-9（15 个任务）
- ...
- Day 10-12: 剩余批次

**Phase 3: Synthesis** (最后 1-2 天)
- 启动 Synthesis Subagent
- 产出 4 个最终报告

### For Memory System

- ✅ 创建 ep-020（本文档）记录批处理架构
- ⏳ 更新 tags.json 和 topics.json（完成后）

---

## Links

- **Prompt updated**: `/claude/prompt/9.research-intensive-analysis.md` (1051 lines, v5.0.0)
- **Previous versions**:
  - v4.0.0 (927 lines) - Agent-based 并行架构
  - v3.0.0 (722 lines) - 源码审计（单个 agent）
  - v2.0.0 (685 lines) - 研究课题识别（旧版）
- **Related episodic notes**:
  - `ep-019` (Agent-based Architecture Redesign)
  - `ep-018` (Prompt Revision - 源码审计)

---

**Session Duration**: ~15 分钟
**Context Used**: ~8K tokens
**Critical Design**: 批处理执行 + 动态 todolist + 可中断恢复
**Impact**: 完全避免账户限制，支持 1-2 周的长期执行

---

## Appendix: Version Evolution

### v2.0.0 → v3.0.0 → v4.0.0 → v5.0.0

**v2.0.0** (研究课题识别):
- 基于未验证的假设
- 识别 10 个研究课题
- **问题**: 可能大部分是误判

**v3.0.0** (源码审计，单个 agent):
- 改为源码验证
- 单个 agent 串行执行
- **问题**: 时间过长（2-3 周）+ Context 溢出

**v4.0.0** (Agent-based 并行):
- 多个 subagents 并行执行
- 分 4 批，每批 4-10 个
- **问题**: 可能触发账户限制 + 无法中断恢复

**v5.0.0** (批处理动态架构):
- ✅ 批处理执行（每批 5 个）
- ✅ 动态 todolist（由 Planning Subagent 创建）
- ✅ 可中断恢复（Todolist 记录进度）
- ✅ 全面审计（所有报告 + 所有 Memory）
- ✅ 两类 Subagent（Planning vs Execution）

**版本演进的核心洞察**:
- v2 → v3: 从"假设"到"验证"（用户的第一次质疑）
- v3 → v4: 从"串行"到"并行"（用户的架构建议）
- v4 → v5: 从"一次性"到"批处理"（用户的实践经验）

**每次演进都源于用户的洞察力**！
