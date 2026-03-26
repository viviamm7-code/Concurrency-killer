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
- 내 예매 조회
- 내 예매 취소

## ☁️ 사용 기술
- HTML , CSS , JS
- Spring Boot 4.0.3 , Spring Data JPA
- MariaDB, Redis
- AWS EC2, AWS S3

## ☁️ 프로젝트 아키텍쳐
<img width="1536" height="1024" alt="image" src="https://github.com/user-attachments/assets/85494f67-b33b-4891-961a-25d6a0736cc3" />

## ☁️ 프로젝트 주요 관심사
- 동시에 여러 사용자가 예매를 시도하는 상황에서 좌석 중복 예매를 방지하기 위한 동시성 제어
- 예매, 취소 과정에서 데이터 정합성을 유지하며 트랜잭션 관리
- 조회 성능 개선과 데이터베이스 부하 감소를 위해 Redis 캐시를 사용
- 예매 시스템의 사용자 UX 고려
- 2차 프로젝트를 대비한 확장성 고려

## ☁️ git 브랜치 전략
- main 브랜치: 서비스에 배포될 코드를 관리하는 브랜치
- feature/project: 개발된 모든 기능들을 포함 & 테스트 하는 브랜치
- 새로운 기능을 개발할때마다 feature/TK[태스크 넘버]-[기능 설명] 브랜치에서 생성 후 작업

## ☁️ 시연 영상
- 시나리오 1 : 기본 예매 (https://youtu.be/zHFRWrlN11c)
- 시나리오 2 : 예매 취소 (https://youtu.be/K2KoGzRjm10)
- 시나리오 3 : 내 예매 필터링 (https://youtu.be/w1J16r12ua4)
- 시나리오 4 : 동시성 문제 해결 (https://youtu.be/W_giU47Eebo)

## ☁️ Wiki
위키 페이지는 분량상 README에 담지 못한 본 프로젝트의 기획 문서와 설계 문서를 포함하고 있습니다.
#### 기획 및 설계 문서
- [Notion](https://www.notion.so/Project-8e5bd34f43e6834097670153af124ece)

#### 협업 문서
- [Trello](https://trello.com/b/xRzx4FIM/%ED%8B%B0%EC%BC%93%ED%8C%85)

