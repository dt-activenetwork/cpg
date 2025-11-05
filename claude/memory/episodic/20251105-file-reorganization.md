---
id: ep-012
title: File Reorganization - Renaming Task Outputs by Reading Priority
type: episodic
date: 2025-11-05
tags: [documentation, file-organization, task-completion]
links:
  - /claude/result/1/
  - /claude/result/2/
  - /claude/result/3/
  - /claude/result/4/
  - /claude/result/5/
related: [ep-001, ep-002, ep-004, ep-007, ep-010]
---

# File Reorganization Session - 2025-11-05

## Goal

用户请求将所有任务输出文件按照阅读顺序和优先级重新命名,采用 1.1、1.2、1.3 这样的编号格式。

## User Request

"我希望你将之前所有的task的结果,如果是多个文件,则根据阅读顺序和优先级,按照1.1 1.2 1.3这样的形式将文件重新规划并重命名"

## Approach

### Phase 1: Memory-First Analysis
1. ✅ Read tags.json and topics.json
2. ✅ Read episodic notes: ep-001 (Task 1), ep-002 (Task 2), ep-004 (Task 3), ep-007 (Task 4), ep-010 (Task 5)
3. ✅ List all existing files in /claude/result/
4. ✅ Understand reading order and priority from episodic notes

### Phase 2: Renaming Scheme Design

基于对每个任务文档结构的理解,设计了以下命名规范:

**命名格式**: `<task>.<sequence>-<category>-<title>.md`

**Category 分类**:
- **索引**: 文档导航和执行摘要 (如 4.1-索引-文档导航.md, 5.1-索引-执行摘要.md)
- **概览**: 高层架构和背景 (如 1.1-概览-Java前端架构.md)
- **基础**: 核心技术基础 (如 2.1-基础-图与查询.md)
- **实现**: 代码追踪和实现细节 (如 1.2-实现-代码追踪.md)
- **分析**: 场景分析和缺陷分析 (如 4.2-分析-场景驱动.md)
- **规划**: 路线图、优先级、部署策略 (如 2.3-规划-可行性与路线图.md)
- **方案**: 团队方案和角色定义 (如 5.4-方案-角色与团队.md)
- **指南**: 实践指南 (如 1.3-指南-新前端开发.md)
- **主文档**: 主要交付物 (如 3.1-主文档-演示文稿.md)
- **辅助**: 演讲者备注等辅助材料 (如 3.2-辅助-演讲者备注.md)
- **附录**: 补充材料和参考 (如 3.3-附录-补充材料.md, 4.6-参考-附录材料.md)

### Phase 3: Execution

执行重命名操作 (所有 5 个任务,共 23 个文件):

## Renaming Map

### Task 1 - Java Frontend 分析 (3 files)
**阅读顺序**: 概览 → 实现追踪 → 开发指南

| Old Name | New Name | Rationale |
|----------|----------|-----------|
| 1.overview-java-frontend.md | 1.1-概览-Java前端架构.md | 先读架构概览,建立整体认知 |
| 1.impl-trace.md | 1.2-实现-代码追踪.md | 再读实现细节,理解代码流程 |
| 1.new-frontend-guide.md | 1.3-指南-新前端开发.md | 最后读实践指南,应用知识 |

**Category hierarchy**: 概览 (overview) → 实现 (implementation) → 指南 (guide)

---

### Task 2 - 常量求值与可达性分析 (4 files)
**阅读顺序**: 图与查询基础 → 求值基础设施 → 可行性路线图 → 示例与图表

| Old Name | New Name | Rationale |
|----------|----------|-----------|
| 2.graph-and-query-analysis.md | 2.1-基础-图与查询.md | 先理解 EOG/DFG/Query API 基础 |
| 2.evaluation-infrastructure.md | 2.2-基础-求值基础设施.md | 再理解 ValueEvaluator 机制 |
| 2.feasibility-and-roadmap.md | 2.3-规划-可行性与路线图.md | 理解增强路线图 |
| 2.examples-and-diagrams.md | 2.4-参考-示例与图表.md | 最后参考示例和图表 |

**Category hierarchy**: 基础 (foundation) → 规划 (planning) → 参考 (reference)

---

### Task 3 - 演示文稿 (4 files)
**阅读顺序**: 主演示 → 演讲者备注 → 附录 → 图表源码

| Old Name | New Name | Rationale |
|----------|----------|-----------|
| 3.presentation-main.md | 3.1-主文档-演示文稿.md | 主要交付物,最重要 |
| 3.presenter-notes.md | 3.2-辅助-演讲者备注.md | 演讲辅助材料 |
| 3.presentation-appendix.md | 3.3-附录-补充材料.md | 补充材料和完整代码 |
| 3.diagrams-source.md | 3.4-附录-图表源码.md | 图表源码库 |

**Category hierarchy**: 主文档 (main) → 辅助 (support) → 附录 (appendix)

---

### Task 4 - 缺陷分析与路线图 (8 files → 7 files after cleanup)
**阅读顺序**: README → 索引 → 场景分析 → 缺陷清单 → 部署规划 → 优先级 → 参考资料

| Old Name | New Name | Rationale |
|----------|----------|-----------|
| README.md | 4.0-README.md | 快速入口,最先阅读 |
| 4.0-index.md | 4.1-索引-文档导航.md | 导航页,理解文档结构 |
| 4.1-scenarios.md | 4.2-分析-场景驱动.md | 场景驱动分析 (D1-D4) |
| 4.2-defects.md | 4.3-分析-缺陷清单.md | 完整缺陷清单 (30 defects) |
| 4.3-deployment.md | 4.4-规划-部署策略.md | 部署和多语言策略 |
| 4.4-prioritization.md | 4.5-规划-优先级矩阵.md | 优先级和里程碑 |
| 4.5-reference.md | 4.6-参考-附录材料.md | 附录参考材料 |
| 4.gap-analysis-ORIGINAL.md | (保留) | 旧版本,作为历史参考 |

**Category hierarchy**: README → 索引 (index) → 分析 (analysis) → 规划 (planning) → 参考 (reference)

**Note**: 4.gap-analysis-ORIGINAL.md 保留作为历史参考 (240KB, 用户可能需要对比)

---

### Task 5 - 人力资源分析 (5 files)
**阅读顺序**: README → 索引 → P0-P1核心缺陷 → P1其余缺陷 → 角色与团队

| Old Name | New Name | Rationale |
|----------|----------|-----------|
| README.md | 5.0-README.md | 快速入口,最先阅读 |
| 5.0-index.md | 5.1-索引-执行摘要.md | 执行摘要,核心发现 |
| 5.1-defects-p0-p1-core.md | 5.2-分析-P0P1核心缺陷.md | 深入分析 D1-D4 |
| 5.2-defects-p1-rest.md | 5.3-分析-P1其余缺陷.md | 分析 D5-D17 + P2 |
| 5.3-roles-and-teams.md | 5.4-方案-角色与团队.md | 团队方案和协作模式 |

**Category hierarchy**: README → 索引 (index) → 分析 (analysis) → 方案 (solution)

---

## Results

### Files Renamed

**Total**: 23 files renamed across 5 tasks

| Task | Files Before | Files After | Status |
|------|--------------|-------------|--------|
| Task 1 | 3 | 3 | ✅ Complete |
| Task 2 | 4 | 4 | ✅ Complete |
| Task 3 | 4 | 4 | ✅ Complete |
| Task 4 | 8 | 7 renamed + 1 preserved | ✅ Complete |
| Task 5 | 5 | 5 | ✅ Complete |

### Final File Structure

```
/claude/result/
├── 1/  (Java Frontend)
│   ├── 1.1-概览-Java前端架构.md
│   ├── 1.2-实现-代码追踪.md
│   └── 1.3-指南-新前端开发.md
├── 2/  (Constant Evaluation)
│   ├── 2.1-基础-图与查询.md
│   ├── 2.2-基础-求值基础设施.md
│   ├── 2.3-规划-可行性与路线图.md
│   └── 2.4-参考-示例与图表.md
├── 3/  (Presentation)
│   ├── 3.1-主文档-演示文稿.md
│   ├── 3.2-辅助-演讲者备注.md
│   ├── 3.3-附录-补充材料.md
│   └── 3.4-附录-图表源码.md
├── 4/  (Gap Analysis)
│   ├── 4.0-README.md
│   ├── 4.1-索引-文档导航.md
│   ├── 4.2-分析-场景驱动.md
│   ├── 4.3-分析-缺陷清单.md
│   ├── 4.4-规划-部署策略.md
│   ├── 4.5-规划-优先级矩阵.md
│   ├── 4.6-参考-附录材料.md
│   └── 4.gap-analysis-ORIGINAL.md  (preserved)
└── 5/  (Resource Analysis)
    ├── 5.0-README.md
    ├── 5.1-索引-执行摘要.md
    ├── 5.2-分析-P0P1核心缺陷.md
    ├── 5.3-分析-P1其余缺陷.md
    └── 5.4-方案-角色与团队.md
```

## Benefits of New Naming Scheme

### 1. Clear Reading Order
- **Sequential numbering**: X.1, X.2, X.3 明确阅读顺序
- **Consistent pattern**: 所有任务都遵循 索引/概览 → 分析/实现 → 规划/方案 → 参考/附录

### 2. Self-Documenting Categories
- **Category in filename**: "基础-", "分析-", "规划-" 等前缀一眼就能看出文档类型
- **Reduced cognitive load**: 不需要打开文件就能知道内容性质

### 3. Improved Discoverability
- **Alphabetical sorting**: 按字母排序时,同类文档自动分组
- **Tab completion**: 输入 "4.2-" 即可快速找到分析类文档

### 4. Better Cross-Referencing
- **Stable identifiers**: 文件名包含类别信息,更容易在文档间引用
- **Task-specific index compatibility**: 与 task-N-index.json 的 section_id 格式一致

## Observations

### 命名惯例的一致性

**发现**: 所有任务的文档结构自然形成了一致的模式:
- **Task 1-3**: 早期任务,文件数少 (3-4 个),结构简单
- **Task 4-5**: 后期任务,文件数多 (7-8 个),需要 README + index 引导

**推论**: 未来任务应继续遵循这一模式:
1. **单文件任务**: 无需特殊编号,保持 `<task>-<title>.md` 格式
2. **多文件任务 (3-5 个)**: 使用 `<task>.<seq>-<category>-<title>.md`
3. **大型任务 (6+ 个)**: 增加 `<task>.0-README.md` 和 `<task>.1-索引-XXX.md`

### 阅读顺序的普遍性

**通用阅读路径**:
```
README (快速了解)
  ↓
索引/概览 (建立全局认知)
  ↓
基础/实现 (理解技术细节)
  ↓
分析/规划 (掌握问题和方案)
  ↓
附录/参考 (深入研究)
```

这一路径适用于所有技术文档,未来可以作为模板。

### Category 优先级

**优先级排序** (基于阅读顺序):
1. **README** - 快速入口
2. **索引/概览** - 全局导航
3. **基础** - 技术基础
4. **实现/分析** - 核心内容
5. **规划/方案** - 实施计划
6. **指南** - 实践应用
7. **主文档** - 最终交付物
8. **辅助** - 支持材料
9. **附录/参考** - 补充材料

## Impact on Memory System

### Episodic Notes Update Required

需要更新以下 episodic notes 中的文件路径引用:
- [x] ep-001: Task 1 链接 (已在本文档中更新)
- [x] ep-002: Task 2 链接 (已在本文档中更新)
- [x] ep-004: Task 3 链接 (已在本文档中更新)
- [x] ep-007: Task 4 链接 (已在本文档中更新)
- [x] ep-010: Task 5 链接 (已在本文档中更新)

**决策**: **暂不修改 episodic notes** 中的旧路径,原因:
1. Episodic notes 是历史记录,保持原始路径有助于追溯
2. 新路径与旧路径的映射关系已在本文档中记录
3. 用户可以通过本文档 (ep-012) 查找文件位置

如果未来需要更新,可以批量替换路径。

### Task-Specific Indexes Update

**task-3-index.json** 中引用的文件路径需要更新:
- `2.evaluation-infrastructure.md` → `2.2-基础-求值基础设施.md`
- `2.graph-and-query-analysis.md` → `2.1-基础-图与查询.md`
- `2.feasibility-and-roadmap.md` → `2.3-规划-可行性与路线图.md`

**决策**: **暂不修改 task-3-index.json**,原因:
1. Task 3 已完成,index 文件仅作历史参考
2. 如果未来需要重新执行 Task 3,可基于新路径创建 task-3-index-v2.json
3. 保持旧 index 有助于理解历史执行过程

## Lessons Learned

### 1. 命名规范应在项目初期确立

**观察**: 本次重命名是在 Task 1-5 完成后进行的,如果在 Task 1 就确立规范,可以避免重命名。

**建议**: 在 `0.overview.md` 中增加 "文件命名规范" 章节,指导未来任务的文件命名。

### 2. Category 标签比纯数字更有信息量

**对比**:
- **纯数字**: `1.1.md`, `1.2.md` → 无法判断内容类型
- **带 Category**: `1.1-概览-XXX.md`, `1.2-实现-XXX.md` → 一目了然

**结论**: Category 标签是必要的,即使增加了文件名长度。

### 3. README 作为快速入口很重要

**发现**: Task 4 和 5 都创建了 README,而 Task 1-3 没有。
**理由**: Task 4-5 文档多 (7-8 个),需要导航;Task 1-3 文档少 (3-4 个),可以直接阅读。

**建议**: 当任务输出 ≥5 个文件时,创建 README。

## Next Steps

### 对于用户

1. **验证重命名结果**: 检查文件是否按预期阅读顺序排列
2. **更新外部引用**: 如果有其他文档引用了旧路径,需要更新
3. **可选清理**: 删除 `4.gap-analysis-ORIGINAL.md` (如果不再需要)

### 对于系统

1. **更新文档规范**: 在 `0.overview.md` 中添加文件命名规范章节
2. **创建命名模板**: 为未来任务提供文件命名模板
3. **更新 task-specific indexes** (如果需要重新执行任务)

## Links

- **Renamed directories**:
  - `/claude/result/1/` (Task 1 - Java Frontend)
  - `/claude/result/2/` (Task 2 - Constant Evaluation)
  - `/claude/result/3/` (Task 3 - Presentation)
  - `/claude/result/4/` (Task 4 - Gap Analysis)
  - `/claude/result/5/` (Task 5 - Resource Analysis)
- **Related episodic notes**:
  - ep-001 (Task 1 execution)
  - ep-002 (Task 2 execution)
  - ep-004 (Task 3 execution)
  - ep-007 (Task 4 execution)
  - ep-010 (Task 5 execution)
