# System Knowledge Directory

**Purpose**: Universal tool and workflow knowledge that applies to **any project**

**Status**: Project-agnostic, fully reusable

---

## What Is System Knowledge?

**System knowledge** consists of universal concepts, tools, and best practices that are **not specific to any project**. This knowledge can be copied to any new project and immediately used without modification.

**Examples**:
- ✅ How to use Mermaid diagrams
- ✅ Markdown documentation best practices
- ✅ Code reference format standards
- ✅ General documentation workflows

**Non-examples** (project-specific):
- ❌ CPG architecture (specific to this project)
- ❌ Java frontend handler pattern (specific to this codebase)
- ❌ Query API design (specific to this project)

---

## Directory Structure

```
/claude/memory/system/
├── tools/                         # Tool usage knowledge
│   ├── mermaid-diagrams.md        (sys-tool-001)
│   ├── markdown-best-practices.md (sys-tool-002)
│   └── code-reference-format.md   (sys-tool-003)
├── workflows/                     # General workflows (future)
│   └── (to be added)
├── index/                         # System knowledge indexes
│   ├── system-tags.json           # System tags
│   └── system-topics.json         # System topics
└── README.md                      # This file
```

---

## Current System Knowledge

### Tools (3 notes)

#### sys-tool-001: Mermaid Diagram Syntax and Best Practices
**Location**: `tools/mermaid-diagrams.md`

**What it covers**:
- Mermaid diagram types (flowchart, sequence, class, state, ER)
- Syntax for each diagram type
- Best practices for creating clear diagrams
- Common pitfalls and how to avoid them

**When to use**: Need to create diagrams in documentation

**Tags**: `mermaid`, `diagram`, `visualization`, `documentation`

---

#### sys-tool-002: Markdown Documentation Best Practices
**Location**: `tools/markdown-best-practices.md`

**What it covers**:
- Document structure and formatting
- Heading hierarchies and organization
- Code blocks, tables, lists
- Writing style (concise, active voice, progressive disclosure)
- Common patterns (examples, comparisons, procedures)

**When to use**: Writing technical documentation

**Tags**: `markdown`, `documentation`, `writing`, `formatting`

---

#### sys-tool-003: Code Reference and Evidence Format Standards
**Location**: `tools/code-reference-format.md`

**What it covers**:
- File path formats (absolute, relative, short)
- Line number formats (single, range, multiple ranges)
- Code quotation formats (inline, blocks, annotations)
- Evidence types (code, test, documentation, build)
- Traceability patterns

**When to use**: Referencing code in documentation, providing evidence for claims

**Tags**: `code-reference`, `evidence`, `traceability`, `documentation`

---

## How to Use System Knowledge

### In Current Project

**Query system knowledge** through global index:

```bash
# Search for "mermaid" in global tags.json
grep "mermaid" /claude/memory/index/tags.json

# Result: Points to both sys-tool-001 (system) and sem-005 (project)
```

**System notes are merged** with project notes in global index:
- Global `tags.json`: Contains both `sys-*` and `sem-*`, `ep-*`, `proc-*`
- Global `topics.json`: Contains both system and project topics

**Cross-reference** from project notes to system notes:
```markdown
<!-- In a project semantic note -->

## Diagram Conventions

We follow standard Mermaid conventions (see [sys-tool-001](../system/tools/mermaid-diagrams.md)).
```

### In New Projects

**To reuse system knowledge in a new project**:

1. **Copy the system directory**:
   ```bash
   cp -r /path/to/old-project/claude/memory/system/ \
         /path/to/new-project/claude/memory/system/
   ```

2. **Initialize new project memory structure**:
   ```bash
   mkdir -p /path/to/new-project/claude/memory/{semantic,episodic,procedural,index}
   ```

3. **Create new project indexes**:
   ```bash
   # Start with empty project-specific tags
   echo '{"tags": {}, "updated": "YYYY-MM-DD"}' > \
     /path/to/new-project/claude/memory/index/tags.json
   ```

4. **Merge system indexes** into project indexes:
   - Add system tags from `system/index/system-tags.json` to global `tags.json`
   - Add system topics from `system/index/system-topics.json` to global `topics.json`

5. **Start working**:
   - Memory system automatically has access to system knowledge
   - Create project-specific `sem-*`, `ep-*`, `proc-*` notes as needed
   - System knowledge (`sys-*`) remains unchanged

---

## System Knowledge Principles

### 1. Project-Agnostic

**Principle**: System knowledge contains **zero** project-specific terminology or examples

**Example**:
- ✅ "Use Mermaid flowcharts for process flows"
- ❌ "Use Mermaid flowcharts to show CPG construction pipeline"

**Why**: Ensures system knowledge can be copied to any project without modification

### 2. Universal Applicability

**Principle**: System knowledge applies to **all technical projects**, not just a specific domain

**Example**:
- ✅ "Code references should use `file:line` format"
- ❌ "CPG code references should use `file:line` format"

**Why**: Maximizes reusability across different project types

### 3. Stability

**Principle**: System knowledge changes **rarely** (only when tools/standards evolve)

**Example**:
- ✅ Mermaid syntax (stable, changes with Mermaid releases)
- ❌ Project architecture (changes frequently as project evolves)

**Why**: Reduces maintenance burden when reusing across projects

### 4. Evidence-Based

**Principle**: System knowledge references **authoritative sources** (official docs, standards)

**Example**:
- ✅ "According to Mermaid documentation..."
- ❌ "I think Mermaid supports..."

**Why**: Ensures accuracy and credibility

---

## Maintenance Policy

### When to Add System Knowledge

**Add new system notes when**:
1. Discovered a universal tool/practice used in multiple projects
2. Identified a standard format/convention that should be consistent
3. Extracted a workflow that applies to any technical project
4. Documented a best practice from authoritative sources

**Don't add**:
- Project-specific knowledge (goes to `semantic/`, `procedural/`)
- One-time solutions (not reusable)
- Experimental practices (not validated)

### When to Update System Knowledge

**Update existing system notes when**:
1. Tool/standard evolves (e.g., new Mermaid diagram type released)
2. Best practice changes (e.g., Markdown standard updated)
3. Found error or omission in existing note

**Update frequency**: Rare (typically once per year or less)

### Versioning

**System knowledge notes use semantic versioning in frontmatter**:

```yaml
created: 2025-10-30   # Initial creation
updated: 2025-10-30   # Last update
```

**Update `updated` date** whenever content changes

---

## Index Organization

### System Indexes vs Global Indexes

**System indexes** (`system/index/system-*.json`):
- Contain **only** system knowledge (`sys-*` note IDs)
- Serve as the "source of truth" for system tags/topics
- Can be copied directly to new projects

**Global indexes** (`/claude/memory/index/*.json`):
- **Merge** system indexes with project indexes
- Contain both `sys-*` (system) and `sem-*`, `ep-*`, `proc-*` (project)
- Used by AI agent for querying all knowledge

### Merging Strategy

**At session start**, AI agent:
1. Reads global `tags.json` and `topics.json`
2. These files already contain both system and project tags/topics
3. Queries return both system and project notes

**When creating new project**:
1. Copy system indexes to new project
2. Create empty project indexes
3. Merge system + project indexes into global indexes

---

## Example Workflows

### Workflow 1: Using System Knowledge in Current Project

**Scenario**: Need to create a diagram in documentation

**Steps**:
1. Query global index: `grep "mermaid" tags.json`
2. Find: `sys-tool-001` (system) and `sem-005` (project)
3. Read `sys-tool-001` for general Mermaid usage
4. Read `sem-005` for project-specific diagram conventions (if exists)
5. Create diagram following both system and project conventions

### Workflow 2: Starting a New Project with System Knowledge

**Scenario**: Starting a new project, want to reuse tool knowledge

**Steps**:
1. Copy `/claude/memory/system/` to new project
2. Initialize new project memory structure
3. Merge system indexes into new project's global indexes
4. Start working - system knowledge immediately available
5. Create project-specific notes (`sem-*`, `ep-*`, `proc-*`) as project evolves

### Workflow 3: Adding New System Knowledge

**Scenario**: Discovered a universal tool (e.g., JSON schema validation)

**Steps**:
1. Check: Is this project-agnostic? (Yes)
2. Check: Is this universally applicable? (Yes)
3. Check: Is this stable? (Yes, based on JSON Schema standard)
4. Create: `system/tools/json-schema-validation.md` (sys-tool-004)
5. Update: `system/index/system-tags.json` (add tags for json-schema)
6. Update: `system/index/system-topics.json` (add to relevant topic)
7. Update: Global `/claude/memory/index/tags.json` (merge system tags)
8. Update: Global `/claude/memory/index/topics.json` (merge system topics)

---

## Benefits of System Knowledge

### 1. Reusability

**Without system knowledge**:
- Learn Mermaid syntax for each project
- Rediscover Markdown best practices each time
- Re-create documentation standards repeatedly

**With system knowledge**:
- Copy `system/` directory → instant access to all tool knowledge
- No re-learning, no duplication
- Consistent standards across all projects

### 2. Consistency

**Without system knowledge**:
- Different code reference formats in different projects
- Inconsistent diagram styles
- Varying documentation quality

**With system knowledge**:
- Unified code reference format across all projects
- Consistent diagram conventions
- Standardized documentation quality

### 3. Efficiency

**Without system knowledge**:
- Context wasted re-reading tool docs for each project
- Time wasted re-deriving best practices

**With system knowledge**:
- Context saved (read once, use everywhere)
- Time saved (pre-learned knowledge)

### 4. Quality

**Without system knowledge**:
- Reinvent the wheel → risk of mistakes
- No central source of truth → inconsistencies

**With system knowledge**:
- Evidence-based practices → fewer mistakes
- Single source of truth → consistency

---

## Future Expansion

### Planned System Knowledge (Future)

**Tools**:
- `sys-tool-004`: JSON Schema validation
- `sys-tool-005`: YAML format standards
- `sys-tool-006`: Git workflow best practices

**Workflows**:
- `sys-wf-001`: Documentation writing workflow
- `sys-wf-002`: Code analysis methodology
- `sys-wf-003`: Requirement tracing workflow

**Standards**:
- `sys-std-001`: API documentation format
- `sys-std-002`: Error message conventions
- `sys-std-003`: Configuration file standards

---

## FAQs

### Q: When should knowledge go to `system/` vs `semantic/`?

**Rule**: If it's **project-agnostic**, it goes to `system/`. If it's **project-specific**, it goes to `semantic/`.

**Examples**:
- Mermaid syntax → `system/` (universal)
- CPG architecture → `semantic/` (project-specific)
- Code reference format → `system/` (universal)
- Query API design → `semantic/` (project-specific)

### Q: Can system notes reference project notes?

**No**. System notes must be **completely independent** of project notes.

**Reason**: System notes need to be copyable to new projects. If they reference project notes, they break when copied.

**Reverse is OK**: Project notes can reference system notes.

### Q: Can I modify system knowledge for my project?

**Don't modify system notes** for project-specific needs.

**Instead**:
1. Keep system note as-is (universal)
2. Create project-specific note that **extends** system note
3. Link project note to system note

**Example**:
```markdown
<!-- In project note sem-015 -->

## Diagram Conventions

This project follows standard Mermaid conventions (see [sys-tool-001](../system/tools/mermaid-diagrams.md)).

**Project-specific additions**:
- Use green for success paths
- Use red for error paths
- Always include caption explaining diagram
```

### Q: How do I know if system knowledge is outdated?

**Check**:
1. `updated` date in frontmatter (if >2 years old, review)
2. Official tool documentation (has syntax changed?)
3. Industry standards (have best practices evolved?)

**If outdated**:
1. Update system note with new information
2. Increment `updated` date
3. Update all projects using this system knowledge (if necessary)

---

## Summary

**System knowledge** is the **foundation** of the memory system. It provides:
- ✅ Universal tool knowledge (Mermaid, Markdown, code references)
- ✅ Reusable across projects (copy `system/` directory)
- ✅ Consistent standards (single source of truth)
- ✅ Efficiency gains (learn once, use everywhere)

**Principle**: Memory system is the **base layer**, projects are **plugins** attached to it. System knowledge is the **common library** that all projects share.

---

**Status**: Active and evolving
**Maintenance**: Update when tools/standards evolve
**Reusability**: 100% (fully project-agnostic)
