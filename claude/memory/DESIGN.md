# CPG Memory System - Complete Design Documentation

**Version**: 2.0.0
**Last Updated**: 2025-10-28
**Status**: Operational and Evolving

---

## Table of Contents

1. [Executive Summary](#executive-summary)
2. [System Knowledge (New)](#system-knowledge)
3. [System Architecture](#system-architecture)
4. [Core Concepts](#core-concepts)
5. [File Structure](#file-structure)
6. [Memory Types](#memory-types)
7. [Index System](#index-system)
8. [Workflows](#workflows)
9. [Quality Standards](#quality-standards)
10. [Performance Optimizations](#performance-optimizations)
11. [Maintenance and Evolution](#maintenance-and-evolution)
12. [Usage Examples](#usage-examples)
13. [Troubleshooting](#troubleshooting)

---

## Executive Summary

### Purpose

The CPG Memory System is a **file-based external memory** designed to enable an AI agent to:
- **Persist knowledge** across sessions
- **Minimize context usage** by storing stable information externally
- **Retrieve knowledge efficiently** through indexed access
- **Maintain consistency** by using memory as source of truth
- **Learn incrementally** from task experiences

### Key Metrics

**Current State** (as of 2025-10-28):
- **8 memory notes** (4 semantic, 3 episodic, 1 procedural)
- **26 tags**, organized into 13 categories
- **11 topics**, spanning 5 hierarchical levels
- **91% context reduction** for Task 3 (8300 lines → 740 lines)
- **2 tasks documented**, 1 in progress

### Design Principles

1. **Externalize, Don't Memorize**: Store knowledge in files, not in conversation context
2. **Index First**: Build indexes for fast retrieval, avoid full-text search
3. **Task-Driven Optimization**: Create task-specific indexes for large documentation
4. **Evidence-Based**: Every claim links to source code or primary artifacts
5. **Incremental Growth**: Add knowledge as discovered, don't wait for task completion
6. **Consistency Over Duplication**: Update existing notes rather than creating duplicates

---

## System Knowledge

### Overview

**System Knowledge** (`/claude/memory/system/`) is a **new addition** (2025-10-30) that extends the memory system with **universal, project-agnostic knowledge** that can be reused across any project.

**Key Distinction**:
- **System Knowledge** (`sys-*`): Universal tools, standards, best practices (e.g., Mermaid, Markdown)
- **Project Knowledge** (`sem-*`, `ep-*`, `proc-*`): Project-specific knowledge (e.g., CPG architecture)

### Architecture Update

The memory system now has **four layers** instead of three:

```
┌─────────────────────────────────────────────────────────────┐
│                    APPLICATION LAYER                         │
│  (AI Agent using memory system for tasks)                   │
└────────────────┬────────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────────┐
│                     INDEX LAYER                              │
│  • Global Index (merges system + project)                   │
│  • Task-Specific Index                                      │
│  • Enhanced Indexes                                         │
└────────────────┬────────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────────┐
│              SYSTEM KNOWLEDGE LAYER (NEW)                    │
│  • Universal tool knowledge (sys-tool-*)                    │
│  • Universal workflows (sys-wf-*)                           │
│  • Universal standards (sys-std-*)                          │
└────────────────┬────────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────────┐
│              PROJECT KNOWLEDGE LAYER                         │
│  • Semantic Memory (sem-*)                                  │
│  • Episodic Memory (ep-*)                                   │
│  • Procedural Memory (proc-*)                               │
└─────────────────────────────────────────────────────────────┘
```

### Current System Knowledge

As of 2025-10-30, the system includes **3 tool knowledge notes**:

| ID | Title | Purpose |
|----|-------|---------|
| `sys-tool-001` | Mermaid Diagram Syntax and Best Practices | Universal diagramming knowledge |
| `sys-tool-002` | Markdown Documentation Best Practices | Universal documentation standards |
| `sys-tool-003` | Code Reference and Evidence Format Standards | Universal code traceability standards |

### Directory Structure

```
/claude/memory/system/
├── tools/                         # Universal tool knowledge
│   ├── mermaid-diagrams.md        (sys-tool-001)
│   ├── markdown-best-practices.md (sys-tool-002)
│   └── code-reference-format.md   (sys-tool-003)
├── workflows/                     # Universal workflows (future)
├── index/                         # System knowledge indexes
│   ├── system-tags.json           # System-only tags
│   └── system-topics.json         # System-only topics
└── README.md                      # System knowledge documentation
```

### Index Merging

**System indexes** are merged with **project indexes** in the global index:

**Global `/claude/memory/index/tags.json`**:
```json
{
  "tags": {
    "mermaid": ["sem-005", "sys-tool-001"],  // Project + System
    "documentation": ["ep-001", "proc-001", "sys-tool-002", "sys-tool-003"],
    "system-knowledge": ["sys-tool-001", "sys-tool-002", "sys-tool-003"]
  }
}
```

**Query behavior**:
- AI agent queries global index (merged)
- Returns both system and project notes
- Example: Query "mermaid" → returns `sem-005` (project-specific conventions) + `sys-tool-001` (universal syntax)

### Reusability

**System knowledge is designed for cross-project reuse**:

**To reuse in new project**:
1. Copy `/claude/memory/system/` directory to new project
2. Initialize new project memory structure
3. Merge system indexes into new project's global indexes
4. System knowledge immediately available

**Benefits**:
- ✅ No re-learning tool syntax for each project
- ✅ Consistent standards across all projects
- ✅ Context savings (read universal knowledge once)

### Design Principles

**System knowledge must be**:
1. **Project-agnostic**: Zero project-specific terminology or examples
2. **Universally applicable**: Applies to all technical projects
3. **Stable**: Changes rarely (only when tools/standards evolve)
4. **Evidence-based**: References authoritative sources

**Example**:
- ✅ "Use Mermaid flowcharts for process flows" (universal)
- ❌ "Use Mermaid flowcharts to show CPG construction" (project-specific)

### Usage Patterns

**Cross-referencing**:
- Project notes → System notes: ✅ Allowed and encouraged
- System notes → Project notes: ❌ Prohibited (breaks reusability)

**Example**:
```markdown
<!-- In project semantic note sem-015 -->

## Diagram Conventions

We follow standard Mermaid conventions (see [sys-tool-001](../system/tools/mermaid-diagrams.md)).

**Project-specific additions**:
- Use green for success paths
- Use red for error paths
```

**Query workflow**:
```
User: "How do I create a sequence diagram?"
    ↓
AI Agent queries global index for "sequence-diagram"
    ↓
Finds: sys-tool-001 (Mermaid syntax)
    ↓
Reads sys-tool-001 and provides answer
    ↓
No need to re-learn Mermaid for each project
```

### Maintenance

**When to add system knowledge**:
- Discovered a universal tool/standard used across projects
- Identified best practice from authoritative source
- Extracted workflow applicable to any technical project

**When NOT to add**:
- Project-specific knowledge (goes to `semantic/`)
- Experimental practices (not validated)
- One-time solutions (not reusable)

**Update frequency**: Rare (when tools/standards evolve)

### Future Expansion

**Planned additions**:
- `sys-tool-004`: JSON Schema validation
- `sys-wf-001`: Documentation writing workflow
- `sys-std-001`: API documentation format

---

## System Architecture

### Three-Layer Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    APPLICATION LAYER                         │
│  (AI Agent using memory system for tasks)                   │
└────────────────┬────────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────────┐
│                     INDEX LAYER                              │
│  • Global Index (tags.json, topics.json)                    │
│  • Task-Specific Index (task-N-index.json)                  │
│  • Enhanced Indexes (metadata, hierarchies, gaps)           │
└────────────────┬────────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────────┐
│                    STORAGE LAYER                             │
│  • Semantic Memory (/semantic/*.md)                         │
│  • Episodic Memory (/episodic/*.md)                         │
│  • Procedural Memory (/procedural/*.md)                     │
└─────────────────────────────────────────────────────────────┘
```

### Data Flow

**Read Path**:
```
User Query
    → Check Task-Specific Index (if exists)
    → Query Global Index (tags/topics)
    → Identify relevant note IDs
    → Read specific notes (with line ranges if task-indexed)
    → Return knowledge to agent
```

**Write Path**:
```
New Knowledge Discovered
    → Create/Update Memory Note (semantic/episodic/procedural)
    → Extract tags and topics
    → Update Global Index (tags.json, topics.json)
    → Add cross-references to related notes
    → Optional: Update task-specific index if in task context
```

---

## Core Concepts

### 1. Memory Types

| Type | Purpose | Lifetime | Example |
|------|---------|----------|---------|
| **Semantic** | Stable facts, definitions, architecture | Permanent, updated as code changes | CPG node types, Query API design |
| **Episodic** | Task records, experiments, outcomes | Permanent, historical record | Task 2 analysis session |
| **Procedural** | Workflows, checklists, SOPs | Permanent, evolves with experience | Frontend analysis workflow |

### 2. Index Types

| Type | Scope | Granularity | Lifetime | Purpose |
|------|-------|-------------|----------|---------|
| **Global Index** | All notes | Note-level | Permanent | Discovery ("what notes exist?") |
| **Task-Specific Index** | One task | Section-level (line ranges) | Task-scoped | Optimization ("what do I need to read?") |
| **Enhanced Index** | All notes + metadata | Note-level + coverage analysis | Permanent | Gap analysis, planning |

### 3. Note Identification

- **Semantic**: `sem-NNN` (e.g., `sem-001`, `sem-004`)
- **Episodic**: `ep-NNN` (e.g., `ep-001`, `ep-003`)
- **Procedural**: `proc-NNN` (e.g., `proc-001`)
- **File naming**: `<type>/<topic>-<slug>.md` (semantic/procedural), `<type>/YYYYMMDD-tN-<slug>.md` (episodic)

---

## File Structure

### Directory Layout

```
/claude/
├── memory/
│   ├── semantic/                    # Stable knowledge
│   │   ├── java-cpg-architecture.md       (sem-001)
│   │   ├── handler-pattern.md             (sem-002)
│   │   ├── unreachable-eog-pass.md        (sem-003)
│   │   └── query-api-dsl.md               (sem-004)
│   ├── episodic/                    # Task history
│   │   ├── 20251027-t1-java-cpg-analysis.md      (ep-001)
│   │   ├── 20251028-t2-constant-eval-analysis.md (ep-002)
│   │   └── 20251028-t2-doc-reorganization.md     (ep-003)
│   ├── procedural/                  # Workflows
│   │   └── cpg-frontend-analysis-workflow.md     (proc-001)
│   ├── index/                       # Indexes
│   │   ├── tags.json                      # Original global tags
│   │   ├── topics.json                    # Original global topics
│   │   ├── enhanced-tags.json             # Extended with metadata
│   │   ├── enhanced-topics.json           # Extended with hierarchy
│   │   ├── task-3-index.json              # Task-specific index
│   │   └── archive/                       # Old task indexes (after 3+ tasks)
│   └── DESIGN.md                    # This file
├── result/                          # Task outputs (not memory)
│   ├── 1/                           # Task 1 deliverables
│   ├── 2/                           # Task 2 deliverables
│   └── 3/                           # Task 3 deliverables (pending)
└── prompt/                          # Task definitions
    ├── 0.overview.md                # Global rules
    ├── 0.memory.md                  # Memory system policies
    ├── 1.java-cpg.md                # Task 1 prompt
    ├── 2.constant-eval-and-reachability.md  # Task 2 prompt
    └── 3.source-example.md          # Task 3 prompt
```

### File Relationships

```
Semantic Notes (sem-NNN)
    ↓ referenced by
Episodic Notes (ep-NNN) ← records
    ↓ links to                      ↓
Output Docs (/result/N/)       Task Prompts (/prompt/N-*.md)
    ↑ synthesized from
Codebase (/home/dai/code/cpg/)
```

---

## Memory Types

### Semantic Memory

**Purpose**: Store stable, reusable facts about the codebase.

**Structure**:
```markdown
---
id: sem-NNN
title: <Concept Name>
type: semantic
tags: [tag1, tag2, tag3]
created: YYYY-MM-DD
updated: YYYY-MM-DD
source: <Where this knowledge came from>
related: [sem-XXX, ep-YYY, proc-ZZZ]
---

# <Title>

## Why now
<1-2 sentences: why this note was created at this time>

## Core Concept
<Definition and high-level explanation>

## Key Features / Components
<Detailed breakdown with code evidence>

## Capabilities / Limitations
<What it can/cannot do>

## Evidence
<Links to source code with line numbers>

## Cross-References
<Links to related notes>
```

**Best Practices**:
- **One idea per note**: Don't mix multiple concepts in one file
- **Evidence-based**: Quote code with file paths and line numbers
- **Update in place**: If code changes, update the note rather than creating a new one
- **Cross-link liberally**: Related notes should reference each other

**When to Create**:
- Discovered a new architectural pattern
- Understood how a major component works
- Found a design principle or convention
- Identified capabilities or limitations of a system

**When to Update**:
- Code has changed since note creation
- User corrected a misunderstanding
- Discovered additional capabilities or edge cases

### Episodic Memory

**Purpose**: Record what was done during a task session.

**Structure**:
```markdown
---
id: ep-NNN
title: <Task Name>
type: episodic
date: YYYY-MM-DD
task: <task-file-name.md>
tags: [task-completion, ...]
links:
  - /claude/result/N/output-file.md
  - /code/path/to/analyzed-file.kt:123-456
related: [sem-XXX, proc-YYY]
---

# <Task Title>

## Goal
<What was the objective?>

## Approach
<What strategy was used?>

## Steps Taken
1. Step 1
2. Step 2
...

## Key Findings
- Finding 1 (with evidence)
- Finding 2 (with evidence)

## Outputs Produced
- /claude/result/N/file1.md
- /claude/result/N/file2.md

## Semantic Notes Created/Updated
- sem-XXX: <what was added>
- sem-YYY: <what was updated>

## Next Steps / Open Questions
- TODO 1
- TODO 2
```

**Best Practices**:
- **Create at task completion**: Don't wait days later
- **Link to outputs**: Every output file should be referenced
- **Extract semantic insights**: Stable findings should be promoted to semantic notes
- **Document mistakes**: Include what didn't work and why

**When to Create**:
- Completed a major task
- Finished a significant analysis phase
- Produced output documents

### Procedural Memory

**Purpose**: Codify reusable workflows and checklists.

**Structure**:
```markdown
---
id: proc-NNN
title: Workflow for <Activity>
type: procedural
tags: [workflow, domain, ...]
created: YYYY-MM-DD
updated: YYYY-MM-DD
domain: <e.g., code-analysis>
verb: <e.g., analyze-and-document>
related: [sem-XXX, ep-YYY]
---

# <Title>

## Purpose
<Why this workflow exists>

## Prerequisites
- Prerequisite 1
- Prerequisite 2

## Steps
1. **Step 1**: <Action>
   - Detail 1
   - Detail 2

2. **Step 2**: <Action>
   - Detail 1
   - Detail 2

## Checks / Validation
- [ ] Check 1
- [ ] Check 2

## Common Pitfalls
- Pitfall 1: <What to avoid and why>
- Pitfall 2: <What to avoid and why>

## Rollback / Recovery
<What to do if something goes wrong>

## Examples
- Example 1: <Link to ep-XXX where this workflow was used>
```

**Best Practices**:
- **Refine iteratively**: Update based on experience
- **Link to examples**: Reference episodic notes showing workflow in action
- **Include failure modes**: Document what can go wrong

**When to Create**:
- Completed a workflow more than twice
- Identified a repeating pattern of steps
- User requests a formal procedure

---

## Index System

### Global Index (tags.json, topics.json)

**Purpose**: Enable discovery of notes by keyword or subject area.

**tags.json Schema**:
```json
{
  "tags": {
    "<tag-name>": ["note-id-1", "note-id-2", ...]
  },
  "updated": "YYYY-MM-DD"
}
```

**topics.json Schema**:
```json
{
  "topics": {
    "<Topic Name>": {
      "description": "<What this topic covers>",
      "notes": ["note-id-1", "note-id-2", ...]
    }
  },
  "updated": "YYYY-MM-DD"
}
```

**Maintenance**:
- Update **immediately** after creating/updating any note
- Add new tags/topics as discovered
- Normalize tag names (lowercase, hyphen-separated)

### Enhanced Index

**Purpose**: Provide metadata, coverage analysis, and gap identification.

**Features**:
- **Tag categories**: Organize tags by type (language, framework, technique, etc.)
- **Topic hierarchy**: 5 levels from infrastructure to process
- **Coverage tracking**: Comprehensive, partial, minimal, none
- **Gap analysis**: Identify missing notes and prioritize creation
- **Knowledge graph**: Visualize relationships between topics

**Location**: `enhanced-tags.json`, `enhanced-topics.json`

**Usage**:
- **Planning**: Use `coverage_gaps` and `recommended_next_notes` to plan work
- **Discovery**: Use `tag_categories` and `topic_hierarchy` to navigate knowledge
- **Quality**: Track coverage percentages to measure completeness

### Task-Specific Index

**Purpose**: Optimize context usage for presentation/teaching tasks.

**When to Build**:
- Starting a NEW task (not continuations)
- Task type is presentation/teaching/documentation
- Existing documentation is large (>3000 lines or >100KB)
- User explicitly requests context optimization

**Schema**:
```json
{
  "task_id": "N",
  "task_name": "<Description>",
  "task_type": "presentation|analysis|documentation|implementation",
  "knowledge_requirements": {
    "critical": {
      "<area>": {
        "source": "path/to/file.md",
        "sections_needed": ["line ranges or section names"],
        "estimated_lines": "~N lines",
        "summary": "Why needed"
      }
    },
    "optional": { ... },
    "reference_only": { ... }
  },
  "content_mapping": {
    "<deliverable-section>": {
      "requires": ["knowledge items"],
      "memory_refs": ["sem-XXX", "ep-YYY"]
    }
  },
  "reading_order": [
    "1. Read X first",
    "2. Read Y before Z"
  ],
  "estimated_context_usage": {
    "critical_reading": "~N lines",
    "total": "~M lines (vs K lines if reading all)",
    "reduction": "X% reduction"
  }
}
```

**Workflow**:
1. **Analyze task prompt**: Extract key concepts, deliverables, audience
2. **Map knowledge**: Identify which semantic/episodic notes are needed
3. **Specify sections**: Pinpoint exact line ranges or section names
4. **Classify priority**: Critical vs optional vs reference-only
5. **Estimate savings**: Calculate context reduction
6. **Create index file**: Save to `index/task-N-index.json`
7. **Use during task**: Read only indexed sections

---

## Workflows

### Workflow 1: Starting a New Session

```
1. Read /claude/memory/index/tags.json and topics.json
2. Check if task-specific index exists (/claude/memory/index/task-<N>-index.json)
   - If yes: Read task index and follow its reading order
   - If no: Continue to step 3
3. Read most recent episodic notes (sorted by date)
4. If task references specific topics, read related semantic notes
5. If task involves known workflows, read related procedural notes
6. Begin task with context from memory
```

### Workflow 2: Creating a Semantic Note

```
1. Analyze code/artifact to understand concept
2. Check if semantic note already exists (search index)
   - If exists: Update existing note instead
   - If not: Continue to step 3
3. Create note file: /claude/memory/semantic/<topic>-<slug>.md
4. Write frontmatter with id (next sem-NNN), title, tags, etc.
5. Write body following semantic note structure
6. Identify related notes and add cross-references
7. Update /claude/memory/index/tags.json (add note ID to each tag)
8. Update /claude/memory/index/topics.json (add note ID to relevant topics)
9. Add backlinks to related notes (add this note's ID to their "related" fields)
```

### Workflow 3: Completing a Task

```
1. Ensure all output files are created in /claude/result/<N>/
2. Create episodic note: /claude/memory/episodic/YYYYMMDD-t<N>-<slug>.md
3. In episodic note:
   - Document goal, approach, steps, findings
   - Link to all output files
   - Reference semantic notes created/updated
   - List next steps or open questions
4. Review discoveries: promote stable insights to semantic notes
5. Extract workflows: if repeating pattern, create/update procedural note
6. Update global index (tags.json, topics.json)
7. If task-specific index was used, archive it to index/archive/ (after 3+ tasks)
8. Update enhanced index: increment coverage stats, close gaps
```

### Workflow 4: Building a Task-Specific Index

```
1. Read task prompt to understand:
   - Task type (analysis, presentation, documentation, implementation)
   - Key deliverables (slides, docs, code)
   - Required concepts (keywords, technical terms)
   - Target audience
2. Query global index to find relevant notes
3. For each relevant note:
   - Skim note to identify key sections
   - Mark specific line ranges or section names needed
   - Classify as critical/optional/reference-only
4. Map task deliverables to knowledge requirements
5. Estimate context savings (total lines with index vs without)
6. Create task index file: /claude/memory/index/task-<N>-index.json
7. Use index during task: read only indexed sections
8. After task: document effectiveness in episodic note
```

### Workflow 5: Memory Hygiene (Every 3-5 Tasks)

```
1. Review all episodic notes from recent tasks
2. Extract common patterns/insights into semantic notes
3. Check for duplicate semantic notes:
   - Merge if covering same concept
   - Split if mixing multiple concepts
4. Normalize tags across notes (consistent naming)
5. Verify all cross-references are valid (notes still exist)
6. Check for outdated notes:
   - If code changed, update note and increment "updated" date
   - If note obsolete, archive or delete
7. Update enhanced index:
   - Refresh coverage stats
   - Update gap analysis
   - Recommend new notes based on patterns
8. Archive old task-specific indexes (keep last 2-3 tasks only)
```

---

## Quality Standards

### Note Quality Checklist

**Semantic Notes**:
- [ ] Frontmatter complete (id, title, type, tags, dates, source, related)
- [ ] "Why now" section explains motivation
- [ ] Code evidence with file paths and line numbers
- [ ] Clear separation of capabilities and limitations
- [ ] Cross-references to related notes
- [ ] One core idea (not mixing multiple concepts)

**Episodic Notes**:
- [ ] Frontmatter complete (id, title, type, date, task, tags, links, related)
- [ ] Goal clearly stated
- [ ] Steps taken documented
- [ ] Key findings with evidence
- [ ] Links to all output files
- [ ] Semantic notes created/updated listed
- [ ] Next steps or open questions

**Procedural Notes**:
- [ ] Frontmatter complete (id, title, type, tags, dates, domain, verb, related)
- [ ] Purpose clearly stated
- [ ] Prerequisites listed
- [ ] Steps are actionable and ordered
- [ ] Checks/validation criteria provided
- [ ] Common pitfalls documented
- [ ] Links to example usage (episodic notes)

### Index Quality Checklist

- [ ] Every note ID referenced in index exists
- [ ] Every note has at least one tag
- [ ] Every note belongs to at least one topic
- [ ] No duplicate tags (check for synonyms)
- [ ] Tag names are consistent (lowercase, hyphen-separated)
- [ ] "updated" date reflects last change

---

## Performance Optimizations

### Context Usage Optimization

| Scenario | Without Optimization | With Optimization | Savings |
|----------|---------------------|-------------------|---------|
| Task 3 (Presentation) | Read all Task 1+2 outputs (8300 lines) | Task-specific index + selected sections (740 lines) | 91% |
| Continuing Task 2 | Re-read all previous outputs (5300 lines) | Read ep-002 summary (150 lines) | 97% |
| Understanding Query API | Read 2.graph-and-query-analysis.md (1500 lines) | Read sem-004 (421 lines) | 72% |

### Retrieval Time Optimization

**Index-First Approach**:
- **Without index**: Grep all files → read all matching files → extract info (slow)
- **With index**: Query index JSON → get note IDs → read specific files → extract info (fast)

**Benchmark** (informal):
- Grep-based search: ~10-30 seconds for complex query
- Index-based search: <1 second for same query

### Memory Growth Management

**Projection** (assuming 1 task per week):
- **Year 1**: ~52 tasks → ~150 semantic notes, ~52 episodic notes, ~20 procedural notes
- **Storage**: ~200 notes × 5KB avg = ~1MB (negligible)
- **Index size**: ~50KB (tags.json + topics.json)

**Scaling Strategy**:
- Archive old task-specific indexes after 3+ tasks
- Merge duplicate semantic notes during hygiene cycles
- Split topics into sub-topics if a topic exceeds 10 notes

---

## Maintenance and Evolution

### Regular Maintenance Tasks

**Daily** (per task):
- Create/update notes as knowledge discovered
- Update indexes immediately after note creation

**Weekly** (every 3-5 tasks):
- Run memory hygiene workflow
- Review coverage gaps
- Update enhanced indexes

**Monthly**:
- Audit all notes for staleness
- Review and consolidate procedural notes
- Check for broken cross-references

### Evolution Roadmap

**Phase 1: Operational** (Current, October 2025)
- ✅ Basic semantic/episodic/procedural notes
- ✅ Global index (tags, topics)
- ✅ Task-specific index system
- ✅ Enhanced index with metadata

**Phase 2: Enrichment** (Q4 2025)
- 📋 Complete coverage of CPG core concepts (sem-005: DFG, sem-006: Pass Infrastructure)
- 📋 Procedural note for memory system operations (proc-002)
- 📋 Add embeddings.json for semantic search
- 📋 Automated index validation tool

**Phase 3: Advanced Features** (Q1 2026)
- 📋 Relationship graph visualization
- 📋 Automatic gap detection
- 📋 Query language for complex memory retrieval
- 📋 Version control for notes (track changes over time)

**Phase 4: Multi-Project** (Future)
- 📋 Support multiple codebases
- 📋 Shared semantic notes across projects
- 📋 Import/export memory notes

---

## Usage Examples

### Example 1: Finding Information About Query API

**Scenario**: Need to understand how to use executionPath() function.

**Steps**:
```bash
1. grep "query" /claude/memory/index/tags.json
   → Find tags: "query-api", "querytree", "dsl"
   → Notes: ["sem-004", "ep-003"]

2. Read /claude/memory/semantic/query-api-dsl.md (sem-004)
   → Section: "executionPath: 控制流路径查询" (lines 86-115)
   → Find code example and parameter explanation

3. Optional: Read /claude/memory/episodic/20251028-t2-doc-reorganization.md (ep-003)
   → Context: How Query API was analyzed during Task 2
```

**Result**: Found precise information in <2 minutes without reading full Task 2 outputs.

### Example 2: Starting Task 3

**Scenario**: Need to create presentation, want to minimize context usage.

**Steps**:
```bash
1. Read /claude/memory/index/task-3-index.json
   → Identified critical knowledge areas
   → Identified reading order

2. Follow reading order:
   - Read sem-003 (EOG mechanism, 185 lines)
   - Read sem-004 (Query API, 421 lines)
   - Read specific sections of Task 2 outputs (~340 lines)

3. Start creating slides using knowledge from memory notes
   → Reference memory notes in speaker notes
   → Avoid loading full Task 2 outputs
```

**Result**: Context usage reduced from 8300 lines to 740 lines (91% savings).

### Example 3: Documenting New Pass Implementation

**Scenario**: Analyzed a new pass and want to document it.

**Steps**:
```bash
1. Create semantic note:
   - File: /claude/memory/semantic/dfg-construction-pass.md
   - ID: sem-005
   - Title: "DFG Construction and Data Flow Analysis"
   - Tags: ["cpg", "dfg", "graph-infrastructure", "passes"]
   - Related: ["sem-003", "sem-004"]

2. Update indexes:
   - Add sem-005 to tags.json under "dfg", "graph-infrastructure", "passes"
   - Add sem-005 to topics.json under "CPG Core", "Pass Infrastructure"
   - Update enhanced-tags.json: remove "dfg" from coverage_gaps

3. Create episodic note:
   - File: /claude/memory/episodic/20251028-t4-dfg-analysis.md
   - Link to sem-005
   - Link to output docs in /claude/result/4/
```

**Result**: Knowledge captured and indexed for future retrieval.

---

## Troubleshooting

### Common Issues

#### Issue 1: Note Not Found in Index

**Symptom**: Created a note but can't find it through index search.

**Cause**: Forgot to update indexes after creating note.

**Solution**:
1. Open note and check its tags and topics
2. Update /claude/memory/index/tags.json: add note ID to each tag
3. Update /claude/memory/index/topics.json: add note ID to relevant topics

#### Issue 2: Duplicate Semantic Notes

**Symptom**: Two semantic notes cover the same concept.

**Cause**: Didn't check index before creating new note.

**Solution**:
1. Read both notes and determine which is more comprehensive
2. Merge content into one note, update "updated" date
3. Delete the duplicate note
4. Update indexes: remove duplicate note ID, keep merged note ID
5. Update any cross-references in other notes

#### Issue 3: Task-Specific Index Saved Too Much Context

**Symptom**: Task-specific index still required reading 3000+ lines.

**Cause**: Marked too many sections as "critical" or indexed entire files.

**Solution**:
1. Review task deliverables: identify exactly what knowledge is needed
2. Narrow down sections to specific line ranges (not entire files)
3. Move non-essential sections to "optional" or "reference_only"
4. Update index with more precise line ranges
5. Re-estimate context usage

#### Issue 4: Stale Note with Outdated Information

**Symptom**: Semantic note describes code that has changed.

**Cause**: Code evolved but note wasn't updated.

**Solution**:
1. Re-analyze current code
2. Update semantic note with new information
3. Increment "updated" date in frontmatter
4. Add note in body: "Updated YYYY-MM-DD: <what changed>"
5. If major changes, consider creating new note with version suffix

#### Issue 5: Cross-References Broken

**Symptom**: Note references sem-XXX but that note doesn't exist.

**Cause**: Referenced note was deleted or renamed.

**Solution**:
1. Search for referenced note in /claude/memory/
2. If note renamed: update cross-reference with new ID
3. If note deleted: remove cross-reference or replace with correct note ID
4. Check index: ensure no orphaned references

---

## Appendix: File Templates

### Semantic Note Template

```markdown
---
id: sem-NNN
title: <Concept Name>
type: semantic
tags: [tag1, tag2, tag3]
created: YYYY-MM-DD
updated: YYYY-MM-DD
source: <Origin of knowledge>
related: [sem-XXX, ep-YYY]
---

# <Title>

## Why now
<Why this note was created>

## Core Concept
<Definition>

## Key Features
<Details>

## Evidence
<Code references>

## Cross-References
<Related notes>
```

### Episodic Note Template

```markdown
---
id: ep-NNN
title: Task N - <Task Name>
type: episodic
date: YYYY-MM-DD
task: <task-file.md>
tags: [task-completion, ...]
links:
  - /claude/result/N/file.md
related: [sem-XXX, proc-YYY]
---

# <Task Title>

## Goal
<Objective>

## Approach
<Strategy>

## Steps Taken
1. Step 1
2. Step 2

## Key Findings
- Finding 1

## Outputs Produced
- /claude/result/N/file.md

## Semantic Notes Created/Updated
- sem-XXX: <change>

## Next Steps
- TODO 1
```

### Procedural Note Template

```markdown
---
id: proc-NNN
title: Workflow for <Activity>
type: procedural
tags: [workflow, domain]
created: YYYY-MM-DD
updated: YYYY-MM-DD
domain: <domain>
verb: <action>
related: [sem-XXX, ep-YYY]
---

# <Title>

## Purpose
<Why>

## Prerequisites
- Prerequisite 1

## Steps
1. **Step 1**: <Action>

## Checks
- [ ] Check 1

## Common Pitfalls
- Pitfall 1

## Examples
- Example 1: ep-XXX
```

---

**End of Design Documentation**

For operational procedures, see: `/claude/memory/procedural/memory-system-operations.md` (proc-002, to be created)

For system configuration, see: `/claude/prompt/0.memory.md`
