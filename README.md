clean, secure, and well-structured Spring Boot application

**CI pipeline**
Runs on push to main/develop and pull requests

**Security Scan**
dependency vulnerability checks by OWASP

**Deploy Pipeline**
Builds Docker images on main branch pushes

**Deploy locally**
Run locally with Spring Boot: run.bat (default behavior)
Deploy with Docker using defaults: run.bat docker
Deploy with Docker using custom passwords: Create .env.docker file first, then run.bat docker