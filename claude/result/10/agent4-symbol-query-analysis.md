# Agent 4: Symbol Resolution and Query API Comprehensive Evaluation

**Date**: 2025-11-13  
**Focus**: Symbol resolution system architecture and Query API capability assessment for Java 11-17 features  
**Status**: In Progress

## Analysis Scope

This agent evaluates:
1. **Symbol Resolution System** (SymbolResolver, ScopeManager, ImportResolver)
2. **Query API Infrastructure** (Query, FlowQueries, QueryTree)
3. **Support for Java 11-17 features** across both systems
4. **Integration gaps** between symbol resolution and query capabilities

---

## 1. Symbol Resolution System Architecture

### 1.1 Core Components

**SymbolResolver.kt** (915 lines)
- Central pass for resolving references, fields, method calls, and constructors
- Depends on: TypeResolver, TypeHierarchyResolver, EvaluationOrderGraphPass, ImportResolver
- Key methods:
  - `handleReference()`: Resolves variable/field references using scope manager
  - `handleMemberExpression()`: Resolves field access and method calls
  - `handleCallExpression()`: Resolves method call targets
  - `handleConstructExpression()`: Resolves constructor calls
  - `handle()`: Central dispatcher

**ScopeManager.kt** (200+ lines)
- Maintains nested scope hierarchy during AST traversal
- Symbol storage: `SymbolMap = MutableMap<Symbol, MutableList<Declaration>>`
- Note: **Symbol is defined as `typealias Symbol = String` (Scope.kt:57)**
- Key features:
  - Multi-level scope stack (Global, Namespace, Record, Function, Block)
  - Wildcard import handling
  - Predefined lookup scope support
  - Symbol lookup with inheritance and shadowing

**ImportResolver.kt** (200+ lines)
- Resolves import declarations into scope symbols
- Builds import dependency graph for topological sorting
- Handles: single imports, wildcard imports, static imports
- Manages cyclic import detection

### 1.2 Scope Hierarchy

```
GlobalScope (root)
├── FileScope (per translation unit)
├── NamespaceScope (packages in Java, namespaces in C++)
├── RecordScope (class/struct/enum bodies)
│   ├── FunctionScope (method bodies)
│   │   └── LocalScope (nested blocks)
│   └── TemplateScope (template parameters)
```

**Key limitation**: No special scope type for pattern variables or switch expressions

### 1.3 Symbol Lookup Process

1. **Unqualified lookup**: Start in current scope, traverse upward to parent scopes
2. **Qualified lookup**: Start in specified namespace scope, stay within that scope
3. **Implicit receiver**: For languages like Java with implicit `this` access
4. **Wildcard imports**: Searched if no exact match found
5. **Predefined lookup scopes**: Language-specific scope override (e.g., `global` in Python)

**Critical observation**: No special handling for pattern variable scoping

---

## 2. Java 11-17 Feature Symbol Resolution Capability

### 2.1 Records (Java 14+)

**Current State**: PARTIAL support

**Parsing**:
- ✓ Records parsed as `RecordDeclaration` (extends `RecordDeclaration`, same as class/interface)
- ✓ Fields created for record components
- ✓ Canonical constructor auto-generated (IMPLICIT ONLY)

**Symbol Resolution**:
- ✓ Record class name resolved via standard symbol lookup
- ✓ Record component fields resolved as normal fields
- **PROBLEM**: No explicit node type for `RecordComponentDeclaration`
  - All components treated as regular `FieldDeclaration`
  - Cannot distinguish between explicit fields and component-derived fields
- **PROBLEM**: Canonical constructor parameters not properly resolved
  - Parameters not explicitly linked to record components
  - DFG missing for component initialization

**Impact**:
- Cannot query "what record components does Record X have?"
- Cannot trace flow from constructor parameter to component field
- Compact constructor semantics not modeled (line 3 of Task 9's cpg-defect-patterns.md)

**Evidence**:
- RecordDeclaration.kt: Lines 44-50 (no special component handling)
- No RecordComponentDeclaration class found

### 2.2 Pattern Matching (Java 16+)

**Current State**: NEAR-ZERO support (5% from Agent 3 evaluation)

**Missing Node Types**:
- ❌ PatternExpression (base class)
- ❌ TypePattern
- ❌ RecordPattern
- ❌ GuardedPattern
- ❌ PatternVariableDeclaration

**instanceof Patterns**:
- ✓ Basic instanceof parsed (legacy behavior)
- ❌ Pattern variables NOT created as declarations
- ❌ Pattern variable scope NOT tracked
- ❌ Variables cannot be resolved after pattern matching

**Switch Patterns** (Java 21+):
- ❌ No SwitchExpression vs SwitchStatement distinction
- ❌ Case labels with patterns not parsed
- ❌ Pattern variable scoping completely missing
- ❌ Exhaustiveness checking impossible

**Impact**:
- Cannot resolve pattern variables in instanceof expressions
- Cannot trace variables matched in patterns
- Query "what variables are matched in pattern X?" impossible
- Reachability analysis cannot use pattern constraints

**Evidence**:
- ExpressionHandler.kt: Only basic instanceof handling
- No Pattern* classes in declarations or expressions directories
- Task 3 Analysis (Agent 3 report): "Pattern matching: 5% support"

### 2.3 Sealed Classes (Java 15+)

**Current State**: ZERO support (0%)

**Missing**:
- ❌ Sealed modifier not tracked on RecordDeclaration
- ❌ `permits` clause not parsed into AST
- ❌ PermitsClause node type missing
- ❌ Sealed class hierarchy not enforced

**Symbol Resolution Impact**:
- Cannot resolve permitted subclasses
- Cannot query "what classes extend this sealed class?"
- Cannot validate sealed class exhaustiveness in switch

**Evidence**:
- RecordDeclaration: No `sealed` or `permits` properties
- No mention of "sealed" in symbol resolution code

### 2.4 Switch Expressions (Java 14+)

**Current State**: PARTIAL (treated as statements, not expressions)

**Problems**:
- ❌ No SwitchExpression node type (only SwitchStatement)
- ❌ Cannot assign switch result to variable
- ❌ `yield` not distinguished from `return`
- ❌ Arrow syntax (`->`) creates ambiguity with lambda

**Symbol Resolution**:
- ✓ Case labels resolved as statements
- ❌ Switch expression result NOT traced as data flow
- ❌ Reachability TODO in UnreachableEOGPass (line 125-126 of Agent 3 report)

**Impact**:
- Cannot query "what is the result type of this switch expression?"
- Cannot flow-trace switch result assignment
- Incomplete reachability analysis for switch statements

**Evidence**:
- StatementHandler.kt: Only `handleSwitchStatement()`
- No SwitchExpression in expressions directory
- UnreachableEOGPass.kt: TODO at line 125-126 for switch handling

### 2.5 Text Blocks (Java 13+)

**Current State**: ZERO support (0%)

**Problems**:
- ❌ Text blocks parsed as regular string literals
- ❌ No MultilineStringLiteral node type
- ❌ Escape sequence handling lost
- ❌ Indentation normalization lost

**Symbol Resolution Impact**:
- No specific impact on symbol resolution
- Potential issue for string content analysis

### 2.6 var Keyword (Local Variable Type Inference, Java 10+)

**Current State**: GOOD support (95%+)

**How it works**:
- ✓ `var` parsed as keyword, actual type inferred by TypeResolver
- ✓ Type inference from initializer expression
- ✓ Variable declared with inferred type
- ✓ Symbol resolution uses inferred type

**Symbol Resolution**:
- ✓ Variable name added to scope
- ✓ Type information available for downstream analysis

**Evidence**:
- Task 3 analysis: "Local Variable Type: 95% support"
- TypeResolver handles type inference

---

## 3. Query API Capability Assessment

### 3.1 Query API Architecture

**Core Components**:

**Query.kt** (250+ lines)
- High-level query functions for graph analysis
- Key functions:
  - `allExtended()`, `all()`: Check property for all nodes
  - `existsExtended()`, `exists()`: Check property for at least one node
  - `sizeof()`, `min()`, `max()`: Value evaluation
  - `dataFlow()`: Data flow analysis
  - `followDFGEdgesUntilHit()`: DFG traversal

**FlowQueries.kt** (200+ lines, 2025)
- Specialized query functions for data and control flow
- `dataFlow()`: Generic data flow analysis
- `Must` vs `May` analysis types
- Path collection and result formatting

**QueryTree.kt**:
- Tree representation of query execution
- Supports reasoning chain tracking
- Multi-path result aggregation

### 3.2 Query Capabilities for Java Features

**Record Queries**:
- ❌ NO Query API support for record-specific operations
- Missing:
  - Query to find all Records
  - Query record components
  - Query canonical constructor parameters
  - Query component field initialization

**Pattern Query Support**:
- ❌ ZERO support
- Missing:
  - Query pattern variables in scope
  - Query instanceof pattern usage
  - Query switch pattern coverage
  - Query guarded pattern conditions

**Sealed Class Queries**:
- ❌ ZERO support
- Missing:
  - Query permitted subclasses
  - Query sealed hierarchy
  - Query exhaustiveness validation
  - Query non-sealed extension points

**Switch Expression Queries**:
- ❌ INCOMPLETE (statement-based only)
- Missing:
  - Query switch expression type
  - Query case arm coverage
  - Query default arm necessity
  - Query pattern case matching

**Data Flow Queries**:
- ✓ Existing support: `dataFlow()`, `followDFGEdgesUntilHit()`
- ❌ Limited support for new feature flows:
  - Record component initialization flows missing (→ D7)
  - Pattern variable flows completely missing
  - Switch expression result flows missing

### 3.3 Query API Extensibility

**Current Design**:
- Query API is relatively open (Kotlin DSL style)
- Custom predicates can be provided
- Graph traversal can follow DFG, EOG, or custom edges
- Analysis sensitivity configurable (field-sensitive, context-sensitive)

**Extensibility for Java 11-17**:
- **Possible**: Add record-specific query functions (e.g., `queryRecordComponents()`)
- **Possible**: Add pattern-specific query functions once PatternExpression types exist
- **Possible**: Add sealed class hierarchy queries once permits clause parsed
- **Limited by**: Missing node types in frontend

---

## 4. Record Defect (D7): RecordComponentDeclaration Missing

### 4.1 Problem

**Current Situation**:
- All record components created as `FieldDeclaration` nodes
- No way to distinguish component fields from explicit fields
- Canonical constructor semantics not modeled

**Example**:
```java
record Point(int x, int y) {
    // x and y are record components
    // CURRENT: Created as FieldDeclaration
    // NEEDED: RecordComponentDeclaration with special semantics
}
```

**Impact on Symbol Resolution**:
1. Component names added to scope as regular fields
2. No link between component and canonical constructor parameter
3. DFG missing for component-to-parameter connection
4. Query API cannot identify components vs explicit fields

### 4.2 Evidence

- **Location**: RecordDeclaration.kt (no field property for components)
- **Parser**: Java frontend treats record components as declaration handler
- **Node Type**: Only `FieldDeclaration` exists, not `RecordComponentDeclaration`

### 4.3 Remediation

**Required Changes**:
1. Create `RecordComponentDeclaration` class extending `ValueDeclaration`
2. Store components separately from fields in `RecordDeclaration`
3. Link canonical constructor parameters to components
4. Extend SymbolResolver to handle component scoping
5. Update FieldDeclarationHandler to create RecordComponentDeclaration for records

---

## 5. Pattern Variable Defect (D8): No Pattern Variable Scope Support

### 5.1 Problem

**Current Situation**:
- No PatternExpression node types
- Pattern variables NOT created as declarations
- Pattern variable scoping completely missing

**Java 16 Example**:
```java
if (obj instanceof String s) {
    // s is a pattern variable, valid in this block
    System.out.println(s);
}
// s NOT accessible here
```

**Current Behavior**:
- instanceof parsed as TypeExpression
- Pattern part ignored
- Variable `s` never created or registered in scope

**Impact on Symbol Resolution**:
1. Pattern variables cannot be resolved
2. References to pattern variables get "unresolved" warning
3. Type information from pattern lost
4. Scope for pattern variables not tracked

### 5.2 Required Node Types

```
PatternExpression (abstract)
├── TypePattern
├── RecordPattern (Java 21)
├── GuardedPattern (Java 21)
└── VariablePattern (wrapper for pattern variables)
```

### 5.3 Evidence

- Task 3 Report: "Pattern matching: 5% support, 0% in symbol resolution"
- No Pattern* files in expression directories
- ExpressionHandler: Only basic instanceof, no pattern handling
- UnreachableEOGPass: No pattern variable scope tracking

---

## 6. Symbol Resolution Gaps Summary

### 6.1 Gap Matrix

| Feature | Node Types | Scope Support | Symbol Resolution | Evidence |
|---------|-----------|---------------|------------------|----------|
| Records | PartialOK* | OK | Partial | No RecordComponentDeclaration |
| Sealed | Missing | Missing | None | No sealed/permits support |
| Patterns | Missing | Missing | None | No Pattern* types |
| Pattern Vars | Missing | Missing | None | No variable declaration |
| Switch Expr | Missing | Limited | Partial | No SwitchExpression type |
| Text Blocks | Missing | N/A | N/A | No MultilineStringLiteral |
| var (local) | OK | OK | OK | Type inferred correctly |

### 6.2 Root Causes

**Tier 1: Fundamental (requires new node types)**:
- Pattern variables (need PatternExpression hierarchy)
- Record components (need RecordComponentDeclaration)
- Sealed modifier (need sealed property and permits parsing)
- Switch expressions (need SwitchExpression type)

**Tier 2: Design (requires scope extension)**:
- Pattern variable scoping rules
- Switch expression result tracking
- Compact constructor semantics

**Tier 3: Integration (requires symbol resolver updates)**:
- Register pattern variables in scope
- Link record components to constructor parameters
- Handle pattern variable shadowing rules
- Manage pattern variable lifetime (block scope)

---

## 7. Query API Gaps Summary

### 7.1 Query Capability Matrix

| Feature | Query Support | Extensibility | Blocker |
|---------|---------------|---------------|---------|
| Records | None | Possible (after D7) | D7: Missing RecordComponentDeclaration |
| Record Components | None | Limited | D7: No component node type |
| Sealed Hierarchy | None | Possible (after permits) | Missing permits parsing |
| Pattern Matching | None | Blocked | D8: Missing PatternExpression types |
| Pattern Variables | None | Blocked | D8: No variable declarations |
| Switch Expressions | Partial | Limited | Missing SwitchExpression type |
| Switch Result | None | Possible (after type) | No SwitchExpression |
| Guarded Patterns | None | Blocked | D8: No GuardedPattern type |

### 7.2 Query Extension Opportunities

**High Feasibility** (after frontend fixes):
```kotlin
// Proposed API extensions
fun <T : RecordDeclaration> T.recordComponents(): List<RecordComponentDeclaration>
fun <T : RecordDeclaration> T.canonicalConstructor(): ConstructorDeclaration?
fun <T : RecordDeclaration> T.sealedPermits(): List<RecordDeclaration>
```

**Requires New Infrastructure**:
```kotlin
// Pattern query functions (require PatternExpression types)
fun <T : PatternExpression> T.matchedVariables(): List<PatternVariableDeclaration>
fun <T : TypePattern> T.matchedType(): Type?
```

---

## 8. Integration Analysis: Symbol Resolution <-> Query API

### 8.1 Critical Path Dependencies

**Chain 1: Record Component Support**:
```
D7: RecordComponentDeclaration
  ↓ (enables)
Symbol Resolution: Register components in scope
  ↓ (enables)
Query API: queryRecordComponents()
  ↓ (enables)
User Analysis: "Find all record component accesses"
```

**Chain 2: Pattern Variable Support**:
```
D8: PatternExpression + PatternVariableDeclaration
  ↓ (enables)
Symbol Resolution: Register pattern variables in scope
  ↓ (enables)
Query API: queryPatternVariables()
  ↓ (enables)
User Analysis: "Find unsafe pattern variable uses"
```

**Chain 3: Sealed Class Support**:
```
Permits Clause Parsing
  ↓ (enables)
Symbol Resolution: Resolve permitted subclasses
  ↓ (enables)
Query API: querySealedHierarchy()
  ↓ (enables)
User Analysis: "Find unhandled sealed subclasses"
```

### 8.2 Execution Sequence

**Priority 1** (Foundation):
1. Create PatternExpression node hierarchy (D8)
2. Create RecordComponentDeclaration (D7)
3. Extend ScopeManager with pattern variable scope type

**Priority 2** (Symbol Resolution):
4. Update SymbolResolver.handle() to register pattern variables
5. Update SymbolResolver to link record components to constructor
6. Add pattern variable shadowing validation

**Priority 3** (Query API):
7. Add record component query functions
8. Add pattern variable query functions
9. Add sealed class query functions

---

## 9. Recommended Implementation Roadmap

### Phase 1: Frontend Node Types (2-3 weeks)

**Tasks**:
- T1.1: Create PatternExpression class hierarchy
  - Base: `PatternExpression(Expression)`
  - Subclasses: TypePattern, RecordPattern, GuardedPattern
  - File: `cpg-core/.../PatternExpression.kt`

- T1.2: Create RecordComponentDeclaration
  - Extends: `ValueDeclaration`
  - Properties: Record parent, position, implicit?
  - File: `cpg-core/.../RecordComponentDeclaration.kt`

- T1.3: Extend RecordDeclaration
  - Add: `components: List<RecordComponentDeclaration>`
  - Add: `sealed: Boolean`, `permits: List<Type>`
  - File: `cpg-core/.../RecordDeclaration.kt`

- T1.4: Create SwitchExpression
  - Extends: `Expression`
  - Distinct from: `SwitchStatement`
  - Properties: resultType, cases, default
  - File: `cpg-core/.../SwitchExpression.kt`

### Phase 2: Frontend Parser Updates (3-4 weeks)

**Tasks**:
- T2.1: Update Java DeclarationHandler
  - Handle record components → RecordComponentDeclaration
  - Handle sealed modifier + permits clause

- T2.2: Update Java ExpressionHandler
  - Handle instanceof patterns → PatternExpression
  - Handle switch expressions → SwitchExpression
  - Extract pattern variables from patterns

- T2.3: Update Java StatementHandler
  - Distinguish switch statement vs expression
  - Handle pattern-based case labels

### Phase 3: Symbol Resolution Extension (2-3 weeks)

**Tasks**:
- T3.1: Extend ScopeManager
  - Add: `PatternVariableScope` class
  - Properties: guarded pattern, scope boundary

- T3.2: Update SymbolResolver
  - Add: `handlePatternExpression()` method
  - Add: `registerPatternVariables()` method
  - Update: `handle()` dispatcher for pattern nodes

- T3.3: Add pattern variable validation
  - Check shadowing rules
  - Verify scope boundaries
  - Validate type consistency

### Phase 4: Query API Extension (1-2 weeks)

**Tasks**:
- T4.1: Add record component queries
  ```kotlin
  fun <T : RecordDeclaration> T.recordComponents(): List<RecordComponentDeclaration>
  fun <T : RecordDeclaration> T.componentsUsedIn(target: Node): List<RecordComponentDeclaration>
  ```

- T4.2: Add pattern queries
  ```kotlin
  fun <T : PatternExpression> T.matchedVariables(): List<PatternVariableDeclaration>
  fun <T : PatternVariableDeclaration> T.usagePoints(): List<Reference>
  ```

- T4.3: Add sealed class queries
  ```kotlin
  fun <T : RecordDeclaration> T.permittedSubclasses(): List<RecordDeclaration>
  fun <T : RecordDeclaration> T.isSealedClass(): Boolean
  ```

**Total Effort**: 8-12 weeks for complete implementation

---

## 10. Critical Blockers and Recommendations

### 10.1 Immediate Blockers

**For Task 3 Scenarios**:
- D1: Static Final Field DFG Missing (blocks all scenarios)
- D2: String.equals() Not Evaluated (blocks all scenarios)
- D3: Interprocedural DFG Missing (blocks Scenario 2)
- D4: Call Graph Infrastructure (foundation for D3)

**Symbol Resolution Specific**:
- D7: RecordComponentDeclaration Missing
- D8: Pattern Variable Support Missing
- Sealed class modifier not tracked

### 10.2 Mitigation Strategies

**Short-term** (for current Task 3):
- Work around D1+D2 with inference engine (as planned)
- Accept Scenario 3 limitations without interprocedural support
- Focus on reachable code within single methods

**Medium-term** (weeks 2-4):
- Implement D7 (RecordComponentDeclaration)
- Implement D8 (PatternExpression hierarchy)
- Fix switch expression support

**Long-term** (architecture improvement):
- Consider "Language-Specific Pass" mechanism (from Task 4 analysis)
- Extend ValueEvaluator with safe method evaluation
- Build comprehensive call graph (D4)

### 10.3 Recommended Priority for CPG Improvement

1. **P0**: D1 + D2 (blocks all constant propagation)
2. **P0**: D4 + D3 (foundation for interprocedural analysis)
3. **P1**: D7 + D8 (required for Java 11-17 support)
4. **P2**: Sealed class modifier + permits clause
5. **P2**: Language-Specific Pass mechanism

---

## 11. Cross-Reference with Previous Analysis

**Confirmed from Agent 3**:
- ✓ D1: Static Final Field DFG Missing (verified line 282-283 of DFGPass)
- ✓ D2: String.equals() Not Evaluated (verified line 146-147 of ValueEvaluator)
- ✓ D4: Switch Statement TODO (verified line 125-126 of UnreachableEOGPass)
- ✓ D5: Pattern Matching Variables Not Created (new finding, confirms Agent 3)
- ✓ D6: SwitchExpression Node Type Missing (new finding, confirms Agent 3)

**Symbol Resolution Specific Issues**:
- NEW D7: RecordComponentDeclaration Missing (found in this analysis)
- NEW D8: Compact Constructor Semantics Not Modeled (implied by lack of D7)
- NEW: Pattern variable scoping not implemented (no scope type exists)

---

## 12. Evidence Summary

### Core Symbol Resolver Files:
- `/home/dai/code/cpg/cpg-core/src/main/kotlin/de/fraunhofer/aisec/cpg/passes/SymbolResolver.kt` (915 lines)
- `/home/dai/code/cpg/cpg-core/src/main/kotlin/de/fraunhofer/aisec/cpg/ScopeManager.kt` (200+ lines)
- `/home/dai/code/cpg/cpg-core/src/main/kotlin/de/fraunhofer/aisec/cpg/passes/ImportResolver.kt` (200+ lines)

### Scope System Files:
- `/home/dai/code/cpg/cpg-core/src/main/kotlin/de/fraunhofer/aisec/cpg/graph/scopes/Scope.kt` (335 lines)
- Individual scope types: FileScope, GlobalScope, NamespaceScope, RecordScope, FunctionScope, LocalScope, TemplateScope

### Query API Files:
- `/home/dai/code/cpg/cpg-analysis/src/main/kotlin/de/fraunhofer/aisec/cpg/query/Query.kt` (250+ lines)
- `/home/dai/code/cpg/cpg-analysis/src/main/kotlin/de/fraunhofer/aisec/cpg/query/FlowQueries.kt` (200+ lines)
- `/home/dai/code/cpg/cpg-analysis/src/main/kotlin/de/fraunhofer/aisec/cpg/query/QueryTree.kt`

### Node Type Inventory:
- **Declaration Types**: 24 files in `declarations/` directory
- **Expression Types**: 29 files in `expressions/` directory
- **Missing Java 11-17 Types**:
  - ❌ PatternExpression
  - ❌ RecordComponentDeclaration
  - ❌ SwitchExpression
  - ❌ MultilineStringLiteral

---

## 13. Conclusion

The CPG symbol resolution and query API systems are well-designed for traditional Java (1.4-10) features but have **critical gaps for Java 11-17 language features**:

### Symbol Resolution:
- **Records**: Missing record component node type (D7)
- **Patterns**: Missing all pattern expression types (D8)
- **Sealed**: Missing modifier tracking and permits clause
- **Switches**: Missing SwitchExpression type distinction
- **Root cause**: Frontend doesn't create appropriate node types

### Query API:
- **Design is sound** (extensible and modular)
- **But blocked** by missing node types from frontend
- **Once types exist**, queries can be added with minimal effort
- **High-value additions**: Record component queries, pattern variable queries, sealed hierarchy queries

### Critical Path to Java 11-17 Support:
1. **Frontend**: Add missing node types (D7, D8, SwitchExpression, sealed)
2. **Symbol Resolution**: Extend scope system and register new node symbols
3. **Query API**: Add feature-specific query functions
4. **Impact**: Enables analysis and transformation of modern Java code

The infrastructure is mature enough to support the new features—the missing pieces are the frontend node types and corresponding symbol resolution handling. This represents a **5-10 week effort** for complete Java 11-17 support at the symbol resolution and query level.

