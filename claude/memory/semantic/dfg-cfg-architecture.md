---
id: sem-007
title: CPG Data Flow and Control Flow Analysis Architecture
type: semantic
tags: [dfg, cfg, eog, flow-analysis, java-11-17, architecture]
created: 2025-11-13
updated: 2025-11-13
source: Agent 3 - DFG and CFG/EOG Analysis Session
related: [sem-001, sem-002, sem-003, sem-004, sem-006, ep-025]
---

# CPG Data Flow and Control Flow Analysis Architecture

## Overview

**Scope**: Data Flow Graph (DFG) and Control Flow Graph (CFG/EOG) construction in CPG  
**Coverage**: Local variables, fields, method calls, control structures  
**Language Scope**: Language-independent core passes + language-specific handlers

## Data Flow Graph (DFG) Architecture

### DFG Pass Structure

**File**: `/cpg-core/src/main/kotlin/.../DFGPass.kt` (572 lines)  
**Dependency**: SymbolResolver (must resolve all symbols first)  
**Execution**: Single AST-forward walk with type dispatch

### DFG Edge Creation Strategy

**By Node Type**:

```
VariableDeclaration     → initializer (line 249-250)
Reference.READ          → declaration via prevDFGEdges (line 457)
Reference.WRITE         → declaration via nextDFGEdges (line 456)
BinaryOperator          → operands (line 505-530)
UnaryOperator           → input (line 385-392)
CallExpression          → arguments + invoked function (line 540-557)
MemberExpression        → base object (line 210-230)
FieldDeclaration        → initializer (line 282-283)
FunctionDeclaration     → return statements (line 275-276)
ReturnStatement         → return values (line 291)
```

### Critical DFG Limitations

**Scope Boundary: Local vs Fields**

```kotlin
// ControlFlowSensitiveDFGPass.kt:217-223
for (varDecl in allChildrenOfFunction.filter {
    (it is VariableDeclaration &&
     !it.isGlobal &&
     it !is FieldDeclaration &&  // ← EXPLICIT EXCLUSION
     it !is TupleDeclaration) || it is ParameterDeclaration
}) {
    // Only processes LOCAL variables
    // Fields are NOT included in control-flow-sensitive analysis
}
```

**Consequence**: 
- Field values never refined by control flow
- All static final fields have same value everywhere
- Record component fields not specially handled

## Control Flow Graph (CFG) and Evaluation Order Graph (EOG)

### EOG Pass Structure

**File**: `/cpg-core/src/main/kotlin/.../EvaluationOrderGraphPass.kt` (1500+ lines)  
**Purpose**: Build execution order graph showing evaluation order  
**Scope**: Intraprocedural only (within function boundaries)

### EOG Handler Pattern

```kotlin
// Pattern for each control structure:
protected fun handleX(node: X) {
    handleEOG(node.preconditions)        // Evaluate conditions first
    attachToEOG(node)                    // Attach node to current order
    handleEOG(node.body)                 // Then evaluate body
    // Special handling for branches, loops, jumps
}
```

**Switch Statement Handler** (line 1222-1252):
```kotlin
protected fun handleSwitchStatement(node: SwitchStatement) {
    handleEOG(node.selector)             // Evaluate selector
    attachToEOG(node)                    // Attach switch node
    
    // For each case, reset predecessors to switch entry
    // Cases branch from switch node
    // Only default guarantees switch not bypassed
}
```

### EOG Branching Semantics

**Branch Types**:
1. **If Statement**: Two branches (true/false)
2. **While Statement**: Back-edge to condition + exit edge
3. **Switch Statement**: Entry from switch to each case
4. **Try-Catch**: Exception flow to handlers
5. **Loops**: Continue/break handling

**Exception Handling**:
- Throw statements create edge to nearest handler
- Try-with-resources call close() in finally
- Exception propagation modeled explicitly

## ValueEvaluator: Symbolic Evaluation

**File**: `/cpg-core/src/main/kotlin/.../ValueEvaluator.kt`

**Purpose**: Evaluate expressions to constant values  
**Approach**: Symbolic (follow DFG) not interpretation

### Evaluation Scope

```kotlin
open fun evaluateInternal(node: Node?, depth: Int): Any? {
    when (node) {
        is Literal<*> → return node.value              // ✓ Direct
        is BinaryOperator → evaluate operands         // ✓ Operators
        is UnaryOperator → evaluate input             // ✓ Unary
        is CastExpression → evaluate expression       // ✓ Casts
        is Reference → follow DFG edges               // ✓ Variables
        is CallExpression → return handlePrevDFG()    // ✗ No evaluation
        else → return cannotEvaluate()                // ✗ Default fail
    }
}
```

**Key Limitation** (line 145-148):
```kotlin
protected open fun handleCallExpression(node: CallExpression, depth: Int): Any? {
    return handlePrevDFG(node, depth)  // No semantic evaluation
}
```

**Why**: Safety concern - method calls may have side effects

## UnreachableEOGPass: Reachability Analysis

**File**: `/cpg-analysis/src/main/kotlin/.../UnreachableEOGPass.kt` (380+ lines)

**Purpose**: Identify unreachable code by evaluating branch conditions

### Coverage

```kotlin
protected fun transfer(...) {
    when (val currentNode = currentEdge.end) {
        is IfStatement → handleIfStatement(...)      // ✓ Full
        is LoopStatement → handleLoopStatement(...) // ✓ Full
        is SwitchStatement → {
            // TODO: Add handling of SwitchStatement (line 125-126)
            // Currently unimplemented!
        }
    }
}
```

**Key Gap** (line 125-126):
```kotlin
// TODO: Add handling of SwitchStatement once we have a good way to follow the EOG edges
//  for them (e.g. based on the branching condition or similar).
```

**Result**: Cannot detect dead code in switch statements

## Record Handling in DFG/CFG

### Current Record Support

**Node Representation**:
- ✓ RecordDeclaration exists (cpg-core)
- ✓ Fields created as FieldDeclaration
- ✓ Auto-generated constructors created
- ✗ RecordComponentDeclaration missing
- ✗ Component accessors not marked

**DFG Edge Status**:

| Flow | Support | Evidence |
|------|---------|----------|
| Parameter → field (constructor) | ✓ 80% | Assignment DFG works |
| Field initializer → field | ✓ 100% | Line 282-283 |
| Field → usage (member expression) | ✗ 0% | No edge created |
| Field → component accessor | ✗ 0% | Accessor is regular method |
| Component accessor → call site | ✗ 0% | Return value not tracked |

**Root Cause**: 
- `handleMemberExpression()` (line 210-230) only connects base object
- `FieldDeclaration` handling (line 282-283) is one-directional
- No special handling for record components

## Switch Expression Challenges

### The Problem

**Switch Statement** (existing, works):
```java
switch (x) {
    case 1: result = A; break;
    case 2: result = B; break;
}
```

**Switch Expression** (Java 12-14, not supported):
```java
int result = switch (x) {
    case 1 -> A;
    case 2 -> B;
    default -> C;
};
```

### Why It's Hard

1. **Node Type**: `SwitchExpression` ≠ `SwitchStatement`
   - Expression returns value
   - Statement performs action
   - Current code only has SwitchStatement

2. **Arrow Syntax**: `->` has different semantics than `:`
   - `->`  no fall-through (must be constant or exhaustive)
   - `:` allows fall-through
   - Parser understands both, CPG doesn't distinguish

3. **Result Collection**: 
   - Need to collect all case values
   - DFG must show value flow from cases to expression result
   - Currently, no place to collect (no SwitchExpression node)

4. **Yield Statement**:
   - Explicit yield in expression form
   - Currently not recognized
   - Would need new node type

## Pattern Matching Architecture Gap

### Current State

**instanceof (Java 16+) Parsing**:
```java
if (obj instanceof String s) { ... }
```

**What CPG creates**:
```
BinaryOperator("instanceof", obj, String)
IfStatement
  thenBranch (s undefined)
  elseBranch
```

**What's missing**:
- PatternExpression node type
- Pattern variable `s` never created
- Pattern scope not modeled
- Guard expression handling

### Required Architecture

**Needed Node Types**:
```
PatternExpression (abstract)
├── TypePattern (e.g., String pattern)
├── RecordPattern (e.g., Point(int x, int y))
├── GuardedPattern (pattern && guard)
└── ConstantPattern (e.g., case "active")
```

**Needed EOG Enhancement**:
```
PatternBinding → PatternExpression → 
IfStatement with pattern scope →
  thenBranch (pattern variable in scope)
  elseBranch (pattern variable not in scope)
```

## Sealed Class Challenges

### Current Representation

**Sealed Class (Java 15+)**:
```java
sealed class Animal permits Cat, Dog { }
```

**What CPG creates**:
- RecordDeclaration with modifiers = ["sealed"]
- No representation of permits clause
- No enforcement of sealed constraint

### Architecture Gap

1. **No SealedClassDeclaration node type**
   - Cannot distinguish sealed from regular classes
   - No semantic type system integration

2. **No permits representation**
   - Cannot list permitted subtypes
   - Cannot verify class hierarchy

3. **No exhaustiveness checking**
   - Switch statements over sealed class
   - Cannot verify all subtypes covered
   - Default branch requirement not validated

## Summary of Architectur Patterns

### Strengths

1. **Clear handler dispatch** (line 105-146 in DFGPass)
   - Each node type has handler
   - Consistent pattern across passes
   - Easy to extend for new node types

2. **EOG specification-driven**
   - Each control structure has documented behavior
   - Clear branching semantics
   - Exception handling explicit

3. **Modular pass design**
   - DFGPass → ControlFlowSensitiveDFGPass → UnreachableEOGPass
   - Each pass refines previous
   - Dependencies explicit (@DependsOn)

### Weaknesses

1. **Field handling separate from variables**
   - Variables in DFG/CFG
   - Fields excluded from flow refinement
   - No bridge between two models

2. **No expression/statement unification**
   - Switch expressions don't fit SwitchStatement
   - Would need new type or refactoring
   - Affects other expression-like constructs

3. **Limited extensibility**
   - Language-specific features hard to add
   - ValueEvaluator not designed for extension
   - No hook for language-specific DFG rules

4. **TODO in critical path**
   - UnreachableEOGPass line 125-126
   - Switch handling explicitly deferred
   - Blocks switch expression support

---

**Architecture Document Created**: 2025-11-13  
**Basis**: Agent 3 DFG/CFG analysis (6000+ LOC reviewed)  
**Status**: Complete architectural overview with identified gaps

