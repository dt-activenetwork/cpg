---
id: sys-tool-003
title: Code Reference and Evidence Format Standards
type: semantic
category: system-tool
tags: [code-reference, evidence, traceability, documentation, system-knowledge]
created: 2025-10-30
updated: 2025-10-30
source: Documentation standards + project experience
related: [sys-tool-002]
---

# Code Reference and Evidence Format Standards

## Why this knowledge exists

Traceability is critical in technical documentation. This **system knowledge** defines standard formats for referencing code, providing evidence, and maintaining traceability across any project.

**Nature**: System tool knowledge (project-agnostic)

---

## Core Principles

### 1. Verifiability

**Principle**: Every claim should be verifiable by the reader

**Implementation**:
- Provide exact file paths and line numbers
- Quote relevant code snippets
- Link to source files (if possible)

### 2. Precision

**Principle**: References should be precise enough to locate code quickly

**Bad**: "The handler pattern is in the frontend"
**Good**: "The handler pattern is implemented in `JavaLanguageFrontend.kt:120-180`"

### 3. Maintainability

**Principle**: References should survive code changes (within reason)

**Implementation**:
- Reference stable identifiers (class names, function signatures)
- Provide context (not just line numbers)
- Update references when code changes significantly

---

## File Path Format

### Absolute Path

**When to use**: Cross-project references, external codebases

**Format**: Full filesystem path
```
/home/dai/code/cpg/cpg-core/src/main/kotlin/de/fraunhofer/aisec/cpg/graph/Node.kt
```

**Example**:
```markdown
The Node class is defined in `/home/dai/code/cpg/cpg-core/src/main/kotlin/de/fraunhofer/aisec/cpg/graph/Node.kt`.
```

### Relative Path (Preferred)

**When to use**: Within-project references

**Format**: Relative to project root
```
cpg-core/src/main/kotlin/de/fraunhofer/aisec/cpg/graph/Node.kt
```

**Example**:
```markdown
The Node class is defined in `cpg-core/src/main/kotlin/de/fraunhofer/aisec/cpg/graph/Node.kt`.
```

### Short Path (Context-Dependent)

**When to use**: When context is clear (e.g., within a section about one module)

**Format**: Module-relative or file name only
```
src/main/kotlin/de/fraunhofer/aisec/cpg/graph/Node.kt
Node.kt
```

**Example**:
```markdown
## CPG Core Module

All classes inherit from `Node.kt` (defined in `src/main/kotlin/...`).
```

**Recommendation**: Prefer relative path over short path for clarity

---

## Line Number Format

### Single Line

**Format**: `file:line`

**Example**:
```markdown
The evaluation entry point is at `ValueEvaluator.kt:45`.
```

**Use case**: Reference a specific line (definition, important statement)

### Line Range

**Format**: `file:start-end`

**Example**:
```markdown
The handler pattern is implemented in `JavaLanguageFrontend.kt:120-180`.
```

**Use case**: Reference a block of code (function, class, section)

### Multiple Ranges

**Format**: `file:range1,range2,range3`

**Example**:
```markdown
The Query API is spread across `FlowQueries.kt:20-80,120-180,200-250`.
```

**Use case**: Reference non-contiguous sections (interface + implementations)

---

## Code Quotation Format

### Inline Code

**When to use**: Short code snippets (1-2 lines), identifiers, keywords

**Format**: Backticks
```markdown
The `executionPath()` function returns a `QueryTree` object.
```

### Fenced Code Block

**When to use**: Multi-line code (3+ lines)

**Format**: Triple backticks with language and optional reference

````markdown
**Source**: `ValueEvaluator.kt:45-60`

```kotlin
fun evaluate(expression: Expression): Value {
    return when (expression) {
        is Literal -> expression.value
        is BinaryOp -> evaluateBinaryOp(expression)
        else -> UnknownValue
    }
}
```
````

**Best practice**: Always include source reference above or below code block

### Code with Annotations

**When to use**: Explaining specific parts of code

````markdown
```kotlin
fun executionPath(from: Node, to: Node): QueryTree {
    // 1. Initialize work queue
    val queue = mutableListOf(from)

    // 2. BFS traversal
    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        // 3. Check if target reached
        if (current == to) return QueryTree.fulfilled()
    }

    return QueryTree.unfulfilled()
}
```

**Key steps**:
1. Line 2: Initialize queue with starting node
2. Line 5-10: Breadth-first search through EOG
3. Line 8: Return fulfilled tree if path found
````

---

## Reference Styles

### Simple Reference

**Format**: Inline mention with file:line

**Example**:
```markdown
The handler pattern is implemented in `JavaLanguageFrontend.kt:120-180`.
```

### Reference with Context

**Format**: Mention + brief explanation + file:line

**Example**:
```markdown
The frontend delegates parsing to language-specific handlers using the handler
pattern (see `JavaLanguageFrontend.kt:120-180` for implementation).
```

### Reference with Quotation

**Format**: Mention + code quote + file:line

**Example**:
```markdown
The evaluation entry point handles multiple expression types:

**Source**: `ValueEvaluator.kt:45-50`

\```kotlin
return when (expression) {
    is Literal -> expression.value
    is BinaryOp -> evaluateBinaryOp(expression)
    else -> UnknownValue
}
\```
```

### Reference with Link

**Format**: Mention + hyperlink (if repository has web UI)

**Example**:
```markdown
The [Query API DSL](https://github.com/user/repo/blob/main/cpg-analysis/src/main/kotlin/FlowQueries.kt#L20-L80)
provides high-level query functions.
```

---

## Evidence Types

### 1. Code Evidence

**Purpose**: Prove code structure, behavior, or implementation

**Format**:
```markdown
## Claim: The Query API uses the QueryTree structure

**Evidence**: `FlowQueries.kt:30-35`

\```kotlin
fun executionPath(from: Node, to: Node): QueryTree {
    return QueryTree(from, to, PathMode.EXECUTION)
}
\```

The return type `QueryTree` confirms the claim.
```

### 2. Test Evidence

**Purpose**: Prove behavior through test cases

**Format**:
```markdown
## Claim: executionPath handles missing paths

**Evidence**: Test output from `QueryApiTest.kt:120`

\```
Test: executionPath_noPath_returnsUnfulfilled
Expected: QueryTree.fulfilled = false
Actual: QueryTree.fulfilled = false
Status: PASS
\```
```

### 3. Documentation Evidence

**Purpose**: Cite authoritative sources

**Format**:
```markdown
## Claim: CPG uses Joern's schema

**Evidence**: [Joern CPG Schema](https://docs.joern.io/cpg-schema/)

According to the Joern documentation:
> The CPG consists of nodes connected by edges, following the OCG schema.

This is implemented in `cpg-core/src/main/kotlin/graph/Node.kt:10-50`.
```

### 4. Build Output Evidence

**Purpose**: Prove build behavior, dependencies, etc.

**Format**:
```markdown
## Claim: cpg-analysis depends on cpg-core

**Evidence**: `cpg-analysis/pom.xml:20-25`

\```xml
<dependency>
    <groupId>de.fraunhofer.aisec</groupId>
    <artifactId>cpg-core</artifactId>
    <version>8.0.0</version>
</dependency>
\```
```

---

## Traceability Patterns

### Forward Traceability (Requirement → Implementation)

**Format**:
```markdown
## Requirement: Query execution paths in CPG

**Implementation**: `FlowQueries.kt:30-80`

The `executionPath()` function implements this requirement by traversing the EOG.
```

### Backward Traceability (Implementation → Requirement)

**Format**:
```markdown
## Implementation: executionPath() function

**Source**: `FlowQueries.kt:30-80`

**Purpose**: Implements requirement "Query execution paths in CPG" from Task 2.
```

### Change Traceability (Code Evolution)

**Format**:
```markdown
## Recent Changes

**Change 1**: Added dataFlow() function (2025-10-25)
- **File**: `FlowQueries.kt:100-150`
- **Reason**: Support data flow queries in addition to control flow
- **Related**: Task 2 requirement expansion

**Change 2**: Updated QueryTree structure (2025-10-26)
- **File**: `QueryTree.kt:20-30`
- **Reason**: Add fulfillment status tracking
- **Related**: Bug fix for unfulfilled path detection
```

---

## Common Pitfalls

### ❌ Pitfall 1: Vague References

**Bad**:
```markdown
The handler pattern is in the frontend code.
```

**Good**:
```markdown
The handler pattern is implemented in `JavaLanguageFrontend.kt:120-180`.
```

### ❌ Pitfall 2: No Line Numbers

**Bad**:
```markdown
See `ValueEvaluator.kt` for evaluation logic.
```

**Good**:
```markdown
See `ValueEvaluator.kt:45-150` for evaluation logic.
```

### ❌ Pitfall 3: Outdated References

**Problem**: Code moved, but references not updated

**Solution**: When refactoring, search for file references in documentation and update

**Example**:
```markdown
<!-- OLD (before refactor) -->
See `Utils.kt:100-120` for helper functions.

<!-- NEW (after refactor) -->
See `helpers/StringUtils.kt:10-30` for string helper functions.
```

### ❌ Pitfall 4: Absolute Paths Everywhere

**Problem**: Paths break when project moves

**Bad**:
```markdown
See `/home/user/code/project/src/Main.kt:20`
```

**Good**:
```markdown
See `src/Main.kt:20`
```

### ❌ Pitfall 5: Code Without Source

**Bad**:
````markdown
```kotlin
fun example() { ... }
```
````

**Good**:
````markdown
**Source**: `Example.kt:10-15`

```kotlin
fun example() { ... }
```
````

---

## Best Practices Summary

### ✅ DO:

1. **Always provide file paths and line numbers**
2. **Use relative paths** (not absolute)
3. **Include source reference** with code blocks
4. **Provide context** (why this code matters)
5. **Keep references precise** (exact line ranges)
6. **Update references** when code changes
7. **Link to authoritative sources** when citing

### ❌ DON'T:

1. **Vague references** ("in the frontend", "somewhere in X")
2. **File-only references** (no line numbers)
3. **Code blocks without source** attribution
4. **Absolute paths** (unless necessary)
5. **Stale references** (outdated line numbers)
6. **Unsupported claims** (no evidence)

---

## Templates

### Claim with Evidence Template

```markdown
## Claim: [Statement about code]

**Evidence**: `file:line-range`

\```[language]
[code snippet]
\```

**Explanation**: [Why this code proves the claim]
```

### Implementation Reference Template

```markdown
## [Feature/Component Name]

**Location**: `file:line-range`

**Purpose**: [What this code does]

**Key implementation details**:
- Detail 1 (`file:line`)
- Detail 2 (`file:line`)
```

### Change Log Entry Template

```markdown
**Change**: [Description]
- **Date**: YYYY-MM-DD
- **File**: `file:line-range`
- **Reason**: [Why change was made]
- **Impact**: [What this affects]
```

---

## Cross-References

- **[sys-tool-002](./markdown-best-practices.md)**: How to format documentation (this provides the reference format)
- **Related project notes**: All project semantic/episodic notes should follow these reference standards

---

## Usage in Projects

When documenting code:

1. **Query this note**: Search for "code-reference" or "evidence" in memory index
2. **Follow format standards**: Use `file:line` format consistently
3. **Provide evidence**: Every claim needs a code reference
4. **Update references**: When refactoring, update doc references

---

**Status**: System knowledge (stable, project-agnostic)
**Maintenance**: Update only if reference format standards change
