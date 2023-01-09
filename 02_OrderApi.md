## :pushpin: API 개발 고급 - 컬렉션 조회 최적화

### 엔티티 직접 노출 & Dto 변환

N+1의 문제 발생
```kotlin
@GetMapping("/api/v2/orders")
fun orderV2(): List<OrderDto> {
    val orders = orderRepository.findAllByString(OrderSearch())
    // LAZY proxy 초기화
    return orders.map {
        OrderDto.of(it)
    }
}
```
이렇게 되면 Order Entity에 연관관계로 연결되어 있는 entity에 대해서 추가 조회 쿼리가 나가게 된다.  
```
Order
- Member
- OrderItem
    - Item
- Delivery
```

<br>

### fetch join 최적화

```kotlin
fun findAllWithItem(): List<Order> {
    return em.createQuery(
        """
            |select o from Order o
            |    join fetch o.member m
            |    join fetch o.delivery d
            |    join fetch o.orderItems oi
            |    join fetch oi.item i
        """.trimMargin(),
        Order::class.java
    ).resultList
}
```
위의 방식으로 fetch join을 하게 되면 order data가 뻥튀기 된다.
```
order id: 4 > order_item id: 6
order id: 4 > order_item id: 7
order id: 11 > order_item id: 13
order id: 11 > order_item id: 14
```
fetch join은 실제 쿼리가 나갈 때 join으로 나가게 되는데 실제 쿼리 결과는 order 데이터가 각각 2개씩 나오게 된다.  
JPA는 이를 그대로 반영해서 Entity에도 똑같은 Order 내용이 2개가 나오게 되는 것을 확인할 수 있다.
```text
order ref = io.brick.jpabook.jpashop.domain.Order@1cde0081 / id = 4
order ref = io.brick.jpabook.jpashop.domain.Order@1cde0081 / id = 4
order ref = io.brick.jpabook.jpashop.domain.Order@215d91f8 / id = 11
order ref = io.brick.jpabook.jpashop.domain.Order@215d91f8 / id = 11
```
이를 방지하기 위해 JPQL distinct 문법을 사용하면 된다.
```kotlin
fun findAllWithItem(): List<Order> {
    return em.createQuery(
        """
            |select distinct o from Order o
            |    join fetch o.member m
            |    join fetch o.delivery d
            |    join fetch o.orderItems oi
            |    join fetch oi.item i
        """.trimMargin(),
        Order::class.java
    ).resultList
}
```
- 이렇게 해도 실제 쿼리 조회시 중복제거는 발생하지 않고 이전과 그대로다.  
  - select distinct order_id, member_id, ... from order ... 생각해보면 된다.  
  - distinct 뒤의 칼럼 데이터들이 모두 같아야 중복을 제거하는 특징
- 하지만 application 단계에서 JPQL distinct는 쿼리 조회 결과를 보고 중복이 있으면 이를 알아서 제거해준다.  
  - JPQL root from entity에서 중복이 있으면 알아서 제거해준다.
- 결국 JPQL fetch join으로 원하는 entity만 미리 가져와서 쿼리 하나로 관련 데이터 모두 조회 가능

> **치명적 단점**  
> 페이징 기능 사용을 못함  
> 만약 페치 조인에 페이징 기능을 적용하면 애플리케이션 단에서 WARN 경고를 낸다.  
> applying in memory >> 실제 쿼리상에서 join된 결과를 페이징 처리할 수 없으니 어쩔 수 없이 메모리로 모든 데이터를 올려서 페이징 처리  
> 만약 데이터 규모가 만개 단위면 OOM 발생 가능

<br>

### 페치 조인에서의 페이징과 한계 돌파

- 우선 ToOne 관계의 연관 entity에 대해서는 fetch join으로 가져온다. (어차피 데이터 뻥튀기 발생할 일 없음)
- 그리고 리스트 연관관계의 entity에 대해서는 BatchSize로 가져온다.
```yml
spring:
  jpa:
    properties:
      hibernate:
        default_batch_fetch_size: 100 # batch size 설정
```
- 이렇게 되면 order 조회로 우선 가져오고 그다음 order_item > item 을 batch size 만큼 in query로 조회하게 된다.
- 즉 batch size 설정시 각각의 order_item_id 에 따라 쿼리 하나씩 생성해서 요청하는 것이 아니라 in query를 통해 bulk하게 조회

**BatchSize Entity 설정**
```kotlin
// Order
@BatchSize(size = 100)
@OneToMany(mappedBy = "order", cascade = [CascadeType.ALL])
var orderItems: MutableList<OrderItem> = ArrayList()

// Member
@BatchSize(size = 100)
@Entity
class Member protected constructor(...)
```
- `@ManyToOne`에서는 연관관계 대상이 되는 Entity class 단에 설정(Order 입장에서는 Member)
- `@OneToMany`에서는 해당 필드에다가 설정

#### 정리
OneToMany를 가져오는데 있어서 N+1 문제가 발생(LAZY)  
 - fetch join으로 OneToMany 연관관계 대상을 쿼리 하나로 가져옴(장점)
 - 하지만 데이터 중복 문제(데이터 뻥튀기 문제), 페이징 처리 불가 문제 발생  

 batch size를 통한 각각의 entity in query 조회
 - 우선 Order만 조회(**필요시 페이징 처리도 가능**)
 - 연관관계의 entity에 대해서 batch size 설정된 대로 id 값들을 가지고 in query 조회
 - 즉 batch size 설정을 잘하면 `N + 1` > `1 + 1`도 가능(물론 batch size를 넘어가는 데이터 량이면 쿼리 개수 증가)
 - fetch join으로 발생했던 데이터 뻥튀기 문제도 해결 가능 > **DB 데이터 전송량 감소**
   - ex. Order 1개에 OrderItem 100개 연결되어 있으면 Order 데이터도 100개가 조회되는 중복문제 발생

<br>

### JPA에서 DTO 직접 조회(JPQL)

