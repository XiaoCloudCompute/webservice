# webservice

## Program language
Java 1.8 or later
New space

## Framework
Spring boot

## Dependency
### Maven
- Spring security
- Spring jpa
- Jjwt
- mysql-connector

## How to test it
run command like this in linux platform
```shell
./mvnw test
```

run command like this in windows platform
```shell
./mvnw.cmd test
```

## How to deploy it locally
### Configure Spring Datasource, JPA, App properties
Open `src/main/resources/application.properties` and check the configuration
```
spring.datasource.url= jdbc:mysql://localhost:3306/testdb?useSSL=false
spring.datasource.username= root
spring.datasource.password= 123456
```
If you want to replace db_name, db_username or db_password, you can change it as you want. But you should make sure the mysql is running, and it has this database and user

### Run Spring Boot application
run command like this in linux platform
```shell
./mvnw spring-boot:run
```

run command like this in windows platform
```shell
./mvnw.cmd spring-boot:run
```
