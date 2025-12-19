# Database Schema – ExpenseTracker

## Table: expenses

```sql
CREATE TABLE expenses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    amount DECIMAL(10,2) NOT NULL,
    category VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    description TEXT
);
```

# ExpenseTracker – Test Data Set

This dataset is designed to support functional testing, filtering, pagination, validation, charts, and multi-user scenarios.

---

## ✅ 1. Base Test Data (20 Expenses)

| id | amount | category      | date       | description                |
|----|--------|---------------|------------|----------------------------|
| 1  | 12.50  | Food          | 2024-01-02 | Lunch                      |
| 2  | 45.00  | Transport     | 2024-01-03 | Monthly bus pass           |
| 3  | 89.99  | Utilities     | 2024-01-05 | Electricity bill           |
| 4  | 15.00  | Entertainment | 2024-01-06 | Movie ticket               |
| 5  | 7.20   | Food          | 2024-01-07 | Coffee                     |
| 6  | 120.00 | Shopping      | 2024-01-10 | Shoes                      |
| 7  | 60.00  | Utilities     | 2024-01-12 | Water bill                 |
| 8  | 25.00  | Transport     | 2024-01-13 | Taxi                       |
| 9  | 14.99  | Food          | 2024-01-14 | Groceries                  |
| 10 | 200.00 | Rent          | 2024-01-15 | Rent contribution          |
| 11 | 9.99   | Entertainment | 2024-02-01 | Music subscription         |
| 12 | 33.50  | Food          | 2024-02-02 | Dinner                     |
| 13 | 5.00   | Food          | 2024-02-03 | Snack                      |
| 14 | 150.00 | Shopping      | 2024-02-05 | Jacket                     |
| 15 | 80.00  | Transport     | 2024-02-06 | Train ticket               |
| 16 | 55.00  | Utilities     | 2024-02-07 | Gas bill                   |
| 17 | 12.00  | Entertainment | 2024-02-08 | Cinema                     |
| 18 | 300.00 | Rent          | 2024-02-10 | Rent contribution          |
| 19 | 20.00  | Food          | 2024-02-11 | Lunch with friend          |
| 20 | 10.00  | Food          | 2024-02-12 | Breakfast                  |

---

## ✅ 2. SQL Insert Script

```sql
INSERT INTO expenses (amount, category, date, description) VALUES
(12.50, 'Food', '2024-01-02', 'Lunch'),
(45.00, 'Transport', '2024-01-03', 'Monthly bus pass'),
(89.99, 'Utilities', '2024-01-05', 'Electricity bill'),
(15.00, 'Entertainment', '2024-01-06', 'Movie ticket'),
(7.20, 'Food', '2024-01-07', 'Coffee'),
(120.00, 'Shopping', '2024-01-10', 'Shoes'),
(60.00, 'Utilities', '2024-01-12', 'Water bill'),
(25.00, 'Transport', '2024-01-13', 'Taxi'),
(14.99, 'Food', '2024-01-14', 'Groceries'),
(200.00, 'Rent', '2024-01-15', 'Rent contribution'),
(9.99, 'Entertainment', '2024-02-01', 'Music subscription'),
(33.50, 'Food', '2024-02-02', 'Dinner'),
(5.00, 'Food', '2024-02-03', 'Snack'),
(150.00, 'Shopping', '2024-02-05', 'Jacket'),
(80.00, 'Transport', '2024-02-06', 'Train ticket'),
(55.00, 'Utilities', '2024-02-07', 'Gas bill'),
(12.00, 'Entertainment', '2024-02-08', 'Cinema'),
(300.00, 'Rent', '2024-02-10', 'Rent contribution'),
(20.00, 'Food', '2024-02-11', 'Lunch with friend'),
(10.00, 'Food', '2024-02-12', 'Breakfast');
```