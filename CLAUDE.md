# AI Agent Prompt System - Architecture and Usage Guide

**Version**: 2.0.1
**Last Updated**: 2025-10-28
**System Status**: Production-Ready with Memory System

---

## 🚫 ABSOLUTE PROHIBITION: No Git Operations

**THIS IS AN ABSOLUTE AND NON-NEGOTIABLE RULE. NEVER VIOLATE THIS.**

### Prohibited Actions

The AI Agent is **ABSOLUTELY FORBIDDEN** from:
- ❌ Performing ANY git operations (commit, push, pull, merge, rebase, etc.)
- ❌ Suggesting git operations to the user
- ❌ Considering git operations in task planning
- ❌ Mentioning git operations in recommendations
- ❌ Creating commit messages
- ❌ Staging files with `git add`
- ❌ Checking git status (except for informational purposes when explicitly requested)

### Rationale

**Version control is EXCLUSIVELY the user's responsibility.** The AI Agent's role is:
- ✅ Create and modify files
- ✅ Maintain the memory system
- ✅ Produce documentation and analysis
- ✅ Answer questions

**NOT**:
- ❌ Manage version control
- ❌ Decide what/when to commit
- ❌ Push changes to remote repositories

### What To Do Instead

When work is complete:
1. ✅ Inform user: "Work complete. Files have been created/updated."
2. ✅ List affected files
3. ✅ Update memory system (episodic notes, indexes)
4. ❌ **DO NOT** offer to commit or suggest git commands

**The user will handle all git operations themselves.**

---

## Welcome

Welcome to the CPG AI Agent Prompt System, a sophisticated architecture designed to assist you in analyzing a Java monorepo, generating professional documentation, and addressing potential defects **with minimal context usage** through an advanced memory system.

This system implements a **memory-driven, task-oriented workflow** that enables the AI Agent to:
- ✅ Retain knowledge across sessions via persistent file-based memory
- ✅ Minimize context waste by avoiding unnecessary file reads
- ✅ Optimize performance through task-specific indexing (91% context reduction)
- ✅ Maintain consistency by using memory as the source of truth

---

## 🚨 MANDATORY: Memory-First Operating Principle

**THIS IS THE FOUNDATION OF ALL WORK. NEVER SKIP THIS.**

### The Three-Phase Mandatory Workflow

Every action MUST follow this sequence:

```
┌────────────────────────────────────────────┐
│ PHASE 1: CONSULT MEMORY (BEFORE work)     │
│ ────────────────────────────────────────── │
│ 1. Read tags.json and topics.json         │
│ 2. Query for relevant knowledge           │
│ 3. Read semantic notes (stable knowledge) │
│ 4. Read episodic notes (history)          │
│ 5. Check task-specific index              │
│ 6. Read procedural notes (workflows)      │
└──────────────┬─────────────────────────────┘
               │
               ▼
┌────────────────────────────────────────────┐
│ PHASE 2: WORK (WITH memory as foundation) │
│ ────────────────────────────────────────── │
│ ✅ Use memory knowledge as base           │
│ ✅ Only read NEW code if memory lacking   │
│ ✅ Create notes IMMEDIATELY on discovery  │
│ ❌ NEVER re-analyze what's in memory      │
│ ❌ NEVER ignore existing knowledge        │
└──────────────┬─────────────────────────────┘
               │
               ▼
┌────────────────────────────────────────────┐
│ PHASE 3: UPDATE MEMORY (AFTER actions)    │
│ ────────────────────────────────────────── │
│ 1. Create/update semantic notes           │
│ 2. Create/update episodic notes           │
│ 3. Update indexes IMMEDIATELY             │
│ 4. Add cross-references                   │
│ 5. Link notes to outputs                  │
└────────────────────────────────────────────┘
```

### Critical Questions (Ask BEFORE every action)

- ❓ Have I read tags.json and topics.json?
- ❓ Does memory already contain what I need?
- ❓ Did I (in past session) already analyze this?
- ❓ Are there semantic notes for these concepts?
- ❓ Is there a task-specific index to optimize reading?

**If answer to ANY is "I don't know" → You MUST read memory FIRST**

### Memory-First Checklist

**Session Start** (ALWAYS do these):
- [ ] Read `/claude/memory/index/tags.json`
- [ ] Read `/claude/memory/index/topics.json`
- [ ] Read last 1-2 episodic notes
- [ ] Query and read relevant semantic notes
- [ ] Check for task-specific index
- [ ] Read `/claude/memory/procedural/memory-first-workflow.md` (proc-003) if unsure

**During Work** (Continuous):
- [ ] Using memory as foundation (not re-deriving)
- [ ] Creating semantic notes IMMEDIATELY on new discoveries
- [ ] Updating episodic notes with progress (not waiting till end)
- [ ] Adding cross-references when finding related concepts

**Before Finishing** (MANDATORY before marking task complete):
- [ ] Created episodic note documenting session
- [ ] Created/updated semantic notes for stable insights
- [ ] Updated tags.json and topics.json
- [ ] Added cross-references between related notes
- [ ] Linked episodic note to output files

**❌ CRITICAL ANTI-PATTERNS (NEVER do these)**:
- ❌ Starting work without reading memory
- ❌ Re-analyzing code already in semantic notes
- ❌ Batching note creation till task end
- ❌ Creating notes without updating indexes
- ❌ Ignoring existing knowledge in memory

**📖 Detailed Workflow**: See `/claude/memory/procedural/memory-first-workflow.md` (proc-003) for complete operating procedures, decision trees, and anti-pattern fixes.

---

## System Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    USER INTERACTION                          │
│  "Please work on Task 3" OR "Analyze the Query API"         │
└────────────────┬────────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────────┐
│                  MEMORY SYSTEM (First)                       │
│  • Global Index (tags, topics)                              │
│  • Semantic Memory (stable knowledge)                       │
│  • Episodic Memory (task history)                           │
│  • Task-Specific Index (context optimization)               │
└────────────────┬────────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────────┐
│               PROMPT SYSTEM (On Demand)                      │
│  • Global Config (0.overview.md, 0.memory.md)               │
│  • Task Prompts (1.java-cpg.md, 2.constant-eval..., etc.)  │
│  ⚠️  ONLY read when user explicitly mentions a task         │
└────────────────┬────────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────────┐
│                   TASK EXECUTION                             │
│  • Use memory for knowledge                                 │
│  • Analyze codebase as needed                               │
│  • Produce outputs to /claude/result/<N>/                      │
│  • Update memory system                                     │
└─────────────────────────────────────────────────────────────┘
```

---

## 🎯 Critical Principle: Memory First, Prompts On-Demand

### ⚠️ DO NOT Read Task Prompts Proactively

**IMPORTANT**: Task prompt files (1.java-cpg.md, 2.constant-eval-and-reachability.md, etc.) should **ONLY** be read when:
1. User explicitly mentions a task number: "Do Task 3", "Work on Task 2", etc.
2. User explicitly requests the task by name: "Create a presentation about reachability", "Analyze the Java frontend"

**DO NOT** read task prompts:
- ❌ At session start
- ❌ "Just to see what's available"
- ❌ "To understand the project"
- ❌ Before user requests a specific task

**Why**: Task prompts can be large (1000+ lines). Reading them wastes context and violates the memory-first principle.

### ✅ What to Read Instead

**At Session Start**:
1. **This file** (`CLAUDE.md`) - You're already reading it
2. **Global configuration** (`/claude/prompt/0.overview.md`) - Mandatory rules
3. **Memory policy** (`/claude/prompt/0.memory.md`) - How to use memory system
4. **Memory indexes** (`/claude/memory/index/tags.json`, `topics.json`) - What knowledge exists
5. **Recent episodic notes** (last 1-2 from `/claude/memory/episodic/`) - What was done recently

**When User Asks a Question**:
1. **Query memory indexes** first to find relevant semantic notes
2. **Read semantic notes** for stable knowledge (e.g., sem-004 for Query API)
3. **Read episodic notes** for task history (e.g., ep-002 for Task 2 outcomes)
4. **Only if memory insufficient**: Read codebase or task outputs

**When User Requests a Task**:
1. **Read task prompt** (`/claude/prompt/<N>-*.md`)
2. **Check for task-specific index** (`/claude/memory/index/task-<N>-index.json`)
3. **Use index to read selectively** (if index exists)
4. **Execute task using memory + selective reading**

---

## Directory Structure and Reading Strategy

### `/claude/prompt/` - Global Config and Task Definitions

```
/claude/prompt/
├── 0.overview.md              # READ: At session start (MANDATORY)
├── 0.memory.md                # READ: At session start (MANDATORY)
├── 1.java-cpg.md              # READ: ONLY when user says "Task 1"
├── 2.constant-eval-and-reachability.md  # READ: ONLY when user says "Task 2"
├── 3.source-example.md        # READ: ONLY when user says "Task 3"
└── ...                        # READ: ONLY when user requests specific task
```

**Reading Priority**:
1. **Always read first**: `0.overview.md`, `0.memory.md`
2. **Read on demand**: `<N>-*.md` only when task explicitly requested
3. **Never read**: "Just browsing" or "to understand the project"

### `/claude/memory/` - Persistent Knowledge (Primary Source)

```
/claude/memory/
├── DESIGN.md                  # READ: For memory system architecture
├── semantic/                  # READ: For stable knowledge
│   ├── java-cpg-architecture.md      (sem-001)
│   ├── handler-pattern.md            (sem-002)
│   ├── unreachable-eog-pass.md       (sem-003)
│   └── query-api-dsl.md              (sem-004)
├── episodic/                  # READ: For task history
│   ├── 20251027-t1-java-cpg-analysis.md      (ep-001)
│   ├── 20251028-t2-constant-eval-analysis.md (ep-002)
│   └── 20251028-t2-doc-reorganization.md     (ep-003)
├── procedural/                # READ: For workflows
│   ├── cpg-frontend-analysis-workflow.md     (proc-001)
│   ├── memory-system-operations.md           (proc-002)
│   └── memory-first-workflow.md              (proc-003) ⚠️ MANDATORY reading
└── index/                     # READ: At session start, before queries
    ├── tags.json              # 28 tags for quick lookup
    ├── topics.json            # 10 topics organized by area
    ├── enhanced-tags.json     # Metadata, categories, gap analysis
    ├── enhanced-topics.json   # Hierarchy, coverage, knowledge graph
    └── task-3-index.json      # Task-specific optimization (91% context reduction)
```

**Reading Strategy**:
1. **Start every session**: Read `tags.json` and `topics.json` to know what exists
2. **For questions**: Query index → identify note IDs → read specific semantic notes
3. **For task history**: Read relevant episodic notes (sorted by date, most recent first)
4. **For workflows**: Read procedural notes (proc-001, proc-002)
5. **Optimize with task index**: If task-N-index.json exists, use it to read only needed sections

### `/claude/result/` - Task Outputs (Secondary Source)

```
/claude/result/
├── 1/                         # Task 1 outputs (3 docs, 120KB)
├── 2/                         # Task 2 outputs (4 docs, 150KB)
└── 3/                         # Task 3 outputs (pending)
```

**Reading Strategy**:
1. **Avoid reading entire outputs**: Task outputs can be 1000+ lines each
2. **Prefer memory notes**: Semantic notes extract key concepts from outputs
3. **Use task-specific index**: If task-N-index.json exists, it specifies exact sections to read
4. **Read selectively**: If memory insufficient, read specific sections (not entire files)

**Example**:
- ❌ Bad: Read all of `/claude/result/2/*.md` (5300+ lines)
- ✅ Good: Read `sem-003` (EOG, 185 lines) + `sem-004` (Query API, 421 lines)
- ✅ Best: Use `task-3-index.json` to read only lines 20-80, 120-180 of specific files (740 lines total)

### `/claude/temp/` - Temporary Working Directory (**NEW**)

```
/claude/temp/                  # Temporary artifacts (NOT in git)
├── task-1/                    # Task 1 working directory
├── task-2/                    # Task 2 working directory
├── task-3/                    # Task 3 working directory
└── task-4/                    # Task 4 working directory
    ├── brainstorm/            # Brainstorming notes, drafts
    ├── drafts/                # Incomplete drafts
    ├── analysis/              # Intermediate analysis
    ├── reference/             # Reference materials
    └── STATUS.md              # Task progress tracking
```

**Purpose**: Temporary working directory for intermediate artifacts during task execution.

**Lifecycle**:
1. **Created**: Automatically when task starts (if not exists)
2. **Used**: Store brainstorms, drafts, incomplete analysis, references
3. **Cleaned**: After task completes, move valuable artifacts to memory or delete

**What Goes Here**:
- ✅ Brainstorming notes (e.g., `TASK4_DEFECTS_BRAINSTORM.md`)
- ✅ Incomplete drafts (e.g., partial documentation)
- ✅ Status tracking files (e.g., `TASK4_COMPLETION_STATUS.md`)
- ✅ Reference materials from previous attempts
- ✅ Temporary analysis artifacts

**What Does NOT Go Here**:
- ❌ Final deliverables (goes to `/claude/result/<N>/`)
- ❌ Semantic knowledge (goes to `/claude/memory/semantic/`)
- ❌ Task history (goes to `/claude/memory/episodic/`)
- ❌ Persistent documentation (stays in `/claude/`)

**Cleanup Policy**:
- **After task completion**: Review temp directory
- **Valuable insights**: Extract to semantic notes
- **Task progress**: Document in episodic note
- **Rest**: Delete or keep as reference (in `reference/` subdirectory)
- **Next task start**: Create fresh temp directory for new task

**Git Ignore**: All `/claude/temp/` directories are ignored by git (see `.gitignore`).

**Subdirectory Structure**:
- `brainstorm/`: Initial ideas, defect lists, concept sketches
- `drafts/`: Incomplete documentation, partial analysis
- `analysis/`: Intermediate analysis results, data extractions
- `reference/`: Previous attempts, reference materials from prior work
- `STATUS.md`: Real-time task progress tracking (optional)

---

## Workflow Examples

### Example 1: Session Start (No Task Requested)

```
User: "Hello, I'm ready to work"

AI Agent:
1. ✅ Read CLAUDE.md (this file)
2. ✅ Read /claude/prompt/0.overview.md (global rules)
3. ✅ Read /claude/prompt/0.memory.md (memory policy)
4. ✅ Read /claude/memory/index/tags.json and topics.json
5. ✅ Read last 1-2 episodic notes (ep-002, ep-003)
6. ✅ Greet user, indicate readiness
7. ❌ DO NOT read task prompts (1.java-cpg.md, 2.constant-eval..., etc.)
8. ❌ DO NOT read task outputs (/claude/result/1/, /claude/result/2/)
```

**Context used**: ~500 lines (global config + indexes + recent episodic notes)

### Example 2: User Asks About Query API

```
User: "How does the Query API work?"

AI Agent:
1. ✅ Query /claude/memory/index/tags.json for "query-api"
   → Found: sem-004, ep-003
2. ✅ Read /claude/memory/semantic/query-api-dsl.md (sem-004, 421 lines)
3. ✅ (Optional) Read /claude/memory/episodic/20251028-t2-doc-reorganization.md (ep-003)
   for context on when this was analyzed
4. ✅ Answer user's question based on sem-004
5. ❌ DO NOT read Task 2 prompt (2.constant-eval-and-reachability.md)
6. ❌ DO NOT read Task 2 outputs (/claude/result/2/*.md) unless sem-004 insufficient
```

**Context used**: ~500 lines (sem-004 + ep-003) vs 5300+ lines if reading all Task 2 outputs

**Savings**: ~90% context reduction

### Example 3: User Requests Task 3

```
User: "Please work on Task 3"

AI Agent:
1. ✅ Read /claude/prompt/3.source-example.md (Task 3 prompt)
2. ✅ Check for /claude/memory/index/task-3-index.json
   → Found! Use it for optimized reading
3. ✅ Read task-3-index.json to identify critical knowledge:
   - sem-003 (EOG mechanism)
   - sem-004 (Query API)
   - Specific sections of Task 2 outputs (lines 20-80, 120-180, etc.)
4. ✅ Read only indexed sections (~740 lines total)
5. ✅ Execute Task 3 (create presentation)
6. ✅ Update memory system:
   - Create ep-004 documenting Task 3 session
   - Update indexes with new knowledge
7. ❌ DO NOT read entire Task 1 or Task 2 outputs (8300+ lines)
```

**Context used**: ~1200 lines (task prompt + indexed sections)
**Without index**: ~9500 lines (task prompt + all previous outputs)
**Savings**: ~87% context reduction

### Example 4: User Asks Generic Question

```
User: "What's the overall architecture of CPG?"

AI Agent:
1. ✅ Query /claude/memory/index/topics.json for "CPG Architecture"
   → Found topic with notes: sem-001, sem-002
2. ✅ Read /claude/memory/semantic/java-cpg-architecture.md (sem-001)
3. ✅ Read /claude/memory/semantic/handler-pattern.md (sem-002)
4. ✅ Answer based on semantic notes
5. ❌ DO NOT read Task 1 prompt or outputs
```

**Context used**: ~200 lines (sem-001 + sem-002)

---

## Memory System Quick Reference

### When to Read Memory

**At session start**:
- ✅ Read `tags.json` and `topics.json` (understand available knowledge)
- ✅ Read recent episodic notes (understand recent work)
- ✅ Read relevant semantic notes based on expected work

**When user asks a question**:
- ✅ Query index → identify relevant notes → read semantic notes
- ✅ Check episodic notes for historical context

**When user requests a task**:
- ✅ Read task prompt (`/claude/prompt/<N>-*.md`)
- ✅ Check for task-specific index (`task-<N>-index.json`)
- ✅ Read indexed sections (if index exists)
- ✅ Read semantic notes for foundational knowledge

### Memory Lookup Pattern

```
Question about <concept>
    ↓
1. grep "<concept>" /claude/memory/index/tags.json
    ↓ (get note IDs)
2. Read /claude/memory/semantic/<note-id>.md
    ↓
3. Answer using semantic note
    ↓ (if insufficient)
4. Check /claude/memory/episodic/ for related task history
    ↓ (if still insufficient)
5. Read specific sections of /claude/result/ (NOT entire files)
```

---

## Task Prompt File Structure

### Global Configuration (Always Read)

**`0.overview.md`**:
- Behavioral guidelines
- Output format requirements
- Evidence requirements
- Language conventions
- Mandatory rules for all tasks

**`0.memory.md`**:
- Memory system policies
- When to read memory
- When to write memory
- Task-specific index construction
- Memory hygiene workflows

### Task Prompts (Read Only When Requested)

**`1.java-cpg.md`**: Java CPG Frontend Analysis
- **When to read**: User says "Task 1", "analyze Java frontend", or similar
- **What it contains**: Task objective, deliverables, documentation structure
- **Memory coverage**: Documented in sem-001, sem-002, ep-001

**`2.constant-eval-and-reachability.md`**: Constant Evaluation Infrastructure
- **When to read**: User says "Task 2", "analyze constant evaluation", or similar
- **What it contains**: Task objective, CPG core analysis requirements
- **Memory coverage**: Documented in sem-003, sem-004, ep-002, ep-003

**`3.source-example.md`**: CPG Reachability Analysis Presentation
- **When to read**: User says "Task 3", "create presentation", or similar
- **What it contains**: Presentation requirements, scenarios, slide structure
- **Memory coverage**: Task index exists (task-3-index.json), no episodic note yet

---

## AI Agent Output Strategy

### Where Outputs Go

All AI Agent outputs are stored under `/claude/result/<task_number>/`:

```
/claude/result/1/    # Task 1: Java Frontend Documentation (3 files)
/claude/result/2/    # Task 2: CPG Core Analysis (4 files)
/claude/result/3/    # Task 3: Presentation (pending)
```

### Output Format

- **Format**: Markdown (`.md`)
- **Language**: Chinese (user-facing content)
- **Evidence**: Code references with file:line citations
- **Cross-references**: Link to memory notes where applicable

### Memory Integration

After producing outputs:
1. **Create episodic note**: Document task session (goal, approach, findings, outputs)
2. **Extract semantic notes**: Promote stable insights to semantic memory
3. **Update indexes**: Add note IDs to tags.json and topics.json
4. **Update episodic notes**: Link outputs to episodic record

---

## Language Convention

- **Internal thinking**: English (for code analysis, technical reasoning)
- **External retrieval**: English (code, documentation, web search)
- **Final outputs**: Chinese (user-facing documentation, reports)
- **Memory notes**: Mixed (English for technical terms, Chinese for explanations)
- **Indexes**: English (for consistency and machine readability)

---

## Performance Metrics

### Current System Performance

| Metric | Value | Target |
|--------|-------|--------|
| **Total Memory Notes** | 10 | -- |
| **Semantic Notes** | 4 | -- |
| **Episodic Notes** | 3 | -- |
| **Procedural Notes** | 3 | -- |
| **Tags** | 29 | <100 |
| **Topics** | 10 | <50 |
| **Context Reduction (Task 3)** | 91% | >70% |
| **Index Lookup Time** | <1 sec | <1 sec |
| **Note Quality** | 100% | 100% |

### Expected Context Usage

| Scenario | Without Memory | With Memory | Reduction |
|----------|----------------|-------------|-----------|
| Session start | 8300 lines (read all outputs) | 500 lines (indexes + recent episodic) | 94% |
| Answer question | 5300 lines (read Task 2 outputs) | 500 lines (semantic notes) | 91% |
| Task 3 execution | 9500 lines (task + all outputs) | 1200 lines (task + indexed sections) | 87% |

---

## Getting Started

### For New Sessions

```bash
# 1. Read this file (CLAUDE.md)
# 2. Read global configuration
Read /claude/prompt/0.overview.md
Read /claude/prompt/0.memory.md

# 3. Understand available knowledge
Read /claude/memory/index/tags.json
Read /claude/memory/index/topics.json

# 4. Check recent work
Read /claude/memory/episodic/*.md (last 1-2 notes)

# 5. Wait for user request
# DO NOT read task prompts or outputs yet
```

### For Answering Questions

```bash
# 1. Query memory index
grep -i "<keyword>" /claude/memory/index/tags.json

# 2. Read relevant semantic notes
Read /claude/memory/semantic/<note>.md

# 3. Answer based on memory
# Only read codebase/outputs if memory insufficient
```

### For Executing Tasks

```bash
# 1. User says "Task N"
Read /claude/prompt/<N>-*.md

# 2. Check for task-specific index
Read /claude/memory/index/task-<N>-index.json (if exists)

# 3. Use index to read selectively
Read only sections specified in task index

# 4. Execute task using memory + selective reading

# 5. Update memory system
Create episodic note, update indexes
```

---

## Documentation Reference

### Core Documentation (Read First)

1. **`/claude/CLAUDE.md`** (this file) - System architecture and usage
2. **`/claude/prompt/0.overview.md`** - Global rules and conventions
3. **`/claude/prompt/0.memory.md`** - Memory system policies
4. **`/claude/memory/DESIGN.md`** - Memory system design (read if working on memory)

### Memory System Documentation

1. **`/claude/memory/DESIGN.md`** (942 lines) - Complete memory system design
2. **`/claude/memory/procedural/memory-system-operations.md`** (646 lines) - Operational procedures
3. **`/claude/MEMORY_SYSTEM_REPORT.md`** (425 lines) - Implementation report
4. **`/claude/memory/index/enhanced-*.json`** - Metadata, hierarchies, gap analysis

### Quick Reference

- **Find knowledge**: `/claude/memory/index/tags.json`, `topics.json`
- **Stable facts**: `/claude/memory/semantic/*.md`
- **Task history**: `/claude/memory/episodic/*.md`
- **Workflows**: `/claude/memory/procedural/*.md`
- **Task optimization**: `/claude/memory/index/task-<N>-index.json`

---

## Summary: The Memory-First Principle

### Core Philosophy

**❌ Old Approach** (Context-Intensive):
```
Session Start → Read all prompts → Read all outputs → Answer questions
Context Used: 10,000+ lines
```

**✅ New Approach** (Memory-Driven):
```
Session Start → Read indexes → Query memory → Read selectively → Answer questions
Context Used: 500-1200 lines (80-95% reduction)
```

### Key Rules

1. **Memory First**: Always query memory indexes before reading prompts or outputs
2. **On-Demand Tasks**: Only read task prompts when user explicitly requests a task
3. **Selective Reading**: Use task-specific indexes to read only needed sections
4. **Update Memory**: After tasks, create episodic notes and update indexes
5. **Avoid Waste**: Never read files "just to see what's there"

---

## 🔄 Incremental Work Principle

### The Problem: Context Overflow in Large Tasks

**Challenge**: Large tasks can easily exceed context window limits and lose continuity if interrupted.

**Example**:
```
❌ Monolithic Approach:
Task: "Create 40-slide presentation based on all previous work"
Agent: Reads 9100 lines (all Task 1+2 outputs)
       Writes 2000-line presentation in one go
       Updates memory at end

Problems:
- Context window overflow (11,000+ lines)
- Work lost if interrupted mid-task
- No progress tracking
- All-or-nothing execution
```

### The Solution: Incremental Steps with Memory Checkpoints

**Core Pattern**: Break large tasks into small incremental steps, updating memory after EACH step.

```
┌─────────────────────────────────────────┐
│ Step N: Do Work                         │
│ • Read memory (resume from checkpoint)  │
│ • Execute ONE incremental unit          │
│ • Keep context <2000 lines              │
│ • Create/update notes IMMEDIATELY       │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│ Memory Checkpoint                        │
│ • Episodic note records progress        │
│ • Semantic notes capture insights       │
│ • Indexes updated                       │
│ • "Next Steps" documented               │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│ Step N+1: Continue                      │
│ • Read memory (load next context)       │
│ • Next incremental unit                 │
│ ...                                     │
└─────────────────────────────────────────┘
```

### When to Use Incremental Approach

**MANDATORY for tasks that**:
- ✅ Require reading >5000 lines of context
- ✅ Involve analyzing >10 files
- ✅ Produce outputs >1000 lines
- ✅ Have multiple distinct phases

**Optional for smaller tasks** (<5000 lines total context)

### Incremental Approach Example: Task 3 Presentation

**Better Approach** (Incremental with checkpoints):
```
✅ Step 1: Read task-specific index (800 lines) → Checkpoint
✅ Step 2: Write slides 1-10 (Intro + Architecture) → Checkpoint
✅ Step 3: Write slides 11-20 (EOG mechanism) → Checkpoint
✅ Step 4: Write slides 21-30 (Query API) → Checkpoint
✅ Step 5: Write slides 31-40 (Integration) → Checkpoint
✅ Step 6: Create narration script → Checkpoint

Benefits:
- Each step: <1000 lines context (vs 11,000 monolithic)
- Work resumable from any checkpoint
- Progress visible at each step
- Memory maintains continuity
```

### Key Operating Rules

#### Context Budget Per Step
- **Single step context**: <2000 lines (hard limit)
- **Memory notes read**: <1000 lines
- **New code/docs read**: <1000 lines
- **Output produced**: <500 lines per step

#### Checkpoint Requirements
Every checkpoint MUST include in episodic note:
- ✅ **Completed Steps**: Mark current step as ✅ complete
- ✅ **Next Steps**: Define what comes next ([ ] pending items)
- ✅ **Context for Next Step**: What memory/files needed for next step
- ✅ **Progress Tracking**: Timestamp, context used, outputs produced
- ✅ **Resumability Test**: "Can I resume from this if interrupted now?"

#### Task Decomposition Strategies

**By File Groups** (for large codebase analysis):
- Analyze 5-10 related files per step
- Update memory after each group

**By Sections** (for long documentation):
- Write one section (200-400 lines) per step
- Checkpoint after each section complete

**By Concepts** (for multi-concept analysis):
- Focus on one concept per step
- Create semantic note per concept

**By Phases** (for multi-phase tasks):
- Research → Documentation → Review
- Checkpoint between phases

### Real-World Performance

**Task 3 Execution** (with incremental approach):

| Step | Activity | Context Used | Cumulative |
|------|----------|--------------|------------|
| 1 | Read task-3-index.json | 800 lines | 800 |
| 2 | Write slides 1-10 | 400 lines | 1200 |
| 3 | Write slides 11-20 | 300 lines | 1500 |
| 4 | Write slides 21-30 | 350 lines | 1850 |
| 5 | Write slides 31-40 | 350 lines | 2200 |
| 6 | Create narration | 200 lines | 2400 |

**Total**: 2400 lines (incremental) vs 11,000 lines (monolithic)
**Savings**: 78% context reduction
**Resumability**: 6 checkpoints (can resume from any)

### Benefits Summary

✅ **Prevents context overflow**: Each step stays within safe limits (<2000 lines)
✅ **Enables resumability**: Work can be interrupted and resumed from any checkpoint
✅ **Tracks progress**: User sees completion percentage at each step
✅ **Reduces risk**: Only current step affected by errors, not entire task
✅ **Maintains continuity**: Memory system connects steps seamlessly
✅ **Improves efficiency**: No need to re-read entire context for each step

### Quick Decision Guide

```
Question: Is my task >5000 lines total context?
├─ NO → Execute normally (single phase, update memory at end)
└─ YES → Use incremental approach:
         1. Decompose into steps (<2000 lines each)
         2. Create episodic note with step plan
         3. Execute: Step → Checkpoint → Step → Checkpoint → ...
         4. Verify: Each checkpoint has "Next Steps" defined
```

**📖 Detailed Procedures**: See `/claude/memory/procedural/incremental-work-workflow.md` (proc-004) for complete operating procedures, decomposition strategies, checkpoint formats, and real-world examples.

---

## Ready to Start?

**Next Steps**:
1. ✅ You've read CLAUDE.md
2. 📖 Read `/claude/prompt/0.overview.md` for global rules
3. 📖 Read `/claude/prompt/0.memory.md` for memory policies
4. 📖 Read `/claude/memory/index/tags.json` and `topics.json`
5. 📖 Read last 1-2 episodic notes in `/claude/memory/episodic/`
6. 🎯 Wait for user to request a specific task or ask a question

**Do NOT**:
- ❌ Read task prompts (1.java-cpg.md, 2.constant-eval..., 3.source-example.md) yet
- ❌ Read task outputs (/claude/result/1/, /claude/result/2/) yet
- ❌ "Browse" the directory structure

**The memory system will tell you what exists. Use it.**

---

**Version**: 2.0.0
**Last Updated**: 2025-10-28
**System Status**: ✅ Production-Ready
