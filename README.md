# Spring Boot JPA (Part 2)

[실전! 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8-JPA-API%EA%B0%9C%EB%B0%9C-%EC%84%B1%EB%8A%A5%EC%B5%9C%EC%A0%81%ED%99%94)

<br>

## :pushpin: API 개발 기본

### 회원 등록 API

##### request dto와 entity를 분리하는 이유

- Member Entity의 name 필드에 `@NotEmpty` validation 적용  
presentation layer의 요청데이터 검증을 위해 entity에 validation 기능까지 적용하는 것은 좋지 못하다.  
A API에서는 name이 필수값일 수 있지만 B API에서는 필수값이 아닐 수 있기 때문이다.  
(즉 API 스펙마다 request body 형태와 조건이 다르기 때문)
- Entity 스펙이 변경되는 시점에서 API request 부분까지 같이 변경이 되는 문제 발생  
entity 스펙이 변경되어도 API 스펙은 변경되어서는 안된다.

### 회원 수정 API
    
서비스 레이어에서 수정 api 작성할 때 되도록 반환값을 없앤다. (혹은 id 값이라도 반환한다.)  
수정 메소드는 요청 데이터를 가지고 데이터를 변경해주는 역할이지 수정된 Entity를 반환하면 조회기능까지 포함되는 것  
그래서 가급적이면 아예 아무것도 반환하지 않는 것이 좋다.