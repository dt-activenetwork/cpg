# 详细代码证据和实现细节

**此文档包含**: 完整的代码引用、行号映射、实现细节分析

---

## 第一部分: JavaLanguageFrontend.kt 完整分析

**文件**: `/home/dai/code/cpg/cpg-language-java/src/main/kotlin/de/fraunhofer/aisec/cpg/frontends/java/JavaLanguageFrontend.kt`  
**总行数**: 563行  
**关键部分**: 初始化、parse方法、类型处理

### 1.1 类定义和初始化

**位置**: 84-103行

```kotlin
@RegisterExtraPass(JavaExternalTypeHierarchyResolver::class)
@RegisterExtraPass(JavaImportResolver::class)
@RegisterExtraPass(JavaExtraPass::class)
open class JavaLanguageFrontend(ctx: TranslationContext, language: Language<JavaLanguageFrontend>) :
    LanguageFrontend<Node, Type>(ctx, language) {

    var context: CompilationUnit? = null
    var javaSymbolResolver: JavaSymbolSolver?
    val nativeTypeResolver = CombinedTypeSolver()

    lateinit var expressionHandler: ExpressionHandler
    lateinit var statementHandler: StatementHandler
    lateinit var declarationHandler: DeclarationHandler

    init {
        setupHandlers()
    }

    private fun setupHandlers() {
        expressionHandler = ExpressionHandler(this)
        statementHandler = StatementHandler(this)
        declarationHandler = DeclarationHandler(this)
    }
}
```

**发现**:
- 三个Handler分别处理表达式、语句、声明
- 通过成员变量统一管理
- setupHandlers在init块中初始化

### 1.2 Parse方法 (核心)

**位置**: 105-185行

```kotlin
@Throws(TranslationException::class)
override fun parse(file: File): TranslationUnitDeclaration {
    return try {
        // ❌ ISSUE: 无Java版本配置
        val parserConfiguration = ParserConfiguration()
        parserConfiguration.setSymbolResolver(javaSymbolResolver)
        val parser = JavaParser(parserConfiguration)

        // parse the file
        var bench = Benchmark(this.javaClass, "Parsing source file")

        context = parse(file, parser)
        bench.addMeasurement()
        bench = Benchmark(this.javaClass, "Transform to CPG")
        context?.setData(Node.SYMBOL_RESOLVER_KEY, javaSymbolResolver)

        // starting point is always a translation declaration
        val tud = newTranslationUnitDeclaration(file.toString(), rawNode = context)
        currentTU = tud
        scopeManager.resetToGlobal(tud)
        val packDecl = context?.packageDeclaration?.orElse(null)

        // namespace处理
        val holder =
            packDecl?.name?.toString()?.split(language.namespaceDelimiter)?.fold(null) {
                previous: NamespaceDeclaration?,
                path ->
                var fqn = previous?.name.fqn(path)
                val nsd = newNamespaceDeclaration(fqn, rawNode = packDecl)
                scopeManager.addDeclaration(nsd)
                val holder = previous ?: tud
                holder.addDeclaration(nsd)
                scopeManager.enterScope(nsd)
                nsd
            } ?: tud

        // 处理所有类型声明
        for (type in context?.types ?: listOf()) {
            val declaration = declarationHandler.handle(type)
            if (declaration != null) {
                scopeManager.addDeclaration(declaration)
                holder.addDeclaration(declaration)
            }
        }

        // 处理import
        scopeManager.enterScope(tud)
        for (anImport in context?.imports ?: listOf()) {
            val incl = newIncludeDeclaration(anImport.nameAsString)
            scopeManager.addDeclaration(incl)
            tud.addDeclaration(incl)
        }

        // Implicit java.lang.* import
        val decl =
            newImportDeclaration(
                    parseName("java.lang"),
                    style = ImportStyle.IMPORT_ALL_SYMBOLS_FROM_NAMESPACE,
                )
                .implicit("import java.lang.*")
        scopeManager.addDeclaration(decl)
        tud.addDeclaration(decl)
        scopeManager.leaveScope(tud)

        if (holder is NamespaceDeclaration) {
            tud.allChildren<NamespaceDeclaration>().reversed().forEach {
                scopeManager.leaveScope(it)
            }
        }
        bench.addMeasurement()
        tud
    } catch (ex: IOException) {
        throw TranslationException(ex)
    }
}
```

**关键问题**:
- **行109**: 无ParserConfiguration.setLanguageLevel()设置
- **行110**: 仅设置符号解析器,无Java版本约束

**改进建议**:

```kotlin
val parserConfiguration = ParserConfiguration()
// 添加这一行:
parserConfiguration.setLanguageLevel(LanguageLevel.JAVA_17)
// 或通过配置:
// val javaVersion = config.properties.getOrDefault("java.version", "17")
// parserConfiguration.setLanguageLevel(LanguageLevel.parse(javaVersion))
parserConfiguration.setSymbolResolver(javaSymbolResolver)
```

### 1.3 类型处理方法

**位置**: 513-537行

```kotlin
override fun typeOf(type: Type): de.fraunhofer.aisec.cpg.graph.types.Type {
    return when (type) {
        is ArrayType -> this.typeOf(type.elementType).array()
        is VoidType -> incompleteType()
        is PrimitiveType -> primitiveType(type.asString())
        is ClassOrInterfaceType ->
            objectType(
                type.nameWithScope,
                type.typeArguments.getOrNull()?.map { this.typeOf(it) } ?: listOf(),
            )
        is ReferenceType -> objectType(type.asString())
        else -> objectType(type.asString())
    }
}

fun typeOf(type: ResolvedType): de.fraunhofer.aisec.cpg.graph.types.Type {
    return when (type) {
        is ResolvedArrayType -> typeOf(type.componentType).array()
        is ResolvedVoidType -> incompleteType()
        is ResolvedPrimitiveType -> primitiveType(type.describe())
        is ResolvedReferenceType ->
            objectType(type.describe(), type.typeParametersValues().map { typeOf(it) })
        else -> objectType(type.describe())
    }
}
```

**特性支持**:
- ✅ 数组类型
- ✅ 原始类型
- ✅ 类/接口类型
- ✅ 泛型类型参数

---

## 第二部分: ExpressionHandler.kt 完整分析

**文件**: `/home/dai/code/cpg/cpg-language-java/src/main/kotlin/de/fraunhofer/aisec/cpg/frontends/java/ExpressionHandler.kt`  
**总行数**: 669行  
**处理器个数**: 18个主要处理函数

### 2.1 Handler映射表

**位置**: init块 (634-667行)

```kotlin
init {
    map[com.github.javaparser.ast.expr.AssignExpr::class.java] = HandlerInterface {
        handleAssignmentExpression(it)
    }
    map[FieldAccessExpr::class.java] = HandlerInterface {
        handleFieldAccessExpression(it.asFieldAccessExpr())
    }
    map[LiteralExpr::class.java] = HandlerInterface { handleLiteralExpression(it) }
    map[ThisExpr::class.java] = HandlerInterface { handleThisExpression(it) }
    map[SuperExpr::class.java] = HandlerInterface { handleSuperExpression(it) }
    map[ClassExpr::class.java] = HandlerInterface { handleClassExpression(it) }
    map[NameExpr::class.java] = HandlerInterface { handleNameExpression(it.asNameExpr()) }
    map[InstanceOfExpr::class.java] = HandlerInterface { handleInstanceOfExpression(it) }
    map[UnaryExpr::class.java] = HandlerInterface { handleUnaryExpression(it) }
    map[BinaryExpr::class.java] = HandlerInterface { handleBinaryExpression(it) }
    map[VariableDeclarationExpr::class.java] = HandlerInterface {
        handleVariableDeclarationExpr(it)
    }
    map[MethodCallExpr::class.java] = HandlerInterface { handleMethodCallExpression(it) }
    map[ObjectCreationExpr::class.java] = HandlerInterface { handleObjectCreationExpr(it) }
    map[com.github.javaparser.ast.expr.ConditionalExpr::class.java] = HandlerInterface {
        handleConditionalExpression(it)
    }
    map[EnclosedExpr::class.java] = HandlerInterface { handleEnclosedExpression(it) }
    map[ArrayAccessExpr::class.java] = HandlerInterface { handleArrayAccessExpr(it) }
    map[ArrayCreationExpr::class.java] = HandlerInterface { handleArrayCreationExpr(it) }
    map[ArrayInitializerExpr::class.java] = HandlerInterface { handleArrayInitializerExpr(it) }
    map[com.github.javaparser.ast.expr.CastExpr::class.java] = HandlerInterface {
        handleCastExpr(it)
    }
    map[com.github.javaparser.ast.expr.LambdaExpr::class.java] = HandlerInterface {
        handleLambdaExpr(it)
    }
}
```

**缺失映射** (应该添加但未添加):
- ❌ `SwitchExpr::class.java` (Java 12-14+)
- ❌ Pattern binding相关

### 2.2 Lambda表达式处理

**位置**: 52-76行

```kotlin
private fun handleLambdaExpr(expr: Expression): Statement {
    val lambdaExpr = expr.asLambdaExpr()
    val lambda = newLambdaExpression(rawNode = lambdaExpr)
    val anonymousFunction = newFunctionDeclaration("", rawNode = lambdaExpr)
    frontend.scopeManager.enterScope(anonymousFunction)
    for (parameter in lambdaExpr.parameters) {
        val resolvedType = frontend.getTypeAsGoodAsPossible(parameter.type)
        val param =
            newParameterDeclaration(parameter.nameAsString, resolvedType, parameter.isVarArgs)
        frontend.processAnnotations(param, parameter)
        frontend.scopeManager.addDeclaration(param)
        anonymousFunction.parameters += param
    }

    // TODO: We cannot easily identify the signature of the lambda
    // val type = lambdaExpr.calculateResolvedType()
    val functionType = computeType(anonymousFunction)
    anonymousFunction.type = functionType
    anonymousFunction.body = frontend.statementHandler.handle(lambdaExpr.body)
    frontend.scopeManager.leaveScope(anonymousFunction)

    lambda.function = anonymousFunction

    return lambda
}
```

**覆盖范围**:
- ✅ 单参数: `x -> x + 1`
- ✅ 多参数: `(x, y) -> x + y`
- ✅ 参数类型推断
- ✅ Lambda body处理
- ⚠️ 类型推断(有TODO注释)

### 2.3 Instanceof表达式处理

**位置**: 390-412行

```kotlin
private fun handleInstanceOfExpression(expr: Expression): BinaryOperator {
    val binaryExpr = expr.asInstanceOfExpr()

    // first, handle the target. this is the first argument of the operator callUnresolved symbol
    val lhs =
        handle(binaryExpr.expression)
            as? de.fraunhofer.aisec.cpg.graph.statements.expressions.Expression
            ?: newProblemExpression("could not parse lhs")
    val typeAsGoodAsPossible = frontend.getTypeAsGoodAsPossible(binaryExpr.type)

    // second, handle the value. this is the second argument of the operator call
    val rhs: de.fraunhofer.aisec.cpg.graph.statements.expressions.Expression =
        newLiteral(
            typeAsGoodAsPossible.typeName,
            this.objectType("class"),
            rawNode = binaryExpr,
        )
    val binaryOperator = newBinaryOperator("instanceof", rawNode = binaryExpr)
    binaryOperator.lhs = lhs
    binaryOperator.rhs = rhs
    return binaryOperator
}
```

**问题分析**:
- ✅ 表达式处理
- ✅ 类型解析
- ❌ 无binding变量提取
- ❌ 模式信息丢失

**JavaParser提供的功能** (但未使用):
```kotlin
// 在JavaParser的InstanceOfExpr中:
fun getPattern(): Optional<Pattern>  // 获取pattern (包括绑定变量)
```

当前代码忽略了getPattern()方法,直接使用getType()。

---

## 第三部分: StatementHandler.kt 完整分析

**文件**: `/home/dai/code/cpg/cpg-language-java/src/main/kotlin/de/fraunhofer/aisec/cpg/frontends/java/StatementHandler.kt`  
**总行数**: 619行  
**关键部分**: Switch语句处理

### 3.1 Switch语句处理

**位置**: 410-446行

```kotlin
fun handleSwitchStatement(stmt: Statement): SwitchStatement {
    val switchStmt = stmt.asSwitchStmt()
    val switchStatement = newSwitchStatement(rawNode = stmt)

    frontend.scopeManager.enterScope(switchStatement)
    switchStatement.selector =
        frontend.expressionHandler.handle(switchStmt.selector)
            as de.fraunhofer.aisec.cpg.graph.statements.expressions.Expression

    // Compute region and code for self generated compound statement to match the c++ versions
    var start: JavaToken? = null
    var end: JavaToken? = null
    val tokenRange = switchStmt.tokenRange
    val tokenRangeSelector = switchStmt.selector.tokenRange
    if (tokenRange.isPresent && tokenRangeSelector.isPresent) {
        start = getNextTokenWith("{", tokenRangeSelector.get().end)
        end = getPreviousTokenWith("}", tokenRange.get().end)
    }
    val compoundStatement = this.newBlock()
    compoundStatement.code = getCodeBetweenTokens(start, end)
    compoundStatement.location = getLocationsFromTokens(switchStatement.location, start, end)
    for (sentry in switchStmt.entries) {
        if (sentry.labels.isEmpty()) {
            compoundStatement.statements += handleCaseDefaultStatement(null, sentry)
        }
        for (caseExp in sentry.labels) {
            compoundStatement.statements += handleCaseDefaultStatement(caseExp, sentry)
        }
        for (subStmt in sentry.statements) {
            compoundStatement.statements +=
                handle(subStmt) ?: ProblemExpression("Could not parse statement")
        }
    }
    switchStatement.statement = compoundStatement
    frontend.scopeManager.leaveScope(switchStatement)
    return switchStatement
}
```

**支持**:
- ✅ 传统switch语句
- ✅ case标签
- ✅ default分支
- ✅ break/continue

**不支持**:
- ❌ Switch表达式 (Java 12+)
- ❌ Case arrows (->) 
- ❌ Multiple case labels (`case 1, 2, 3:`)

### 3.2 Handler映射表

**位置**: init块 (552-618行)

```kotlin
init {
    map[com.github.javaparser.ast.stmt.IfStmt::class.java] =
        HandlerInterface { stmt: Statement -> handleIfStatement(stmt) }
    map[com.github.javaparser.ast.stmt.AssertStmt::class.java] =
        HandlerInterface { stmt: Statement -> handleAssertStatement(stmt) }
    map[com.github.javaparser.ast.stmt.WhileStmt::class.java] =
        HandlerInterface { stmt: Statement -> handleWhileStatement(stmt) }
    map[com.github.javaparser.ast.stmt.DoStmt::class.java] =
        HandlerInterface { stmt: Statement -> handleDoStatement(stmt) }
    map[com.github.javaparser.ast.stmt.ForEachStmt::class.java] =
        HandlerInterface { stmt: Statement -> handleForEachStatement(stmt) }
    map[com.github.javaparser.ast.stmt.ForStmt::class.java] =
        HandlerInterface { stmt: Statement -> handleForStatement(stmt) }
    map[com.github.javaparser.ast.stmt.BreakStmt::class.java] =
        HandlerInterface { stmt: Statement -> handleBreakStatement(stmt) }
    map[com.github.javaparser.ast.stmt.ContinueStmt::class.java] =
        HandlerInterface { stmt: Statement -> handleContinueStatement(stmt) }
    map[com.github.javaparser.ast.stmt.ReturnStmt::class.java] =
        HandlerInterface { stmt: Statement -> handleReturnStatement(stmt) }
    map[BlockStmt::class.java] = HandlerInterface { stmt: Statement ->
        handleBlockStatement(stmt)
    }
    map[LabeledStmt::class.java] = HandlerInterface { stmt: Statement ->
        handleLabelStatement(stmt)
    }
    map[ExplicitConstructorInvocationStmt::class.java] = HandlerInterface { stmt: Statement ->
        handleExplicitConstructorInvocation(stmt)
    }
    map[ExpressionStmt::class.java] = HandlerInterface { stmt: Statement ->
        handleExpressionStatement(stmt)
    }
    map[com.github.javaparser.ast.stmt.SwitchStmt::class.java] =
        HandlerInterface { stmt: Statement -> handleSwitchStatement(stmt) }
    map[com.github.javaparser.ast.stmt.EmptyStmt::class.java] =
        HandlerInterface { stmt: Statement -> handleEmptyStatement(stmt) }
    map[com.github.javaparser.ast.stmt.SynchronizedStmt::class.java] =
        HandlerInterface { stmt: Statement -> handleSynchronizedStatement(stmt) }
    map[com.github.javaparser.ast.stmt.TryStmt::class.java] =
        HandlerInterface { stmt: Statement -> handleTryStatement(stmt) }
    map[ThrowStmt::class.java] = HandlerInterface { stmt: Statement -> handleThrowStmt(stmt) }
}
```

**缺失**:
- ❌ `SwitchExpr::class.java` (Java 12+)

---

## 第四部分: DeclarationHandler.kt 完整分析

**文件**: `/home/dai/code/cpg/cpg-language-java/src/main/kotlin/de/fraunhofer/aisec/cpg/frontends/java/DeclarationHandler.kt`  
**总行数**: 517行  
**关键问题**: Records处理缺失

### 4.1 类处理

**位置**: 173-212行

```kotlin
open fun handleClassOrInterfaceDeclaration(
    classInterDecl: ClassOrInterfaceDeclaration
): RecordDeclaration {
    val fqn = classInterDecl.fullyQualifiedName.orElse(classInterDecl.nameAsString)

    // add a type declaration
    val recordDeclaration = this.newRecordDeclaration(fqn, "class", rawNode = classInterDecl)
    recordDeclaration.superClasses =
        classInterDecl.extendedTypes
            .map { type -> frontend.getTypeAsGoodAsPossible(type) }
            .toMutableList()
    recordDeclaration.implementedInterfaces =
        classInterDecl.implementedTypes
            .map { type -> frontend.getTypeAsGoodAsPossible(type) }
            .toMutableList()

    frontend.typeManager.addTypeParameter(
        recordDeclaration,
        classInterDecl.typeParameters.map { ParameterizedType(it.nameAsString, language) },
    )

    processImportDeclarations(recordDeclaration)

    frontend.scopeManager.enterScope(recordDeclaration)
    processRecordMembers(classInterDecl, recordDeclaration)
    frontend.scopeManager.leaveScope(recordDeclaration)

    if (frontend.scopeManager.currentScope is RecordScope) {
        // inner class handling
        processInnerRecord(recordDeclaration)
    }
    return recordDeclaration
}
```

**问题**:
- ❌ 无`classInterDecl.isSealed`检查
- ❌ 无`classInterDecl.permittedTypes`处理
- ❌ 对Records无特殊处理

### 4.2 成员处理

**位置**: 328-387行

```kotlin
private fun <T : TypeDeclaration<T>> processRecordMembers(
    typeDecl: T,
    recordDeclaration: RecordDeclaration,
) {
    for (decl in typeDecl.members) {
        when (decl) {
            is MethodDeclaration -> {
                val md =
                    handle(decl) as de.fraunhofer.aisec.cpg.graph.declarations.MethodDeclaration
                frontend.scopeManager.addDeclaration(md)
                recordDeclaration.methods += md
            }
            is com.github.javaparser.ast.body.FieldDeclaration -> {
                val seq = handle(decl) as DeclarationSequence
                seq.declarations.filterIsInstance<FieldDeclaration>().forEach {
                    frontend.scopeManager.addDeclaration(it)
                    recordDeclaration.fields += it
                }
            }
            is ConstructorDeclaration -> {
                val c =
                    handle(decl)
                        as de.fraunhofer.aisec.cpg.graph.declarations.ConstructorDeclaration
                frontend.scopeManager.addDeclaration(c)
                recordDeclaration.constructors += c
            }
            is ClassOrInterfaceDeclaration -> {
                val cls = handle(decl) as RecordDeclaration
                frontend.scopeManager.addDeclaration(cls)
                recordDeclaration.records += cls
            }
            is com.github.javaparser.ast.body.EnumDeclaration -> {
                val cls = handle(decl) as RecordDeclaration
                frontend.scopeManager.addDeclaration(cls)
                recordDeclaration.records += cls
            }
            is InitializerDeclaration -> {
                val initializerBlock = frontend.statementHandler.handleBlockStatement(decl.body)
                initializerBlock.isStaticBlock = decl.isStatic
                recordDeclaration.statements += initializerBlock
            }
            else -> {
                log.debug(
                    "Member {} of type {} is something that we do not parse yet: {}",
                    decl,
                    recordDeclaration.name,
                    decl.javaClass.simpleName,
                )
            }
        }
    }
    // ...
}
```

**问题分析**:
- Records的`RecordDeclaration`类型 (JavaParser层)
- 不在已处理的`when`分支中
- 落入`else`分支,被记为"not parse yet"
- 导致Records无法正确处理

### 4.3 Handler映射表

**位置**: init块 (493-516行)

```kotlin
init {
    map[MethodDeclaration::class.java] = HandlerInterface { decl ->
        handleMethodDeclaration(decl as MethodDeclaration)
    }
    map[ConstructorDeclaration::class.java] = HandlerInterface { decl ->
        handleConstructorDeclaration(decl as ConstructorDeclaration)
    }
    map[ClassOrInterfaceDeclaration::class.java] = HandlerInterface { decl ->
        handleClassOrInterfaceDeclaration(decl as ClassOrInterfaceDeclaration)
    }
    map[com.github.javaparser.ast.body.FieldDeclaration::class.java] =
        HandlerInterface { decl ->
            handleFieldDeclaration(decl as com.github.javaparser.ast.body.FieldDeclaration)
        }
    map[com.github.javaparser.ast.body.EnumDeclaration::class.java] = HandlerInterface { decl ->
        handleEnumDeclaration(decl as com.github.javaparser.ast.body.EnumDeclaration)
    }
    map[com.github.javaparser.ast.body.EnumConstantDeclaration::class.java] =
        HandlerInterface { decl ->
            handleEnumConstantDeclaration(
                decl as com.github.javaparser.ast.body.EnumConstantDeclaration
            )
        }
}
```

**缺失**:
- ❌ `com.github.javaparser.ast.body.RecordDeclaration::class.java`
- ❌ `handleRecordDeclaration()`方法

---

## 第五部分: 缺失特性的具体影响

### 5.1 Switch表达式解析失败

**输入代码**:
```java
int day = 3;
int numLetters = switch(day) {
    case 1, 2, 3 -> 3;
    case 4, 5, 6 -> 3;
    case 7, 8, 9 -> 3;
    default -> 0;
};
```

**当前输出**:
```
[ERROR] Parsing of type com.github.javaparser.ast.expr.SwitchExpr is not supported (yet)
```

**原因**: ExpressionHandler的init块缺少:
```kotlin
map[SwitchExpr::class.java] = HandlerInterface { handleSwitchExpr(it) }
```

### 5.2 Records处理失败

**输入代码**:
```java
record Point(int x, int y) {}
```

**当前输出**:
```
[DEBUG] Member RecordDeclaration of type Point is something that we do not parse yet: RecordDeclaration
```

**原因**: DeclarationHandler.processRecordMembers()无Record案例

### 5.3 Pattern Binding信息丢失

**输入代码**:
```java
if (obj instanceof String s) {
    System.out.println(s.length());
}
```

**当前输出**:
- instanceof被处理为简单的BinaryOperator
- 绑定变量`s`被完全忽略
- 后续代码无法引用`s`

**原因**: ExpressionHandler.handleInstanceOfExpression()无pattern提取

---

## 第六部分: 改进实施指南

### 6.1 添加Switch表达式支持

**步骤1**: 创建处理函数 (ExpressionHandler.kt)

```kotlin
private fun handleSwitchExpr(expr: Expression): Statement {
    val switchExpr = expr.asSwitchExpr()
    val switchExpression = newSwitchExpression(rawNode = expr)
    
    // selector处理
    switchExpression.selector = 
        frontend.expressionHandler.handle(switchExpr.selector)
            as Expression
    
    // entries处理
    for (entry in switchExpr.entries) {
        // ... 处理每个entry
    }
    
    return switchExpression
}
```

**步骤2**: 注册映射 (ExpressionHandler.kt, init块)

```kotlin
map[SwitchExpr::class.java] = HandlerInterface { 
    handleSwitchExpr(it) 
}
```

**工作量**: 8-12小时

### 6.2 添加Records支持

**步骤1**: 创建处理函数 (DeclarationHandler.kt)

```kotlin
private fun handleRecordDeclaration(
    recordDecl: com.github.javaparser.ast.body.RecordDeclaration
): RecordDeclaration {
    val fqn = recordDecl.fullyQualifiedName.orElse(recordDecl.nameAsString)
    val recordDeclaration = this.newRecordDeclaration(fqn, "record", rawNode = recordDecl)
    
    // Components处理
    for (component in recordDecl.components) {
        val type = frontend.getTypeAsGoodAsPossible(component.type)
        val field = this.newFieldDeclaration(component.name.asString(), type)
        recordDeclaration.fields += field
    }
    
    // 标准成员处理
    frontend.scopeManager.enterScope(recordDeclaration)
    processRecordMembers(recordDecl, recordDeclaration)
    frontend.scopeManager.leaveScope(recordDeclaration)
    
    return recordDeclaration
}
```

**步骤2**: 更新processRecordMembers (DeclarationHandler.kt)

```kotlin
is com.github.javaparser.ast.body.RecordDeclaration -> {
    val rec = handle(decl) as RecordDeclaration
    frontend.scopeManager.addDeclaration(rec)
    recordDeclaration.records += rec
}
```

**步骤3**: 注册映射 (DeclarationHandler.kt, init块)

```kotlin
map[com.github.javaparser.ast.body.RecordDeclaration::class.java] =
    HandlerInterface { decl ->
        handleRecordDeclaration(
            decl as com.github.javaparser.ast.body.RecordDeclaration
        )
    }
```

**工作量**: 20-30小时

---

## 总结

| 特性 | 位置 | 行号 | 状态 | 工作量 |
|-----|-----|-----|-----|--------|
| Lambda | ExpressionHandler.kt | 52-76 | ✅ | 已完成 |
| Instanceof | ExpressionHandler.kt | 390-412 | ⚠️ 部分 | 需改进 |
| Switch语句 | StatementHandler.kt | 410-446 | ✅ | 已完成 |
| Switch表达式 | ❌ Missing | - | ❌ | 8-12h |
| Records | ❌ Missing | - | ❌ | 20-30h |
| Sealed Classes | DeclarationHandler.kt | 173-212 | ❌ | 15-20h |
| Pattern Binding | ❌ Missing | - | ❌ | 15-25h |

