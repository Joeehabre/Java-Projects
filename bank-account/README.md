# 🏦 Java Bank Account System

A terminal-based banking simulation supporting multiple accounts, transfers, timestamped transaction history, and persistent storage across sessions.

---

## ✨ Features

- 🏧 Create multiple named bank accounts
- 🔑 Access any account by unique ID
- 💵 Deposit and withdraw with full validation
- 🔄 Transfer money between any two accounts
- 📜 View timestamped transaction history per account
- 📋 List all accounts with balances
- 💾 Accounts and transactions persist to `accounts.dat`
- ❌ Robust input validation — no crashes on bad input

---

## 🚀 How to Run

```bash
cd bank-account
javac BankAccount.java
java BankApp
```

---

## 💡 Example

```
SimpleBank
1. Create Account
2. Access Account
3. List Accounts
4. Exit

  [Joe | ID: 1001]
  Balance: $500.00
  1. Deposit
  2. Withdraw
  3. Transfer to Another Account
  4. Transaction History
  5. Back
```
