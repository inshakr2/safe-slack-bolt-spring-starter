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
- `safe-slack-bolt-spring-boot-starter`
  - `SafeSlackBoltAutoConfiguration`
  - `SafeSlackBoltProperties`
  - `HandlerRegistryValidator`
  - `SocketModeLifecycle`
- `safe-slack-bolt-sample`
  - starter 사용 예제 애플리케이션

## 요구사항
- Java 11+
- Gradle 8.x

## 의존성 추가

### Gradle
```gradle
dependencies {
    implementation "io.github.inshakr2:safe-slack-bolt-core:0.1.0-SNAPSHOT"
    implementation "io.github.inshakr2:safe-slack-bolt-spring-boot-starter:0.1.0-SNAPSHOT"
}
```

### Maven
```xml
<dependencies>
  <dependency>
    <groupId>io.github.inshakr2</groupId>
    <artifactId>safe-slack-bolt-core</artifactId>
    <version>0.1.0-SNAPSHOT</version>
  </dependency>
  <dependency>
    <groupId>io.github.inshakr2</groupId>
    <artifactId>safe-slack-bolt-spring-boot-starter</artifactId>
    <version>0.1.0-SNAPSHOT</version>
  </dependency>
</dependencies>
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

- 모든 핸들러는 `SafeBoltHandler` 계약으로 자동 수집됩니다.
- 동일 `identifier`가 중복되면 앱 시작 시 즉시 실패합니다.

## 로컬 검증
```bash
./gradlew clean test
./gradlew publishToMavenLocal -Psigning.skip=true
```

## CI
- `.github/workflows/build-test.yml`
  - push/PR 시 전체 모듈 빌드/테스트
- `.github/workflows/publish-dry-run.yml`
  - `workflow_dispatch`, `v*` 태그에서 로컬 퍼블리시 드라이런 수행

## 보안 주의
- 실제 Slack 토큰은 저장소에 커밋하지 마세요.
- CI 비밀값(`secrets`) 또는 런타임 환경변수만 사용하세요.

## 배포 로드맵
- 현재 단계: First Commit + dry-run
- 다음 단계: GPG/Portal 자격증명 구성 후 Maven Central 정식 배포
