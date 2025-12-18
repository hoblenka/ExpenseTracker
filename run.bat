@echo off

if "%1"=="docker" (
    docker-compose down
    if exist .env.docker (
        docker-compose --env-file .env.docker build --no-cache
        docker-compose --env-file .env.docker up -d
    ) else (
        docker-compose build --no-cache
        docker-compose up -d
    )
) else if "%1"=="hub" (
    docker-compose down
    if exist .env.docker (
        docker-compose --env-file .env.docker -f docker-compose.hub.yml up -d
    ) else (
        docker-compose -f docker-compose.hub.yml up -d
    )
) else (
    set DB_USERNAME_MYSQL=root
    set DB_PASSWORD_MYSQL=your_mysql_password
    mvn spring-boot:run
)