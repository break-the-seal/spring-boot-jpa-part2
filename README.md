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

### JPA, DB 설정, 동작확인
- 설정파일에 hibernate dialect 설정해야함
- MySQL dialect 선택하면 `MVCC=true` 대신 `MODE=MySQL`로 설정해야 함
- `MVCC=true`: h2 1.4 버전 이후 지원 X (아예 제거해야함)
- `org.hibernate.type: trace`: 이거 사용하면 쿼리에 파라미터로 쓰인 실제 값을 로깅할 수 있음
  - 쿼리 파라미터를 보기좋게 하기 위해 외부 라이브러리 이용
  - https://github.com/gavlyukovskiy/spring-boot-data-source-decorator
```groovy
implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.8.0")
```


#### Test
- `@Test`와 함께 `@Transactional` 사용하면 테스트 종료 후 자동으로 rollback 함
- `@Rollback(false)`를 통해 rollback을 안할 수 있음
  - false 설정시 insert 쿼리가 날라가는 이유
    - **rollback**을 안하기에 `@Transactional` 지정한 method(테스트)가 끝난 경우에 **쓰기지연** 저장소에서 쿼리가 날라가게 됨
  - insert 쿼리만 있고 select 쿼리 없는 이유 
    - 1차 캐시에 이미 member entity 저장, 이후 조회에서는 id값 가지고 1차 캐시에서 조회하기에 select 쿼리 X
