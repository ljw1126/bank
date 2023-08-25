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