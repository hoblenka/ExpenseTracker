## ✅ 5. Implement DAO
`ExpenseDAOImpl.java`:
- Implement all CRUD operations using JDBC
- Use prepared statements
- Map ResultSet → Expense object

SQL operations:
- SELECT * FROM expenses
- SELECT by id
- INSERT
- UPDATE
- DELETE

## ✅ 6. Create Servlets
### 6.1 ListExpensesServlet
- GET `/expenses`
- Fetch all expenses
- Forward to `list.jsp`

### 6.2 AddExpenseServlet
- GET → show form
- POST → insert into DB
- Redirect to `/expenses`

### 6.3 EditExpenseServlet
- GET → load expense by ID
- POST → update DB
- Redirect to `/expenses`

### 6.4 DeleteExpenseServlet
- GET `/expenses/delete?id=...`
- Delete record
- Redirect to `/expenses`

---

## ✅ 7. Create JSP Views
### 7.1 `list.jsp`
- Table of expenses
- Buttons: Edit / Delete
- Link to Add New Expense

### 7.2 `add.jsp`
- Form fields:
- amount
- category
- date
- description

### 7.3 `edit.jsp`
- Same as add.jsp but pre-filled

---

## ✅ 8. Add tests
Add expense
Edit expense
Delete expense
List expense

## ✅ 9. Configure web.xml (if needed)
Map servlets:
```xml
<servlet>
  <servlet-name>ListExpenses</servlet-name>
  <servlet-class>...</servlet-class>
</servlet>
<servlet-mapping>
  <servlet-name>ListExpenses</servlet-name>
  <url-pattern>/expenses</url-pattern>
</servlet-mapping>
