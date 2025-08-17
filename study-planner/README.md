# 📚 Study Planner (CLI)

A fast, no-dependencies CLI app to organize your study tasks with **priorities, due dates, tags, filters, sorting,** and **stats**.  
Data is stored locally in a simple **CSV** file (`tasks.csv`).

> Built with core Java only — perfect for showcasing OOP, file I/O, enums, and CLI UX.

---

## ✨ Features

- Add / list / edit / delete tasks
- **Priorities:** `LOW` | `MEDIUM` | `HIGH`
- **Statuses:** `TODO` | `DOING` | `DONE`
- **Tags** (comma-separated, e.g. `algorithms,math`)
- Filters: by **status**, **priority**, **tag**, **due-before**
- Sorting: by **due**, **priority**, **status**, **id**
- **Autosave** to `tasks.csv` after changes
- **Import/Export** CSV (`tasks_import.csv` ⇆ `tasks_export.csv`)
- **Overdue highlighting** and **summary stats**
- ANSI-colored output (falls back gracefully if terminal lacks color)

---

## 🚀 Quick Start

From the repo root:

```bash
cd study-planner
javac *.java
java Main
