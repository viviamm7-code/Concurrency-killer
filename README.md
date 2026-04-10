# ☁️ Concurrency-Killer

---

# ☁️ 백엔드 팀원
  <table>
  <tr>
    <td align="center" width="25%">
      <a href="https://github.com/viviamm7-code">
        <img src="https://github.com/viviamm7-code.png" width="100%">
      </a>
    </td>
    <td align="center" width="25%">
      <a href="https://github.com/tjtjdfbf">
        <img src="https://github.com/tjtjdfbf.png" width="100%">
      </a>
    </td>
    <td align="center" width="25%">
      <a href="https://github.com/Hojin00/">
        <img src="https://github.com/Hojin00.png" width="100%">
      </a>
    </td>
    <td align="center" width="25%">
      <a href="https://github.com/Lee-Yeonjoo">
        <img src="https://github.com/Lee-Yeonjoo.png" width="100%">
      </a>
    </td>
  </tr>
  <tr>
    <td align="center"><a href="https://github.com/viviamm7-code">김진성</a></td>
    <td align="center"><a href="https://github.com/tjtjdfbf">서성률</a></td>
    <td align="center"><a href="https://github.com/Hojin00">류호진</a></td>
    <td align="center"><a href="https://github.com/Lee-Yeonjoo">이연주</a></td>
  </tr>
</table>

## ☁️ 소개
Concurrency-Killer은 동시성을 고려한 온라인 뮤지컬 티켓팅 프로젝트입니다.

## ☁️ 기능
- 공연 정보 및 좌석 정보 조회
- 공연 좌석 예매
- 관리자 페이지 
- 내 정보 조회 (비밀번호 변경, 회원 탈퇴)
- 내 예매 조회 및 환불
- 회원가입 및 소셜 로그인
- 토스페이먼츠 API 연동 후 결제 시스템
- 카카오맵 API 연동 후 지도 표시

## ☁️ 사용 기술
- HTML , CSS , JS
- Spring Boot 4.0.3 , Spring Data JPA
- MariaDB, Redis
- AWS EC2, AWS S3, AWS RDS

## ☁️ 프로젝트 아키텍쳐
<img width="1536" height="1024" alt="image" src="https://github.com/user-attachments/assets/b2b249d0-c1ce-436b-95cc-eed281c8524d" />


## ☁️ 프로젝트 주요 관심사
- 동시에 여러 사용자가 예매를 시도하는 상황에서 좌석 중복 예매를 방지하기 위한 동시성 제어
- 예매, 취소 과정에서 데이터 정합성을 유지하며 트랜잭션 관리
- 조회 성능 개선과 데이터베이스 부하 감소를 위해 Redis 캐시를 사용
- 예매 시스템의 사용자 UX 고려
- 여러 요인을 고려한 단위 및 통합 테스트 (prometheus + grafana cloud + K6) 
- 각종 API를 연동해 편리한 서비스 제공
- OpenAPI Swagger를 통한 API 문서 자동화

## ☁️ git 브랜치 전략
- main 브랜치: 서비스에 배포될 코드를 관리하는 브랜치
- feature/project: 개발된 모든 기능들을 포함 & 테스트 하는 브랜치
- 새로운 기능을 개발할때마다 feature/TK[태스크 넘버]-[기능 설명] 브랜치에서 생성 후 작업

## ☁️ 시연 영상
### 1차 프로젝트 :
- 시나리오 1 : <a href="https://youtu.be/qJN7NWEaD3o">기본 예매</a>
- 시나리오 2 : <a href="https://youtu.be/6wcqGtdElQw">예매 취소</a>
- 시나리오 3 : <a href="https://youtu.be/MjMEg_p_3Rc">내 예매 필터링</a>
- 시나리오 4 : <a href="https://youtu.be/TQd6w_Ithjs">동시성 문제 해결</a>
### 2차 프로젝트 :
- 시나리오 5 : <a href="https://youtu.be/xbHfR-yul48">대기열</a>
- 시나리오 6 : <a href="https://youtu.be/w_Sd11NYsLI">회원 가입과 내 정보</a>
- 시나리오 7 : <a href="https://youtu.be/fROqw3g986g">관리자</a>
- 시나리오 8 : <a href="https://youtu.be/qlSmHTl87tU">외부 API 기능</a>

## ☁️ Wiki
위키 페이지는 분량상 README에 담지 못한 본 프로젝트의 기획 문서와 설계 문서를 포함하고 있습니다.
#### 기획 및 설계 문서
- [Notion](https://www.notion.so/Project-8e5bd34f43e6834097670153af124ece)

#### 협업 문서
- [Trello1](https://trello.com/b/xRzx4FIM/%ED%8B%B0%EC%BC%93%ED%8C%85)
- [Trello2](https://trello.com/b/jVXhTGSw/%ED%8B%B0%EC%BC%93%ED%8C%852)

