/**
 * 查询脚本：查找不可达代码（死代码）
 *
 * 功能：
 * - 查找所有 if 语句
 * - 检测包含不可达分支的 if 语句
 * - 输出不可达代码的位置和上下文
 *
 * 前置要求：
 * - 构建时需注册 UnreachableEOGPass
 *
 * 输出格式：
 * {
 *   "totalIfStatements": 1250,
 *   "unreachableBranches": [
 *     {"file": "Foo.java", "line": 42, "code": "if (true) { ... } else { ... }"}
 *   ]
 * }
 */

import de.fraunhofer.aisec.cpg.graph.statements.IfStatement
import de.fraunhofer.aisec.cpg.graph.edges.flows.EvaluationOrder

// 步骤 1: 查找所有 if 语句
log("Step 1: Finding all if statements...")
val ifStatements = result.allNodes<IfStatement>()
log("Found ${ifStatements.size} if statements")

// 步骤 2: 检测包含不可达分支的 if 语句
log("Step 2: Detecting unreachable branches...")
val unreachableBranches = ifStatements.filter { ifStmt ->
    ifStmt.nextEOGEdges.any { edge ->
        edge is EvaluationOrder && edge.unreachable == true
    }
}
log("Found ${unreachableBranches.size} unreachable branches")

// 步骤 3: 收集详细信息
log("Step 3: Collecting details...")
val branchDetails = unreachableBranches.map { ifStmt ->
    mapOf(
        "file" to ifStmt.location?.artifactLocation?.uri,
        "line" to ifStmt.location?.region?.startLine,
        "code" to ifStmt.code,
        "condition" to ifStmt.condition.code,
        "severity" to "warning"  // 死代码通常是警告，不是错误
    )
}

// 步骤 4: 输出结果
output(mapOf(
    "summary" to mapOf(
        "totalIfStatements" to ifStatements.size,
        "unreachableBranchCount" to unreachableBranches.size,
        "unreachablePercentage" to String.format("%.2f%%",
            unreachableBranches.size.toDouble() / ifStatements.size * 100)
    ),
    "unreachableBranches" to branchDetails
))

log("✅ Unreachable code analysis completed")
