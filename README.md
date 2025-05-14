# ⭐️ 백엔드 개발 과제 (Java)
## 🛠️ Tools : Java(17), SpringBoot(3.3.2), Redis, MySQL(8.0), JPA, AWS
## 🚩 Period : 2025.03.13 ~ 2025.03.15
## 🚩 Refactor : 2025.05.12 ~ 2025.05.13
## 🔗 url : <a-href>http://3.39.230.168:8080/swagger-ui/index.html</a-href>
## 👨‍💻 About Project

## 👨‍💻 사용자 API (/users)

- 회원가입 : **📌 POST /users/signup**  

    - 설명: 사용자가 회원가입을 진행  
      응답: 200 OK: 회원가입 성공  
      400 Bad Request: 유효하지 않은 입력 값  
      409 Conflict: 이미 존재하는 사용자

- 로그인 : **📌 POST /users/login**  

    - 설명: 사용자가 로그인 
      응답: 200 OK: 로그인 성공  
      401 Unauthorized: 잘못된 로그인 정보

- 회원 정보 조회 : **📌 GET /users**   

    - 설명: 특정 사용자 정보를 조회 
      응답: 200 OK: 회원 조회 성공  
      404 Not Found: 회원 정보를 찾을 수 없음

- 로그아웃 : **📌 POST /users/logout**  

    - 설명: 사용자가 로그아웃을 수행 
      응답: 200 OK: 로그아웃 성공  
      401 Unauthorized: 인증되지 않은 사용자

- 회원 정보 수정 : **📌 PATCH /users**  

    - 설명: 사용자 정보를 수정  
      응답: 200 OK: 회원 정보 수정 성공  
      400 Bad Request: 유효하지 않은 입력 값  
      401 Unauthorized: 인증되지 않은 사용자  
      404 Not Found: 사용자 정보를 찾을 수 없음

- 회원 탈퇴 : **📌 DELETE /users**  

    - 설명: 사용자가 회원 탈퇴를 진행
      응답: 200 OK: 회원 탈퇴 성공  
      400 Bad Request: 유효하지 않은 입력 값  
      401 Unauthorized: 인증되지 않은 사용자  
      404 Not Found: 사용자 정보를 찾을 수 없음

### 👨‍💻 관리자 API (/admin)

- 관리자 회원가입 : **📌 POST /admin**  

    - 설명: 관리자가 회원가입을 진행 (```SUPER_ADMIN_SECRET_KEY```) adminSecreykey에 입력
      응답: 201 Created: 관리자 회원가입 성공  
      400 Bad Request: 유효하지 않은 입력 값  
      409 Conflict: 이미 존재하는 관리자 계정

- 유저 권한 변경 : **📌 PATCH /admin/users/{id}/role**   

    - 설명: 관리자가 특정 유저의 권한을 변경
      응답: 200 OK: 유저 권한 변경 성공  
      400 Bad Request: 유효하지 않은 요청 값  
      401 Unauthorized: 인증되지 않은 사용자  
      403 Forbidden: 관리자 권한 없음  
      404 Not Found: 해당 유저를 찾을 수 없음

## 👨‍💻 Swagger 문서 확인  
**엔드포인트: <a-href>http://3.39.230.168:8080/swagger-ui/index.html</a-href>** 
설명: API 문서를 확인하고 테스트

## 🔁 Refactoring
✅ AOP 적용
SaveRefreshToken, DeleteRefreshToken 동작을 각각 로그인 / 로그아웃 시점에 AOP로 적용

코드 중복 제거 및 유지보수 용이

✅ 관리자 권한 체크 모듈화
AdminPermissionChecker를 @Component로 등록

관리 API 접근 시 @PreAuthorize 같은 방식이 아닌 직접 주입 방식으로 명시적 권한 체크 처리

## 🏗 인프라 아키텍처
AWS EC2 + RDS + Docker 기반 인프라 구성
```
[ Local Build ]          [ Remote Deployment (EC2) ]
   ┌────────────┐         ┌──────────────────────────┐
   │ Spring Boot│  --->   │  Docker (Spring App)     │
   │ App + Redis│         │  Docker (Redis)          │
   └────────────┘         │  Docker (MySQL via RDS)  │
                          └──────────────────────────┘
                                 ↓
                            AWS RDS (MySQL)

```

## 🧨 TroubleShooting
🔴 Redis 연결 실패 이슈
문제: Docker 컨테이너 내부에서 localhost:6379로 연결을 시도하여 RedisConnectionFailureException 발생  
원인: Redis 호스트명을 환경 변수로 넘겼지만, RedisProperties에서 제대로 주입되지 않음

해결:
@Value 기반으로 spring.redis.host, spring.redis.port 직접 바인딩하여 문제 해결  
Docker Compose에서 command를 사용하여 명시적으로 --spring.redis.host=redis 전달

🔒 Logout 블랙리스트 처리
로그아웃 시 AccessToken을 Redis에 블랙리스트로 저장하여 재사용 방지  
성공적으로 JWT 블랙리스트 기반 로그아웃 처리 완료

⚠️ JWT 테스트 실패
JwtBlackListTokenServiceTest에서 예상 값과 실제 블랙리스트 처리 값이 달라 실패  
블랙리스트 키 포맷을 통일하여 테스트 성공

  
