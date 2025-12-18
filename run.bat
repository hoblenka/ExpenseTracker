@echo off

if "%1"=="docker" (
    if not exist "target\ExpenseTracker-1.0-SNAPSHOT.jar" (
        echo ERROR: JAR file not found!
        echo Please build the JAR first in IntelliJ:
        echo 1. Maven panel ^> clean
        echo 2. Maven panel ^> package
        exit /b 1
    )
    echo Found JAR file, building Docker...
    docker-compose down
    if exist .env.docker (
        docker-compose --env-file .env.docker build --no-cache
        docker-compose --env-file .env.docker up -d
    ) else (
        docker-compose build --no-cache
        docker-compose up -d
    )
) else (
    set DB_USERNAME_MYSQL=root
    set DB_PASSWORD_MYSQL=your_mysql_password
    mvn spring-boot:run
)