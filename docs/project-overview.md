# ExpenseTracker – Project Overview

## Purpose
ExpenseTracker is a Java web application running on Apache Tomcat.  
It allows users to record, view, update, and delete personal expenses stored in a MySQL database.

## Tech Stack
- Java 17+
- Apache Tomcat 9/10
- JSP + Servlets
- JDBC
- MySQL 8.x
- Maven
- Bootstrap (optional)

## Core Features
- Add new expense
- List all expenses
- Edit existing expense
- Delete expense
- Filter by category or date (future enhancement)

## Database Schema
Table: `expenses`

| Column      | Type         | Notes                     |
|-------------|--------------|---------------------------|
| id          | INT PK AI    | Primary key               |
| amount      | DECIMAL(10,2)| Expense amount            |
| category    | VARCHAR(255) | Category name             |
| date        | DATE         | Expense date              |
| description | TEXT         | Optional description      |

## Application Modules
- `/model` – POJOs
- `/dao` – Data access layer
- `/servlets` – Controllers
- `/views` – JSP pages
- `/config` – DB connection utilities

