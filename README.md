<<<<<<< HEAD
# FreeMarket - 교내 중고거래 플랫폼
- FreeMarket은 대학교 내에서 학생들이 안전하고 편리하게 중고물품을 거래할 수 있는 웹 플랫폼입니다. 학교 이메일 인증을 통해 신뢰성을 확보하고, 다양한 카테고리의 물품을 거래할 수 있습니다.

## 주요 기능
### 사용자 관리
- 일반 회원가입 및 소셜 로그인(Google, Kakao, Naver) 지원
- 학교 이메일 인증을 통한 신원 확인
- 사용자 프로필 관리 및 비밀번호 변경

### 상품 관리
- 다양한 카테고리별 상품 등록, 조회, 수정, 삭제
- 다중 이미지 업로드 및 썸네일 자동 생성
- 상품 검색 및 필터링 기능

### 거래 시스템
- 관심상품(찜하기) 등록 및 관리
- 판매완료 처리 및 구매자 지정
- 구매/판매 내역 조회

### 리뷰 시스템
- 거래 후 판매자에 대한 별점 및 리뷰 작성
- 리뷰 수정 및 삭제
- 사용자별 평점 통계 확인

## 기술 스택
### 백엔드
- 언어: Java 21
- 프레임워크: Spring Boot 3.4.4, Spring Security, Spring Data JPA
- 빌드 도구: Gradle 8.13
- 데이터베이스: MySQL (프로덕션), H2 (개발)
- 인증/인가: JWT, OAuth2.0

### 프론트엔드

## 주요 라이브러리
- QueryDSL: 타입 안전한 쿼리 작성
- Swagger/SpringDoc: API 문서화
- Thumbnailator: 이미지 썸네일 생성
- JJWT: JWT 토큰 관리

## 프로젝트 구조
<img width="1078" alt="image" src="https://github.com/user-attachments/assets/341424c3-5a7c-4c63-bcdc-31c1a933a39e" />



## 개발 환경 설정
### 프로젝트 클론
- `bashgit clone https://github.com/your-username/FreeMarket.git`
- `cd FreeMarket`

### 개발용 환경 변수 설정 (`application-secret.yml` 파일 생성)
`yaml# src/main/resources/application-secret.yml`
```java
jwt:
  secret: your-jwt-secret-key

mysql:
  host: localhost
  port: 3306
  database: freemarket
  username: your-username
  password: your-password

oauth2:
  google:
    client-id: your-google-client-id
    secret: your-google-client-secret
  kakao:
    client-id: your-kakao-client-id
    secret: your-kakao-client-secret
  naver:
    client-id: your-naver-client-id
    secret: your-naver-client-secret

mail:
  username: your-email@gmail.com
  password: your-email-password

file:
  upload-dir: /path/to/images
  thumbnail-dir: /path/to/thumbnails

```

## API 문서
- Swagger UI를 통해 API 문서를 확인할 수 있습니다:
- `http://{baseurl}/swagger-ui/index.html`

## 주요 기능 세부 설명
### 인증 시스템
- JWT 기반 인증: 액세스 토큰(30분), 리프레시 토큰(7일)
- 소셜 로그인(Google, Kakao, Naver) 지원
- 학교 이메일 인증을 통한 서비스 이용 자격 확인

### 상품 관리
- 카테고리: 교재/서적, 전자기기, 의류/패션, 화장품/미용, 스포츠/레저, 생활용품, 취미/게임, 기타
- 상품 상태: 판매중, 품절, 판매중단, 승인대기
- 다중 이미지 업로드 및 썸네일 자동 생성

### 리뷰 시스템
- 1~5점 별점 평가
- 리뷰 작성은 실제 구매자만 가능
- 판매자별 평점 평균 및 리뷰 통계 제공
=======
# FreeMarket
캡스톤 디자인 교내 중고마켓 
>>>>>>> 91a48b4be237baa0bb972d6e44b7d3d49644fc45
