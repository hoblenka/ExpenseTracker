# ExpenseTracker – Test Plan for Future Enhancements

This test plan defines the testing strategy, test cases, expected results, and acceptance criteria for all enhancements listed in `future-enhancements.md`.

---

## ✅ 1. Test Objectives
- Verify that each enhancement works as intended
- Ensure no regressions in existing functionality
- Validate UI, backend logic, and database operations
- Confirm error handling and edge cases behave correctly

---

## ✅ 2. Test Scope

### In Scope
- Filtering features
- Pagination
- Authentication
- CSV export
- REST API
- Validation improvements
- UI enhancements
- Dashboard charts
- Multi-user support

### Out of Scope
- Performance testing
- Security penetration testing
- Load testing

---

## ✅ 3. Test Environment
- Java web app deployed on Tomcat
- MySQL database with sample data
- Browser: Chrome or Firefox
- Test dataset:
    - At least 20 expenses
    - Multiple categories
    - Multiple dates across months

---

## ✅ 4. Test Data

### Sample Categories
- Food
- Transport
- Utilities
- Entertainment

### Sample Date Ranges
- Current month
- Previous month
- Last 90 days

### Sample Users (for multi-user support)
- userA / passA
- userB / passB

---

## ✅ 5. Test Cases (Matching Future Enhancements)

### ✅ 5.1 Filtering by Category
| Test Case | Input | Expected Result |
|-----------|--------|----------------|
| TC-CAT-01 | Food | Only Food expenses shown |
| TC-CAT-02 | travel | Empty list (case-insensitive) |
| TC-CAT-03 | "" | All expenses shown |

---

### ✅ 5.2 Filtering by Date Range
| Test Case | Input | Expected Result |
|-----------|--------|----------------|
| TC-DATE-01 | 2024-01-01 → 2024-01-31 | Only January expenses |
| TC-DATE-02 | Start only | All expenses from start date |
| TC-DATE-03 | End only | All expenses up to end date |
| TC-DATE-04 | Invalid date | Validation error |
| TC-DATE-05 | Start > End | Error or empty result |

---

### ✅ 5.3 Pagination
| Test Case | Input | Expected Result |
|-----------|--------|----------------|
| TC-PAG-01 | Page 1 → Page 2 | Next 10 items |
| TC-PAG-02 | Page 2 → Page 1 | Previous 10 items |
| TC-PAG-03 | Page > max | Redirect to last page |
| TC-PAG-04 | Empty DB | No errors |

---

### ✅ 5.4 Authentication
| Test Case | Input | Expected Result |
|-----------|--------|----------------|
| TC-AUTH-01 | Valid login | Redirect to /expenses |
| TC-AUTH-02 | Invalid login | Error message |
| TC-AUTH-03 | Access without login | Redirect to login |
| TC-AUTH-04 | Logout | Session cleared |

---

### ✅ 5.5 CSV Export
| Test Case | Input | Expected Result |
|-----------|--------|----------------|
| TC-CSV-01 | Export all | CSV with all rows |
| TC-CSV-02 | Export filtered | CSV matches filter |
| TC-CSV-03 | CSV format | Correct headers, UTF‑8 |

---

### ✅ 5.6 REST API
| Test Case | Input | Expected Result |
|-----------|--------|----------------|
| TC-API-01 | GET /api/expenses | JSON array |
| TC-API-02 | POST valid JSON | 201 Created |
| TC-API-03 | POST invalid JSON | 400 Bad Request |
| TC-API-04 | PUT update | 200 OK |
| TC-API-05 | DELETE | 204 No Content |

---

### ✅ 5.7 Validation Improvements
| Test Case | Input | Expected Result |
|-----------|--------|----------------|
| TC-VAL-01 | Negative amount | Error |
| TC-VAL-02 | Empty category | Error |
| TC-VAL-03 | Future date | Error |
| TC-VAL-04 | Long description | Error or truncation |

---

### ✅ 5.8 UI Enhancements
| Test Case | Input | Expected Result |
|-----------|--------|----------------|
| TC-UI-01 | Mobile view | Responsive layout |
| TC-UI-02 | Form submission | Clear validation messages |
| TC-UI-03 | Table display | Clean formatting |

---

### ✅ 5.9 Dashboard Charts
| Test Case | Input | Expected Result |
|-----------|--------|----------------|
| TC-CHART-01 | Category data | Correct totals |
| TC-CHART-02 | Monthly data | Correct totals |
| TC-CHART-03 | Empty DB | Zero values |

---

### ✅ 5.10 Multi-user Support
| Test Case | Input | Expected Result |
|-----------|--------|----------------|
| TC-MULTI-01 | User A login | Only A’s expenses |
| TC-MULTI-02 | User B login | Only B’s expenses |
| TC-MULTI-03 | Cross-user access | Forbidden |

---

## ✅ 6. Acceptance Criteria
A feature is accepted when:
- All test cases pass
- No regressions occur
- UI behaves consistently
- Database operations are correct
- Error handling is user-friendly

---

## ✅ 7. Risks & Mitigations
| Risk | Mitigation |
|------|------------|
| Incorrect SQL queries | Add DAO unit tests |
| Session handling bugs | Add integration tests |
| CSV encoding issues | Force UTF‑8 |
| API inconsistencies | Use Postman test suite |

---

## ✅ 8. Test Completion Criteria
Testing is complete when:
- All enhancements have passing test cases
- All bugs are resolved or documented
- Application runs without errors on Tomcat
