# ExpenseTracker – Future Enhancements & Test Scenarios

This document lists planned enhancements for the ExpenseTracker web application and defines the corresponding test cases.  
Each enhancement can be implemented independently and tested using the scenarios below.

---
✅ - DONE  ❌ - TODO

## ✅ 1. Filtering Expenses by Category

### Description
Add ability to filter the expenses list by category using a dropdown or text input.

### Tasks
- Add `category` filter parameter to ListExpensesServlet
- Update DAO: `getAll(String categoryFilter)`
- Modify `list.jsp` to include category filter UI
- Preserve filter state after form submission

### Test Scenarios
1. **Filter by existing category**
    - Input: "Food"
    - Expected: Only expenses with category "Food" appear
2. **Filter by non-existing category**
    - Input: "Travel"
    - Expected: Empty list, no errors
3. **Filter with empty value**
    - Input: ""
    - Expected: All expenses displayed
4. **Case-insensitive filtering**
    - Input: "food"
    - Expected: Matches "Food"

---

## ✅ 2. Filtering by Date Range

### Description
Allow users to filter expenses between two dates.

### Tasks
- Add `startDate` and `endDate` parameters to servlet
- Update DAO: `getAll(LocalDate start, LocalDate end)`
- Add date inputs to `list.jsp`
- Validate date formats

### Test Scenarios
1. **Valid date range**
    - Input: 2024-01-01 → 2024-01-31
    - Expected: Only expenses in this range appear
2. **Start date only**
    - Expected: All expenses from start date onward
3. **End date only**
    - Expected: All expenses up to end date
4. **Invalid date format**
    - Expected: Error message or ignored filter
5. **Start > End**
    - Expected: Validation error or empty result

---

## ✅ 3. Pagination for Large Expense Lists

### Description
Split the expenses list into pages (e.g., 10 per page).

### Tasks
- Add `page` parameter to servlet
- Update DAO: `getPage(int page, int size)`
- Add pagination controls to `list.jsp`
- Display current page and total pages

### Test Scenarios
1. **Navigate to next page**
    - Expected: Next 10 items displayed
2. **Navigate to previous page**
    - Expected: Previous 10 items displayed
3. **Page out of range**
    - Expected: Redirect to last valid page
4. **Empty database**
    - Expected: No errors, empty list

---

## ❌ 4. User Authentication (Login System)

### Description
Add login/logout functionality so only authenticated users can manage expenses.

### Tasks
- Create `users` table
- Add LoginServlet + LogoutServlet
- Add session handling
- Protect expense routes with filter

### Test Scenarios
1. **Valid login**
    - Expected: Redirect to `/expenses`
2. **Invalid login**
    - Expected: Error message, stay on login page
3. **Access protected page without login**
    - Expected: Redirect to login
4. **Logout**
    - Expected: Session cleared, redirect to login

---

## ✅ 5. Export Expenses to CSV

### Description
Allow users to export the current filtered list to a CSV file.

### Tasks
- Add `/expenses/export` endpoint
- Generate CSV from DAO results
- Set correct response headers

### Test Scenarios
1. **Export full list**
    - Expected: CSV with all expenses
2. **Export filtered list**
    - Expected: CSV matches filter results
3. **CSV formatting**
    - Expected: Comma-separated, UTF‑8, correct headers

---

## ✅ 6. REST API Endpoints

### Description
Expose CRUD operations via JSON REST API.

### Tasks
- Add `/api/expenses` endpoints (GET, POST, PUT, DELETE)
- Use JSON serialization (Jackson or manual)
- Return proper HTTP status codes

### Test Scenarios
1. **GET /api/expenses**
    - Expected: JSON array of expenses
2. **POST /api/expenses**
    - Expected: 201 Created + new ID
3. **PUT /api/expenses/{id}**
    - Expected: 200 OK + updated object
4. **DELETE /api/expenses/{id}**
    - Expected: 204 No Content
5. **Invalid JSON**
    - Expected: 400 Bad Request

---

## ✅ 7. Input Validation Improvements

### Description
Add stronger validation for amount, category, date, and description.

### Tasks
- Validate amount > 0
- Validate category not empty
- Validate date not in the future
- Limit description length

### Test Scenarios
1. **Negative amount**
    - Expected: Validation error
2. **Empty category**
    - Expected: Validation error
3. **Future date**
    - Expected: Validation error
4. **Long description**
    - Expected: Error or truncation

---

## ❌ 8. UI Improvements with Bootstrap

### Description
Improve layout and usability using Bootstrap.

### Tasks
- Add Bootstrap CDN
- Style forms, tables, buttons
- Add responsive layout

### Test Scenarios
1. **Mobile view**
    - Expected: Layout adapts correctly
2. **Form usability**
    - Expected: Clear labels, spacing, error messages
3. **Table readability**
    - Expected: Striped rows, aligned columns

---

## ❌ 9. Dashboard with Charts

### Description
Add a dashboard showing spending by category or month.

### Tasks
- Add `/dashboard` servlet
- Query aggregated data
- Use Chart.js for visualization

### Test Scenarios
1. **Category chart**
    - Expected: Correct totals per category
2. **Monthly chart**
    - Expected: Correct totals per month
3. **Empty database**
    - Expected: Chart shows zero values

---

## ❌ 10. Multi-user Support

### Description
Allow each user to have their own expenses.

### Tasks
- Add `user_id` column to `expenses`
- Modify DAO queries to filter by user
- Update servlets to use session user ID

### Test Scenarios
1. **User A sees only their expenses**
2. **User B sees only their expenses**
3. **Cross-user access attempt**
    - Expected: Forbidden or empty result

