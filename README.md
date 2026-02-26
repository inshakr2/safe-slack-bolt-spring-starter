# safe-slack-bolt-spring-starter

Slack Bolt Java 기반 인터랙션 핸들러를 안전하게 공통화하고, Spring Boot에서 자동 등록할 수 있도록 제공하는 라이브러리입니다.

## 목표
- Slack 핸들러 공통 예외 처리(`ack fallback`) 표준화
- `instanceof` 체인 없이 `register(App app)` 계약으로 핸들러 등록 확장성 확보
- Spring Boot 자동설정으로 Socket Mode 구동/종료 라이프사이클 제공

## 모듈
- `safe-slack-bolt-core`
  - `SafeBoltHandler`
  - `AbstractSafeCommandHandler`
  - `AbstractSafeBlockActionHandler`
  - `AbstractSafeViewSubmissionHandler`
  - `AbstractSafeMessageEventHandler`
  - `AbstractSafeAppHomeOpenedEventHandler`
  - `AbstractSafeGlobalShortcutHandler`
  - `ViewSubmissionValidationErrors`
- `safe-slack-bolt-spring-boot-starter`
  - `SafeSlackBoltAutoConfiguration`
  - `SafeSlackBoltProperties`
  - `HandlerRegistryValidator`
  - `SocketModeLifecycle`
- `safe-slack-bolt-sample`
  - starter 사용 예제 애플리케이션
  - Maven Central 배포 대상 아님

## 요구사항
- Java 11+
- Gradle 8.x

## 의존성 추가

### Gradle
```gradle
dependencies {
    implementation "io.github.inshakr2:safe-slack-bolt-core:0.1.1"
    implementation "io.github.inshakr2:safe-slack-bolt-spring-boot-starter:0.1.1"
}
```

### Maven
```xml
<dependencies>
  <dependency>
    <groupId>io.github.inshakr2</groupId>
    <artifactId>safe-slack-bolt-core</artifactId>
    <version>0.1.1</version>
  </dependency>
  <dependency>
    <groupId>io.github.inshakr2</groupId>
    <artifactId>safe-slack-bolt-spring-boot-starter</artifactId>
    <version>0.1.1</version>
  </dependency>
</dependencies>
```

## Slack App 생성 (Manifest)

Slack 공식 템플릿의 온보딩 방식을 벤치마킹해 샘플 매니페스트를 제공합니다.

1. Slack App 생성 화면에서 **From an app manifest** 선택
2. [safe-slack-bolt-sample/manifest.json](./safe-slack-bolt-sample/manifest.json) 내용 붙여넣기
3. 앱 생성 후 Bot Token / App Token 발급

환경 변수:

```bash
export SLACK_BOT_TOKEN=xoxb-...
export SLACK_APP_TOKEN=xapp-...
```

## 설정
`safe.slack.bolt.*` 프로퍼티를 사용합니다.

| Key | Required | Default | Description |
|---|---|---|---|
| `safe.slack.bolt.enabled` | N | `true` | starter 활성화 여부 |
| `safe.slack.bolt.bot-token` | Y (`enabled=true`) | - | Slack bot token |
| `safe.slack.bolt.socket-mode-enabled` | N | `true` | Socket Mode 사용 여부 |
| `safe.slack.bolt.app-token` | Y (`socket-mode-enabled=true`) | - | Slack app token |
| `safe.slack.bolt.socket-mode-auto-startup` | N | `true` | 앱 시작 시 Socket Mode 자동 시작 여부 |

`0.1.1`부터 starter가 Socket Mode 필수 런타임(`javax.websocket-api`, `tyrus-standalone-client`)을 기본 제공합니다.

예시:
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

## 빠른 시작

```java
@Component
public class HelloCommandHandler extends AbstractSafeCommandHandler {
    @Override
    protected String getCommand() {
        return "/hello";
    }

    @Override
    protected Response handle(SlashCommandRequest req, SlashCommandContext ctx) {
        return ctx.ack("hello");
    }
}
```

```java
@Component
public class SampleShortcutHandler extends AbstractSafeGlobalShortcutHandler {
    @Override
    protected String getCallbackId() {
        return "sample-global-shortcut";
    }

    @Override
    protected Response handle(GlobalShortcutRequest req, GlobalShortcutContext ctx) {
        return ctx.ack();
    }
}
```

- 모든 핸들러는 `SafeBoltHandler` 계약으로 자동 수집됩니다.
- 동일 `identifier`가 중복되면 앱 시작 시 즉시 실패합니다.

## 로컬 검증
```bash
./gradlew clean test
./gradlew publishToMavenLocal -Psigning.skip=true
```

## 릴리즈

### Dry-run (로컬 퍼블리시만)
```bash
./gradlew clean publishToMavenLocal -Psigning.skip=true
```

### Real release (Maven Central)
`v*` 태그 push 시 [release-publish.yml](./.github/workflows/release-publish.yml) 워크플로가 실행됩니다.

필수 조건:
- GitHub Environment: `release`
- Secrets
  - `SONATYPE_USERNAME`
  - `SONATYPE_PASSWORD`
  - `GPG_SIGNING_KEY_ID`
  - `GPG_SIGNING_KEY`
  - `GPG_SIGNING_PASSWORD`

자세한 절차는 [RELEASE.md](./RELEASE.md)를 참고하세요.

## CI
- `.github/workflows/build-test.yml`
  - push/PR 시 전체 모듈 빌드/테스트
- `.github/workflows/publish-dry-run.yml`
  - `workflow_dispatch`에서 로컬 퍼블리시 드라이런 수행
- `.github/workflows/release-publish.yml`
  - `v*` 태그 push 시 승인 게이트 후 Maven Central 실배포 수행

## 보안 주의
- 실제 Slack 토큰은 저장소에 커밋하지 마세요.
- CI 비밀값(`secrets`) 또는 런타임 환경변수만 사용하세요.

## 배포 로드맵
- 현재 단계: Phase 2/3 구현 진행
- 릴리즈 기준 버전: `0.1.1`
