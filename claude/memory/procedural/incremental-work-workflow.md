---
id: proc-004
title: Incremental Work with Memory Checkpoints
type: procedural
tags: [workflow, memory-system, context-management, best-practices, incremental]
created: 2025-10-28
updated: 2025-10-28
related: [proc-003, proc-002]
---

# Incremental Work with Memory Checkpoints

**Purpose**: Define how to break large tasks into incremental steps with memory checkpoints to prevent context overflow and maintain continuity.

**Scope**: MANDATORY for all tasks that:
- Require reading >5000 lines of context
- Involve analyzing >10 files
- Produce outputs >1000 lines
- Have multiple distinct phases

**Priority**: CRITICAL - Prevents context overflow and ensures work continuity

---

## Core Principle

**Large tasks MUST be decomposed into incremental steps. After each step, update memory BEFORE proceeding.**

### The Problem

```
❌ Monolithic Approach:
User: "Create presentation based on all previous work"
Agent: Reads 8300 lines (all Task 1+2 outputs)
       Writes 2000-line presentation
       Updates memory at end

Problem: Context window overflow, work lost if interrupted, no progress tracking
```

### The Solution

```
✅ Incremental Approach:
User: "Create presentation based on all previous work"
Agent: Step 1: Read task-specific index (740 lines) → Checkpoint
       Step 2: Write slides 1-10 → Checkpoint
       Step 3: Write slides 11-20 → Checkpoint
       Step 4: Write slides 21-30 → Checkpoint
       Step 5: Write slides 31-40 → Checkpoint
       Step 6: Create narration → Checkpoint

Benefit: Each step is "saved", work resumable, context stays lean
```

---

## The Incremental Work Pattern

### Universal Step Structure

**EVERY incremental step MUST follow this 4-phase pattern**:

```
┌─────────────────────────────────────────────────────┐
│ PHASE 1: CHECKPOINT IN (Read Memory)                │
│ ─────────────────────────────────────────────────── │
│ 1. Read episodic note for current task             │
│ 2. Check "Progress Tracking" section               │
│    - What steps are completed?                     │
│    - What is the current step?                     │
│    - What's the next step?                         │
│ 3. Read "Context for Next Step"                    │
│    - What knowledge is needed?                     │
│    - Which semantic notes to load?                 │
│    - Any intermediate results to use?              │
│ 4. Load referenced semantic/procedural notes       │
│ 5. Verify: Ready to proceed with current step     │
└──────────────┬──────────────────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────────────────┐
│ PHASE 2: DO WORK (Execute One Unit)                │
│ ─────────────────────────────────────────────────── │
│ 1. Focus on ONE incremental unit                   │
│    - Analyze one file group (5-10 files)           │
│    - Write one documentation section               │
│    - Implement one feature component               │
│    - Create one presentation slide group           │
│ 2. Keep context usage <2000 lines for this step   │
│ 3. Track discoveries/insights in working notes     │
│ 4. Complete the unit fully (don't leave partial)  │
└──────────────┬──────────────────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────────────────┐
│ PHASE 3: CHECKPOINT OUT (Update Memory)            │
│ ─────────────────────────────────────────────────── │
│ 1. Update episodic note IMMEDIATELY:               │
│    - Mark current step as ✅ complete             │
│    - Add discoveries to "Findings"                 │
│    - Update "Progress Tracking"                    │
│    - Define "Next Steps"                           │
│    - Add "Context for Next Step"                   │
│ 2. Create/update semantic notes (if insights)     │
│ 3. Update indexes (if new notes created)          │
│ 4. Save any intermediate results to /tmp/         │
└──────────────┬──────────────────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────────────────┐
│ PHASE 4: VERIFY CONTINUITY                         │
│ ─────────────────────────────────────────────────── │
│ 1. Re-read episodic note's "Next Steps"           │
│ 2. Question: Can next step resume from this?      │
│    - Is context clear?                             │
│    - Are references complete?                      │
│    - Is progress unambiguous?                      │
│ 3. If YES: Proceed to next step (back to Phase 1) │
│ 4. If NO: Fix episodic note before proceeding     │
└─────────────────────────────────────────────────────┘
```

### Context Budget Per Step

**Hard Limits** (MUST NOT exceed):
- **Single step context**: <2000 lines
- **Total memory notes read**: <1000 lines
- **New code/docs read**: <1000 lines
- **Output produced**: <500 lines

**Why These Limits**:
- Prevents context overflow
- Ensures each step is digestible
- Forces proper task decomposition
- Maintains focus on one unit

---

## Task Decomposition Strategies

### Decision Tree: How to Decompose Tasks

```
┌─────────────────────────────────────┐
│ Estimate total context needed       │
│ (files to read + outputs to create) │
└───┬─────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────┐
│ >5000 lines?                        │
└───┬─────────────────────────────────┘
    │ NO → Execute as single task (no decomposition needed)
    │
    │ YES ↓
    │
    ▼
┌─────────────────────────────────────┐
│ Task involves >10 files?            │
└───┬─────────────────────────────────┘
    │ YES → Decompose by FILE GROUPS
    │       • Group 1: Files 1-5 or 1-10
    │       • Group 2: Files 6-10 or 11-20
    │       • One step per group
    │
    ▼
┌─────────────────────────────────────┐
│ Output document >1000 lines?        │
└───┬─────────────────────────────────┘
    │ YES → Decompose by SECTIONS
    │       • Section 1: Introduction + Overview
    │       • Section 2: Core Concepts
    │       • Section 3: Examples
    │       • One step per section
    │
    ▼
┌─────────────────────────────────────┐
│ Analysis involves multiple concepts?│
└───┬─────────────────────────────────┘
    │ YES → Decompose by CONCEPT
    │       • Step 1: Analyze Concept A
    │       • Step 2: Analyze Concept B
    │       • Step 3: Analyze relationships
    │
    ▼
┌─────────────────────────────────────┐
│ Task has distinct phases?           │
└───┬─────────────────────────────────┘
    │ YES → Decompose by PHASE
    │       • Phase 1: Research/Analysis
    │       • Phase 2: Documentation
    │       • Phase 3: Review/Refinement
    │
    ▼
[ Execute with incremental steps ]
```

### What Constitutes "One Incremental Unit"?

**✅ Good Examples** (Appropriately sized):

| Work Type | Unit Size | Rationale |
|-----------|-----------|-----------|
| Code Analysis | 5-10 related files | Enough context for cohesive understanding |
| Documentation | One section (200-400 lines) | Complete logical unit |
| Presentation | One slide group (10-12 slides) | One major topic |
| Implementation | One feature component | Independently testable |
| Review | One document or code module | Focused review scope |

**❌ Too Large** (Risk context overflow):
- "Analyze entire codebase" (>50 files)
- "Write complete documentation" (>2000 lines)
- "Implement full feature" (>10 components)
- "Create entire 40-slide presentation" (all at once)

**❌ Too Small** (Excessive overhead):
- "Analyze one function" (unless complex)
- "Write one paragraph"
- "Read one file" (unless large)
- "Create one slide"

**🎯 Just Right**:
- "Analyze Query API implementation (5 files: QueryTree.kt, Quantifier.kt, etc.)"
- "Write documentation section: Core Architecture (300 lines)"
- "Create slides 1-10: Introduction and CPG Overview"
- "Implement authentication component (login + session management)"

---

## Memory Checkpoint Operations

### Checkpoint Format: Episodic Note Structure

Every episodic note for incremental tasks MUST include:

```markdown
---
id: ep-NNN
title: YYYYMMDD-tN-task-name
type: episodic
tags: [task-completion, incremental-work, <task-tags>]
created: YYYY-MM-DD
updated: YYYY-MM-DD
task: N
outputs: [files created so far]
related: [sem-XXX, proc-004]
---

# Task N - Task Name

**Date**: YYYY-MM-DD
**Status**: in-progress (or complete)
**Work Mode**: Incremental with checkpoints

## Goal

<Overall task objective>

## Approach

<Overall strategy and decomposition plan>

## Progress Tracking

**Completed Steps**:
1. ✅ [Step 1 description] (YYYY-MM-DD HH:MM)
   - Key findings: ...
   - Context used: ~XXX lines
   - Outputs: file.md (section 1-3)

2. ✅ [Step 2 description] (YYYY-MM-DD HH:MM)
   - Key findings: ...
   - Context used: ~XXX lines
   - Outputs: file.md (section 4-6)

3. 🔄 **Currently**: [Step 3 description] (in progress)
   - Started: YYYY-MM-DD HH:MM
   - Expected context: ~XXX lines

**Pending Steps**:
- [ ] Step 4: [description]
- [ ] Step 5: [description]
- [ ] Step 6: [description]

## Context for Next Step

**What's needed for Step 4**:
- Memory to read: sem-003 (EOG mechanism)
- Code to analyze: src/main/kotlin/analysis/*.kt (if needed)
- Previous outputs: /claude/result/N/file.md (section 1-6 complete)
- Intermediate results: /tmp/examples.kt (prepared in Step 3)

**Key context**:
- Section 4-6 covered: Query DSL operators
- Next focus: Integration with EOG
- Related concepts: See sem-004 for Query API, sem-003 for EOG

## Findings

<Cumulative findings from all completed steps>

## Outputs

- **[file1.md](/claude/result/N/file1.md)**: Description (Sections 1-6 complete, 7-9 pending)

## Code References

<All code references from all steps>

## Lessons Learned

<What worked well in this incremental approach, what didn't>

---

**Checkpoint History**:
- Checkpoint 1 (Step 1): YYYY-MM-DD HH:MM - [Brief summary]
- Checkpoint 2 (Step 2): YYYY-MM-DD HH:MM - [Brief summary]
- Checkpoint 3 (Step 3): YYYY-MM-DD HH:MM - [Current checkpoint]
```

### Checkpoint Verification Checklist

After updating episodic note, verify:

- [ ] **Current step marked complete** (✅ not 🔄)
- [ ] **Next step clearly defined** (description + number)
- [ ] **"Context for Next Step" present** (what to read, what's done)
- [ ] **Progress percentages updated** (if applicable)
- [ ] **Intermediate results saved** (if any)
- [ ] **Findings updated** (new discoveries added)
- [ ] **Timestamp recorded** (when checkpoint created)
- [ ] **Resumability test**: "Can I resume from this if interrupted now?" (YES/NO)

### Resuming from Checkpoint

When resuming work (same session or new session):

```
┌─────────────────────────────────────────┐
│ 1. Read episodic note for task          │
│    - Find: ep-NNN for task N            │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│ 2. Locate current state                 │
│    - Check "Progress Tracking"          │
│    - Find last ✅ completed step        │
│    - Find 🔄 current/next step          │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│ 3. Load context for next step           │
│    - Read "Context for Next Step"       │
│    - Load semantic notes referenced     │
│    - Check intermediate results         │
│    - Review previous outputs            │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│ 4. Resume work                          │
│    - Mark next step as 🔄 in-progress   │
│    - Execute step following 4-phase     │
│    - Create checkpoint when done        │
└─────────────────────────────────────────┘
```

**Expected Resume Time**: <2 minutes (reading episodic note + loading context)

---

## Real-World Examples

### Example 1: Task 3 (40-Slide Presentation Creation)

#### Initial Task Assessment

**User Request**: "Create 40-slide presentation on CPG reachability analysis"

**Estimate**:
- Knowledge needed: Task 1 outputs (3 docs, 3800 lines) + Task 2 outputs (4 docs, 5300 lines) = 9100 lines
- Output to create: 40 slides (~2000 lines Markdown)
- Total context: ~11,000 lines → **Exceeds safe limit**

**Decision**: Use incremental approach with task-specific index

#### Decomposition Plan

```
Step 0: Preparation
- Read task-3-index.json (optimized index)
- Create ep-004 with decomposition plan
- Checkpoint: Plan ready

Step 1: Research Phase (Context: ~800 lines)
- Read indexed sections from Task 1+2 outputs (740 lines)
- Read sem-003 (EOG) and sem-004 (Query API) from memory
- Checkpoint: Knowledge loaded

Step 2: Slides 1-10 (Introduction + CPG Architecture)
- Content: What is CPG, Why reachability matters, CPG architecture
- Based on: sem-001, sem-002
- Checkpoint: Slides 1-10 complete

Step 3: Slides 11-20 (EOG Mechanism)
- Content: Control flow graphs, EOG structure, Branch handling
- Based on: sem-003
- Checkpoint: Slides 11-20 complete

Step 4: Slides 21-30 (Query API)
- Content: Query DSL, Traversal, Analysis workflows
- Based on: sem-004
- Checkpoint: Slides 21-30 complete

Step 5: Slides 31-40 (Integration + Examples)
- Content: EOG+Query integration, Real scenarios, Code examples
- Based on: sem-003 + sem-004 cross-references
- Checkpoint: Slides 31-40 complete

Step 6: Narration Script
- Create speaker notes for each slide
- Checkpoint: Script complete

Step 7: Final Review
- Check consistency, cross-references, completeness
- Checkpoint: Task complete
```

#### Actual Execution (with Checkpoints)

**ep-004 Progress Tracking** (excerpt):

```markdown
## Progress Tracking

**Completed Steps**:
1. ✅ Read task-3-index.json and indexed sections (2025-10-28 10:00)
   - Context used: ~800 lines
   - Key knowledge: EOG mechanism, Query API DSL

2. ✅ Created slides 1-10 (Intro + Architecture) (2025-10-28 10:20)
   - Context used: ~400 lines (sem-001, sem-002)
   - Output: presentation.md (lines 1-250)

3. ✅ Created slides 11-20 (EOG Mechanism) (2025-10-28 10:40)
   - Context used: ~300 lines (sem-003)
   - Output: presentation.md (lines 251-500)

4. 🔄 Currently: Creating slides 21-30 (Query API) (in progress)
   - Started: 2025-10-28 11:00
   - Using: sem-004 (Query API DSL)

**Pending Steps**:
- [ ] Step 5: Create slides 31-40 (Integration + Examples)
- [ ] Step 6: Create narration script
- [ ] Step 7: Final review

## Context for Next Step (Step 5)

**What's needed**:
- Memory: sem-003 (EOG) + sem-004 (Query API) - focus on integration
- Previous output: presentation.md (slides 1-30 complete)
- Examples: Code snippets from Task 2 outputs (indexed sections)

**Key context**:
- Slides 21-30 covered Query API operators and DSL syntax
- Next focus: How Query API uses EOG for reachability analysis
- Integration points: See sem-004 section "Collaboration with Graph Layer"
```

**Benefits Realized**:
- ✅ Total context per step: <1000 lines (vs 11,000 if monolithic)
- ✅ Work resumable after each checkpoint
- ✅ Progress clearly tracked (user can see completion)
- ✅ No context overflow

---

### Example 2: Large Codebase Analysis (50 Files)

#### Initial Task Assessment

**User Request**: "Analyze all constant evaluation files for design patterns"

**Estimate**:
- Files to analyze: 50 Kotlin files (~10,000 lines total)
- Expected output: Documentation (~2000 lines)
- Total context: ~12,000 lines → **Exceeds safe limit**

**Decision**: Decompose by file groups

#### Decomposition Plan

```
Step 1: Analyze files 1-10 (Core evaluation logic)
- Files: ConstantEvaluator.kt, ValueResolver.kt, etc.
- Create semantic note if pattern found
- Checkpoint: Group 1 complete

Step 2: Analyze files 11-20 (Type-specific evaluators)
- Files: IntEvaluator.kt, BoolEvaluator.kt, etc.
- Update semantic note if same pattern
- Checkpoint: Group 2 complete

Step 3: Analyze files 21-30 (Expression handlers)
- Files: BinaryExprHandler.kt, UnaryExprHandler.kt, etc.
- Compare with previous patterns
- Checkpoint: Group 3 complete

Step 4: Analyze files 31-40 (Utility classes)
- Files: EvaluationContext.kt, ValueCache.kt, etc.
- Identify supporting patterns
- Checkpoint: Group 4 complete

Step 5: Analyze files 41-50 (Integration & tests)
- Files: Integration tests, usage examples
- Validate patterns in practice
- Checkpoint: Group 5 complete

Step 6: Synthesize findings
- Read all semantic notes created in steps 1-5
- Create consolidated documentation
- Checkpoint: Documentation complete

Step 7: Final review
- Cross-check with code
- Add code examples
- Checkpoint: Task complete
```

#### Key Checkpoint Example (After Step 2)

**ep-005 excerpt**:

```markdown
## Progress Tracking

**Completed Steps**:
1. ✅ Analyzed files 1-10 (Core evaluation) (2025-10-28 14:00)
   - Pattern found: Visitor pattern for AST traversal
   - Created: sem-005 (Visitor Pattern in CPG)
   - Context used: ~1500 lines

2. ✅ Analyzed files 11-20 (Type evaluators) (2025-10-28 14:30)
   - Pattern confirmed: Same visitor pattern
   - Updated: sem-005 with type-specific implementations
   - Context used: ~1200 lines

3. 🔄 Currently: Analyzing files 21-30 (Expression handlers)

**Context for Next Step (Step 3)**:
- Memory: sem-005 (Visitor Pattern) - check if expr handlers follow same pattern
- Files 21-30: Focus on how binary/unary expressions handled
- Hypothesis: Expression handlers may use Strategy pattern (not Visitor)
- If confirmed: Create sem-006 for Strategy pattern
```

**Benefits**:
- Each step <2000 lines context
- Patterns identified incrementally
- Semantic notes created immediately (not at end)
- Can resume from any checkpoint

---

## Anti-Patterns and Fixes

### Anti-Pattern 1: "I'll do all work then update memory"

**Symptom**:
```
Agent: Reading all 50 files...
      (No memory updates for 30 minutes)
      Creating documentation...
      (Still no memory updates)
      Finally: Creating episodic note
```

**Why bad**:
- Context overflow risk
- Work lost if interrupted
- No progress tracking
- Overwhelming batch of knowledge at end

**Fix**:
```
Agent: Reading files 1-10... → Checkpoint (ep-NNN updated)
      Reading files 11-20... → Checkpoint (ep-NNN updated)
      Creating doc section 1... → Checkpoint (ep-NNN updated)
      Creating doc section 2... → Checkpoint (ep-NNN updated)
```

**How to fix**:
- Set timer: Update memory every 15-30 minutes
- Follow 4-phase pattern strictly
- Mark each step complete BEFORE starting next

---

### Anti-Pattern 2: "Steps are too small"

**Symptom**:
```
Step 1: Read file1.kt (100 lines) → Checkpoint
Step 2: Read file2.kt (100 lines) → Checkpoint
Step 3: Read file3.kt (100 lines) → Checkpoint
...
Step 50: Read file50.kt (100 lines) → Checkpoint
```

**Why bad**:
- Excessive checkpoint overhead
- 50 checkpoints for one analysis task
- Loses "big picture" view
- Inefficient (100 line context is trivial)

**Fix**:
```
Step 1: Read files 1-10 (1000 lines, one cohesive group) → Checkpoint
Step 2: Read files 11-20 (1000 lines) → Checkpoint
Step 3: Read files 21-30 (1000 lines) → Checkpoint
...
```

**Decision rule**: If step is <500 lines context, consider grouping with next step

---

### Anti-Pattern 3: "Steps are too large"

**Symptom**:
```
Step 1: Analyze entire codebase (50 files, 10,000 lines) → Checkpoint
```

**Why bad**:
- Context overflow (>2000 line limit)
- Can't resume if interrupted mid-step
- All-or-nothing (no partial progress)

**Fix**:
```
Step 1: Analyze core files 1-10 (1500 lines) → Checkpoint
Step 2: Analyze supporting files 11-20 (1500 lines) → Checkpoint
...
```

**Decision rule**: If step is >2000 lines context, MUST decompose further

---

### Anti-Pattern 4: "Checkpoint doesn't have 'Next Steps'"

**Symptom**:
```markdown
## Progress Tracking

**Completed Steps**:
1. ✅ Analyzed Query API
2. ✅ Wrote documentation

(No "Next Steps" section)
```

**Why bad**:
- Can't resume work (what to do next?)
- Loses continuity between sessions
- Agent must re-analyze task from scratch

**Fix**:
```markdown
## Progress Tracking

**Completed Steps**:
1. ✅ Analyzed Query API
2. ✅ Wrote documentation sections 1-3

**Pending Steps**:
- [ ] Write documentation sections 4-6
- [ ] Create code examples
- [ ] Final review

## Context for Next Step (Step 3)

**What's needed**:
- Memory: sem-004 (Query API)
- Previous output: /claude/result/2/query-api-doc.md (sections 1-3 complete)
- Focus: Advanced query operators and composition
```

**How to fix**: ALWAYS include "Next Steps" and "Context for Next Step" in checkpoint

---

### Anti-Pattern 5: "Ignoring context budget"

**Symptom**:
```
Step 1: Read 15 files + 3 semantic notes + write 800-line doc section
        Total context: ~4000 lines (exceeds 2000 limit)
```

**Why bad**:
- Defeats purpose of incremental approach
- Context overflow risk
- Step too large to resume easily

**Fix**:
```
Step 1: Read indexed sections (800 lines) + sem-003 (200 lines)
        Total: 1000 lines → Checkpoint

Step 2: Write doc section part 1 (400 lines output, 600 lines context)
        Total: 600 lines → Checkpoint

Step 3: Write doc section part 2 (400 lines output, 600 lines context)
        Total: 600 lines → Checkpoint
```

**How to fix**: Before starting step, estimate context. If >2000 lines, split step.

---

## Performance Metrics

### Target Metrics for Incremental Work

| Metric | Target | Measurement | Rationale |
|--------|--------|-------------|-----------|
| **Step Context Size** | <2000 lines | Lines read (memory + code) per step | Prevent overflow |
| **Checkpoint Frequency** | Every 15-30 min | Time between memory updates | Regular save points |
| **Step Completion Rate** | >90% | % of steps completed without restart | Good decomposition |
| **Resume Time** | <3 minutes | Time to resume from checkpoint | Efficient continuity |
| **Context Waste** | <10% | Re-read content between steps | Minimize redundancy |
| **Resumability** | 100% | Can resume from any checkpoint | Critical requirement |

### Red Flags (Indicates poor incremental practice)

- 🚨 **Step context >2000 lines**: Step too large, needs further decomposition
- 🚨 **No checkpoints >30 min**: Forgetting to update memory, risk of loss
- 🚨 **Episodic note has no "Next Steps"**: Can't resume, loses continuity
- 🚨 **Multiple step failures**: Poor decomposition, steps not well-defined
- 🚨 **Context re-reading >20%**: Poor checkpoint context, redundant reads

### Success Indicators

- ✅ All steps <2000 lines context
- ✅ Checkpoints every 15-30 minutes
- ✅ Episodic note has complete progress tracking
- ✅ Can resume from any checkpoint in <3 minutes
- ✅ Total context (sum of all steps) << monolithic approach
- ✅ User can see progress at any time

---

## Integration with Memory-First Workflow

**Incremental Work (proc-004)** is a specialization of **Memory-First Workflow (proc-003)**.

### Relationship

```
proc-003 (Memory-First Workflow)
    ↓ Applied to large tasks
proc-004 (Incremental Work with Checkpoints)

Memory-First = General operating principle for ALL tasks
Incremental Work = Specific strategy for LARGE tasks (>5000 lines context)
```

### Combined Workflow

For large tasks, combine both:

```
1. CONSULT MEMORY (proc-003 Phase 1)
   - Read indexes, semantic notes, episodic notes
   - Build foundational knowledge

2. DECOMPOSE TASK (proc-004 addition)
   - Estimate total context
   - If >5000 lines: Decompose into steps
   - Create decomposition plan in episodic note

3. INCREMENTAL EXECUTION (proc-004)
   For each step:
     a. Checkpoint In (read memory for this step)
     b. Do Work (one unit, <2000 lines)
     c. Checkpoint Out (update memory)
     d. Verify Continuity (can resume?)

4. UPDATE MEMORY (proc-003 Phase 3)
   - After task complete: Final episodic note update
   - Extract semantic notes (if not done per-step)
   - Update indexes
   - Cross-references
```

---

## Quick Reference: Incremental Work Checklist

### Before Starting Large Task

- [ ] Estimated total context >5000 lines?
- [ ] Decomposed into steps (<2000 lines each)?
- [ ] Created episodic note with step plan?
- [ ] Each step clearly defined with scope?

### During Each Step

- [ ] **Checkpoint In**: Read episodic note, load context
- [ ] **Do Work**: Execute one unit (<2000 lines)
- [ ] **Checkpoint Out**: Update episodic note immediately
- [ ] **Verify**: "Next Steps" and "Context for Next Step" present?

### At Each Checkpoint

- [ ] Current step marked ✅ complete
- [ ] Next step clearly defined
- [ ] "Context for Next Step" documented
- [ ] Intermediate results saved (if any)
- [ ] Findings updated
- [ ] Timestamp recorded
- [ ] Resumability verified (can resume from here?)

### After Task Complete

- [ ] All steps marked ✅ complete
- [ ] Final episodic note update
- [ ] Semantic notes created/updated
- [ ] Indexes updated
- [ ] Outputs linked in episodic note

---

## Related Documentation

- **[proc-003](./memory-first-workflow.md)**: Memory-First Workflow (mandatory for all tasks)
- **[proc-002](./memory-system-operations.md)**: Memory operations (create, update, maintain)
- **[/claude/prompt/0.memory.md](../../prompt/0.memory.md)**: Memory policies
- **[/claude/CLAUDE.md](../../CLAUDE.md)**: System overview and architecture

---

## Summary

**Incremental Work with Memory Checkpoints** is the strategy for handling large tasks without context overflow.

**Core Pattern**: Decompose → Step (Checkpoint In → Work → Checkpoint Out → Verify) → Step → ... → Complete

**Key Benefits**:
- ✅ Prevents context overflow (each step <2000 lines)
- ✅ Enables resumability (checkpoint = save point)
- ✅ Tracks progress (user sees completion percentage)
- ✅ Reduces risk (only current step affected by errors)
- ✅ Maintains continuity (memory connects steps)

**When to Use**: MANDATORY for tasks requiring >5000 lines context

**Success Metric**: Can resume work from any checkpoint in <3 minutes

---

**Version**: 1.0.0
**Status**: Mandatory for large tasks
**Last Updated**: 2025-10-28
