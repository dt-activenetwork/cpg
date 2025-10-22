#!/usr/bin/env python3
"""Compile the MkDocs documentation into a single Markdown file.

This script reads the navigation structure defined in ``mkdocs.yaml`` and
concatenates every referenced Markdown document into a single file while
preserving the navigation order.  The generated document keeps the original
Markdown content untouched so that embedded elements such as Mermaid diagrams
continue to work.

The script doesn't require any third-party dependencies.  It implements a very
small YAML subset parser that understands the shape of the ``nav`` section used
in the project configuration.
"""

from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path
from typing import Iterable, List, Optional


PROJECT_ROOT = Path(__file__).resolve().parent
MKDOCS_CONFIG = PROJECT_ROOT / "mkdocs.yaml"
DOCS_ROOT = PROJECT_ROOT / "docs"
OUTPUT_FILE = PROJECT_ROOT / "site-single.md"


def strip_quotes(text: str) -> str:
    if (text.startswith('"') and text.endswith('"')) or (
        text.startswith("'") and text.endswith("'")
    ):
        return text[1:-1]
    return text


@dataclass
class NavNode:
    title: Optional[str]
    path: Optional[str]
    children: List["NavNode"]

    @property
    def is_group(self) -> bool:
        return bool(self.children)


def _next_non_empty_line(lines: List[str], start: int) -> Optional[int]:
    for index in range(start, len(lines)):
        stripped = lines[index].strip()
        if stripped == "" or stripped.startswith("#"):
            continue
        return index
    return None


def parse_nav(lines: List[str], start: int, indent: int) -> tuple[List[NavNode], int]:
    nodes: List[NavNode] = []
    index = start
    while index < len(lines):
        raw_line = lines[index]
        stripped = raw_line.strip()
        if stripped == "" or stripped.startswith("#"):
            index += 1
            continue

        current_indent = len(raw_line) - len(raw_line.lstrip(" "))
        if current_indent < indent:
            break
        if not stripped.startswith("-"):
            break

        content = stripped[1:].strip()
        if content == "":
            index += 1
            continue

        if ":" in content:
            key, value = content.split(":", 1)
            key = strip_quotes(key.strip())
            value = value.strip()
            if value:
                nodes.append(NavNode(title=key, path=strip_quotes(value), children=[]))
                index += 1
            else:
                child_nodes, new_index = parse_nav(lines, index + 1, current_indent + 2)
                nodes.append(NavNode(title=key, path=None, children=child_nodes))
                index = new_index
        else:
            nodes.append(NavNode(title=None, path=strip_quotes(content), children=[]))
            index += 1

    return nodes, index


def load_nav() -> List[NavNode]:
    config_lines = MKDOCS_CONFIG.read_text(encoding="utf-8").splitlines()
    nav_line_index = None
    for idx, line in enumerate(config_lines):
        if line.strip().startswith("nav:"):
            nav_line_index = idx + 1
            break

    if nav_line_index is None:
        raise RuntimeError("Unable to locate 'nav' section in mkdocs.yaml")

    nav_nodes, _ = parse_nav(config_lines, nav_line_index, indent=2)
    return nav_nodes


def iter_documents(nodes: Iterable[NavNode], parent_titles: Optional[List[str]] = None) -> Iterable[tuple[List[str], Path]]:
    if parent_titles is None:
        parent_titles = []

    for node in nodes:
        titles = parent_titles
        if node.title:
            titles = parent_titles + [node.title]
        if node.is_group:
            yield from iter_documents(node.children, titles)
        elif node.path:
            path = DOCS_ROOT / node.path
            if path.suffix.lower() != ".md":
                continue
            if not path.exists():
                raise FileNotFoundError(f"Referenced document '{node.path}' does not exist")
            yield titles, path


def heading(level: int, text: str) -> str:
    level = max(1, min(level, 6))
    return f"{'#' * level} {text}\n\n"


def compile_markdown() -> None:
    nav = load_nav()
    output_parts: List[str] = []

    for titles, doc_path in iter_documents(nav):
        if titles:
            heading_level = len(titles)
            output_parts.append(heading(heading_level, " / ".join(titles)))
        output_parts.append(f"<!-- Source: {doc_path.relative_to(PROJECT_ROOT)} -->\n\n")
        output_parts.append(doc_path.read_text(encoding="utf-8"))
        output_parts.append("\n\n")

    OUTPUT_FILE.write_text("".join(output_parts), encoding="utf-8")
    print(f"Wrote {OUTPUT_FILE.relative_to(PROJECT_ROOT)}")


if __name__ == "__main__":
    compile_markdown()
