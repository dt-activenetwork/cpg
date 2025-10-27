---
id: proc-001
title: Workflow for Analyzing and Documenting CPG Language Frontends
type: procedural
tags: [workflow, cpg, frontend-analysis, documentation]
created: 2025-10-27
updated: 2025-10-27
domain: code-analysis
verb: analyze-and-document
related: [sem-001, ep-001]
---

# Workflow: Analyzing and Documenting CPG Language Frontends

## Prerequisites
- Access to CPG repository
- Understanding of CPG concepts (nodes, edges, passes)
- Target language frontend module identified
- Output directory structure prepared

## Overview
This workflow guides the analysis and documentation of a CPG language frontend. It ensures systematic exploration, accurate code references, and comprehensive documentation.

## Steps

### 1. Initial Discovery (30 minutes)

#### 1.1 Identify Repository Structure
- [ ] Check build system (Gradle vs Maven)
- [ ] Locate language frontend module directory
- [ ] Read module's `build.gradle.kts` or `pom.xml`
- [ ] Identify external parser dependencies

**Example commands**:
```bash
ls -la                          # List top-level structure
cat build.gradle.kts            # Check Gradle configuration
find . -name "*Frontend*.kt"    # Find frontend classes
```

#### 1.2 Read Project README
- [ ] Understand project purpose and scope
- [ ] Note supported languages and their maturity
- [ ] Identify key documentation and specifications

**Check for**:
- CPG specifications (DFG, EOG, Graph Model)
- Language support table
- Build and configuration instructions

#### 1.3 Identify Frontend Components
- [ ] Find language definition class (`*Language.kt`)
- [ ] Find frontend class (`*LanguageFrontend.kt`)
- [ ] List handler classes (if any)
- [ ] List pass classes in `passes/` directory

**Glob patterns**:
```
**/*Language.kt
**/*Frontend.kt
**/*Handler.kt
**/passes/*.kt
```

### 2. Deep Code Exploration (60-90 minutes)

#### 2.1 Launch Explore Agent
Use the Explore agent for systematic analysis:

**Agent parameters**:
- `subagent_type`: "Explore"
- `thoroughness`: "very thorough"
- `prompt`: Detailed task description covering:
  - Entry points and initialization
  - Parsing pipeline
  - Node and edge construction
  - Passes and overlays
  - Code examples with file:line references

**Key search terms**:
- Entry: `parse()`, `Language`, `Frontend`
- Nodes: `newMethodDeclaration`, `newVariableDeclaration`, `newCallExpression`
- Edges: `addContainsEdge`, `prevDFG`, `nextEOG`
- Passes: `@RegisterExtraPass`, `ComponentPass`, `TranslationUnitPass`

#### 2.2 Trace Key Flows
For each major flow, collect:
- [ ] Entry method signature and location
- [ ] Key method calls with locations
- [ ] CPG node creation calls
- [ ] Scope management operations
- [ ] Error handling patterns

**Flows to trace**:
1. Parse entry → AST → CPG root
2. Type declaration → CPG record
3. Method declaration → CPG method (with parameters, body)
4. Expression → CPG expression nodes
5. Pass execution order and dependencies

#### 2.3 Document Code References
For EVERY significant claim, record:
- [ ] File path (absolute)
- [ ] Line number or range
- [ ] Code snippet (if complex)
- [ ] Purpose and context

**Format**: `file_path:line_number`

### 3. Documentation Generation (90-120 minutes)

#### 3.1 Overview Document
**Purpose**: High-level architectural understanding

**Structure**:
1. **Background**: What is CPG? What is this frontend?
2. **Module structure**: Directory layout, dependencies
3. **Core components**: Frontend, Language, Handlers, Passes
4. **Pipeline**: Complete flow with diagrams
5. **Design decisions**: Why specific patterns were chosen
6. **Glossary**: Terms and translations

**Diagrams to include** (Mermaid):
- Architecture overview (components and relationships)
- Parsing pipeline (source → AST → CPG → enriched CPG)
- Module interaction (if multi-module)

**Guidelines**:
- Start with concepts, then dive into specifics
- Use diagrams liberally
- Explain "why" not just "what"
- Target beginners but include depth

#### 3.2 Implementation Trace Document
**Purpose**: Code-level walkthrough with exact references

**Structure**:
1. **Frontend registration**: Language class, annotations
2. **Parse entry**: Setup, configuration, invocation
3. **Declaration processing**: Classes, methods, fields with code
4. **Statement processing**: Control flow examples with code
5. **Expression processing**: Calls, operators with code
6. **Pass implementation**: Each pass with code
7. **Complete example**: End-to-end trace of a real code sample

**Guidelines**:
- ALWAYS include file:line references
- Quote code blocks for critical sections
- Trace execution step-by-step
- Show input code → CPG structure transformations

#### 3.3 Practical Guide Document
**Purpose**: Actionable guidance for implementation/extension

**Structure**:
1. **Decision tree**: Help readers choose approach
2. **Architecture choices**: Parser selection, integration mode
3. **Minimal implementation**: Working frontend in minimal code
4. **Complete checklist**: Every feature to implement
5. **Extension guide**: Adding syntax, passes, customizations
6. **Troubleshooting**: Common problems and fixes
7. **Testing**: Strategies and templates

**Guidelines**:
- Provide code templates ready to use
- Include checklists with checkboxes
- Show concrete examples for common tasks
- Focus on practical, actionable advice

### 4. Memory System Population (30 minutes)

#### 4.1 Create Semantic Notes
For stable, reusable knowledge:

**Topics to capture**:
- [ ] Frontend architecture overview
- [ ] Key design patterns used
- [ ] Pass system mechanics
- [ ] Parser integration approach

**Format**:
- YAML front matter with metadata
- "Why now" motivation (1-2 sentences)
- Core concept explanation
- Key evidence and references
- Cross-references to related notes

#### 4.2 Create Episodic Note
For this analysis session:

**Contents**:
- [ ] Goal and task description
- [ ] Steps taken with findings
- [ ] Observations and insights
- [ ] Deliverables produced
- [ ] Challenges encountered
- [ ] Next steps

**Format**: Chronological with sections for each phase

#### 4.3 Update Index Files
If using index files:
- [ ] Update `tags.json` with new tags
- [ ] Update `topics.json` with new topics
- [ ] Add cross-links between notes

### 5. Quality Checks (15 minutes)

#### 5.1 Verify All Code References
- [ ] Spot-check 10+ file:line references
- [ ] Ensure file paths are absolute or clearly relative
- [ ] Confirm line numbers are accurate

**Method**: Use Read tool on random samples

#### 5.2 Check Documentation Completeness
- [ ] All required diagrams present
- [ ] Code examples compile/make sense
- [ ] Glossary terms consistent
- [ ] Cross-references valid

#### 5.3 Language and Style
- [ ] User-facing content in target language (Chinese for this project)
- [ ] Technical terms preserved in English
- [ ] Consistent terminology throughout
- [ ] Beginner-friendly tone

### 6. Finalization (15 minutes)

#### 6.1 Organize Output Files
- [ ] Files in correct directory (`/claude/out/<task_number>/`)
- [ ] Naming follows convention
- [ ] File permissions correct

#### 6.2 Create Summary
For user visibility:
- [ ] List all deliverables with paths
- [ ] Highlight key findings
- [ ] Note any blockers or unresolved issues

## Checks and Guardrails

### During Exploration
⚠️ **Avoid**: Guessing behavior without code evidence
✅ **Do**: Quote code and note exact locations

⚠️ **Avoid**: Shallow breadth-only scanning
✅ **Do**: Deep-dive into key flows end-to-end

### During Documentation
⚠️ **Avoid**: Documenting without code references
✅ **Do**: Every claim backed by file:line

⚠️ **Avoid**: Copy-pasting raw code without context
✅ **Do**: Explain purpose and flow around code

⚠️ **Avoid**: Single massive document
✅ **Do**: Split by concern (overview, trace, guide)

## Pitfalls

1. **Assumption traps**: Always verify build system, dependencies, architecture
2. **Reference drift**: Code changes, keep notes dated and linked to commits
3. **Over-generalization**: Be specific to THIS frontend, note deviations from others
4. **Missing diagrams**: Visual aids are critical for architecture understanding

## Rollback

If analysis is incomplete or incorrect:
1. Identify gaps using checklist above
2. Re-run Explore agent with refined prompt
3. Read critical source files directly
4. Update documentation with corrections
5. Note learnings in episodic memory

## Examples

### Good Exploration Prompt
```
Analyze the Java frontend in cpg-language-java module. Conduct a VERY THOROUGH exploration:

1. Entry points: Find JavaLanguageFrontend class, parse() method, initialization
2. Parsing: Trace JavaParser usage, AST to CPG transformation
3. Handlers: Identify all handlers and their responsibilities
4. Nodes: Show how METHOD, CALL, FIELD nodes are created with code examples
5. Passes: List all passes, their dependencies, and purposes

For EACH point, provide exact file paths, line numbers, and code quotes.
```

### Good Code Reference
```kotlin
// JavaLanguageFrontend.kt:114-119
val parserConfiguration = ParserConfiguration()
parserConfiguration.setSymbolResolver(javaSymbolResolver)
val parser = JavaParser(parserConfiguration)
context = parse(file, parser)
```
**Explanation**: Configures JavaParser with symbol resolver, then parses file to CompilationUnit AST.

### Good Troubleshooting Entry
**Problem**: Type resolution fails for external classes

**Diagnosis**: Symbol resolver not configured with project root

**Fix** (JavaLanguageFrontend.kt:558-560):
```kotlin
val javaParserTypeSolver = JavaParserTypeSolver(root)
nativeTypeResolver.add(javaParserTypeSolver)
```

**Verification**: Check logs for "Source file root used for type solver: <path>"

## Related Procedures
- Code defect analysis workflow
- Pass implementation workflow
- Frontend testing workflow

## Links
- **Example output**: `/home/user/cpg/claude/out/1/`
- **Example memory**: `/home/user/cpg/claude/memory/episodic/20251027-t1-java-cpg-analysis.md`
