# Servlet Endpoints

## List Expenses
**GET** `/expenses`

Returns list of all expenses.

---

## Add Expense
**POST** `/expenses/add`

Form fields:
- amount
- category
- date
- description

---

## Edit Expense
**GET** `/expenses/edit?id=123`  
**POST** `/expenses/update`

---

## Delete Expense
**GET** `/expenses/delete?id=123`
