---
id: sem-003
title: UnreachableEOGPass - Constant Evaluation and Branch Pruning
type: semantic
tags: [cpg, unreachable-code, constant-evaluation, branch-pruning, eog]
created: 2025-10-28
updated: 2025-10-28
source: cpg-analysis module analysis (Task 2)
related: [sem-001, sem-002]
---

# UnreachableEOGPass - 常量求值与分支剪枝

## Why now
This note documents the discovery that CPG already implements constant evaluation and branch pruning through the `UnreachableEOGPass`, which was the core requirement of Task 2. This is a critical finding for understanding CPG's dead code analysis capabilities.

## Core Concept

`UnreachableEOGPass` is a **data-flow analysis pass** that:
1. Evaluates branch conditions using `ValueEvaluator`
2. Determines which EOG (Evaluation Order Graph) edges are unreachable
3. Marks these edges with `EvaluationOrder.unreachable = true`
4. Enables the query engine to automatically filter dead code paths

## File Location

- **Implementation**: `/home/dai/code/cpg/cpg-analysis/src/main/kotlin/de/fraunhofer/aisec/cpg/passes/UnreachableEOGPass.kt`
- **Tests**: `/home/dai/code/cpg/cpg-analysis/src/test/kotlin/de/fraunhofer/aisec/cpg/passes/UnreachableEOGPassTest.kt`

## Key Features

### 1. Supported Statements

- **IfStatement**: Prunes unreachable then/else branches
- **WhileStatement**: Detects infinite loops and never-executed bodies
- **DoStatement**: Similar to while loops
- **ForStatement**: Handles constant loop conditions
- **SwitchStatement**: ⚠️ TODO (line 125 comment indicates planned support)

### 2. Algorithm: Lattice-based Data Flow Analysis

```kotlin
// Line 60-62: State lattice definition
sealed interface Reachability
object REACHABLE : Reachability
object UNREACHABLE : Reachability
object BOTTOM : Reachability
```

Uses a **forward data-flow analysis** with a three-level lattice:
```
    BOTTOM (⊥)
       ↓
   REACHABLE
       ↓
  UNREACHABLE (⊤)
```

### 3. Condition Evaluation

**IfStatement handling** (lines 150-182):
```kotlin
val evalResult = n.language.evaluator.evaluate(n.condition)

if (evalResult == true) {
    // False branch is unreachable
    unreachableEdges = n.nextEOGEdges.filter { e -> e.branch == false }
} else if (evalResult == false) {
    // True branch is unreachable
    unreachableEdges = n.nextEOGEdges.filter { e -> e.branch == true }
} else {
    // Cannot evaluate: both branches reachable
    unreachableEdges = listOf()
}
```

### 4. Integration with Query Engine

The `executionPath` query automatically uses `FilterUnreachableEOG` sensitivity:

```kotlin
// cpg-analysis/.../query/FlowQueries.kt:160
sensitivities = FilterUnreachableEOG + ContextSensitive
```

This means queries automatically skip unreachable code paths without user intervention.

## Capabilities

### ✅ Can Handle

1. **Constant literal conditions**:
   ```java
   if (true) { reachable(); }
   else { unreachable(); }  // Pruned
   ```

2. **Simple constant variables**:
   ```java
   final int x = 5;
   if (x > 3) { reachable(); }
   else { unreachable(); }  // Pruned
   ```

3. **Constant arithmetic**:
   ```java
   if (10 + 20 > 15) { reachable(); }
   else { unreachable(); }  // Pruned
   ```

4. **Infinite/never-executing loops**:
   ```java
   while (false) { unreachable(); }  // Body pruned
   while (true) { loop(); }  // Exit edge pruned
   ```

### ❌ Cannot Handle

1. **Merge points** (multiple DFG edges):
   ```java
   int x = condition ? 10 : 20;
   if (x > 15) { ... }  // Cannot evaluate
   ```

2. **Function calls**:
   ```java
   if (compute() > 0) { ... }  // Cannot evaluate
   ```

3. **Complex loops**:
   ```java
   for (int i = 0; i < 10; i++) { sum += i; }
   if (sum == 45) { ... }  // Cannot evaluate
   ```

## Coverage Estimate

Based on analysis in `2.feasibility-and-roadmap.md`:
- **Current**: ~35% of constant condition scenarios
- **With enhancements** (boolean ops, switch): ~50-60%
- **Theoretical maximum** (symbolic execution): ~70-80%

## Dependencies

```kotlin
@DependsOn(ControlFlowSensitiveDFGPass::class)
```

Must run **after** `ControlFlowSensitiveDFGPass` to have correct DFG edges for constant propagation.

## Example Usage

```kotlin
val result = TranslationManager.builder()
    .config(
        TranslationConfiguration.builder()
            .registerPass<UnreachableEOGPass>()
            .build()
    )
    .build()
    .analyze()

// Query unreachable edges
val unreachableEdges = result.allNodes<IfStatement>()
    .flatMap { it.nextEOGEdges }
    .filter { it.unreachable }
```

## Planned Enhancements

1. **SwitchStatement support** (TODO at line 125)
2. **Boolean operators** (&&, ||, !) in ValueEvaluator
3. **DeadCodeReporter** for explicit reporting
4. **Enhanced ConditionalExpression** handling

## Evidence

All claims verified in source code at commit 04680b1 (October 2025).

## Cross-References

- **ValueEvaluator**: See `sem-004` (evaluation infrastructure)
- **EOG Edge Properties**: See `sem-001` (Node architecture)
- **Related Docs**: `/claude/out/2/2.feasibility-and-roadmap.md`
