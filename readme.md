#Junit Bank App

## Jpa LocalDateTime 자동으로 생성하는 법 
- @EnableJpaAuditing (Main 클래스)
- @EntityListeners(AuditingEntityListener.class) (Entity 클래스)

```java 
@CreatedDate // Insert
@Column(nullable = false)
private LocalDateTime createdAt;

@LastModifiedDate // Insert, Update
@Column(nullable = false)
private LocalDateTime updatedAt;
```

## 계좌 등록 절차
- AccountService
- 파라미터 AccountSaveRequestDto , userId 
1) User 검증 
2) 해당 계좌가 DB가 있는지 중복 여부 체크
3) 계좌 등록
4) DTO 응답 (AccountSaverResponseDto)

## @SQL teardown.sql 
Controller 테스트시 데이터 초기화할 경우 
- @Transacational rollback
- @AfterEach teardown method에서 deleteAllInBatch() 실행하여 매 테스트시 데이터 초기화 하도록하였다.

그런데 두 경우 auto-increase 속성이 초기화 되지 않아, 테스트 실행 전 초기화시 더미 데이터의 id 값이 계속 증가하여 
원치 않는 값을 가지는 경우를 확인

### /resource/db/teardown.sql 생성
``` 
SET REFERENTIAL_INTEGRITY FALSE;
truncate table transaction_tb;
truncate table account_tb;
truncate table user_tb;
SET REFERENTIAL_INTEGRITY TRUE;
```

### Controller 테스트에 @Sql 추가
- tearDown method와 @Transactional 삭제
- 매 테스트마다 auto-increase 초기화 확인
```java
@Sql("classpath:/db/teardown.sql")
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class UserControllerTest extends DummyObject {
    //..
}
```

 