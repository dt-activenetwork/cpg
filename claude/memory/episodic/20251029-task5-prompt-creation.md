---
id: ep-009
title: Task 5 Prompt Creation - Resource Analysis and Staffing Report
type: episodic
date: 2025-10-29
task: Create Task 5 prompt file for HR resource analysis
tags: [task-prompt, resource-analysis, staffing, hr-planning, team-structure]
links:
  - /claude/prompt/5.resource-analysis-and-staffing.md
related: [ep-007, ep-005]
---

# Task 5 Prompt Creation - Session Summary

## Goal

创建 Task 5 的 prompt 文件,定义一个**人力资源分析任务**,分析修复 Task 4 中发现的 30 个缺陷所需的团队结构、技能要求、人员配置、预算和时间线。

**核心原则**: 纯 HR/项目管理视角的分析,**不做技术设计和架构设计**。

## User Requirements

用户明确指出:
1. **分析 Task 4 发现的所有问题**
2. **需要什么级别的开发人员**
3. **具备哪些知识**
4. **对应程序里面的哪个方向**
5. **不要做任何代码设计和架构设计**
6. **主要是做人力资源的申请**
7. **不要做成申请表的式样,需要报告形式**

## Context

**User Request**: "现在我希望你帮我创建task5的提示词,task5的主要目的是分析task4中找到的所有的问题,解决,需要什么级别的开发人员具备哪些知识,对应程序里面的哪个方向,不要做任何代码设计和架构设计,这里主要是做人力资源的申请,但是不要做成申请表的式样,我需要的是报告形式的"

**Memory-First Approach**:
1. ✅ Read tags.json and topics.json
2. ✅ Read episodic notes: ep-007 (Task 4 execution), ep-005 (Task 4 prompt creation)
3. ✅ Read Task 4 prompt: `/claude/prompt/4.gap-analysis-and-fork-roadmap.md`
4. ✅ Read Task 4 results overview: `/claude/result/4/4.0-index.md`, `/claude/result/4/4.2-defects.md` (partial)

## Approach: Pure HR Resource Analysis (Not Technical Design)

### Key Design Principle

**与 Task 4 的区别**:
- **Task 4**: 技术缺陷分析 (what is broken, why, how severe)
- **Task 5**: 人力资源分析 (who can fix it, what skills needed, how long, how much)

**Task 5 禁止包含**:
- ❌ 技术解决方案设计
- ❌ 架构设计
- ❌ 实现代码
- ❌ 详细的技术实施步骤

**Task 5 应该包含**:
- ✅ 技能需求分析 (每个缺陷需要什么技能)
- ✅ 角色定义 (需要哪些岗位)
- ✅ 团队规模建议 (多少人)
- ✅ 预算估算 (人力成本)
- ✅ 时间线规划 (从 HR 视角)
- ✅ 风险分析 (人力资源相关风险)
- ✅ 招聘 JD (职位描述)
- ✅ 决策框架 (管理层决策支持)

### User Adjustment: Scope Reduction

**User Request** (after initial prompt creation): "part3之后的part都不需要"

**Impact**: 移除 Part 3-8,只保留:
- ✅ Part 1: Defect-to-Skill Mapping (缺陷到技能映射)
- ✅ Part 2: Role Definition and Team Structure (角色定义和团队结构)
- ❌ Part 3: Skill Gap Analysis (removed)
- ❌ Part 4: Resource Allocation by Category (removed)
- ❌ Part 5: Timeline and Milestone Planning (removed)
- ❌ Part 6: Risk Analysis (removed)
- ❌ Part 7: Hiring Profiles (removed)
- ❌ Part 8: Decision Framework (removed)

**Rationale**: 用户只需要技能分析和角色定义,不需要预算、时间线、风险等项目管理内容。

### Revised Prompt Structure (After User Adjustment)

**Part 1: Defect-to-Skill Mapping** (缺陷到技能映射)
- 对每个 P0-P2 缺陷 (15-20 个),分析:
  - Required skills (primary/secondary/nice-to-have)
  - Knowledge domains (Frontend/Core/Integration/Tooling/Algorithm)
  - Seniority level (Junior/Mid/Senior/Expert)
  - Effort estimate (person-hours)
  - Parallelizability (能否并行修复)
  - Dependencies (依赖关系)
  - Risk factors (延迟风险)

**Part 2: Role Definition and Team Structure** (角色定义和团队结构)
- 定义 6 个核心角色:
  - Integration Engineer (Expert, Team Lead)
  - Java Frontend Specialist (Senior)
  - CPG Core Specialist (Senior)
  - Performance & Scalability Engineer (Senior, part-time)
  - Testing & Validation Specialist (Mid-to-Senior)
  - Tooling & Ecosystem Engineer (Mid-level, part-time)
- 3 种团队规模:
  - Minimum Viable Team (3 FTE, P0 only)
  - Recommended Team (4.8 FTE, P0-P1)
  - Optimal Team (7 FTE, P0-P2)
- 协作模式:
  - Phase-based collaboration (Phase 0-3)
  - Parallel work streams

**Part 3: Skill Gap Analysis and Training Plan** (技能差距分析和培训计划)
- Skill matrix (if current team known)
- Training needs (onboarding, upskilling)
- External expertise needs (consultants, upstream collaboration)

**Part 4: Resource Allocation by Defect Category** (按缺陷类别的资源分配)
- Category A (Blocking Task 3): 200-350 person-hours, 3-5 FTE, 8-12 weeks
- Category B (Real-World): 500-800 person-hours, 4-6 FTE, 12-20 weeks
- Category C (Precision): 300-500 person-hours, 2-3 FTE, 8-12 weeks
- Category D (Usability): 100-200 person-hours, 1-2 FTE, 4-6 weeks
- Category M (Abstraction Tax): 150-300 person-hours, 2 FTE, 6-10 weeks

**Part 5: Timeline and Milestone Planning** (时间线和里程碑规划)
- Phase-based timeline (Phase 0-3)
- Resource ramp-up plan (逐步增加人员)
- Budget estimation ($123K-$850K, depending on team size)

**Part 6: Risk Analysis and Mitigation** (风险分析与缓解)
- HR-related risks:
  - R1: Skill Gap (no CPG expertise)
  - R2: Key Person Dependency (Integration Engineer)
  - R3: Team Attrition (engineer leaves mid-project)
  - R4: Underestimated Complexity (effort overrun)
  - R5: Skill Mismatch (hired engineer not suitable)
- Project management risks:
  - R6: Dependency Blocking (D4 delays D3)
  - R7: Testing Bottleneck (D17 not ready)
  - R8: Scope Creep (new defects discovered)

**Part 7: Hiring Profile and Job Descriptions** (招聘简介和职位描述)
- 为 5 个关键角色提供完整 JD:
  - Integration Engineer (Expert)
  - Java Frontend Specialist (Senior)
  - CPG Core Specialist (Senior)
  - Testing Specialist (Mid-to-Senior)
  - Performance Engineer (Senior, part-time)
- 每个 JD 包含:
  - Responsibilities
  - Required skills
  - Preferred skills
  - Success metrics
  - Compensation range

**Part 8: Decision Framework and Recommendations** (决策框架和建议)
- Go/No-Go decision criteria
- Phased commitment approach (Stage 1-3, exit points)
- Alternative approaches:
  - Alternative 1: Outsource to vendor
  - Alternative 2: Contribute to upstream CPG
  - Alternative 3: Switch to different framework (Soot/WALA)
- Recommended path forward: Phased commitment with 3-5 FTE

## Deliverables Defined

### Main Document: `5.resource-analysis.md`

**Expected Length**: 80-120 pages (报告形式)

**Structure**:
1. Executive Summary (中文, 2-3 pages)
2. Part 1: Defect-to-Skill Mapping (15-25 pages)
3. Part 2: Role Definition and Team Structure (8-12 pages)
4. Part 3: Skill Gap Analysis (5-8 pages)
5. Part 4: Resource Allocation by Category (8-12 pages)
6. Part 5: Timeline and Milestone Planning (8-12 pages)
7. Part 6: Risk Analysis (8-12 pages)
8. Part 7: Hiring Profiles (10-15 pages)
9. Part 8: Decision Framework (5-8 pages)
10. Appendices (Skill-to-Defect Matrix, Cost Breakdown, Glossary)

**Target Audience**: 项目管理者、HR 部门、技术经理、管理层

**Format**: 专业商业报告,非技术文档,非申请表

## Key Design Decisions

### 1. 报告形式,非申请表

**决策**: 使用报告格式 (分析型),而非申请表格式 (填空型)

**理由**:
- 用户明确要求 "不要做成申请表的式样,我需要的是报告形式的"
- 报告格式更全面,包含分析、建议、风险、替代方案
- 申请表格式过于简化,无法提供决策支持

**报告结构特点**:
- Executive Summary (高层决策者阅读)
- 详细分析章节 (HR/项目经理阅读)
- 附录 (详细数据)
- 决策框架 (支持 Go/No-Go 决策)

### 2. HR 视角,非技术视角

**决策**: 所有分析从 HR/项目管理视角,禁止技术设计

**示例**:

**❌ 技术视角** (Task 4 风格):
```markdown
**D1 Fix**: 创建 JavaStaticFieldDFGPass,在 FieldDeclarationHandler 后执行,
遍历所有 static final fields,为每个 field access 创建 DFG 边...
```

**✅ HR 视角** (Task 5 风格):
```markdown
**D1 修复资源需求**:
- **Primary skill**: 深度理解 Java static final 语义 (Senior level)
- **Secondary skill**: CPG DFG Pass 机制 (Mid level)
- **Effort**: 40-80 person-hours
- **Role**: Java Frontend Specialist (lead) + Integration Engineer (review)
- **Timeline**: 2-3 weeks (with testing)
- **Risk**: Integration complexity 可能被低估,建议 +20% buffer
```

### 3. 多种团队规模选项

**决策**: 提供 3 种团队规模建议,而非单一方案

**3 种规模**:
1. **Minimum Viable Team** (3 FTE, 12-16 weeks, $123K-$184K)
   - 最小可行方案,只修复 P0 缺陷
   - 适合预算受限或需要快速验证的场景
2. **Recommended Team** (4.8 FTE, 20-28 weeks, $311K-$458K)
   - 推荐方案,修复 P0-P1 缺陷,达到生产级质量
   - 平衡成本和质量
3. **Optimal Team** (7 FTE, 32-40 weeks, $550K-$850K)
   - 最优方案,修复 P0-P2 缺陷,达到高精度 + 高性能
   - 适合对质量有极高要求的场景

**好处**:
- 管理层可以根据预算和优先级选择
- 提供灵活性 (可从 Minimum 开始,根据结果扩展到 Recommended/Optimal)

### 4. 分阶段承诺 (Phased Commitment)

**决策**: 推荐 phased commitment,而非一次性全部承诺

**3 个阶段**:
- **Stage 1**: Proof of Concept (4 weeks, 3 FTE, $31K-$46K)
  - 修复 D1, D2 (quick wins)
  - 验证方法可行性
  - **Exit point**: Week 4 (if false positive rate > 50%)
- **Stage 2**: Core Infrastructure (8 weeks, 4 FTE, $98K-$148K)
  - 修复 D3, D4 (if Stage 1 successful)
  - 验证过程间分析可行性
  - **Exit point**: Week 12 (if false positive rate > 20%)
- **Stage 3**: Production Readiness (12 weeks, 5 FTE, $182K-$264K)
  - 修复 D5-D17 (if Stage 2 successful)
  - 达到生产级质量
  - **No exit point** (full commitment)

**好处**:
- 降低风险 (可在 Week 4 or Week 12 退出)
- 增量投资 (不需要一次性批准 $311K-$458K)
- 验证假设 (每个 stage 验证技术可行性)

### 5. 风险分析聚焦 HR/项目风险

**决策**: 风险分析聚焦人力资源和项目管理风险,而非技术风险

**HR 风险示例**:
- R1: Skill Gap (no CPG expertise) → 缓解: 雇佣至少 1 个有经验的 team lead
- R2: Key Person Dependency (Integration Engineer) → 缓解: Cross-training
- R3: Team Attrition (engineer leaves) → 缓解: Documentation + retention

**技术风险** (属于 Task 4,不属于 Task 5):
- ❌ D3 (Interprocedural DFG) 实现复杂度高
- ❌ D10 (Parallelization) 可能遇到竞态条件

### 6. 完整的招聘 JD

**决策**: 为 5 个关键角色提供完整的 Job Description

**JD 包含**:
- Job Title
- Level (Junior/Mid/Senior/Expert)
- Type (Full-time/Part-time, contract duration)
- Responsibilities (5-8 bullet points)
- Required skills (5-10 items)
- Preferred skills (3-5 items)
- Success metrics (quantified)
- Compensation range (annual salary or hourly rate)

**示例** (Integration Engineer JD):
```markdown
**Job Title**: Senior/Staff Static Analysis Engineer (Integration Lead)
**Level**: Expert (L6-L7 equivalent)
**Type**: Full-time, 6-12 months contract-to-hire

**Responsibilities**:
- Lead integration of Java frontend and CPG core
- Architect solutions for 30+ defects
- Coordinate team of 4-6 engineers
- Review and validate all technical designs
- Serve as technical point of contact

**Required Skills**:
- 10+ years software engineering
- 3+ years static analysis framework experience (CPG, Soot, WALA)
- Deep compiler internals (AST, CFG, DFG, SSA)
- Frontend + Core architecture understanding
- Strong architectural design skills
- Kotlin or Scala programming

**Preferred Skills**:
- Prior CPG contribution or fork experience
- Team lead experience (5-10 person teams)
- Published papers/talks on static analysis
- Java language semantics (generics, annotations, lambda)

**Success Metrics**:
- Deliver 30+ defect fixes within 24-28 weeks
- Achieve < 5% false positive rate for Task 3 scenarios
- Mentor team to self-sufficiency

**Compensation**: $180K-$250K annual (or $90-125/hour for consultant)
```

**好处**:
- HR 可以直接用于招聘
- 候选人清楚了解期望
- 管理层理解为什么需要这个级别的人才

### 7. 替代方案分析

**决策**: 提供 3 种替代方案,而非只推荐一种路径

**3 种替代方案**:
1. **Alternative 1: Outsource to Vendor**
   - Pros: 不需要招聘,vendor 可能有经验
   - Cons: 更高成本 (1.5-2x),知识不保留
   - Cost: $500K-$900K
2. **Alternative 2: Contribute to Upstream CPG**
   - Pros: 成本分担,建立开源声誉
   - Cons: 上游可能不接受 Java-specific 修复,流程慢
   - Timeline: 1.5-2x longer
3. **Alternative 3: Switch to Different Framework (Soot/WALA)**
   - Pros: 这些框架已支持部分需求,无需修复 30 个缺陷
   - Cons: 切换成本高,可能无法满足精度要求
   - Cost: 4-8 weeks migration + 4-8 weeks adaptation

**好处**:
- 管理层可以比较不同方案
- 证明推荐方案不是唯一选择 (提高推荐方案的可信度)

## Technical Highlights

### Defect-to-Role Mapping Strategy

**Challenge**: 30 个缺陷如何分配到 6 个角色?

**Solution**: 基于 **知识领域** 分类缺陷

**知识领域分类**:
- **Frontend Domain**: D1, D6, D15, M1, M2 (Java-specific features)
- **Core Domain**: D3, D4, D8, D13, D19, D25, D26 (Graph algorithms, DFG, Call Graph)
- **Integration Domain**: D1 (partial), D7 (Type System)
- **Performance Domain**: D10, D12, D22 (Parallelization, Bytecode, Incremental)
- **Testing Domain**: D17 (Testing infrastructure)
- **Tooling Domain**: D11, D23, D24 (Error recovery, Maven/Gradle, Partial analysis)

**映射到角色**:
- Java Frontend Specialist → Frontend Domain
- CPG Core Specialist → Core Domain
- Integration Engineer → Integration Domain (+ review all)
- Performance Engineer → Performance Domain
- Testing Specialist → Testing Domain
- Tooling Engineer → Tooling Domain

### Effort Estimation Methodology

**Challenge**: 如何估算每个缺陷的修复工作量 (person-hours)?

**Approach**: 基于缺陷复杂度分类

**复杂度因素**:
1. **Algorithm Complexity**: 算法设计复杂度 (Low/Medium/High)
2. **Integration Complexity**: 跨 Frontend-Core 边界复杂度 (Low/Medium/High)
3. **Testing Effort**: 测试覆盖难度 (Low/Medium/High)
4. **Dependencies**: 依赖其他缺陷数量 (0/1-2/3+)

**Effort Mapping**:
| Complexity Profile | Effort Estimate | Example Defects |
|-------------------|----------------|-----------------|
| Low/Low/Low, 0 deps | 20-40 hours | D2 (String.equals), D5 (Integer eval) |
| Medium/Low/Medium, 0-1 deps | 40-80 hours | D1 (Static final DFG), D6 (Enum) |
| High/Medium/High, 1-2 deps | 80-150 hours | D3 (Interprocedural DFG), D4 (Call Graph) |
| High/High/High, 2+ deps | 150-300 hours | D10 (Parallelization), D12 (Bytecode) |

**Example**:
```markdown
**D1: Static Final DFG Missing**:
- Algorithm: Low (simple DFG edge creation)
- Integration: High (跨 Frontend-Core)
- Testing: Medium (需要覆盖所有 Java constant patterns)
- Dependencies: 0 (独立)
- **Total Effort**: 40-80 hours
```

### Timeline Estimation from HR Perspective

**Challenge**: 如何从 HR 视角估算时间线 (而非技术实施角度)?

**HR 视角考虑因素**:
1. **Onboarding time**: 新员工需要 4-6 周熟悉 CPG 架构
2. **Ramp-up time**: 即使有经验的工程师,也需要 2-4 周理解当前代码库
3. **Review time**: 所有修复需要 Integration Engineer review (20-30% overhead)
4. **Testing time**: 所有修复需要 Testing Specialist 验证 (30-50% overhead)
5. **Integration time**: 跨 Frontend-Core 的修复需要集成时间 (10-20% overhead)
6. **Contingency buffer**: 静态分析项目常常低估,建议 +20% buffer

**Timeline Formula** (from HR perspective):
```
Total Timeline = (Sum of Effort / Team Size / Work Hours per Week) * (1 + Review Overhead + Testing Overhead + Integration Overhead + Contingency Buffer)

Example:
- Total Effort for P0 defects: 200-350 hours
- Team Size: 3 FTE
- Work Hours per Week: 40 hours/person = 120 hours/week (3 FTE)
- Review Overhead: 25%
- Testing Overhead: 40%
- Integration Overhead: 15%
- Contingency Buffer: 20%
- Total Multiplier: 1 + 0.25 + 0.40 + 0.15 + 0.20 = 2.0

Timeline = (200-350 hours / 120 hours/week) * 2.0 = (1.67-2.92 weeks) * 2.0 = 3.3-5.8 weeks

Rounded: 4-6 weeks (但用户可能期望 8-12 weeks 以包含 onboarding)
```

**Realistic Timeline** (包含 onboarding):
- Phase 0 (D1, D2): 4 weeks (包含 1 周 onboarding)
- Phase 1 (D3, D4): 8 weeks (包含 ramp-up)
- Phase 2 (D5-D17): 12 weeks (full team)

## Observations

### Memory-First Effectiveness

**Success**:
- 快速理解 Task 4 的 30 个缺陷 (通过 ep-007 和 4.0-index.md)
- 避免重新阅读 Task 4 的所有 6 个文档 (只读了 index 和部分 4.2-defects.md)
- 一致性: Task 5 prompt 使用与 Task 4 相同的缺陷 ID (D1-D26, M1-M4)

**Context Savings**:
- Memory read: ~1500 lines (ep-007 + 4.0-index.md + 4.2-defects.md partial)
- Avoided: ~4800 lines (完整的 Task 4 的 6 个文档)
- Net savings: ~3300 lines (69% context reduction)

### Prompt Design Philosophy: HR-First, Not Tech-First

**Key Insight**: Task 5 的核心是 "换一种视角看同样的问题"

- Task 4 看缺陷: "What is broken? Why? How severe?"
- Task 5 看缺陷: "Who can fix it? What do they need to know? How long? How much?"

**这种视角转换体现在**:
1. **语言风格**: Task 5 使用 HR/商业语言,而非技术术语
   - Task 4: "DFG Pass 缺少 static final field 处理逻辑"
   - Task 5: "需要深度理解 Java static final 语义的 Senior Frontend Engineer"
2. **分析维度**: Task 5 关注人 (skills, roles, team),Task 4 关注技术 (algorithms, architecture)
3. **交付物**: Task 5 是报告 (for decision-making),Task 4 是分析文档 (for understanding)

### Report Format vs Application Form

**User 明确要求 "不要做成申请表的式样"**,这个要求很重要:

**申请表格式** (❌ 不采用):
```
项目名称: _______________
申请部门: _______________
申请人数: ___ 人
预算: ___ 万元
理由: _______________
```

**报告格式** (✅ 采用):
```
# Executive Summary
本报告分析修复 30 个 CPG 缺陷所需的人力资源...

## Part 1: Defect-to-Skill Mapping
每个缺陷的技能需求分析...

## Part 8: Decision Framework
管理层可基于以下标准决策 Go/No-Go...
```

**报告格式的优势**:
- 更全面 (包含分析、建议、风险、替代方案)
- 更有说服力 (数据驱动,逻辑清晰)
- 更适合决策 (提供 Go/No-Go 框架,phased commitment 选项)

## Challenges Encountered

### Challenge 1: 平衡 HR 视角与技术细节

**问题**: 如何在不涉及技术设计的前提下,准确描述缺陷修复的技能需求?

**Solution**:
- 使用 "抽象技能描述" 而非 "具体技术方案"
- 示例:
  - ❌ 技术: "需要实现 JavaStaticFieldDFGPass,遍历 FieldDeclaration,创建 DFG 边"
  - ✅ HR: "需要深度理解 Java static final 语义,熟悉 CPG DFG Pass 机制,能够跨 Frontend-Core 集成"

### Challenge 2: 工作量估算的准确性

**问题**: 如何在没有详细技术设计的情况下,估算工作量?

**Solution**:
- 基于 Task 4 的复杂度分析 (Root Cause, Dependencies, Impact)
- 借鉴行业经验 (类似静态分析项目的工作量)
- 提供 range (40-80 hours) 而非 point estimate (60 hours)
- 明确标注 "estimated" (if no prior data)

### Challenge 3: 多种团队规模选项的权衡

**问题**: 如何平衡 "最小可行" vs "推荐" vs "最优" 三种选项?

**Solution**:
- 最小可行 (3 FTE): 只修复 P0,快速验证
- 推荐 (4.8 FTE): 修复 P0-P1,达到生产级质量
- 最优 (7 FTE): 修复 P0-P2,达到高精度 + 高性能
- 明确说明每种选项的 tradeoff (成本 vs 质量 vs 时间线)

## Quality Metrics

| Metric | Target | Actual | Status |
|--------|--------|--------|--------|
| **Prompt Length** | 500-800 lines | ~800 lines | ✅ Met |
| **Parts Defined** | 8 parts | 8 parts | ✅ Complete |
| **Roles Defined** | 5-6 roles | 6 roles | ✅ Complete |
| **Team Size Options** | 3 options | 3 options (min/rec/opt) | ✅ Complete |
| **JD Provided** | 5 JDs | 5 JDs (complete) | ✅ Complete |
| **Risks Identified** | 8+ risks | 8 risks (R1-R8) | ✅ Complete |
| **Alternative Approaches** | 3 alternatives | 3 alternatives | ✅ Complete |
| **HR-First Principle** | No tech designs | ✅ No code, no architecture | ✅ Met |
| **Report Format** | Not application form | ✅ Report structure | ✅ Met |
| **Chinese Readability** | Accessible to non-tech | ✅ HR-friendly language | ✅ Met |

## Results

### Main Deliverable (Revised After User Adjustment)

**File**: `/claude/prompt/5.resource-analysis-and-staffing.md`
**Size**: ~380 lines (reduced from ~800 lines after scope reduction)
**Quality**: Production-ready, focused on skills and roles analysis

**Key Sections** (Final):
1. ✅ Part 1: Defect-to-Skill Mapping (15-20 defects × skill analysis)
   - Required Skills (primary/secondary/nice-to-have)
   - Seniority Level Required (Junior/Mid/Senior/Expert)
   - Knowledge Domain Categorization (Frontend/Core/Integration/Tooling/Algorithm)
   - Dependencies and Parallelizability
2. ✅ Part 2: Role Definition and Team Structure
   - 6 roles catalog (Integration Engineer, Frontend Specialist, Core Specialist, Performance Engineer, Testing Specialist, Tooling Engineer)
   - 3 team composition options (Minimum/Recommended/Optimal)
   - 4 collaboration patterns (Integration-led, Specialist-led, Sequential, Parallel)
   - Defect-to-Role assignment matrix (30 defects × 6 roles)

**Removed Sections** (per user request):
- ❌ Part 3: Skill Gap Analysis + Training Plan
- ❌ Part 4: Resource Allocation by Category
- ❌ Part 5: Timeline and Milestone Planning
- ❌ Part 6: Risk Analysis
- ❌ Part 7: Hiring Profiles (JDs)
- ❌ Part 8: Decision Framework

### Key Insights Captured

**Insight 1: 最小团队 (3 FTE) 可完成 P0 修复**
- 3 FTE: Integration Engineer + Frontend Specialist + Testing Specialist
- 12-16 weeks timeline
- $123K-$184K budget
- 覆盖 D1-D5 (P0 缺陷), 满足 Task 3 场景需求

**Insight 2: 推荐团队 (4.8 FTE) 达到生产级质量**
- 增加 Performance Engineer (part-time) + Tooling Engineer (part-time)
- 20-28 weeks timeline
- $311K-$458K budget
- 覆盖 D1-D17 (P0-P1), 满足真实部署需求 (100K LOC < 1 min, JAR support)

**Insight 3: 分阶段承诺降低风险**
- Stage 1 (4 weeks, $31K-$46K): D1, D2 quick wins
- Stage 2 (8 weeks, $98K-$148K): D3, D4 interprocedural
- Stage 3 (12 weeks, $182K-$264K): D5-D17 production readiness
- **Exit points** at Week 4 and Week 12 if results unsatisfactory

**Insight 4: HR 风险主要是技能缺口和人员依赖**
- R1 (Skill Gap): 需要至少 1 个有 CPG 经验的 team lead
- R2 (Key Person Dependency): Integration Engineer 是单点,需要 cross-training
- R3 (Team Attrition): 20-30% 年流失率,需要 documentation + retention

**Insight 5: 替代方案成本更高或时间更长**
- Outsource to vendor: $500K-$900K (1.5-2x internal cost)
- Contribute to upstream: 1.5-2x timeline (due to community process)
- Switch framework: 8-16 weeks migration + adaptation

## Links

- **Task 5 Prompt**: `/claude/prompt/5.resource-analysis-and-staffing.md`
- **Task 4 Results**: `/claude/result/4/4.0-index.md` (30 defects identified)
- **Task 4 Episodic**: `/claude/memory/episodic/20251028-task4-execution.md` (ep-007)
- **Task 4 Prompt**: `/claude/prompt/4.gap-analysis-and-fork-roadmap.md`

## Next Steps

### For User

1. **Review** Task 5 prompt (`5.resource-analysis-and-staffing.md`)
2. **Validate** 角色定义和团队规模是否符合预期
3. **Adjust** 如果需要增加/删除角色,或调整团队规模选项
4. **Execute** Task 5 (when ready): "执行 task5" 或 "请根据 Task 5 prompt 创建资源分析报告"

### For Memory System

1. ✅ Created ep-009 (this file)
2. ⏳ Update tags.json and topics.json
3. ⏳ No new semantic notes needed (all knowledge in ep-009 + Task 5 prompt)

## Acceptance Checklist

- [x] Task 5 prompt 明确为 HR 资源分析任务 (NOT technical design)
- [x] 8 parts 完整定义 (Defect-to-Skill, Roles, Skills, Allocation, Timeline, Risks, JDs, Decision)
- [x] 6 roles 定义 (Integration, Frontend, Core, Performance, Testing, Tooling)
- [x] 3 team size options (minimum/recommended/optimal)
- [x] 5 complete JDs (Integration, Frontend, Core, Testing, Performance)
- [x] 8+ HR/project risks with mitigation
- [x] 3 alternative approaches analyzed
- [x] Phased commitment approach detailed
- [x] Go/No-Go decision criteria defined
- [x] Report format (not application form)
- [x] Chinese prose clear and HR-friendly
- [x] No technical designs, no architecture, no code
- [x] Episodic note (ep-009) created
- [ ] Memory indexes updated (next step)

---

## Revision: Scope Reduction (2025-10-29)

**User Feedback**: "part3之后的part都不需要"

**Changes Made**:
1. ✅ Removed Part 3-8 from Task 5 prompt
2. ✅ Updated prompt to focus only on Part 1 (Skills) and Part 2 (Roles)
3. ✅ Reduced expected output from 80-120 pages to 40-60 pages
4. ✅ Removed: Budget estimation, Timeline planning, Risk analysis, Hiring JDs, Decision framework
5. ✅ Kept: Defect-to-Skill mapping, Role definitions, Team structure, Collaboration patterns

**Final Prompt Structure**:
- Part 1: Defect-to-Skill Mapping (每个 P0-P2 缺陷的技能分析)
- Part 2: Role Definition and Team Structure (6 roles + 3 team options + 4 collaboration patterns)

**Rationale**: 用户只需要技能和角色分析,不需要完整的项目管理分析 (预算、时间线、风险等)。聚焦于回答 "需要什么技能的什么角色" 这个核心问题。

---

**Session Duration**: ~50 minutes (including revision)
**Context Used**: ~1500 lines (memory + Task 4 overview)
**Output Size**: ~380 lines (Task 5 prompt, revised)
**Status**: ✅ **Complete** - ready for user review and Task 5 execution

---
