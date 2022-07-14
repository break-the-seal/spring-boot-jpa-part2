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

<br>

## 📌 Section 2. 도메인 분석 설계 

### 도메인 모델과 테이블 설계
> 외래키가 있는 곳을 연관관계의 주인으로 정하자.  
> - Team:Member = 1:N 관계에서 일대다 단방향인 경우(Team -> Member)
> - Team entity: List<Member>에 @JoinColumn("TEAM_ID")
> - MEMBER table: TEAM_ID(FK) 외래키 관리
> - 외래키 관리하는 테이블 반대편인 Team entity에서 연관관계 주인으로 설정되어 있음
> - 이러면 persist할 때 Member, Team 각각 insert -> Member FK update 쿼리 추가해야 함
> - Team entity에서 관리하지 않는 Member 테이블의 FK가 update되는 문제


### 엔티티 클래스 개발 1
```kotlin
@OneToMany(mappedBy = "member")
val orders: MutableList<Order> = mutableListOf()
```
- 반대편 연관관계 주인이 되는 곳과 매핑(read-only 개념)
- 여기서 orders 변경해도 order 테이블의 FK 변경 X

```kotlin
// Order.kt
@OneToOne
val delivery: Delivery
```
- `one to one`인 경우 주로 많이 사용되는 entity(`Order`)에다가 OneToOne 연관관계 주인 설정
- 
### 엔티티 클래스 개발 2
- 다대다 연관관계 매핑(실무에선 사용하지 말자)
```kotlin
@ManyToMany
val items: MutableList<Item> = mutableListOf()
```
- 값 타입
  - 값 타입(`Address`)은 변경 불가능하게 설계해야 한다. (코틀린에서 고민해야되는 부분)
  - setter X, getter와 생성자만 허용 
  - 기본 생성자가 있어야 JPA가 reflection 사용 가능
    - `protected`로 허용, 이것도 코틀린에서 고민해야 되는 부분

### 엔티티 설계시 주의점
- Setter 사용하지 말자 (**코틀린인 경우 어떻게 작성해야 하는지 궁금**)
- 모든 연관관계는 지연로딩으로 설정
  - EAGER(즉시로딩) 설정은 예측이 어렵다.
  - JPQL 사용시 N+1 문제 발생이 쉽다.
  - 연관된 엔티티를 함께 즉시 조회해야 한다면 fetch join, entity graph 등 기능으로 할 수 있다.
  - `@XToOne`(`ManyToOne`, `OneToOne`) 관계는 default가 EAGER라서 주의해야 한다.(LAZY로 바꿔줘야 한다.)
- 컬렉션은 필드에서 초기화하자
  - null safe 하다는 장점
  - 엔티티 영속화할 때 컬렉션을 감싸서 하이버네이트 제공해주는 내장 컬렉션으로 변경해준다.
  - 해당 컬렉션은 중간에 변경하거나 하지 말자(하이버네이트 제공 내장 컬렉션으로 동작하기에)

#### 테이블, 컬럼명 생성 전략
- `SpringPhysicalNamingStrategy` (Camel -> underscore)
  - 논리명 생성
    - `spring.jpa.hibernate.naming.implicit-strategy` 테이블, 칼럼명 명시하지 않으면 논리명 적용
  - 물리명 적용
    - `spring.jpa.hibernate.naming.physical-strategy` 모든 논리명에 적용, 실제 테이블 적용(사내 룰로 정할 수 있음)
