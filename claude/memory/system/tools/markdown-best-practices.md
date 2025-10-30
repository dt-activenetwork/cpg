---
id: sys-tool-002
title: Markdown Documentation Best Practices
type: semantic
category: system-tool
tags: [markdown, documentation, writing, formatting, system-knowledge]
created: 2025-10-30
updated: 2025-10-30
source: Documentation standards + project experience
related: [sys-tool-001, sys-tool-003]
---

# Markdown Documentation Best Practices

## Why this knowledge exists

Markdown is the standard format for technical documentation. This **system knowledge** provides universal best practices for writing clear, maintainable documentation that can be applied to any project.

**Nature**: System tool knowledge (project-agnostic)

---

## Core Principles

### 1. Write for Humans First

**Target audience**: Beginners should understand high-level purpose; experts should find details

**Structure**:
- Start with "Why" before "How"
- Use progressive disclosure (simple → complex)
- Include examples for every concept

### 2. Scannable Structure

**Problem**: Readers scan, not read linearly

**Solution**:
- Use headings hierarchically (H1 → H2 → H3)
- Use lists for multi-item content
- Use tables for comparisons
- Use code blocks for examples
- Use callouts for important notes

### 3. Evidence-Based Claims

**Principle**: Every non-trivial claim needs evidence

**Evidence types**:
- Code references (file:line)
- Links to authoritative sources
- Examples demonstrating the claim
- Test outputs or logs

---

## Document Structure

### Standard Template

```markdown
# Title (H1 - One per document)

**Brief description** (1-2 sentences)

## Overview (H2)

What this document covers, why it exists.

## Core Concepts (H2)

### Concept 1 (H3)

Definition, explanation, examples.

### Concept 2 (H3)

...

## Detailed Analysis (H2)

In-depth exploration with evidence.

## Examples (H2)

Real-world usage examples.

## Cross-References (H2)

Links to related documents.
```

### Section Ordering

**Recommended order**:
1. **Title + Brief description** (what is this?)
2. **Overview** (why should I read this?)
3. **Prerequisites** (what do I need to know first?)
4. **Core concepts** (fundamental ideas)
5. **Detailed analysis** (deep dive)
6. **Examples** (how to use)
7. **Common pitfalls** (what to avoid)
8. **Cross-references** (related docs)

---

## Formatting Guidelines

### Headings

**Rules**:
- H1 (`#`): Title only (one per document)
- H2 (`##`): Major sections
- H3 (`###`): Subsections
- H4 (`####`): Rarely needed (consider restructuring)
- No H5/H6 (definitely restructure)

**Example**:
```markdown
# CPG Query API Design

## Overview

## Core Components

### QueryTree

#### Internal Structure (avoid H4 if possible)
```

### Lists

**Unordered lists** (for items without order):
```markdown
- Item 1
- Item 2
  - Nested item
- Item 3
```

**Ordered lists** (for sequential steps):
```markdown
1. First step
2. Second step
3. Third step
```

**When to use**:
- Use unordered for features, properties, concepts
- Use ordered for procedures, workflows, prerequisites

### Code Blocks

**Inline code** (for short code/terms):
```markdown
The `executionPath()` function queries the CFG.
```

**Fenced code blocks** (for multi-line code):
````markdown
```kotlin
fun executionPath(from: Node, to: Node): QueryTree {
    return QueryTree(from, to, PathMode.EXECUTION)
}
```
````

**Always specify language**:
- ✅ `\```kotlin` (syntax highlighting)
- ❌ `\``` (no highlighting)

**Common languages**:
- `kotlin`, `java`, `python`, `javascript`
- `bash`, `shell`, `sh`
- `json`, `yaml`, `xml`
- `mermaid` (diagrams)

### Code References

**Format**: `file:line` or `file:start-end`

**Examples**:
```markdown
The handler pattern is implemented in `JavaLanguageFrontend.kt:120-180`.

See `ValueEvaluator.kt:45` for the evaluation logic.
```

**Best practice**: Always provide line numbers for code references (see [sys-tool-003](./code-reference-format.md))

### Tables

**Use for**: Comparisons, parameter lists, option matrices

**Syntax**:
```markdown
| Column 1 | Column 2 | Column 3 |
|----------|----------|----------|
| Value 1  | Value 2  | Value 3  |
| Value 4  | Value 5  | Value 6  |
```

**Alignment**:
```markdown
| Left | Center | Right |
|:-----|:------:|------:|
| L1   |   C1   |    R1 |
```

**Best practice**: Keep tables under 5 columns (readability)

### Emphasis

**Bold** (`**text**`): Important terms, key concepts
```markdown
**Memory-First** is the core operating principle.
```

**Italic** (`*text*`): Emphasis, technical terms
```markdown
The *semantic memory* stores stable knowledge.
```

**Avoid**: Underline (conflicts with links), ALL CAPS (too aggressive)

### Links

**Internal links** (same repository):
```markdown
See [Memory System Design](../DESIGN.md) for details.
See [Semantic Note](../semantic/query-api-dsl.md) for Query API.
```

**External links** (with context):
```markdown
According to [Joern documentation](https://docs.joern.io/), the CPG schema...
```

**Reference-style links** (for readability):
```markdown
The [Query API][1] uses the [QueryTree][2] structure.

[1]: ../semantic/query-api-dsl.md
[2]: ../semantic/querytree-design.md
```

### Callouts / Admonitions

**Using blockquotes**:
```markdown
> **Note**: This is an important note.

> **Warning**: This operation is destructive.

> **Example**: Here's how to use this feature.
```

**Using emoji (optional)**:
```markdown
> ✅ **Best Practice**: Always update indexes immediately.

> ⚠️ **Warning**: Do not skip memory consultation.

> 💡 **Tip**: Use task-specific indexes for large docs.

> ❌ **Anti-Pattern**: Batching note creation.
```

### Horizontal Rules

**Use sparingly** (separate major sections):
```markdown
## Section 1

Content...

---

## Section 2

Content...
```

---

## Writing Style

### Concise and Clear

**Bad** (verbose):
```
In this section, we will discuss the various different ways in which
you can potentially utilize the Query API to perform queries on the CPG.
```

**Good** (concise):
```
The Query API provides functions to query the CPG.
```

### Active Voice

**Bad** (passive):
```
The CPG is constructed by the frontend after the AST is parsed.
```

**Good** (active):
```
The frontend constructs the CPG after parsing the AST.
```

### Consistent Terminology

**Problem**: Using different terms for same concept
- "Query API" vs "query system" vs "querying interface"

**Solution**: Define terms once, use consistently

**Example**:
```markdown
## Terminology

- **Query API**: The DSL for querying CPG (always use this term)
- **QueryTree**: Result structure (not "query result" or "query output")
```

### Progressive Disclosure

**Start high-level**:
```markdown
## Query API

The Query API allows querying execution paths in the CPG.
```

**Then add details**:
```markdown
### executionPath Function

`executionPath(from: Node, to: Node)` queries the control flow graph (CFG)
to find all paths from `from` to `to`.
```

**Then add implementation details**:
```markdown
### Implementation

The function uses a breadth-first search on the EOG:

\```kotlin
fun executionPath(from: Node, to: Node): QueryTree {
    // BFS implementation
}
\```
```

---

## Common Patterns

### Example Block

```markdown
## Example: Basic Usage

\```kotlin
val tree = executionPath(startNode, endNode)
if (tree.fulfilled) {
    println("Path exists")
}
\```

**Expected output**:
```
Path exists
```

**Explanation**: The `fulfilled` property indicates if a path was found.
```

### Comparison Table

```markdown
## Query Functions Comparison

| Function | Purpose | Returns | Use When |
|----------|---------|---------|----------|
| `executionPath` | CFG paths | QueryTree | Need control flow |
| `dataFlow` | DFG paths | QueryTree | Need data flow |
| `allPaths` | All paths | QueryTree | Exploratory analysis |
```

### Step-by-Step Procedure

```markdown
## How to Create a Semantic Note

**Steps**:

1. **Check for existing note**
   ```bash
   grep -i "<keyword>" /claude/memory/index/tags.json
   ```

2. **Determine note ID**
   - Find highest `sem-NNN`
   - Increment by 1

3. **Create note file**
   - Path: `/claude/memory/semantic/<topic>-<slug>.md`
   - Use descriptive slug

4. **Write frontmatter**
   ```yaml
   ---
   id: sem-NNN
   title: ...
   ---
   ```

5. **Update indexes immediately**
   - Add to `tags.json`
   - Add to `topics.json`
```

### Decision Matrix

```markdown
## Should I Create a Semantic Note?

| Condition | Create Note? | Why |
|-----------|-------------|-----|
| Understood new architecture | ✅ Yes | Stable knowledge |
| Found one-time bug | ❌ No | Transient issue |
| Discovered design pattern | ✅ Yes | Reusable insight |
| Completed routine task | ❌ No | Episodic only |
```

---

## Best Practices Summary

### ✅ DO:

1. **Use hierarchical headings** (H1 → H2 → H3)
2. **Provide code references** with file:line
3. **Include examples** for every concept
4. **Use tables** for comparisons
5. **Add context** to links ("See X for Y")
6. **Specify language** in code blocks
7. **Keep sections scannable** (lists, short paragraphs)
8. **Use diagrams** where helpful (see [sys-tool-001](./mermaid-diagrams.md))

### ❌ DON'T:

1. **Long paragraphs** (break into lists or shorter paragraphs)
2. **Vague references** ("See above" - use links)
3. **Mixing levels** (H1 → H3, skipping H2)
4. **No examples** (abstract explanations only)
5. **Code without context** (explain what the code does)
6. **Inconsistent terminology** (define and stick to terms)
7. **Wall of text** (use headings, lists, code blocks)

---

## Document Length Guidelines

**Ideal length by type**:

| Document Type | Target Length | Max Length |
|---------------|---------------|------------|
| Semantic note | 200-500 lines | 800 lines |
| Episodic note | 100-300 lines | 500 lines |
| Procedural note | 300-600 lines | 1000 lines |
| Task output | 500-1500 lines | 3000 lines |

**If exceeding max**: Split into multiple documents

**Example split**:
```markdown
# CPG Architecture (Overview)
- High-level architecture
- Component relationships

# CPG Frontend Details
- Frontend-specific deep dive

# CPG Pass Infrastructure
- Pass system deep dive
```

---

## Cross-References

- **[sys-tool-001](./mermaid-diagrams.md)**: How to create diagrams in Markdown
- **[sys-tool-003](./code-reference-format.md)**: How to reference code properly
- **Related project notes**: Any project documentation should follow these practices

---

## Usage in Projects

When writing documentation:

1. **Query this note**: Search for "markdown" or "documentation" in memory index
2. **Follow templates**: Use section structures from this note
3. **Check best practices**: Review DO/DON'T lists
4. **Reference in project notes**: Link to `sys-tool-002` for documentation standards

---

**Status**: System knowledge (stable, project-agnostic)
**Maintenance**: Update when Markdown standards evolve or new patterns emerge
