# 💰 Java Expense Tracker

A terminal expense tracker for logging, filtering, and analyzing personal spending — with proper CSV persistence and category summaries.

---

## ✨ Features

- ➕ Add expenses with description, category, and amount
- 🗑️ Delete any expense by number
- 🧾 View full expense history in a formatted table
- 🔍 Filter by category (partial match, case-insensitive)
- 📆 Filter by month and year
- 📊 Category summary sorted alphabetically with grand total
- 💵 View total for any filtered result
- 💾 Auto-saves to `expenses.csv` with proper quote escaping
- ❌ Input validation on all fields

---

## 🚀 How to Run

```bash
cd expense-tracker
javac ExpenseTracker.java
java ExpenseTracker
```

---

## 💡 Example

```
Menu:
1. Add Expense
2. View All Expenses
3. Filter by Category
4. Filter by Month/Year
5. Summary by Category
6. Total Expenses
7. Delete Expense
8. Save & Exit

Category Summary:
------------------------------
  Food             $120.50
  Transport        $45.00
  Utilities        $80.00
------------------------------
  TOTAL            $245.50
```
