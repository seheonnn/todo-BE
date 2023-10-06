# todo-BE

# ✏️ 프로젝트 요약
- 투두를 생성하여 일정 관리 웹 서비스입니다.
- 팔로우 기능을 통하여 친구와 일정을 공유할 수 있습니다.
  
# 🎯 프로젝트 목표
- JWT 토큰 및 Spring Security 학습
- NOSQL인 REDIS를 이용한 JWT 토큰 관리
- Spring boot 라이브러리를 이용한 메일 발송
- 카카오 소셜 로그인 구현
- Swagger 및 Postman을 사용한 Api 테스트
- [Amazon AWS, RDS](https://aws.amazon.com/ko/)를 이용한 ubuntu 환경 배포

# 🛠️ 사용 기술
Front-End : <img src="https://img.shields.io/badge/React-20232A?style=flat&logo=react&logoColor=61DAFB">
<br>
Back-End : <img src="https://img.shields.io/badge/Spring-6DB33F?style=for-the-flat&logo=spring&logoColor=white"> <img src="https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-flat&logo=spring-boot"> <img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-flat&logo=Spring-Security&logoColor=white"> <img src="https://img.shields.io/badge/JWT-000000?style=for-the-flat&logo=JSON%20web%20tokens&logoColor=white"><br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<img src="https://img.shields.io/badge/MySQL-005C84?style=for-the-flat&logo=mysql&logoColor=white"> <img src="https://img.shields.io/badge/redis-%23DD0031.svg?&style=for-the-flat&logo=redis&logoColor=white"> 
<br>
배포 환경 : 
<img src="https://img.shields.io/badge/Amazon_AWS-FF9900?style=for-the-flat&logo=amazonaws&logoColor=white"> <img src="https://img.shields.io/badge/Ubuntu-E95420?style=for-the-flat&logo=ubuntu&logoColor=white">

# 📺 프로젝트 화면 구성

| Swagger 적용 화면 |
| ------------------ |
| <img width="700" alt="1" src="https://github.com/seheonnn/todo-BE/assets/101795921/24af3e43-95b6-4ebd-a5b9-b1a6572a3e71"> |

| Refresh & Access token 발급 |
| ------------ |
| <img width="700" alt="2" src="https://github.com/seheonnn/todo-BE/assets/101795921/6efe1b93-a10e-404a-903a-e8b0c1684026"> |

| Redis를 이용한 JWT token 관리|
| ----------- |
|<img width="700" alt="3" src="https://github.com/seheonnn/todo-BE/assets/101795921/61aa6f5f-2712-40cf-a085-2a761162cf0d"> |
| 로그인 상태의 토큰은 "이메일:토큰" 형태로, 로그아웃 상태의 토큰은 "토큰:logout" 형태로 Redis에 저장하여 관리 |

| 이메일 인증 | ERD |
| ------------ | ------------ |
| <img width="700" alt="4" src="https://github.com/seheonnn/todo-BE/assets/101795921/8551a6ce-8164-4d0c-ab14-4d4e1011c441"> | <img width="700" alt="5" src="https://github.com/seheonnn/todo-BE/assets/101795921/2af40a2a-a094-4b20-914a-c6b28aa6482c"> |

|    Type     | Description  |
|:-----------:|---|
|   `Feat`    | 새로운 기능 추가 |
|    `Fix`    | 버그 수정 |
|    `Ci`     | CI관련 설정 수정 |
|   `Docs`    | 문서 (문서 추가, 수정, 삭제) |
|   `Style`   | 스타일 (코드 형식, 세미콜론 추가: 비즈니스 로직에 변경 없는 경우) |
| `Refactor`  | 코드 리팩토링 |
|   `Test`    | 테스트 (테스트 코드 추가, 수정, 삭제: 비즈니스 로직에 변경 없는 경우) |
|   `Chore`   | 기타 변경사항 (빌드 스크립트 수정 등) |
