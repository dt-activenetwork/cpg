---
id: proc-002
title: Memory System Operations and Maintenance Workflow
type: procedural
tags: [workflow, memory-system, knowledge-management, meta]
created: 2025-10-28
updated: 2025-10-28
domain: knowledge-management
verb: operate-and-maintain
related: [proc-001]
links:
  - /claude/memory/DESIGN.md
  - /claude/prompt/0.memory.md
---

# Memory System Operations and Maintenance Workflow

## Purpose

This workflow documents the complete operational procedures for the CPG Memory System, including:
- Creating and updating memory notes
- Maintaining indexes
- Building task-specific indexes
- Performing hygiene cycles
- Troubleshooting common issues

This ensures consistent, high-quality knowledge management across all sessions and tasks.

---

## Prerequisites

- Familiarity with memory system structure (/claude/memory/)
- Understanding of note types (semantic, episodic, procedural)
- Access to index files (tags.json, topics.json)
- Knowledge of YAML frontmatter format

---

## Core Operations

### Operation 1: Creating a Semantic Note

**When to use**: Discovered stable, reusable knowledge about codebase architecture or concepts.

**Steps**:

1. **Check for existing note**
   ```bash
   # Search in tags.json or topics.json
   grep -i "<keyword>" /claude/memory/index/tags.json
   ```
   - If note exists → **Update it** (see Operation 4)
   - If not → Continue to step 2

2. **Determine note ID**
   ```bash
   # Find highest existing sem-NNN
   ls /claude/memory/semantic/ | grep "^sem-" | sort | tail -1
   # Increment: if last is sem-004, use sem-005
   ```

3. **Create note file**
   - Path: `/claude/memory/semantic/<topic>-<slug>.md`
   - Use descriptive slug: `query-api-dsl`, not `api-thing`

4. **Write frontmatter**
   ```markdown
   ---
   id: sem-NNN
   title: <Concept Name>
   type: semantic
   tags: [tag1, tag2, tag3]
   created: YYYY-MM-DD
   updated: YYYY-MM-DD
   source: <Where knowledge came from>
   related: [sem-XXX, ep-YYY]
   ---
   ```
   - **tags**: 3-5 tags, use existing tags from tags.json when possible
   - **source**: E.g., "Task 2 analysis", "ValueEvaluator.kt:20-150"
   - **related**: IDs of related notes (check index for relevant topics)

5. **Write body**
   - Follow template in DESIGN.md (Appendix: File Templates)
   - Include "## Why now" section
   - Provide code evidence with file:line references
   - One core idea per note

6. **Update global index - tags.json**
   ```json
   {
     "tags": {
       "tag1": ["sem-001", "sem-002", "sem-NNN"],  // Add sem-NNN
       "tag2": ["sem-NNN"],  // Add sem-NNN
       ...
     },
     "updated": "2025-10-28"  // Update date
   }
   ```

7. **Update global index - topics.json**
   ```json
   {
     "topics": {
       "Topic Name": {
         "description": "...",
         "notes": ["sem-001", "sem-NNN"]  // Add sem-NNN
       }
     },
     "updated": "2025-10-28"  // Update date
   }
   ```

8. **Add backlinks to related notes**
   - Open each note referenced in "related" field
   - Add `sem-NNN` to their "related" field
   - Increment their "updated" date

**Validation checks**:
- [ ] Frontmatter complete and valid YAML
- [ ] Note ID added to tags.json for each tag
- [ ] Note ID added to relevant topics in topics.json
- [ ] Backlinks added to related notes
- [ ] Code evidence includes file paths and line numbers

**Common pitfalls**:
- **Mixing concepts**: If note covers multiple unrelated concepts, split into separate notes
- **Missing evidence**: Always link to source code or primary artifacts
- **Forgetting indexes**: Must update indexes immediately, not batched later

---

### Operation 2: Creating an Episodic Note

**When to use**: Completed a task or major analysis phase.

**Steps**:

1. **Determine note ID and filename**
   - ID: Next ep-NNN (check existing episodic notes)
   - Filename: `YYYYMMDD-t<N>-<slug>.md`
   - Example: `20251028-t2-constant-eval-analysis.md`

2. **Create note file**
   - Path: `/claude/memory/episodic/<filename>`

3. **Write frontmatter**
   ```markdown
   ---
   id: ep-NNN
   title: Task N - <Task Name>
   type: episodic
   date: YYYY-MM-DD
   task: <task-filename.md>
   tags: [task-completion, ...]
   links:
     - /claude/result/N/output-file.md
     - /code/path/analyzed-file.kt:123-456
   related: [sem-XXX, proc-YYY]
   ---
   ```
   - **links**: ALL output files and key source code analyzed
   - **related**: Semantic notes created/updated, procedural notes used

4. **Write body**
   - Follow template in DESIGN.md
   - Sections: Goal, Approach, Steps Taken, Key Findings, Outputs, Semantic Notes Created/Updated, Next Steps

5. **Update global index**
   - Add ep-NNN to tags.json (task-completion, documentation, domain-specific tags)
   - Add ep-NNN to topics.json ("Task Sessions", related topics)

6. **Link from semantic notes**
   - For each semantic note created/updated during task, add backlink to ep-NNN in its "related" field

**Validation checks**:
- [ ] All output files linked
- [ ] Semantic notes created/updated listed
- [ ] Next steps or open questions documented
- [ ] Indexes updated

**Common pitfalls**:
- **Creating too late**: Create episodic note immediately after task completion, not days later
- **Missing outputs**: Forgetting to link some output files
- **No next steps**: Always document what remains to be done

---

### Operation 3: Creating a Procedural Note

**When to use**: Identified a repeating workflow (used 2+ times).

**Steps**:

1. **Determine note ID**
   - Next proc-NNN

2. **Create note file**
   - Path: `/claude/memory/procedural/<domain>-<verb>-<slug>.md`
   - Example: `memory-system-operations.md`

3. **Write frontmatter**
   ```markdown
   ---
   id: proc-NNN
   title: Workflow for <Activity>
   type: procedural
   tags: [workflow, domain, ...]
   created: YYYY-MM-DD
   updated: YYYY-MM-DD
   domain: <e.g., knowledge-management>
   verb: <e.g., operate-and-maintain>
   related: [proc-XXX, ep-YYY]
   ---
   ```
   - **domain**: Area of activity (code-analysis, documentation, knowledge-management)
   - **verb**: Action performed (analyze-and-document, operate-and-maintain)

4. **Write body**
   - Follow template in DESIGN.md
   - Sections: Purpose, Prerequisites, Steps, Checks, Common Pitfalls, Examples

5. **Update global index**
   - Add proc-NNN to tags.json (workflow, domain tag)
   - Add proc-NNN to topics.json (relevant topic, e.g., "Analysis Workflows")

6. **Link from episodic notes**
   - For each task where workflow was used, add backlink to proc-NNN

**Validation checks**:
- [ ] Steps are actionable and ordered
- [ ] Prerequisites listed
- [ ] Common pitfalls documented
- [ ] Examples linked (episodic notes)

**Common pitfalls**:
- **Too abstract**: Steps must be concrete and actionable
- **Missing validation**: Always include checks to verify correct execution
- **No examples**: Link to episodic notes showing workflow in action

---

### Operation 4: Updating an Existing Note

**When to use**: Code changed, user corrected understanding, or discovered additional information.

**Steps**:

1. **Read existing note**
   - Understand current content

2. **Make updates**
   - Add new information
   - Correct mistakes
   - Update code references if code changed

3. **Update frontmatter**
   - Increment "updated" date: `updated: YYYY-MM-DD`
   - Add new tags if applicable
   - Add new related notes if discovered

4. **Document change in body** (optional but recommended)
   - Add note at relevant section: `**Updated YYYY-MM-DD**: <what changed>`

5. **Update indexes if new tags/topics added**
   - Add note ID to new tags in tags.json
   - Add note ID to new topics in topics.json

6. **Update cross-references if related notes changed**

**Validation checks**:
- [ ] "updated" date incremented
- [ ] New tags added to indexes
- [ ] Change documented in body (if significant)

**Common pitfalls**:
- **Creating duplicate instead of updating**: Always check if note exists first
- **Not documenting change**: For significant updates, note what changed and why

---

### Operation 5: Building a Task-Specific Index

**When to use**: Starting a presentation/teaching task with large existing documentation (>3000 lines).

**Steps**:

1. **Analyze task prompt**
   - Read `/claude/prompt/<N>-*.md`
   - Extract:
     - Task type (presentation, analysis, documentation, implementation)
     - Key deliverables (slides, docs, code)
     - Required concepts (keywords, technical terms)
     - Target audience (engineers, managers, researchers)

2. **Query global index**
   ```bash
   # Find relevant tags
   grep -i "<concept>" /claude/memory/index/tags.json

   # Find relevant topics
   grep -i "<concept>" /claude/memory/index/topics.json
   ```
   - Collect relevant note IDs

3. **Read note metadata**
   - For each relevant note, read just the frontmatter and first few lines
   - Determine if note is critical, optional, or reference-only

4. **Identify specific sections**
   - Open each note and identify exact sections or line ranges needed
   - Example: "lines 20-80, 120-180" instead of "entire file"

5. **Classify priority**
   - **critical**: Must read to complete task
   - **optional**: Nice to have, adds depth
   - **reference_only**: Cite in output, don't need to read

6. **Map to deliverables**
   - For each section of deliverable (e.g., slides 1-4), identify which knowledge is required

7. **Estimate context savings**
   ```
   Total lines without index: <sum of all Task 1+2 outputs>
   Total lines with index: <sum of critical + optional sections>
   Reduction: ((Total_without - Total_with) / Total_without) * 100%
   ```

8. **Create index file**
   - Path: `/claude/memory/index/task-<N>-index.json`
   - Follow schema in DESIGN.md (Section: Index System → Task-Specific Index)

9. **Use during task**
   - Read only sections specified in index
   - Follow reading order
   - Update index if discover need for additional sections

10. **Post-task: Document effectiveness**
    - In episodic note, document:
      - Context savings achieved
      - Whether index was accurate
      - Sections that were missing or unnecessary

**Validation checks**:
- [ ] Estimated context savings >= 70%
- [ ] All critical knowledge areas identified
- [ ] Specific line ranges or section names (not "entire file")
- [ ] Reading order logical (foundational concepts first)

**Common pitfalls**:
- **Too broad**: Indexing entire files defeats the purpose
- **Too narrow**: Missing critical sections causes task failure
- **No reading order**: Reading out of order wastes time

---

### Operation 6: Maintaining Indexes

**When to use**: After creating/updating any note, and during hygiene cycles (every 3-5 tasks).

**Steps**:

1. **After each note creation/update**
   - Immediately update tags.json: add note ID to each tag
   - Immediately update topics.json: add note ID to relevant topics
   - Update "updated" date in both index files

2. **During hygiene cycle (every 3-5 tasks)**

   **2a. Validate index consistency**
   ```bash
   # Check that all note IDs in indexes actually exist
   for id in $(jq -r '.tags | .[] | .[]' tags.json | sort -u); do
       if ! grep -r "^id: $id" /claude/memory/{semantic,episodic,procedural}/; then
           echo "Orphaned ID: $id"
       fi
   done
   ```

   **2b. Check for missing notes in index**
   ```bash
   # Check that all notes have at least one tag
   for note in /claude/memory/{semantic,episodic,procedural}/*.md; do
       id=$(grep "^id:" "$note" | cut -d: -f2 | tr -d ' ')
       if ! grep -q "$id" /claude/memory/index/tags.json; then
           echo "Missing from index: $id"
       fi
   done
   ```

   **2c. Normalize tag names**
   - Check for synonyms: "query-api" vs "queryapi" vs "query_api"
   - Standardize to lowercase, hyphen-separated
   - Merge duplicate tags

   **2d. Update enhanced indexes**
   - Add new tags to enhanced-tags.json with descriptions, categories
   - Add new topics to enhanced-topics.json with descriptions, coverage
   - Update coverage stats (comprehensive, partial, minimal, none)
   - Review coverage_gaps: close gaps for completed notes

3. **Archive old task-specific indexes**
   ```bash
   # After Task N+3 completes, archive task N index
   mv /claude/memory/index/task-N-index.json \
      /claude/memory/index/archive/
   ```

**Validation checks**:
- [ ] All note IDs in indexes exist as actual notes
- [ ] All notes appear in at least one tag
- [ ] All notes appear in at least one topic
- [ ] No duplicate or synonym tags
- [ ] Enhanced indexes updated with new notes
- [ ] Old task indexes archived (keep last 2-3 only)

**Common pitfalls**:
- **Batching index updates**: Update immediately after note creation, not later
- **Ignoring orphans**: Clean up orphaned note IDs immediately
- **Not archiving**: Keep only recent task indexes, archive old ones

---

### Operation 7: Memory Hygiene Cycle

**When to use**: Every 3-5 tasks, or when memory feels fragmented.

**Steps**:

1. **Review recent episodic notes**
   ```bash
   ls -lt /claude/memory/episodic/ | head -5
   ```
   - Read last 3-5 episodic notes
   - Identify common patterns or insights

2. **Extract stable insights to semantic notes**
   - If same concept appears in 2+ episodic notes → create semantic note
   - If episodic note contains architectural insight → extract to semantic note

3. **Check for duplicate semantic notes**
   ```bash
   # List all semantic notes
   ls /claude/memory/semantic/
   ```
   - Look for notes covering same concept
   - If duplicates found → merge into one note
   - Update indexes to point to merged note

4. **Check for multi-concept notes**
   - Read each semantic note
   - If note mixes 2+ unrelated concepts → split into separate notes
   - Example: "CPG Architecture and Query API" → split into two notes

5. **Verify cross-references**
   ```bash
   # For each note, check that related notes exist
   for note in /claude/memory/{semantic,episodic,procedural}/*.md; do
       grep "^related:" "$note" | grep -oE "(sem|ep|proc)-[0-9]+" | while read rel_id; do
           if ! grep -rq "^id: $rel_id" /claude/memory/; then
               echo "Broken reference in $note: $rel_id"
           fi
       done
   done
   ```
   - Fix or remove broken cross-references

6. **Check for outdated notes**
   - For each semantic note, check "updated" date
   - If note references code, verify code hasn't changed
   - If code changed → update note with new information

7. **Normalize tags across notes**
   - Check for inconsistent tag usage
   - Example: some notes use "constant-evaluation", others use "const-eval"
   - Standardize to one canonical form

8. **Update enhanced indexes**
   - Refresh coverage stats
   - Update gap analysis based on new notes
   - Update recommended_next_notes based on patterns discovered

9. **Archive old artifacts**
   - Move old task-specific indexes to archive/
   - Consider archiving very old episodic notes (>1 year) if not referenced

10. **Document hygiene cycle in episodic note** (optional)
    - Create ep-XXX documenting what was cleaned up
    - Record metrics: notes merged, duplicates removed, gaps closed

**Validation checks**:
- [ ] No duplicate semantic notes
- [ ] No multi-concept notes (each note has one core idea)
- [ ] All cross-references valid
- [ ] Tags normalized
- [ ] Outdated notes updated or archived
- [ ] Enhanced indexes reflect current state

**Common pitfalls**:
- **Skipping hygiene**: Memory fragmentation accumulates over time
- **Over-merging**: Don't merge notes if concepts are distinct
- **Deleting history**: Archive, don't delete, episodic notes (they're historical records)

---

## Troubleshooting

### Issue 1: Can't Find Note Through Index

**Symptoms**:
- Created note but search in index returns nothing

**Diagnosis**:
```bash
# Check if note exists
ls /claude/memory/{semantic,episodic,procedural}/ | grep <note-id>

# Check if note is in indexes
grep <note-id> /claude/memory/index/tags.json
grep <note-id> /claude/memory/index/topics.json
```

**Solution**:
- If note exists but not in index → Run Operation 6 (Maintaining Indexes, step 1)
- If note doesn't exist → Recreate note or update index to remove orphaned ID

---

### Issue 2: Duplicate Semantic Notes

**Symptoms**:
- Two notes cover same concept (e.g., both explain Query API)

**Diagnosis**:
```bash
# Search for keyword in all semantic notes
grep -i "<keyword>" /claude/memory/semantic/*.md
```

**Solution**:
1. Read both notes
2. Determine which is more comprehensive
3. Merge content into better note
4. Update "updated" date and document merge in body
5. Delete duplicate note file
6. Update indexes: remove duplicate ID, keep merged ID
7. Update cross-references in other notes

---

### Issue 3: Task-Specific Index Ineffective

**Symptoms**:
- Task index still required reading 3000+ lines (context reduction <70%)

**Diagnosis**:
- Review task index file
- Check if sections are too broad ("entire file" vs specific line ranges)
- Check if too many sections marked as "critical"

**Solution**:
1. Re-analyze task deliverables
2. Identify minimum knowledge needed
3. Narrow sections to specific line ranges
4. Move non-essential sections to "optional" or "reference_only"
5. Update index file
6. Re-estimate context usage

---

### Issue 4: Cross-Reference Broken

**Symptoms**:
- Note references sem-XXX but that note doesn't exist

**Diagnosis**:
```bash
# Search for referenced note
grep -r "^id: sem-XXX" /claude/memory/
```

**Solution**:
- If note renamed → Update cross-reference with new ID
- If note deleted → Remove cross-reference or replace with correct ID
- Update indexes to remove orphaned references

---

### Issue 5: Index Growing Too Large

**Symptoms**:
- tags.json or topics.json exceeds 1000 lines
- Too many tags (>100) or topics (>50)

**Diagnosis**:
- Review tag/topic usage: `jq '.tags | to_entries | map({key: .key, count: (.value | length)}) | sort_by(.count) | reverse' tags.json`

**Solution**:
1. **Merge similar tags**: "query" + "query-api" → "query-api"
2. **Create tag hierarchy**: Move subtags under main tags
3. **Archive stale tags**: If tag has 0 notes, remove it
4. **Split large topics**: If topic has >10 notes, split into subtopics
5. **Use enhanced indexes**: Move detailed metadata to enhanced-*.json

---

## Performance Metrics

Track these metrics to measure memory system effectiveness:

| Metric | Target | Current (2025-10-28) |
|--------|--------|----------------------|
| Total notes | -- | 8 |
| Semantic notes | -- | 4 |
| Episodic notes | -- | 3 |
| Procedural notes | -- | 1 |
| Tags | < 100 | 26 |
| Topics | < 50 | 11 |
| Coverage gaps | → 0 | 5 |
| Context reduction (avg) | > 70% | 91% (Task 3) |
| Index lookup time | < 1 sec | < 1 sec |
| Note quality (checklist pass) | 100% | 100% |

**How to measure**:
- **Context reduction**: `((lines_without_index - lines_with_index) / lines_without_index) * 100`
- **Note quality**: Run through quality checklist in DESIGN.md for each note

---

## Related Documentation

- **System Design**: `/claude/memory/DESIGN.md` - Complete architecture and design patterns
- **Policy Configuration**: `/claude/prompt/0.memory.md` - When to read/write memory
- **Index Schemas**: `/claude/memory/index/enhanced-*.json` - Detailed metadata
- **Frontend Analysis Workflow**: `/claude/memory/procedural/cpg-frontend-analysis-workflow.md` (proc-001)

---

## Version History

- **v1.0** (2025-10-28): Initial version documenting all core operations and workflows discovered during Task 3 preparation

---

**End of Memory System Operations Workflow**
