## :pushpin: API 개발 기본

### request dto와 entity를 분리하는 이유

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

### 회원 조회 API

- Entity 혹은 List<Entity> 형태로 반환하는 것은 좋지 않다.
    - List 형식 반환은 json에서는 array 형태로 반환(스펙 추가에 대한 유연성이 떨어진다.)
- Entity 그대로 노출하는 것도 좋지 않다.
    - Entity에다가 @JsonIgnore 등 적용하는 것도 말이 안됨(api 스펙마다 다 다르다.)
- api 스펙은 말 그대로 필요한 내용만 노출하고 응답해야한다.
- 유지보수 입장에서도 dto 반환이 훨씬 좋다.

<br>

## :pushpin: API 개발 고급 - 지연 로딩과 조회 성능 최적화

### 엔티티를 직접 노출
```kotlin
@GetMapping("/api/v1/simple-orders")
fun ordersV1(): List<Order> {
    return orderRepository.findAllByString(OrderSearch())
}
```
- 위 방식대로 Order Entity를 그대로 반환하면 `StackOverflowException` 에러 발생한다.
- Order entity에는 연관관계로 있는 `Delivery`, `OrderItem`, `Member` entity에도 Order가 연결되어 있기 때문에  
  무한 순환하게 되어 발생하는 에러다.

이러한 이유로 `Delivery`, `OrderItem`, `Member` entity들에 Order 부분을 `@JsonIgnore` 처리해서 돌려보면  
다음과 같은 에러가 나온다.
```text
org.springframework.http.converter.HttpMessageConversionException:
Type definition error: [simple type, class org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor];
nested exception is com.fasterxml.jackson.databind.exc.InvalidDefinitionException: 
No serializer found for class org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor and no properties discovered to create BeanSerializer 
(to avoid exception, disable SerializationFeature.FAIL_ON_EMPTY_BEANS)
```
- Order의 Member가 LAZY로 연결되어 있다. 그래서 Order를 DB에서 가져올 때 Member는 조회하지 않고 Proxy 형태로 가져오게 된다.
- Proxy 가져올 때 사용되는 것이 `bytebuddy` 기술이다. (Member 필드에 `ByteBuddyInterceptor` 형태로 들어가 있음)
- jackson library에서 json 형태로 변형할 때 Order의 Member 접근하려고 하는데 `Member` 형태가 아니라 `ByteBuddyInterceptor`로 되어 있어서 발생한 에러

```kt
implementation 'com.fasterxml.jackson.datatype:jackson-datatype-hibernate5'
```
위의 문제를 해결하기 위해서 Hibernate5 모듈이 필요
```kt
//SpringBootJpaApplication
@SpringBootApplication
class SpringBootJpaApplication {
  @Bean
  fun hibernate5Module(): Hibernate5Module {
    return Hibernate5Module()
  }
}
```
- Hibernate5 모듈을 받아서 Bean을 설정해준다.
- 기본전략은 LAZY loading이면 무시하는 것인데 다르게 설정가능
    - 그래서 위의 설정만으로 api 호출하면 lazy에 대해서는 null이 나오는 것을 볼 수 있다.
```kotlin
@Bean
fun hibernate5Module(): Hibernate5Module {
    return Hibernate5Module().apply { 
        this.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true)
    }
}
```
- 위 방식대로 하면 LAZY 설정되어 있는 연관관계 필드에 대해서 LAZY 방식으로 조회하게 된다.
- 이렇게 설정하면 lazy 방식으로 연관관계 필드에 대해서 전부 호출해서 값을 내려보내는 것을 알 수 있다.

> 하지만 애초에 Entity 자체를 API response로 내려보내주는 것 자체가 말이 안된다. (위 내용 참고)  
> 그리고 위의 Hibernate5Module 사용해서 따로 설정하는 것도 좋지는 않음

```kt
@GetMapping("/api/v1/simple-orders")
fun ordersV1(): List<Order> {
    val orders = orderRepository.findAllByString(OrderSearch())
    orders.forEach {
        it.member?.name // lazy 강제 초기화
        it.delivery?.address // lazy 강제 초기화
    }
    
    return orders
}
```
Hibernate5Module configure 내용 주석하고 위에 내용처럼 Order에서 원하는 데이터에 대해서만 초기화를 시켜서 response를 보낼 수 있다.  
하지만 이 방법도 좋지는 않음  
api response 스펙도 좋지 않다.

#### EAGER로 성능 최적화 할 수 있지 않을까?(N+1 문제 해결 가능?) - 중요
```kotlin
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "member_id")
var member: Member? = null
    set(member) {
        field = member!!
        member.orders.add(this)
    }
```
- 위에서 api에 lazy 강제 호출하지 않고 원하는 데이터를 EAGER로 바꾸면 성능 최적화할 수 있지 않을까 생각 가능
    - lazy 강제 호출하는 순간 N+1 발생하기 때문(member 관련 쿼리가 또 날라가게 되기에)
- 하지만 EAGER로 했다고 여기서는 N+1 문제가 해결되지 않는다.
    - 이유는 `orderRepository.findAllByString(OrderSearch())` 이게 내부적으로 JPQL을 사용하기 때문
    - JPQL에 의해 order 내용만 selection 되므로 Order Entity만 반환받게 된다.
    - **그 때 Member는 EAGER로 설정되어 있어서 추가로 쿼리를 날려서 member 내용만 따로 db에서 조회하게 된다.**
- 또한 EAGER로 했을 때 다른 곳에서 Order entity를 사용하려 할 때에도 큰 문제 발생 가능
    - EAGER로 강제로 가져오기 때문에 원치 않은 Member 까지 계속 조회를 하게 된다.
- EAGER를 통한 성능 최적화
    - `em.find(id)`로 가져올 때에는 EAGER 하면 join 쿼리로 한방에 가져오게 된다.
- 만약 성능 최적화를 하고 싶다면 `fetch join`으로 할 것을 권장

> 결국 Hibernate5Module 사용하기보다 DTO 형태로 원하는 데이터만 Entity에서 뽑아서 반환하는 것이 좋다.

### DTO로 변환

```kotlin
@GetMapping("/api/v1/simple-orders")
fun ordersV1(): List<SimpleOrderDto> {
    val orders = orderRepository.findAllByString(OrderSearch())

    return orders.map {
        SimpleOrderDto.of(it)
    }
}

// SimpleOrderDto
companion object {
  fun of(order: Order): SimpleOrderDto {
    return SimpleOrderDto(
      order.id,
      order.member!!.name,
      order.orderDate!!,
      order.status!!,
      order.delivery!!.address!!
    )
  }
}
```
- 이렇게 하면 orders 데이터가 2개인 경우 총 5개의 쿼리가 날라가게 된다.
- order의 member, delivery가 LAZY로 묶여있음
- 그런 상황에서 stream map으로 가져오게 되는데 proxy lazy loading으로 동작
- 최초 select orders절 1개
- 각 order에 대해서 select member, delivery 2개
- **1 + 4 쿼리 실행 => 이것을 `N+1 문제`라고 한다.**  
  (이 상황에서는 1 + N + N / member, delivery)

### fetch join 최적화

```kotlin
@GetMapping("/api/v3/simple-orders")
fun ordersV3(): List<SimpleOrderDto> {
  return orderRepository.findAllWithMemberDelivery().map {
    SimpleOrderDto.of(it)
  }
}
```
- JPQL의 fetch join 기능을 사용하면 위에서 5번 발생했던 쿼리가 한 번 호출로 줄일 수 있음
- LAZY 설정되어 있어도 fetch join으로 묶여있으면 해당 Entity는 진짜 Entity로 해서 가져옴
- 실제 쿼리는 inner join 한 방 쿼리로 가져옴
- 실무에서 정말 많이 사용하는 기법(fetch join 너무 중요, **90%의 JPA 관련 문제는 여기서 발생**)

### JPA에서 DTO로 바로 조회

```kotlin
return em.createQuery(
    "SELECT new io.brick.jpabook.jpashop.repository.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address) " +
            "FROM Order o " +
            "join o.member m " +
            "join o.delivery d",
    OrderSimpleQueryDto::class.java
).resultList
```
- 여기서 join fetch 와 차이점은 select 절이 JPQL에서 지정한 필드에 대해서만 projection 한다
- Dto로 반환하게끔 JPQL을 작성한 경우 join fetch로 하면 에러 발생한다.

> - fetch join  
    > 엔티티 그래프를 한꺼번에 같이 조회하기 위해 사용하는 기능
> - JPQL Dto 반환  
    > select new를 통해 엔티티가 아닌 DTO를 조회하기 때문에 fetch join과 어긋남

- Dto 반환 JPQL은 활용성이 떨어짐(Dto 반환으로 고정되어 있음)
- 성능 최적화 측면에서 아주 쪼금 더 좋다고 할 수는 있지만 Entity 반환이 범용성이 좋아서 이대로 사용하는 것이 좋다.

> 정리
> - 우선 Entity -> Dto 변환 방식 사용
> - 필요시 fetch join으로 성능 최적
> - 그래도 안되면 Dto 직접 조회
> - 최후에는 네이티브 쿼리, Spring JDBC Template 사용