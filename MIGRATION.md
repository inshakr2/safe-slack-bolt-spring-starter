# Migration Guide

기존 `BR-message`의 Slack Bolt 공통화 코드에서 본 라이브러리로 이전할 때의 매핑 가이드입니다.

## 1. 클래스 매핑

| 기존 클래스 | 신규 클래스 |
|---|---|
| `com.brobotics.core.infrastructure.bolt.SafeBoltAppHandler` | `io.github.inshakr2.safeslackbolt.core.handler.SafeBoltHandler` |
| `com.brobotics.core.infrastructure.bolt.SafeCommandHandler` | `io.github.inshakr2.safeslackbolt.core.handler.command.AbstractSafeCommandHandler` |
| `com.brobotics.core.infrastructure.bolt.SafeBlockActionHandler` | `io.github.inshakr2.safeslackbolt.core.handler.action.AbstractSafeBlockActionHandler` |
| `com.brobotics.core.infrastructure.bolt.SafeViewSubmissionHandler` | `io.github.inshakr2.safeslackbolt.core.handler.view.AbstractSafeViewSubmissionHandler` |

## 2. 등록 방식 변경

### 기존
- `SlackBoltServerConfig`에서 `instanceof` 기반 분기 등록

### 신규
- 각 핸들러가 `register(App app)` 계약으로 직접 등록
- starter가 `SafeBoltHandler` 빈을 자동 수집 및 등록

## 3. 비동기 처리 변경

### 기존
- 핸들러 내부에서 `app.executorService().submit(...)` 강제 비동기 실행

### 신규
- 기본 동기 처리
- `handle(...)` 예외 발생 시 공통 로깅 후 `ctx.ack()` fallback

## 4. 설정 이동

### 기존
- `SlackBoltServerConfig`에서 토큰/소켓모드 초기화

### 신규
- `safe.slack.bolt.*` 프로퍼티로 자동 설정

```yaml
safe:
  slack:
    bolt:
      enabled: true
      bot-token: ${SLACK_BOT_TOKEN}
      app-token: ${SLACK_APP_TOKEN}
      socket-mode-enabled: true
      socket-mode-auto-startup: true
```

## 5. 운영 안정성 강화 포인트
- 핸들러 identifier 중복 시 시작 단계 Fail-Fast
- SocketMode 시작/종료 라이프사이클 객체 분리
- 설정 누락(bot/app token) 시 명확한 예외 메시지 제공
