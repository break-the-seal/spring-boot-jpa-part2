# Spring Boot JPA (Part 1)

[실전! 스프링 부트와 JPA 활용1 - 웹 애플리케이션 개발(김영한님)](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8-JPA-%ED%99%9C%EC%9A%A9-1/dashboard)

## 📌 Section 1. 프로젝트 환경설정

### 라이브러리 살펴보기
- `spring-boot-starter-data-jpa`
  - `spring-boot-starter-jdbc`
    - `HikariCP`: connection pool (보편적으로 사용되는 CP)
    - `hibernate`
    - `spring-data-jpa`
  - `spring-boot-starter-aop`
    - `spring-boot-starter`
      - `spring-boot-starter-logging`
        - `logback`, `slf4j` 사용
        - `kotlin-logging`을 통해 `slf4j` 를 wrapping해서 사용
- `spring-boot-starter`라이브러리를 땡기면 관련 라이브러리들을 자동으로 같이 설정해준다.

### View 환경 설정
- thymeleaf 사용