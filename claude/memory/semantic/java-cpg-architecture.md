---
id: sem-001
title: Java CPG Frontend Architecture
type: semantic
tags: [java, cpg, architecture, fraunhofer-aisec]
created: 2025-10-27
updated: 2025-10-27
source: cpg-language-java module analysis
related: [sem-002, sem-003]
---

# Java CPG Frontend Architecture

## Why now
This note captures the fundamental architecture of the Fraunhofer AISEC CPG Java frontend, which serves as the foundation for understanding how Java source code is transformed into a Code Property Graph. This knowledge is essential for maintaining, extending, and debugging the Java frontend.

## Core Concept

The Java CPG frontend is a **modular, multi-stage pipeline** that transforms Java source code into a Code Property Graph (CPG) using the following architecture:

```
Java Source → JavaParser AST → Handler Pipeline → CPG Nodes → Pass Pipeline → Enriched CPG
```

## Key Components

### 1. Frontend Entry Point
- **File**: `JavaLanguageFrontend.kt`
- **Location**: `cpg-language-java/src/main/kotlin/de/fraunhofer/aisec/cpg/frontends/java/JavaLanguageFrontend.kt`
- **Lines**: 79-563
- **Role**: Coordinates the entire parsing process, initializes handlers, manages symbol resolution

### 2. Language Definition
- **File**: `JavaLanguage.kt`
- **Location**: `cpg-language-java/src/main/kotlin/de/fraunhofer/aisec/cpg/frontends/java/JavaLanguage.kt`
- **Lines**: 44-150
- **Role**: Defines Java language metadata, built-in types, operators, and language features

### 3. Handler Triad
Three handlers convert JavaParser AST to CPG nodes:

- **DeclarationHandler** (517 lines): Classes, methods, fields, constructors
- **StatementHandler** (619 lines): Control flow, loops, exception handling
- **ExpressionHandler** (689 lines): Method calls, field access, operators, literals

### 4. Pass Pipeline
Three Java-specific passes enhance the CPG:

1. **JavaImportResolver**: Maps import statements to declarations
2. **JavaExternalTypeHierarchyResolver**: Resolves external type hierarchies (e.g., JDK classes)
3. **JavaExtraPass**: Transforms static field access patterns

## Design Patterns

### Handler Pattern
- **Purpose**: Separate concerns by syntax category
- **Benefit**: Each handler focuses on one aspect (declarations, statements, or expressions)
- **Implementation**: Map-based dispatch from JavaParser AST nodes to handler methods

### Scope Stack Pattern
- **Purpose**: Manage nested scopes (namespaces, classes, methods, blocks)
- **Benefit**: Enables correct symbol resolution in nested contexts
- **Implementation**: `ScopeManager` maintains a stack, declarations are registered on entry

### Multi-Pass Analysis
- **Purpose**: Separate concerns and manage dependencies between analyses
- **Benefit**: Modular, composable, can be selectively executed
- **Implementation**: Pass dependencies declared via `@DependsOn` and `@ExecuteBefore` annotations

## Technology Stack

- **Parser**: JavaParser 3.24+ (https://javaparser.org/)
- **Build System**: Gradle (not Maven!)
- **Language**: Kotlin
- **CPG Core**: Provided by `cpg-core` module
- **Symbol Resolution**: JavaParser's `JavaSymbolSolver` with `ReflectionTypeSolver` and `JavaParserTypeSolver`

## Critical Paths

### Parsing Pipeline
1. `JavaLanguageFrontend.parse(file)` (line 106)
2. JavaParser creates `CompilationUnit` AST (line 119)
3. Create `TranslationUnitDeclaration` root (line 119)
4. Process package → nested `NamespaceDeclaration` (lines 125-145)
5. Process types → `RecordDeclaration` via `DeclarationHandler` (lines 145-153)
6. Process members → methods, fields, constructors (DeclarationHandler.kt:328-384)

### Symbol Resolution Setup
- Initialize `ReflectionTypeSolver` for JDK classes (line 546)
- Initialize `JavaParserTypeSolver` for project sources (line 558)
- Combine into `CombinedTypeSolver` (line 562)
- Create `JavaSymbolSolver` (line 562)

## Evidence
All paths and line numbers verified in source code as of commit 04680b1.

## Cross-References
- **Related**: sem-002 (Handler Pattern Implementation)
- **Related**: sem-003 (Pass System Architecture)
- **Source Code**: `/home/user/cpg/cpg-language-java/`
- **Documentation**: `/home/user/cpg/claude/result/1/1.overview-java-frontend.md`
