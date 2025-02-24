# 🔖 한눈에 보는 북마크 서비스
### 🌟 별담: 별을 담다  
**[북마크 기반 큐레이션] 서비스**  
: 크롬 익스텐션과 태그 기반 추천 기능을 제공하는 **스마트한 북마크 관리 서비스** 

<br>

# ✨ 주요 기능 

### 1. 크롬 익스텐션 설치

![익스텐션 이미지](/image/1.익스텐션_설치.gif)

<br>

### 2. 익스텐션을 활용한 북마크 저장 

![익스텐션 이미지](/image/2.북마크_젖ㅇ.gif)

<br>

### 3. 사용자 관심 태그 기반 북마크 추천

![익스텐션 이미지](/image/3.추천_태그.gif)

<br>

### 4. 공유 컬렉션을 통한 북마크 공유 및 소통

![익스텐션 이미지](/image/4._공유컬렉션.gif)

##### ❗ 초대 및 강퇴 기능

![익스텐션 이미지](/image/초대강퇴.gif)

<br>

### 5. 태그 기반 검색으로 북마크 찾기

![익스텐션 이미지](/image/5.검색.gif)

<br>

### 6. RSS 피드 구독 및 알림 받기

![익스텐션 이미지](/image/6.피드_구독.gif)

![익스텐션 이미지](/image/7.피드_목록.gif)

<br>

### 7. 알림 기능

- 공유 컬렉션 초대 알림

- 사용자 설정 N일 지난 북마크 알림

<img src="/image/알림.png" alt="익스텐션 이미지" width="500">

<br>

### 8. 기타 기능

##### 로그인 / 회원가입

<img src="/image/로그인.png" alt="익스텐션 이미지" width="600">

<img src="/image/회원가입.png" alt="익스텐션 이미지" width="600">


##### 마이페이지

<img src="/image/마이페이지.png" alt="익스텐션 이미지" width="600">


##### 중요 북마크 및 오래된 북마크 모아보기

<img src="/image/중요.png" alt="익스텐션 이미지" width="600">

<img src="/image/오래된.png" alt="익스텐션 이미지" width="600">
<br>

---
<br>


## 🌐 사이트
🔗 [별담 서비스 바로가기](https://byeoldam.store)  

### 🧪 테스트 계정  
- **아이디:** `ucs210212@naver.com`  
- **비밀번호:** `1234`  


---
<br>

## 💡 프로젝트 특장점
#### 1. RSS 연동을 통한 실시간 업데이트 알림
- **구독 중인 뉴스, 블로그, 유튜브** 등 피드가 업데이트되면 알림  

#### 2. 태그 기반 추천 시스템
- **AI 기반 자동 태그 추천**을 통해 개인 맞춤형 북마크 추천  

#### 3. 공유 컬렉션을 통한 협업
- **그룹별 북마크 공유 기능**  
- **북마크 관련 의견을 나눌 수 있는 메모 기능 지원**  


---
<br>

## 🏆 프로젝트 차별점
#### 1. RabbitMQ 기반 비동기 알림
- **빠르고 안정적인 알림 시스템**을 제공  

<img src="/image/3.래빗.png" alt="익스텐션 이미지" width="600">

#### 2. Redis 기반 이메일 인증
- **빠른 이메일 인증 처리로 사용자 경험 향상**  

#### 3. 크롬 익스텐션 활용
- **북마크 저장, 태그 관리 등 사용자 접근성 극대화**  

<img src="/image/1.익스텐션.png" alt="익스텐션 이미지" width="600">

#### 4. AI 활용 - 자동 태그 추천 (GPT-3.5 Turbo API)
- **웹페이지 URL과 제목을 분석하여 최적의 태그 추천**  
- **사용자들이 많이 사용하는 태그를 기반으로 추천**  
- **적절한 태그가 없을 경우, 페이지 제목을 기반으로 AI 생성**  

<img src="/image/2.ai.png" alt="익스텐션 이미지" width="600">

#### 5. Grafana를 활용한 모니터링 시각화
- **대량의 로그를 쉽게 관리**
- **문제 발생 시 경고 메일 기능**

<img src="/image/4.그라파나.png" alt="익스텐션 이미지" width="600">

---
<br>

## 🛠 기술 스택
### 🖥 Frontend
- `Vue.js`
- `JavaScript`
- `HTML5`
- `Tailwind CSS`

### 🔥 Backend
- `Java 17`
- `Spring Boot 3.4.1`
- `RabbitMQ`

### 💾 Database
- `MySQL 8.0.4`
- `Redis`

### 🌐 Infrastructure
- `Docker`
- `Prometheus`
- `Grafana`


---

<br>

## 📜 산출물 

### 영상
[🔗 서비스 소개 영상](https://www.youtube.com/watch?v=OfSdOV68q54)

### ERD
![ERD](image/ERD.png)

### Architecture Diagram
![설명 텍스트](image/Web%20App%20Reference%20Architecture%20%285%29.png)



### User Flow & Wire Frame
[🔗 User Flow](https://www.figma.com/board/Ce1fyDbZh7lUdKMhZG1E4r/A208---FlowChart?node-id=0-1&p=f&t=gaoDUj24KI55Ow2o-0)
<br>
[🔗 Wire Frame](https://www.figma.com/design/uAl2EqrRoCL7BtRWtmnprv/A208---WireFrame?node-id=0-1&t=VfCv9iurRQE2e5lK-1)

### API 명세서
[🔗 API Docs](https://eenzzi.notion.site/API-17a45cc04c9d80e4accef604699f301b?pvs=4)

### 팀 노션
<a href="https://eenzzi.notion.site/PJT-17445cc04c9d80e48ec5feefdaf49286?pvs=4"><img alt="notion" src ="https://img.shields.io/badge/notion-skyblue.svg?&style=for-the-badge&logo=notion&logoColor=black"/></a>

<br>


 
