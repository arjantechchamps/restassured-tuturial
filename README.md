# Readme for TechChamps-casus

## Pre-requisites
- Install Git: https://git-scm.com
- Install Maven: https://maven.apache.org/download.cgi
- Install Java 17 or higher: https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html
  - Or install OpenJDK 17 or higher: https://adoptopenjdk.net
- Install NodeJS: https://nodejs.org/en/download/, [Node Version Manager-linux/Mac](https://github.com/nvm-sh/nvm) or [Node Version Manager-Windows](https://github.com/coreybutler/nvm-windows)
- Optional: install Docker: https://docs.docker.com/get-docker

## Installation
- open a terminal and make sure you are in the folder casus
- run from terminal `mvn clean install`

## Start Back-end local

### Backend:
  - in folder restbackend go to src/main/resources/java/Application.java
  - run application main class
  - Backend is running on http://localhost:8085
    - alternative: run from terminal 
    
    ```sh
    # Build the server
    mvn package spring-boot:repackage
    # Run the server
    java - jar target/restbackend-0.0.1-SNAPSHOT.jar 
    ```

## Backend Swagger and Database console
You can use:
- swagger-url: http://localhost:8085/swagger-ui.html
- h2-console: http://localhost:8085/h2-console
  - login with username: sa and password: empty
  - jdbc:h2:mem:testdb

## H2-Database
- The H2 database is running in memory, so if you restart the backend then the database will go back to default state
- Default there are 3 user inside the database:
* admin, password admin1234, role admin and role user
* user, password user1234, role user
* moderator password moderator1234, role moderator
