# ⭐️ 백엔드 개발 과제 (Java)
## 🛠️ Tools : Java, SpringBoot, Redis, H2, JPA, AWS
## 🚩 Period : 2025.03.13 ~ 2025.03.15
## 🔗 url : <a-href>http://13.209.1.200:8080/swagger-ui/index.html</a-href>
## 👨‍💻 About Project

### 👨‍💻 사용자 API (/users)

- 회원가입 : **📌 POST /users/signup**  

    - 설명: 사용자가 회원가입을 진행합니다.  
      응답: 200 OK: 회원가입 성공  
      400 Bad Request: 유효하지 않은 입력 값  
      409 Conflict: 이미 존재하는 사용자

- 로그인 : **📌 POST /users/login**  

    - 설명: 사용자가 로그인합니다.  
      응답: 200 OK: 로그인 성공  
      401 Unauthorized: 잘못된 로그인 정보

- 회원 정보 조회 : **📌 GET /users/{id}**   

    - 설명: 특정 사용자 정보를 조회합니다.  
      응답: 200 OK: 회원 조회 성공  
      404 Not Found: 회원 정보를 찾을 수 없음

- 로그아웃 : **📌 POST /users/logout**  

    - 설명: 사용자가 로그아웃을 수행합니다.  
      응답: 200 OK: 로그아웃 성공  
      401 Unauthorized: 인증되지 않은 사용자

- 회원 정보 수정 : **📌 PATCH /users**  

    - 설명: 사용자 정보를 수정합니다.  
      응답: 200 OK: 회원 정보 수정 성공  
      400 Bad Request: 유효하지 않은 입력 값  
      401 Unauthorized: 인증되지 않은 사용자  
      404 Not Found: 사용자 정보를 찾을 수 없음

- 회원 탈퇴 : **📌 DELETE /users**  

    - 설명: 사용자가 회원 탈퇴를 진행합니다.  
      응답: 200 OK: 회원 탈퇴 성공  
      400 Bad Request: 유효하지 않은 입력 값  
      401 Unauthorized: 인증되지 않은 사용자  
      404 Not Found: 사용자 정보를 찾을 수 없음

### 👨‍💻 관리자 API (/admin)

- 관리자 회원가입 : **📌 POST /admin**  

    - 설명: 관리자가 회원가입을 진행합니다. (```ADMIN_SECRET_KEY```) adminSecreykey에 입력
      응답: 201 Created: 관리자 회원가입 성공  
      400 Bad Request: 유효하지 않은 입력 값  
      409 Conflict: 이미 존재하는 관리자 계정

- 유저 권한 변경 : **📌 PATCH /admin/users/{id}/role**   

    - 설명: 관리자가 특정 유저의 권한을 변경합니다.
      응답: 200 OK: 유저 권한 변경 성공  
      400 Bad Request: 유효하지 않은 요청 값  
      401 Unauthorized: 인증되지 않은 사용자  
      403 Forbidden: 관리자 권한 없음  
      404 Not Found: 해당 유저를 찾을 수 없음

### 👨‍💻 Swagger 문서 확인  
** 엔드포인트: <a-href>http://13.209.1.200:8080/swagger-ui/index.html</a-href> ** 
설명: API 문서를 확인하고 테스트

## 🧨 TroubleShooting
- Logout
     원인 : 로그아웃 진행 후 토큰을 블랙리스트에 추가하는 문제 발생 
     해결 방법 : 요청에서 전달받은 기존 액세스 토큰을 블랙리스트에 추가하도록 수정함.
     성과 : 로그아웃된 토큰으로 다른 기능 수행 못함

- JwtBlackListTokenServiceTest
     원인 : test에서 예상한 결과 값이랑 내가 BlackListToken값을 설정한 값이 다름
     해결 방법 : 기존에 설정한 BlackListToken 설정 값과 동일하게 지정 

  
