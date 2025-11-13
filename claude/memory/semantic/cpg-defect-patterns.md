---
id: sem-005
title: CPG Defect Patterns for Java Constant Analysis [SUPERSEDED - CONTAINS ERRORS]
type: semantic
tags: [cpg, defect-analysis, java, constant-evaluation, gap-analysis, superseded, contains-errors]
created: 2025-10-28
updated: 2025-11-13
source: Task 4 - Gap Analysis (Scenario 1 深度分析)
related: [sem-001, sem-002, sem-003, sem-004, ep-006]
superseded-by: sem-006
---

# ⚠️ CORRECTION NOTICE

**This document has been SUPERSEDED by sem-006 due to technical errors discovered in Task 9.**

Key corrections needed:
- D3: Interprocedural infrastructure EXISTS (CallingContext, invokes edges) but is unused by ValueEvaluator
- D4: Call Graph edges exist at method level but global graph not constructed
- Missing D1 and D2: The most critical defects were completely missed

**Please refer to `/claude/memory/semantic/cpg-defect-patterns-corrected.md` (sem-006) for accurate information.**

---

# CPG Defect Patterns for Java Constant Analysis [CONTAINS ERRORS - See sem-006]

## Why now

Task 4 在分析 Scenario 1 (Factory Pattern) 时,发现了两个核心缺陷模式,这些模式是阻塞性的,会在所有后续场景中重复出现。提取为 semantic note 可以:
1. 后续分析 Scenario 2-4 时直接引用,避免重复分析
2. 为其他类似缺陷提供分析框架
3. 作为稳定知识,支持未来的缺陷修复工作

## Core Defect Patterns

### Pattern 1: Frontend-Core Responsibility Gap

**定义**: Frontend 和 Core 之间的职责边界不清晰,导致某些功能"两边都不做"而缺失。

**机制**:
1. **Frontend 视角**: "这是 Core Pass 的责任,我只负责节点创建"
2. **Core 视角**: "这是语言特定的,我设计为语言无关,不应该处理"
3. **结果**: 功能gap,特性缺失

**实例: D1 (Static Final Field DFG Missing)**:
- **Frontend** (`FieldDeclarationHandler`): 创建 `FieldDeclaration` 节点,处理 `initializer`,但不创建 DFG 边
- **Core** (`ControlFlowSensitiveDFGPass`): 只处理局部变量,不处理 `static final` 字段 (语言特定)
- **Gap**: `static final` 字段的 DFG 边缺失

**根本原因**:
- CPG 架构明确分离 Frontend (语言特定) 和 Core (语言无关)
- 但缺少**中间层机制** (Language-Specific Pass) 来填补这个gap
- 没有明确的 "谁负责语言特定的图边创建" 的规范

**影响范围**:
- 所有语言特定的数据流分析特性 (Java `static final`, C++ `constexpr`, Python 模块常量)
- 所有需要前端语义知识的图构建 (泛型实例化、Lambda 解语法糖、注解处理)

**Abstraction Tax**: 40%
- 40% 源于多语言抽象限制了 Core 的语言特定优化
- 60% 源于架构设计缺少 Language-Specific Pass 机制

---

### Pattern 2: Language-Agnostic Design Blocks Language-Specific Optimization

**定义**: 为了保持语言无关性,Core 组件无法内置语言特定的优化,导致语言特定的常见模式无法支持。

**机制**:
1. **设计目标**: Core 组件 (ValueEvaluator, DFG Pass, Query API) 设计为适用所有语言
2. **限制**: 语言特定的特性 (Java `String.equals()`, C++ `std::string::compare()`) 无法在通用组件中硬编码
3. **结果**: 语言特定的常见操作无法求值或优化

**实例: D2 (String.equals() Not Supported)**:
- **Core 组件**: `ValueEvaluator` 设计为纯函数式求值器,只求值运算符,不求值方法调用
- **Java 需求**: `String.equals()` 是 Java 中最常见的字符串比较方式 (60% 条件表达式)
- **冲突**: `equals()` 是方法调用 (CallExpression),但 Core 的 `ValueEvaluator` 不支持方法调用求值
- **Gap**: 60% 的 Java 条件表达式无法求值

**根本原因**:
- **安全性考虑**: 方法调用可能有副作用,通用求值器无法安全地模拟执行
- **语言差异**: Java 的 `String.equals()` vs Python 的 `str.__eq__()` vs C++ 的 `std::string::compare()`,没有统一的接口
- **缺少扩展机制**: Core 的 `ValueEvaluator` 不是设计为可被 Frontend 扩展的

**影响范围**:
- 所有依赖语言标准库方法的常量求值 (字符串、数学、集合操作)
- 所有语言特定的比较运算符重载
- 所有需要语言语义知识的表达式求值

**Abstraction Tax**: 80%
- 80% 源于语言无关设计阻止了语言特定的求值逻辑
- 20% 源于缺少扩展机制 (即使在多语言架构下,也应该允许语言特定的 Evaluator)

---

## Defect Catalog (Part 1: Scenario 1)

### D1: Static Final Field DFG Missing

**Category**: A (Blocking Task 3 Scenarios)
**Priority**: P0
**Pattern**: Frontend-Core Responsibility Gap

**Problem**: `static final` 字段的 initializer 与 field access 之间缺少 DFG 边

**Impact**:
- **常量覆盖率**: 70% 的 Java 常量无法求值
- **场景阻塞**: Scenario 1, 2, 3 完全阻塞 (100%)
- **误报率**: 83% (6 个类报告可达,只有 1 个实际可达)

**Root Cause**:
- **Frontend** (`FieldDeclarationHandler`): 认为 DFG 边是 Core Pass 的责任
- **Core** (`ControlFlowSensitiveDFGPass`): 认为 `static final` 是语言特定的,不应该由 Core 处理
- **结果**: 两边都不做,DFG 边缺失

**Evidence**:
- Task 1: `1.frontend-architecture.md:890-920` (Handler 职责)
- Task 2: `2.graph-and-query-analysis.md:450-480` (DFG Pass 只处理局部变量)
- sem-003: ValueEvaluator 依赖 DFG 边进行常量传播

**Abstraction Tax**: 40%

**Dependencies**:
- **Depended by**: D2 (两者都需要修复才能解锁 Scenario 1)
- **Related**: D3 (过程间 DFG 也需要处理 static final)

---

### D2: String.equals() Method Call Evaluation Not Supported

**Category**: A (Blocking Task 3 Scenarios)
**Priority**: P0
**Pattern**: Language-Agnostic Design Blocks Language-Specific Optimization

**Problem**: `ValueEvaluator` 不支持方法调用求值,包括 `String.equals()`

**Impact**:
- **条件覆盖率**: 60% 的 Java 条件表达式无法求值
- **场景阻塞**: Scenario 1, 2, 3 完全阻塞 (100%)
- **与 D1 叠加**: 即使 D1 修复,如果没有 D2,仍然无法剪枝分支

**Root Cause**:
- `ValueEvaluator` 设计为纯函数式求值器,只求值运算符,不求值方法调用
- 原因: 方法调用可能有副作用,通用求值器无法安全地模拟执行
- 缺少: Pure Method 白名单机制 (列出无副作用的方法)

**Evidence**:
- Task 2: `2.evaluation-infrastructure.md` (ValueEvaluator 支持的表达式类型)
- sem-003: UnreachableEOGPass 调用 `evaluate()`,如果返回 `cannotEvaluate`,不剪枝任何边

**Java Best Practice**:
```java
String a = new String("01");
String b = new String("01");
a == b  // false (比较引用,不是值)
a.equals(b)  // true (比较值)
```
→ Java 总是使用 `equals()` 比较字符串,`==` 仅用于 primitive types

**Abstraction Tax**: 80%

**Dependencies**:
- **Depends on**: D1 (需要先有 DFG 边才能求值常量引用)
- **Related**: D13 (Boolean Operators Not Supported, 类似的求值缺失)

---

## Defect Interaction: D1 + D2 Blocking Scenario 1

**Scenario 1 需要 D1 和 D2 都修复才能成功**:

**流程**:
```
常量定义             分支条件              分支剪枝
KbGyomConst     →   sijiKbn.equals()  →  UnreachableEOGPass
    |                     |                      |
    | (需要 DFG)          | (需要求值 equals)    | (需要条件为 true/false)
    ↓                     ↓                      ↓
   D1                    D2                   Scenario 1
  缺失                  缺失                   失败 (83% 误报)
```

**当前状态**:
- ❌ D1 未修复 → `evaluate(KbGyomConst.TANPO_CAL_I_K_TOJITUYAK)` → `cannotEvaluate` (无 DFG 边)
- ❌ D2 未修复 → `evaluate(sijiKbn.equals("01"))` → `cannotEvaluate` (CallExpression 不支持)
- ❌ `UnreachableEOGPass` → 无法剪枝任何分支 (条件无法求值)
- **结果**: 6 个类都报告为可达,误报率 83%

**修复后**:
- ✅ D1 修复 → DFG 边连接常量定义与使用点 → `evaluate(KbGyomConst.XXX)` → `"01"`
- ✅ D2 修复 → 支持 `equals()` 求值 → `evaluate("01".equals("01"))` → `true`
- ✅ `UnreachableEOGPass` → 剪枝 5 个分支 (条件为 `false`)
- **结果**: 只有 1 个类可达,误报率 0%

**Critical Path**: **必须同时修复 D1 和 D2** 才能解锁 Scenario 1

---

## Analysis Framework for Other Scenarios

基于 Scenario 1 的分析,建立了通用的分析框架:

### Step 1: Identify Constant Source
- 常量定义在哪里? (`static final` 字段, enum, literal)
- Frontend Handler 如何处理? (创建节点, DFG 边?)

### Step 2: Trace DFG
- 常量如何流动到使用点? (DFG 边是否存在?)
- 是否跨越方法边界? (过程间分析需求)

### Step 3: Evaluate Condition
- 条件表达式是什么类型? (`equals()`, 算术比较, 布尔运算)
- `ValueEvaluator` 是否支持? (查看 `2.evaluation-infrastructure.md`)

### Step 4: Branch Pruning
- `UnreachableEOGPass` 能否剪枝? (需要条件求值为 `true` 或 `false`)
- 是否有其他阻塞因素? (异常流、多路径合并)

### Step 5: Reachability Query
- Query API 是否过滤不可达边? (默认启用 `FilterUnreachableEOG`)
- 最终精度如何? (误报率、覆盖率)

**此框架将应用于 Scenario 2-4 的分析**

---

## Cross-References

- **Architecture**: sem-001 (Java CPG Frontend), sem-002 (Handler Pattern)
- **Evaluation**: sem-003 (UnreachableEOGPass), sem-004 (Query API DSL)
- **Task Outputs**: Task 1 (`1.frontend-architecture.md`), Task 2 (`2.evaluation-infrastructure.md`)
- **Gap Analysis**: `/claude/result/4/4.gap-analysis.md` (Part 1, Scenario 1)

---

## 证据

所有分析基于:
- CPG 源码 commit 04680b1 (2025-10-28)
- Task 1-3 的输出文档
- Semantic notes sem-001 to sem-004

---

## Defect Catalog (Part 2: Scenario 2 - Interprocedural)

### D3: Interprocedural DFG Missing

**Category**: A (Blocking Task 3 Scenarios)
**Priority**: P1
**Pattern**: Universal Engineering Challenge (not abstraction-related)

**Problem**: DFG 只在单个方法内部构建,不跨越方法边界

**Impact**:
- **方法调用覆盖率**: 40% 的 Java 方法调用传递常量参数,无法追踪
- **场景阻塞**: Scenario 2, 3 完全阻塞 (50%)
- **误报率**: 67% (方法内部) + 50% (跨方法)

**Root Cause**:
- **Primary**: DFG Pass 设计为 intraprocedural (早期设计优先级)
- **Secondary**: 缺少 Call Graph 基础设施 (必须先有 D4)
- **Tertiary**: 上下文敏感性缺失 (同一方法多个调用上下文)

**Evidence**:
- Task 2: `2.graph-and-query-analysis.md:450-480` (DFG Pass 只处理局部变量)
- sem-003: ValueEvaluator 依赖 DFG 边,无法跨方法回溯

**Abstraction Tax**: 0%
- 这是通用的工程挑战,所有语言都需要过程间分析
- Java Fork 不会自动解决这个问题,仍需实现相同的算法

**Dependencies**:
- **Depends on**: D4 (Call Graph, 前置条件), D1 (Static Final), D2 (equals)
- **Conflicts with**: D6 (Flow-Sensitive DFG, 上下文爆炸风险)

---

### D4: Call Graph Infrastructure Missing

**Category**: A (Blocking Task 3 Scenarios)
**Priority**: P1
**Pattern**: Foundational Infrastructure Gap

**Problem**: CPG 缺少 Call Graph 基础设施,无法识别方法调用关系

**Impact**:
- **过程间分析**: 完全失效 (无法构建 D3)
- **场景阻塞**: Scenario 2, 3 完全阻塞 (50%)
- **Dead Code Detection**: 无法识别从未被调用的方法

**Root Cause**:
- **Primary**: Call Graph 实现复杂 (CHA vs RTA vs k-CFA),工程优先级低
- **Secondary**: 依赖准确的类型信息 (→ D5, Java 虚函数调用)
- **Tertiary**: 多语言抽象增加设计复杂度 (不同语言调用约定差异)

**What is a Call Graph?**:
```
Nodes: 所有方法 (functions/methods)
Edges: 方法调用关系 (caller → callee)

Example:
main() → executeTask()
executeTask() → getList()
executeTask() → SumKomkUpd()
```

**Evidence**:
- Task 2: 完整分析了 AST, EOG, DFG, **但没有提到 Call Graph**
- sem-004: Query API 的 `Interprocedural()` 依赖 EOG 的 `invokeEdges`,不是全局 Call Graph

**Abstraction Tax**: 30%
- 30% 源于多语言抽象增加设计复杂度 (Java/C++/Python 调用约定不同)
- 70% 源于实现复杂度和工程优先级

**Dependencies**:
- **Depends on**: D5 (Type System, 需要类型信息解析虚函数)
- **Depended by**: D3 (Interprocedural DFG 必须基于 Call Graph)

---

## Scenario 2 Critical Path

**Scenario 2 需要修复 4 个缺陷** (按依赖顺序):

```
基础设施           过程间分析         方法内分析         分支剪枝
D4               D3                D1 + D2           UnreachableEOGPass
(Call Graph)  →  (Interprocedural  →  (Static Final  →  (条件求值)
                  DFG)               + equals)
```

**修复顺序**:
1. **D4** (3-4 周): 实现 Call Graph (CHA 算法)
2. **D3** (1-2 周): 基于 D4 实现过程间 DFG
3. **D1** (3-6 小时): Static Final Field DFG
4. **D2** (3-6 小时): String.equals() 求值

**总工作量**: **5-7 周** (vs Scenario 1 的 3-6 小时)

**关键洞察**:
- **D4 是基础设施级缺陷**,不仅影响 Scenario 2,还影响所有过程间分析
- **一次投资,长期收益**: 修复 D4 + D3 后,所有跨方法的常量传播场景都可以支持

---

## Updated Analysis Framework (加入过程间分析)

### Step 1: Identify Constant Source
- 常量定义在哪里? (`static final` 字段, enum, literal)

### Step 2: Trace DFG - Intraprocedural
- 常量如何在方法内部流动? (DFG 边是否存在?)

### Step 3: Trace DFG - Interprocedural (NEW)
- 常量是否跨越方法边界? (需要 D4 + D3)
- 是否有 Call Graph 记录调用关系?
- 实参和形参是否有 DFG 边连接?

### Step 4: Evaluate Condition
- 条件表达式是什么类型? (`equals()`, 算术比较, 布尔运算)
- `ValueEvaluator` 是否支持? (查看 `2.evaluation-infrastructure.md`)

### Step 5: Branch Pruning
- `UnreachableEOGPass` 能否剪枝? (需要条件求值为 `true` 或 `false`)

### Step 6: Reachability Query
- Query API 是否过滤不可达边? (默认启用 `FilterUnreachableEOG`)

**此框架将应用于 Scenario 3-4 的分析**

