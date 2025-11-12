/**
 * 查询脚本：代码质量检查
 *
 * 功能：
 * - 计算圈复杂度（Cyclomatic Complexity）
 * - 检测过长方法
 * - 检测过深嵌套
 * - 检测代码异味
 *
 * 输出格式：
 * {
 *   "highComplexityFunctions": [...],
 *   "longMethods": [...],
 *   "deeplyNestedCode": [...]
 * }
 */

import de.fraunhofer.aisec.cpg.graph.declarations.FunctionDeclaration
import de.fraunhofer.aisec.cpg.graph.statements.IfStatement
import de.fraunhofer.aisec.cpg.graph.statements.ForStatement
import de.fraunhofer.aisec.cpg.graph.statements.WhileStatement
import de.fraunhofer.aisec.cpg.graph.statements.SwitchStatement

log("Starting code quality analysis...")

// ========================================
// 分析 1: 圈复杂度分析
// ========================================
log("Analyzing cyclomatic complexity...")

val functions = result.allNodes<FunctionDeclaration>()

val complexityReport = functions.map { func ->
    // 圈复杂度 = 1 + 决策点数量
    val ifCount = func.allChildren<IfStatement>().size
    val forCount = func.allChildren<ForStatement>().size
    val whileCount = func.allChildren<WhileStatement>().size
    val switchCount = func.allChildren<SwitchStatement>().size

    val complexity = 1 + ifCount + forCount + whileCount + switchCount

    mapOf(
        "functionName" to func.name.localName,
        "file" to func.location?.artifactLocation?.uri,
        "line" to func.location?.region?.startLine,
        "complexity" to complexity,
        "ifStatements" to ifCount,
        "loops" to (forCount + whileCount),
        "switches" to switchCount
    )
}

// 高复杂度函数（阈值：10）
val highComplexityFunctions = complexityReport.filter {
    (it["complexity"] as Int) > 10
}.sortedByDescending { it["complexity"] as Int }

log("Found ${highComplexityFunctions.size} functions with complexity > 10")

// ========================================
// 分析 2: 过长方法检测
// ========================================
log("Detecting long methods...")

val longMethods = functions.filter { func ->
    val startLine = func.location?.region?.startLine ?: 0
    val endLine = func.location?.region?.endLine ?: 0
    val loc = endLine - startLine + 1
    loc > 100  // 阈值：100 行
}.map { func ->
    val startLine = func.location?.region?.startLine ?: 0
    val endLine = func.location?.region?.endLine ?: 0
    val loc = endLine - startLine + 1

    mapOf(
        "functionName" to func.name.localName,
        "file" to func.location?.artifactLocation?.uri,
        "line" to startLine,
        "linesOfCode" to loc,
        "recommendation" to "Consider splitting into smaller functions"
    )
}.sortedByDescending { it["linesOfCode"] as Int }

log("Found ${longMethods.size} methods with LOC > 100")

// ========================================
// 分析 3: 过深嵌套检测
// ========================================
log("Detecting deeply nested code...")

fun calculateNestingDepth(node: Any, depth: Int = 0): Int {
    // 简化实现：计算 if/for/while 嵌套深度
    if (node !is de.fraunhofer.aisec.cpg.graph.Node) return depth

    val children = node.allChildren<de.fraunhofer.aisec.cpg.graph.Node>()
    val controlFlowChildren = children.filterIsInstance<IfStatement>() +
                               children.filterIsInstance<ForStatement>() +
                               children.filterIsInstance<WhileStatement>()

    if (controlFlowChildren.isEmpty()) return depth

    return controlFlowChildren.maxOfOrNull {
        calculateNestingDepth(it, depth + 1)
    } ?: depth
}

val deeplyNestedCode = functions.filter { func ->
    calculateNestingDepth(func) > 4  // 阈值：嵌套深度 > 4
}.map { func ->
    mapOf(
        "functionName" to func.name.localName,
        "file" to func.location?.artifactLocation?.uri,
        "line" to func.location?.region?.startLine,
        "nestingDepth" to calculateNestingDepth(func),
        "recommendation" to "Reduce nesting depth by extracting methods or using early returns"
    )
}.sortedByDescending { it["nestingDepth"] as Int }

log("Found ${deeplyNestedCode.size} functions with nesting depth > 4")

// ========================================
// 分析 4: 代码异味检测
// ========================================
log("Detecting code smells...")

// 异味 1: 过多参数（> 5 个）
val functionsWithManyParameters = functions.filter { func ->
    func.parameters.size > 5
}.map { func ->
    mapOf(
        "smell" to "TOO_MANY_PARAMETERS",
        "functionName" to func.name.localName,
        "file" to func.location?.artifactLocation?.uri,
        "line" to func.location?.region?.startLine,
        "parameterCount" to func.parameters.size,
        "recommendation" to "Consider using parameter objects or builder pattern"
    )
}

// 异味 2: 魔术数字（字面量）
val literalCount = result.allNodes<de.fraunhofer.aisec.cpg.graph.statements.expressions.Literal<*>>()
    .filter { it.value is Number && it.value != 0 && it.value != 1 }
    .size

log("Detected ${functionsWithManyParameters.size} functions with too many parameters")
log("Detected $literalCount potential magic numbers")

// ========================================
// 计算总体指标
// ========================================
val avgComplexity = complexityReport.map { it["complexity"] as Int }.average()
val maxComplexity = complexityReport.maxOfOrNull { it["complexity"] as Int } ?: 0
val avgLOC = functions.map {
    val start = it.location?.region?.startLine ?: 0
    val end = it.location?.region?.endLine ?: 0
    end - start + 1
}.average()

// ========================================
// 输出结果
// ========================================
output(mapOf(
    "summary" to mapOf(
        "totalFunctions" to functions.size,
        "averageComplexity" to String.format("%.2f", avgComplexity),
        "maxComplexity" to maxComplexity,
        "averageLOC" to String.format("%.1f", avgLOC),
        "highComplexityCount" to highComplexityFunctions.size,
        "longMethodCount" to longMethods.size,
        "deepNestingCount" to deeplyNestedCode.size,
        "codeSmellCount" to functionsWithManyParameters.size
    ),
    "issues" to mapOf(
        "highComplexity" to highComplexityFunctions.take(20),  // 前 20 个
        "longMethods" to longMethods.take(20),
        "deepNesting" to deeplyNestedCode.take(20),
        "codeSmells" to functionsWithManyParameters.take(20)
    )
))

log("✅ Code quality analysis completed")
