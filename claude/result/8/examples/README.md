# CPG 容器化架构 - POC 示例

本目录包含 CPG 容器化架构的 POC（概念验证）示例，包括：
- Docker Compose 配置
- 环境变量示例
- 示例查询脚本（.kts）

---

## 快速开始（5 分钟）

### 1. 准备环境

**前置条件**：
- Docker 24.0+
- Docker Compose 2.20+

**验证**：
```bash
docker --version
docker-compose --version
```

---

### 2. 配置环境变量

```bash
# 复制环境变量模板
cp .env.example .env

# 编辑 .env 文件（根据你的项目修改）
vi .env
```

**最小配置**（使用默认值）：
```bash
GIT_REPO=https://github.com/spring-projects/spring-petclinic.git
GIT_REF=main
PASSES=EvaluationOrderGraphPass,ControlFlowSensitiveDFGPass,UnreachableEOGPass
LANGUAGES=java
```

---

### 3. 启动构建容器

```bash
# 构建 CPG 图（一次性运行）
docker-compose up builder

# 查看构建统计
cat results/graph.dump.stats.json | jq .
```

**预期输出**：
```
✅ Build completed successfully!
   - Graph: /output/graph.dump
   - Config: /output/graph.dump.config.json
   - Stats: /output/graph.dump.stats.json
========================================
Build Statistics:
{
  "nodeCount": 50000,
  "edgeCount": 255000,
  "buildDurationSeconds": 180
}
```

---

### 4. 运行第一个查询

```bash
# 设置查询名称
export QUERY_NAME=find-unreachable-code

# 运行查询
docker-compose run query

# 查看结果
cat results/find-unreachable-code-result.json | jq .
```

**预期输出**：
```
✅ Query completed successfully!
```

**结果示例**（`results/find-unreachable-code-result.json`）：
```json
[
  {
    "totalIfStatements": 1250,
    "unreachableBranches": [
      {
        "file": "src/main/java/org/springframework/samples/petclinic/owner/OwnerController.java",
        "line": 45,
        "code": "if (true) { ... } else { /* unreachable */ }"
      }
    ]
  }
]
```

---

### 5. 并发执行多个查询

```bash
# 方法 1: 使用专用查询容器并发运行
docker-compose up query-security query-quality query-unreachable

# 方法 2: 使用 scale 扩展查询容器（运行相同查询）
docker-compose up --scale query=3 query
```

---

## 目录结构

```
examples/
├── docker-compose.yml    # Docker Compose 配置文件
├── .env.example          # 环境变量模板
├── README.md             # 本文档
├── queries/              # 查询脚本目录
│   ├── find-unreachable-code.kts
│   ├── security-scan.kts
│   ├── code-quality-check.kts
│   ├── license-check.kts
│   └── complexity-analysis.kts
└── results/              # 查询结果输出目录（运行后自动创建）
```

---

## 查询脚本说明

### 1. find-unreachable-code.kts

**功能**：查找不可达代码（死代码）

**前置要求**：构建时需注册 `UnreachableEOGPass`

**示例输出**：
```json
{
  "totalIfStatements": 1250,
  "unreachableBranches": [
    {"file": "Foo.java", "line": 42, "code": "if (true) { ... } else { ... }"}
  ]
}
```

---

### 2. security-scan.kts

**功能**：安全漏洞扫描（SQL 注入、命令注入、XSS 等）

**前置要求**：构建时需注册 `ControlFlowSensitiveDFGPass`

**示例输出**：
```json
{
  "sqlInjectionRisks": [
    {"file": "UserDao.java", "line": 30, "query": "SELECT * FROM users WHERE id = " + userId}
  ],
  "commandInjectionRisks": [
    {"file": "FileHandler.java", "line": 50, "command": "Runtime.exec(userInput)"}
  ]
}
```

---

### 3. code-quality-check.kts

**功能**：代码质量检查（圈复杂度、代码异味等）

**示例输出**：
```json
{
  "highComplexityFunctions": [
    {"functionName": "processOrder", "file": "OrderService.java", "line": 100, "complexity": 15}
  ],
  "longMethods": [
    {"functionName": "generateReport", "file": "ReportGenerator.java", "line": 200, "loc": 500}
  ]
}
```

---

### 4. license-check.kts

**功能**：开源许可证合规检查

**示例输出**：
```json
{
  "dependenciesWithGPL": [
    {"library": "mysql-connector-java", "license": "GPL", "risk": "高"}
  ],
  "missingLicenseHeaders": [
    {"file": "CustomUtil.java", "line": 1}
  ]
}
```

---

### 5. complexity-analysis.kts

**功能**：代码复杂度分析

**示例输出**：
```json
{
  "averageComplexity": 4.5,
  "maxComplexity": 25,
  "functionsOverThreshold": [
    {"functionName": "handleRequest", "file": "RequestHandler.java", "complexity": 25}
  ]
}
```

---

## 自定义查询脚本

### 创建新查询脚本

1. 在 `queries/` 目录创建新文件（如 `my-custom-query.kts`）
2. 编写查询逻辑（参考现有脚本）
3. 更新 `.env` 文件：`QUERY_NAME=my-custom-query`
4. 运行查询：`docker-compose run query`

**示例**（`queries/my-custom-query.kts`）：
```kotlin
// 查询所有包含 TODO 注释的位置
import de.fraunhofer.aisec.cpg.graph.statements.expressions.Literal

val allLiterals = result.allNodes<Literal<String>>()

val todoComments = allLiterals.filter {
    it.value?.contains("TODO", ignoreCase = true) == true
}

log("Found ${todoComments.size} TODO comments")

output(mapOf(
    "totalTodos" to todoComments.size,
    "locations" to todoComments.map {
        mapOf(
            "file" to it.location?.artifactLocation?.uri,
            "line" to it.location?.region?.startLine,
            "comment" to it.value
        )
    }
))
```

---

## 高级用法

### 1. 并发执行多个查询（不同查询脚本）

**方法**：使用专用查询容器

```bash
# 同时运行 3 个不同的查询
docker-compose up query-security query-quality query-unreachable

# 查看所有结果
ls -lh results/
# security-result.json
# quality-result.json
# unreachable-result.json
```

---

### 2. 使用 Kubernetes 部署

**部署构建容器（Job）**：
```bash
kubectl apply -f k8s/build-job.yaml
```

**部署查询容器（Deployment + HPA）**：
```bash
kubectl apply -f k8s/query-deployment.yaml
kubectl apply -f k8s/query-hpa.yaml
```

---

### 3. 集成到 CI/CD

**GitLab CI 示例**（`.gitlab-ci.yml`）：
```yaml
stages:
  - build-graph
  - security-scan

build-cpg-graph:
  stage: build-graph
  image: cpg-builder:v1.0
  script:
    - docker-compose up builder
  artifacts:
    paths:
      - results/graph.dump*

security-scan:
  stage: security-scan
  image: cpg-query:v1.0
  dependencies:
    - build-cpg-graph
  script:
    - export QUERY_NAME=security-scan
    - docker-compose run query
  artifacts:
    reports:
      security: results/security-result.json
```

---

## 故障排查

### 问题 1: 构建容器失败

**检查日志**：
```bash
docker-compose logs builder
```

**常见原因**：
- Git 仓库无法访问（检查 `GIT_REPO` URL）
- Pass 配置错误（检查 `PASSES` 顺序）
- 内存不足（增加 `JAVA_OPTS=-Xmx8g`）

---

### 问题 2: 查询容器无法找到图文件

**验证图文件存在**：
```bash
docker run --rm -v cpg-examples_graph-data:/data alpine ls -lh /data
```

**预期输出**：
```
-rw-r--r-- 1 root root 45M Nov 12 10:30 graph.dump
-rw-r--r-- 1 root root 1.2K Nov 12 10:30 graph.dump.config.json
-rw-r--r-- 1 root root 512 Nov 12 10:30 graph.dump.stats.json
```

---

### 问题 3: 查询结果为空

**检查 Pass 配置**：
```bash
cat results/graph.dump.config.json | jq .registeredPasses
```

**验证查询脚本逻辑**：
```bash
# 添加调试日志
log("DEBUG: Total nodes: ${result.allNodes<Node>().size}")
```

---

## 下一步

- **阅读完整文档**：`/claude/result/8/`
  - `8.1-架构-容器化设计.md`：架构设计和原理
  - `8.2-实现-分步指南.md`：详细实施步骤
  - `8.3-手册-使用指南.md`：完整使用指南
  - `8.4-参考-API文档.md`：API 参考

- **生产部署**：参考 Kubernetes 部署指南

- **社区贡献**：向 CPG 项目提交 PR

---

**版本**: 1.0
**最后更新**: 2025-11-12
**反馈**: https://github.com/Fraunhofer-AISEC/cpg/issues
