---
id: proc-003
title: Memory-First Workflow - Mandatory Operating Procedure
type: procedural
tags: [workflow, memory-system, meta, best-practices]
created: 2025-10-28
updated: 2025-10-28
related: [proc-002]
---

# Memory-First Workflow - Mandatory Operating Procedure

**Purpose**: Define the mandatory Memory-First workflow that MUST be followed for ALL tasks to maximize efficiency, maintain consistency, and minimize context waste.

**Scope**: Applies to ALL AI agent activities: task execution, code analysis, documentation, user interactions, troubleshooting.

**Priority**: CRITICAL - This is the foundational operating principle.

---

## Core Principle

**The memory system is your external brain. You MUST consult it before any action and update it after every significant discovery.**

**Philosophy**:
- Memory system = Persistent knowledge store across sessions
- Context window = Temporary working memory (expensive, limited)
- Goal: Maximize use of persistent memory, minimize use of temporary context

---

## The Three-Phase Workflow

### Phase 1: CONSULT MEMORY (BEFORE work)

**When**: At the start of EVERY session, EVERY task, EVERY user question

**What to do**:

```
Step 1.1: Read Index Files (ALWAYS first)
├─ Read /claude/memory/index/tags.json
├─ Read /claude/memory/index/topics.json
└─ Understand: What knowledge exists in memory system?

Step 1.2: Identify Relevant Knowledge
├─ Extract keywords from user request/task prompt
├─ Query tags.json for matching tags
├─ Query topics.json for matching topics
└─ Result: List of note IDs to read (sem-XXX, ep-YYY, proc-ZZZ)

Step 1.3: Read Semantic Notes (Stable Knowledge)
├─ Read identified semantic notes in priority order
├─ Build foundational understanding from existing knowledge
└─ Note: These are authoritative - don't re-derive what's here

Step 1.4: Read Episodic Notes (Historical Context)
├─ Read last 1-2 episodic notes (sorted by date, most recent first)
├─ Understand: What work was done recently? What issues encountered?
└─ Check: Are there pending tasks or follow-ups from previous sessions?

Step 1.5: Check for Task-Specific Index (Context Optimization)
├─ Check /claude/memory/index/task-<N>-index.json (if starting Task N)
├─ If exists: Use it to read ONLY required sections (not full documents)
└─ Expected: 80-95% context reduction for large documentation

Step 1.6: Read Procedural Notes (Workflow Guidance)
├─ If task involves known workflow (analysis, documentation, etc.)
├─ Read relevant procedural note (proc-001, proc-002, proc-003)
└─ Follow established workflow to maintain consistency

Step 1.7: Assess Knowledge Gaps
├─ Question: Is memory knowledge sufficient for this task?
├─ If YES: Proceed to Phase 2 (work)
├─ If NO: Plan what NEW knowledge needs to be gathered
└─ If PARTIAL: Note what exists vs what needs discovery
```

**Time Investment**: 30-60 seconds reading indexes and notes

**Context Savings**: 80-95% (reading targeted notes vs reading all code/docs)

---

### Phase 2: WORK WITH MEMORY AS FOUNDATION (DURING work)

**Principle**: Memory is the source of truth. Only read new code/docs when memory is insufficient.

**Operating Rules**:

#### ✅ DO:
1. **Use semantic notes as architectural foundation**
   - Treat semantic notes as authoritative knowledge
   - Base analysis and documentation on existing understanding
   - Maintain consistent terminology and concepts from memory

2. **Reference episodic notes for past analysis results**
   - Check if code sections were already analyzed
   - Reuse findings instead of re-analyzing
   - Learn from mistakes/corrections documented in episodic notes

3. **Follow procedural notes for workflows**
   - Use established workflows for consistency
   - Don't reinvent processes that are documented
   - Update procedural notes if you discover improvements

4. **Only read NEW code if memory insufficient**
   - First exhaust memory knowledge
   - Then read new code to fill specific gaps
   - Immediately capture new insights in memory (see Phase 3)

5. **Continuously update memory during work**
   - Don't wait till end to create notes
   - Create semantic note IMMEDIATELY when understanding new architecture
   - Update episodic note with progress as you go
   - Update indexes IMMEDIATELY after creating notes

#### ❌ DON'T:
1. **NEVER re-analyze what's already in memory**
   - Example: If sem-001 documents Java CPG architecture, don't re-read the code
   - Use memory knowledge, only verify if memory seems outdated

2. **NEVER ignore existing knowledge**
   - Example: If episodic note says "Query API is same-level, not subordinate", don't contradict
   - If you believe memory is wrong, verify and UPDATE memory (don't ignore)

3. **NEVER batch memory updates**
   - Bad: "I'll create all notes at the end"
   - Good: "I just understood EOG, creating sem-003 NOW"

4. **NEVER create duplicate notes**
   - Always check if note exists before creating new one
   - Update existing notes rather than creating duplicates
   - Merge similar notes during memory hygiene

5. **NEVER skip index updates**
   - Creating note without updating index = orphaned knowledge
   - Index is the "table of contents" for memory system
   - Update index IMMEDIATELY after creating/updating notes

**Real-Time Memory Updates (During Work)**:

```
Trigger: Understood new architectural insight
Action: Create semantic note IMMEDIATELY
Example: After analyzing QueryTree.kt → Create sem-004 (don't wait till end)

Trigger: User corrected understanding
Action: Update semantic note IMMEDIATELY + create episodic note documenting correction
Example: User says "Query API is same-level" → Update sem-004 + create ep-003

Trigger: Completed major analysis phase
Action: Update episodic note with progress
Example: After analyzing 4 files → Update ep-003 with findings and file references

Trigger: Discovered related concepts
Action: Add cross-references to notes
Example: Query API uses EOG → Add link from sem-004 to sem-003

Trigger: Found reusable pattern/workflow
Action: Create/update procedural note
Example: Discovered effective documentation reorganization workflow → Update proc-001
```

---

### Phase 3: UPDATE MEMORY (AFTER work)

**Principle**: Knowledge not captured in memory is lost. Update memory after EVERY significant action.

**Mandatory Updates**:

#### Update 1: Create/Update Semantic Notes (Stable Knowledge)

**When**:
- Discovered new architectural pattern
- Understood core component design
- Identified design principles or conventions
- Found important API contracts or interfaces

**How**:
```markdown
---
id: sem-NNN
title: <Concept Name>
type: semantic
tags: [tag1, tag2, tag3]
created: YYYY-MM-DD
updated: YYYY-MM-DD
source: <code files analyzed>
related: [sem-XXX, ep-YYY]
---

# <Concept Name>

**What**: <One-sentence definition>

**Why now**: <Why this knowledge was captured at this time>

**Context**: <Where this concept fits in the architecture>

## Core Design

<Explanation with code references>

## Key Insights

<Bullet points of important findings>

## Evidence

<Code quotes with file:line references>

## Related Concepts

- See [sem-XXX](path) for <related concept>
```

**Validation**:
- [ ] Title is clear and searchable
- [ ] Tags are consistent with tags.json
- [ ] Source files referenced with file:line
- [ ] Cross-references to related notes
- [ ] One core idea (not multiple concepts)

#### Update 2: Create/Update Episodic Notes (Session Record)

**When**:
- Completed significant analysis
- Finished task or major phase
- Created output files
- Encountered and resolved issues

**How**:
```markdown
---
id: ep-NNN
title: <YYYYMMDD>-t<N>-<task-name>
type: episodic
tags: [task-completion, <task-tags>]
created: YYYY-MM-DD
updated: YYYY-MM-DD
task: <N>
outputs: [list of files created]
related: [sem-XXX, ep-YYY]
---

# Task <N> - <Task Name>

**Date**: YYYY-MM-DD
**Status**: <complete/in-progress>

## Goal

<What was the objective>

## Approach

<How did I approach the problem>

## Findings

<Key discoveries, insights, patterns>

## Outputs

- `/claude/result/<N>/file1.md` (description)
- `/claude/result/<N>/file2.md` (description)

## Code References

- `path/to/file.kt:100-200` (description)

## Next Steps

<What should be done next, pending tasks>

## Lessons Learned

<What worked well, what didn't, corrections made>
```

**Validation**:
- [ ] Links to all output files
- [ ] Code references with file:line
- [ ] Documents corrections/mistakes
- [ ] Identifies next steps
- [ ] Cross-references related notes

#### Update 3: Update Index Files (IMMEDIATELY after creating notes)

**When**: IMMEDIATELY after creating or updating ANY note

**How**:

**For tags.json**:
```json
{
  "tags": {
    "<tag-name>": [
      "existing-note-1",
      "existing-note-2",
      "NEW-NOTE-ID"  // ← Add here
    ]
  },
  "updated": "YYYY-MM-DD"  // ← Update date
}
```

**For topics.json**:
```json
{
  "topics": {
    "<topic-name>": {
      "description": "...",
      "notes": [
        "existing-note-1",
        "NEW-NOTE-ID"  // ← Add here
      ]
    }
  },
  "updated": "YYYY-MM-DD"  // ← Update date
}
```

**Validation**:
- [ ] Note ID added to all relevant tags
- [ ] Note ID added to all relevant topics
- [ ] Updated timestamp changed
- [ ] No duplicate entries

#### Update 4: Add Cross-References

**When**: When notes reference related concepts

**How**:

**In note frontmatter**:
```yaml
related: [sem-003, ep-002, proc-001]
```

**In note body**:
```markdown
## Related Concepts

- See [sem-003](../semantic/unreachable-eog-pass.md) for EOG mechanism
- See [ep-002](../episodic/20251028-t2-constant-eval-analysis.md) for Task 2 analysis
```

**Validation**:
- [ ] Cross-references are bidirectional (A → B and B → A)
- [ ] Links are valid (file paths correct)
- [ ] Relationship is explained (not just "see X")

#### Update 5: Link Episodic Notes to Output Files

**When**: After creating output files in /claude/result/<N>/

**How**:

**In episodic note frontmatter**:
```yaml
outputs:
  - /claude/result/3/presentation.md
  - /claude/result/3/script.md
```

**In episodic note body**:
```markdown
## Outputs

- **[presentation.md](/claude/result/3/presentation.md)**: 40-slide presentation on CPG reachability analysis
- **[script.md](/claude/result/3/script.md)**: Narration script for presentation
```

**In output file** (optional backlink):
```markdown
<!-- This document was created as part of Task 3 -->
<!-- See memory note: /claude/memory/episodic/20251028-t3-presentation.md -->
```

**Validation**:
- [ ] All output files listed in episodic note
- [ ] File paths are correct
- [ ] Brief description of each output
- [ ] Optional: backlinks in output files

---

## Memory-First Decision Trees

### Decision Tree 1: Should I read memory?

```
┌──────────────────────────────────┐
│ Am I starting a new session?     │
└───┬──────────────────────────────┘
    │ YES → Read tags.json, topics.json, last 1-2 episodic notes
    │
    ▼
┌──────────────────────────────────┐
│ User mentioned a specific topic? │
└───┬──────────────────────────────┘
    │ YES → Query index for topic → Read semantic notes
    │
    ▼
┌──────────────────────────────────┐
│ Starting a task?                 │
└───┬──────────────────────────────┘
    │ YES → Read task-N-index.json (if exists) + relevant semantic notes
    │
    ▼
┌──────────────────────────────────┐
│ About to analyze code?           │
└───┬──────────────────────────────┘
    │ YES → Check semantic notes first (already analyzed?)
    │
    ▼
┌──────────────────────────────────┐
│ User corrected me?               │
└───┬──────────────────────────────┘
    │ YES → Read notes that might contain correct info
    │
    ▼
[ Continue with work ]
```

### Decision Tree 2: Should I write memory?

```
┌──────────────────────────────────┐
│ Just understood new architecture?│
└───┬──────────────────────────────┘
    │ YES → Create semantic note NOW (don't wait)
    │
    ▼
┌──────────────────────────────────┐
│ User corrected understanding?    │
└───┬──────────────────────────────┘
    │ YES → Update semantic note IMMEDIATELY
    │
    ▼
┌──────────────────────────────────┐
│ Completed major analysis phase?  │
└───┬──────────────────────────────┘
    │ YES → Create/update episodic note
    │
    ▼
┌──────────────────────────────────┐
│ Created output files?            │
└───┬──────────────────────────────┘
    │ YES → Create episodic note with links
    │
    ▼
┌──────────────────────────────────┐
│ Discovered reusable workflow?    │
└───┬──────────────────────────────┘
    │ YES → Create/update procedural note
    │
    ▼
┌──────────────────────────────────┐
│ Created/updated ANY note?        │
└───┬──────────────────────────────┘
    │ YES → Update indexes IMMEDIATELY
    │
    ▼
[ Continue with work ]
```

---

## Common Anti-Patterns and Fixes

### Anti-Pattern 1: "I'll read code first, then check memory"
**Why bad**: Wastes context on re-analyzing already-documented code
**Fix**: Always read memory FIRST, only read code if memory insufficient

### Anti-Pattern 2: "I'll create all notes at the end"
**Why bad**: Knowledge gets lost, insights fade, overwhelming batch at end
**Fix**: Create notes IMMEDIATELY when you understand something new

### Anti-Pattern 3: "I'll update indexes later"
**Why bad**: Creates orphaned knowledge, makes retrieval impossible
**Fix**: Update indexes IMMEDIATELY after creating/updating notes

### Anti-Pattern 4: "I'll skip reading episodic notes"
**Why bad**: Repeats mistakes, ignores lessons learned, breaks continuity
**Fix**: ALWAYS read last 1-2 episodic notes at session start

### Anti-Pattern 5: "Memory seems wrong, I'll ignore it"
**Why bad**: Creates inconsistency, undermines memory as source of truth
**Fix**: If memory seems wrong, VERIFY and UPDATE memory (don't ignore)

---

## Performance Metrics

### Expected Performance with Memory-First Workflow

| Metric | Target | Rationale |
|--------|--------|-----------|
| **Context Reduction** | 80-95% | Reading targeted notes vs reading all code/docs |
| **Session Startup Time** | <60 seconds | Reading indexes + 2-3 notes |
| **Knowledge Reuse Rate** | >70% | % of knowledge from memory vs new analysis |
| **Index Consistency** | 100% | All notes indexed, no orphans |
| **Note Freshness** | <5 tasks old | Memory stays current |

### Red Flags (Indicates Memory-First NOT being followed)

- 🚨 Context usage >5000 lines per task
- 🚨 Re-analyzing code already in semantic notes
- 🚨 Creating notes at end of task (not continuously)
- 🚨 Orphaned notes (not in index)
- 🚨 Starting work without reading memory

---

## Memory-First Checklist (Quick Reference)

**Before EVERY task:**
- [ ] Read tags.json and topics.json
- [ ] Query for relevant tags/topics
- [ ] Read identified semantic notes
- [ ] Read last 1-2 episodic notes
- [ ] Check for task-specific index
- [ ] Read relevant procedural notes

**For large tasks (>5000 lines context):**
- [ ] Estimated total context (files + outputs)
- [ ] Task requires >5000 lines? → MUST use incremental approach
- [ ] Decomposed into steps (<2000 lines each)
- [ ] Created episodic note with step-by-step plan
- [ ] Each step has clear scope and deliverable
- [ ] Read proc-004 for incremental work guidelines

**During work:**
- [ ] Using memory as foundation (not re-analyzing)
- [ ] Creating notes continuously (not batching)
- [ ] Updating indexes immediately (not deferring)

**During incremental work (if applicable):**
- [ ] Each step stays within context budget (<2000 lines)
- [ ] Checkpoint created after EACH step (not batched)
- [ ] Episodic note updated with progress after each step
- [ ] "Next Steps" section always present in episodic note
- [ ] "Context for Next Step" documented for resumability
- [ ] Can answer: "If interrupted now, can I resume from this checkpoint?"

**Before finishing:**
- [ ] Created episodic note documenting session
- [ ] Created/updated semantic notes for insights
- [ ] Updated tags.json and topics.json
- [ ] Added cross-references
- [ ] Linked episodic note to outputs

**Before finishing (incremental work):**
- [ ] All steps marked ✅ complete in episodic note
- [ ] Progress tracking shows 100% completion
- [ ] All intermediate checkpoints preserved
- [ ] Final outputs linked to episodic note
- [ ] Lessons learned documented (what worked, what didn't)

---

## Related Documentation

- **[proc-002](./memory-system-operations.md)**: Detailed memory operations (create, update, maintain)
- **[/claude/memory/DESIGN.md](../DESIGN.md)**: Memory system architecture
- **[/claude/prompt/0.memory.md](../../prompt/0.memory.md)**: Memory policies for AI agent
- **[/claude/CLAUDE.md](../../CLAUDE.md)**: System overview and usage guide

---

**Version**: 1.0.0
**Status**: Mandatory - Must be followed for ALL tasks
**Last Updated**: 2025-10-28
