# 🧮 Java Calculator

A terminal-based calculator supporting arithmetic, advanced operations, calculation history, and result chaining — all with clean input validation.

---

## ✨ Features

- ➕ Addition, Subtraction, Multiplication, Division
- 🔢 Power (`^`) and Modulus (`%`)
- √ Square root (`sqrt`) as a unary operator
- 🔗 `ans` — reuse the last result to chain calculations
- 📜 `h` — view the last 10 calculations
- 🔁 Continuous loop until you type `q`
- ❌ Input validation and division-by-zero handling
- 🧹 Clean number output (e.g. `5.0` shows as `5`)

---

## 🚀 How to Run

```bash
cd calculator
javac Calculator.java
java Calculator
```

---

## 💡 Example Session

```
>> First number: 12
   Operator : *
   Second   : 3
  = 36

>> First number: ans
  Using ans = 36
   Operator : sqrt
  = 6

>> First number: h
  sqrt(36) = 6
  12 * 3 = 36
```
