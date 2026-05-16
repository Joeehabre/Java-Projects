# 📚 Study Planner (CLI)

A fast, no-dependencies CLI app to organize your study tasks with **priorities, due dates, tags, filters, sorting,** and **stats**.  
Data is stored locally in a simple **CSV** file (`tasks.csv`).

> Built with core Java only — perfect for showcasing OOP, file I/O, enums, and CLI UX.

---

## ✨ Features

- Add / list / edit / delete tasks
- **Priorities:** `LOW` | `MEDIUM` | `HIGH` (color-coded)
- **Statuses:** `TODO` | `DOING` | `DONE` (color-coded)
- **Tags** (comma-separated, e.g. `algorithms,math`)
- Filters: by **status**, **priority**, **tag**, **due-before**
- Sorting: by **due**, **priority**, **status**, **id**
- **Overdue** tasks highlighted in red
- **Autosave** after every change
- **Import/Export** CSV (`tasks_import.csv` ⇆ `tasks_export.csv`)
- **Stats** — totals by status and priority, overdue count
- ANSI-colored output with correct column alignment

---

## 🚀 How to Run

```bash
cd study-planner
javac *.java
java Main
```

---

## 💡 Example

```
==== Tasks ====

ID   PRIOR   STATUS   DUE          TAGS       TITLE
---------------------------------------------------------------------
1    [HIGH]  (TODO)   2025-06-01   math       Calculus problem set
2    [MED]   (DOING)  2025-05-20   algo       Binary search trees
3    [LOW]   (DONE)   -            -          Read chapter 3
```

---

## 📁 File Structure

| File | Role |
|------|------|
| `Main.java` | Entry point, menus, user interaction |
| `Planner.java` | Task list logic, filtering, sorting |
| `Task.java` | Task data model |
| `Storage.java` | CSV read/write |
| `Csv.java` | CSV escaping and parsing |
| `TagUtil.java` | Tag parsing helpers |
| `UI.java` | ANSI colors, display formatting |
| `Priority.java` | Priority enum |
| `Status.java` | Status enum |
