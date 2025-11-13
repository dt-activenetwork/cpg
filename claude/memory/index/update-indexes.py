#!/usr/bin/env python3
"""Update tags.json and topics.json with Agent 3 findings"""

import json

# Update tags.json
tags_data = {
    "dfg": ["sem-007", "ep-025"],
    "cfg": ["sem-007", "ep-025"],
    "eog": ["sem-003", "sem-007", "ep-025"],
    "flow-analysis": ["sem-007", "ep-025"],
    "java-11-17-support": ["ep-024", "ep-025"],
    "field-handling": ["sem-007", "ep-025"],
    "switch-expressions": ["sem-007", "ep-025"],
    "pattern-matching": ["sem-007", "ep-025"],
    "sealed-classes": ["sem-007", "ep-025"],
    "record-components": ["sem-007", "ep-025"],
    "reachability-analysis": ["sem-007", "ep-025"],
    "control-flow-graph": ["sem-003", "sem-007", "ep-025"],
}

topics_data = {
    "Data Flow Analysis": {
        "description": "DFG architecture, edge creation, limitations",
        "notes": ["sem-007", "ep-025"]
    },
    "Control Flow Graph": {
        "description": "CFG and EOG construction, reachability analysis",
        "notes": ["sem-003", "sem-007", "ep-025"]
    },
    "Java 11-17 Flow Analysis": {
        "description": "Support assessment for records, patterns, switch expressions, sealed classes",
        "notes": ["ep-024", "ep-025"]
    },
}

print("Tags to add to tags.json:")
for tag, notes in tags_data.items():
    print(f'  "{tag}": {notes}')

print("\nTopics to add to topics.json:")
for topic, data in topics_data.items():
    print(f'  "{topic}": {data}')

print("\nNote IDs referenced:")
print("  - sem-007: DFG/CFG Architecture")
print("  - ep-025: Agent 3 DFG/CFG Analysis")

