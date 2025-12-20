**Overview**

clean, secure, and well-structured Spring Boot application

**CI pipeline**

Runs on push to main/develop and pull requests

**Deploy Pipeline**

Builds Docker images on main branch pushes

**Deploy locally**

Run locally with Spring Boot: run.bat (default behavior)
Deploy with Docker using defaults: run.bat docker
Deploy with Docker using custom passwords: Create .env.docker file first, then run.bat docker

**Start APP**

Docker
http://localhost:8081/api/expenses

Local
http://localhost:8080/api/expenses

**Layered architecture Design**

* Controller → Service → DAO → Database
* DAO (Data Access Object) - Handles database operations, SQL queries, data persistence, pure data access
* Service - Contains business logic, validation, transaction management, orchestrates multiple DAOs



**Notes**

* first note
