/**
 * 查询脚本：安全漏洞扫描
 *
 * 功能：
 * - 检测 SQL 注入风险
 * - 检测命令注入风险
 * - 检测路径遍历风险
 * - 检测 XSS 风险
 *
 * 前置要求：
 * - 构建时需注册 ControlFlowSensitiveDFGPass
 *
 * 输出格式：
 * {
 *   "sqlInjectionRisks": [...],
 *   "commandInjectionRisks": [...],
 *   "pathTraversalRisks": [...],
 *   "xssRisks": [...]
 * }
 */

import de.fraunhofer.aisec.cpg.graph.statements.expressions.CallExpression
import de.fraunhofer.aisec.cpg.graph.declarations.VariableDeclaration
import de.fraunhofer.aisec.cpg.graph.statements.expressions.Literal

log("Starting security vulnerability scan...")

// ========================================
// 检测 1: SQL 注入风险
// ========================================
log("Detecting SQL injection risks...")

val sqlCalls = result.allNodes<CallExpression>().filter { call ->
    call.name.localName in listOf(
        "executeQuery", "executeUpdate", "execute",
        "prepareStatement", "createQuery"
    )
}

val sqlInjectionRisks = sqlCalls.filter { call ->
    // 检查是否有参数来自字符串拼接或用户输入
    call.arguments.any { arg ->
        // 简化检测：如果参数不是 PreparedStatement 占位符，可能有风险
        arg !is Literal<*> || (arg.value as? String)?.contains("+") == true
    }
}.map { call ->
    mapOf(
        "type" to "SQL_INJECTION",
        "severity" to "high",
        "file" to call.location?.artifactLocation?.uri,
        "line" to call.location?.region?.startLine,
        "function" to call.name.localName,
        "code" to call.code,
        "recommendation" to "Use PreparedStatement with parameterized queries"
    )
}

log("Found ${sqlInjectionRisks.size} SQL injection risks")

// ========================================
// 检测 2: 命令注入风险
// ========================================
log("Detecting command injection risks...")

val commandCalls = result.allNodes<CallExpression>().filter { call ->
    call.name.localName in listOf(
        "exec", "Runtime.getRuntime", "ProcessBuilder",
        "eval", "system"
    )
}

val commandInjectionRisks = commandCalls.map { call ->
    mapOf(
        "type" to "COMMAND_INJECTION",
        "severity" to "critical",
        "file" to call.location?.artifactLocation?.uri,
        "line" to call.location?.region?.startLine,
        "function" to call.name.localName,
        "code" to call.code,
        "recommendation" to "Sanitize user input and avoid executing system commands"
    )
}

log("Found ${commandInjectionRisks.size} command injection risks")

// ========================================
// 检测 3: 路径遍历风险
// ========================================
log("Detecting path traversal risks...")

val fileCalls = result.allNodes<CallExpression>().filter { call ->
    call.name.localName in listOf(
        "File", "FileInputStream", "FileOutputStream",
        "FileReader", "FileWriter"
    )
}

val pathTraversalRisks = fileCalls.filter { call ->
    // 检查文件路径参数是否包含用户输入
    call.arguments.any { arg ->
        // 简化检测：如果参数不是字面量，可能来自用户输入
        arg !is Literal<*>
    }
}.map { call ->
    mapOf(
        "type" to "PATH_TRAVERSAL",
        "severity" to "medium",
        "file" to call.location?.artifactLocation?.uri,
        "line" to call.location?.region?.startLine,
        "function" to call.name.localName,
        "code" to call.code,
        "recommendation" to "Validate and sanitize file paths, use whitelist of allowed paths"
    )
}

log("Found ${pathTraversalRisks.size} path traversal risks")

// ========================================
// 检测 4: XSS 风险（Web 应用）
// ========================================
log("Detecting XSS risks...")

val outputCalls = result.allNodes<CallExpression>().filter { call ->
    call.name.localName in listOf(
        "print", "println", "write", "getWriter",
        "setAttribute", "sendRedirect"
    )
}

val xssRisks = outputCalls.filter { call ->
    // 检查是否直接输出用户输入
    call.arguments.any { arg ->
        arg !is Literal<*>
    }
}.map { call ->
    mapOf(
        "type" to "XSS",
        "severity" to "high",
        "file" to call.location?.artifactLocation?.uri,
        "line" to call.location?.region?.startLine,
        "function" to call.name.localName,
        "code" to call.code,
        "recommendation" to "Use HTML encoding for output, or use template engines with auto-escaping"
    )
}

log("Found ${xssRisks.size} XSS risks")

// ========================================
// 输出结果
// ========================================
output(mapOf(
    "summary" to mapOf(
        "totalRisks" to (sqlInjectionRisks.size + commandInjectionRisks.size + pathTraversalRisks.size + xssRisks.size),
        "criticalCount" to commandInjectionRisks.size,
        "highCount" to (sqlInjectionRisks.size + xssRisks.size),
        "mediumCount" to pathTraversalRisks.size
    ),
    "vulnerabilities" to mapOf(
        "sqlInjection" to sqlInjectionRisks,
        "commandInjection" to commandInjectionRisks,
        "pathTraversal" to pathTraversalRisks,
        "xss" to xssRisks
    )
))

log("✅ Security vulnerability scan completed")
