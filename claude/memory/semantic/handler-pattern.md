---
id: sem-002
title: Handler Pattern in CPG Java Frontend
type: semantic
tags: [design-pattern, handlers, ast-transformation]
created: 2025-10-27
updated: 2025-10-27
source: DeclarationHandler.kt, StatementHandler.kt, ExpressionHandler.kt
related: [sem-001]
---

# Handler Pattern in CPG Java Frontend

## Why now
Understanding the Handler pattern is crucial for extending the Java frontend with new syntax support or debugging transformation issues.

## Pattern Description

The Handler pattern separates AST-to-CPG transformation logic by **syntax category**, using three specialized handler classes:

1. **DeclarationHandler**: Types, methods, fields, constructors, enums
2. **StatementHandler**: Control flow, loops, blocks, exception handling
3. **ExpressionHandler**: Calls, operators, literals, member access

## Implementation Structure

### Handler Base
Each handler extends `Handler<ResultType, ParserNodeType>` and uses **map-based dispatch**:

```kotlin
// StatementHandler.kt:552-570
init {
    map[IfStmt::class.java] = HandlerInterface { handleIfStatement(it) }
    map[WhileStmt::class.java] = HandlerInterface { handleWhileStatement(it) }
    map[ForStmt::class.java] = HandlerInterface { handleForStatement(it) }
    // ... more mappings
}
```

### Handler Invocation
Generic `handle()` method dispatches to specific handlers:

```kotlin
fun handle(node: ParserNode): CPGNode? {
    val handlerInterface = map[node.javaClass]
    return handlerInterface?.handle(node) ?: handleDefault(node)
}
```

## Key Responsibilities

### DeclarationHandler
- **File**: `cpg-language-java/src/main/kotlin/de/fraunhofer/aisec/cpg/frontends/java/DeclarationHandler.kt`
- **Size**: 517 lines
- **Key Methods**:
  - `handleClassOrInterfaceDeclaration()` (lines 173-212)
  - `handleMethodDeclaration()` (lines 105-158)
  - `handleConstructorDeclaration()` (lines 296-326)
  - `handleFieldDeclaration()` (lines 233-294)
  - `handleEnumDeclaration()` (lines 214-231)

### StatementHandler
- **File**: `cpg-language-java/src/main/kotlin/de/fraunhofer/aisec/cpg/frontends/java/StatementHandler.kt`
- **Size**: 619 lines
- **Handles**: 20+ statement types
- **Key Methods**:
  - `handleIfStatement()` (lines 112-126)
  - `handleForEachStatement()` (lines 156-171)
  - `handleTryStatement()` (lines 486-526)
  - `handleSwitchStatement()` (lines 394-451)

### ExpressionHandler
- **File**: `cpg-language-java/src/main/kotlin/de/fraunhofer/aisec/cpg/frontends/java/ExpressionHandler.kt`
- **Size**: 689 lines
- **Handles**: 20+ expression types
- **Key Methods**:
  - `handleMethodCallExpression()` (lines 453-542)
  - `handleObjectCreationExpr()` (lines 557-628)
  - `handleLambdaExpr()` (lines 429-451)
  - `handleBinaryExpression()` (lines 189-243)

## Pattern Benefits

1. **Separation of Concerns**: Each handler focuses on one syntax category
2. **Maintainability**: New syntax support only affects one handler
3. **Testability**: Each handler can be tested independently
4. **Scalability**: Large grammar (100+ constructs) remains manageable

## Adding New Syntax Support

### Step 1: Identify Category
Determine which handler should process the new syntax:
- Declarations? → DeclarationHandler
- Statements? → StatementHandler
- Expressions? → ExpressionHandler

### Step 2: Add Handler Method
```kotlin
// Example: Adding record support to DeclarationHandler
fun handleRecordDeclaration(recordDecl: RecordDeclaration): RecordDeclaration {
    val declaration = newRecordDeclaration(...)
    // Process record components
    // Process record members
    return declaration
}
```

### Step 3: Register in Map
```kotlin
// In handler's init block or handle() method
when (bodyDecl) {
    is RecordDeclaration -> handleRecordDeclaration(bodyDecl)
    // ...
}
```

## Common Patterns Within Handlers

### Scope Management Pattern
```kotlin
fun handleScopeNode(node: Node): CPGNode {
    val cpgNode = newNode(...)
    scopeManager.enterScope(cpgNode)  // Enter
    // Process children
    scopeManager.leaveScope(cpgNode)  // Leave
    return cpgNode
}
```

### Child Processing Pattern
```kotlin
fun handleParentNode(node: ParentNode): CPGParent {
    val parent = newParentNode(...)
    for (child in node.children) {
        val cpgChild = appropriateHandler.handle(child)
        parent.addChild(cpgChild)
    }
    return parent
}
```

## Evidence
All references verified in source code, commit 04680b1.

## Cross-References
- **Architecture**: sem-001
- **Implementation Details**: `/home/user/cpg/claude/result/1/1.impl-trace.md`
