# slack-bolt-socket-mode-spring-boot-starter

`v1.0.0`은 Slack **Socket Mode 전용** 라이브러리입니다.
Slack Bolt Java 핸들러를 Spring Boot에서 일관되게 등록하고, 예외 발생 시 `ack fallback`으로 안전하게 응답하도록 공통화합니다.

## 핵심 기능
- Socket Mode 기반 Slack Bolt 핸들러 등록 자동화
- `register(App app)` 계약 기반 확장 가능한 핸들러 구조
- 핸들러 실행 예외 시 공통 로깅 + `ctx.ack()` fallback 처리
- 핸들러 식별자(`identifier`) 중복 검증 Fail-Fast

## 모듈
- `slack-bolt-socket-mode-core`
  - `BoltHandler`
  - `AbstractCommandHandler`
  - `AbstractBlockActionHandler`
  - `AbstractViewSubmissionHandler`
  - `AbstractMessageEventHandler`
  - `AbstractAppHomeOpenedEventHandler`
  - `AbstractGlobalShortcutHandler`
  - `ViewSubmissionValidationErrors`
- `slack-bolt-socket-mode-spring-boot-starter`
  - `SlackBoltSocketModeAutoConfiguration`
  - `SlackBoltSocketModeProperties`
  - `HandlerRegistryValidator`
  - `SocketModeLifecycle`
- `slack-bolt-socket-mode-sample`
  - starter 사용 예제 앱
  - Maven Central 배포 대상 아님

## 요구사항
- Java 11+
- Gradle 8.x

## 의존성 추가

### Gradle
```gradle
dependencies {
    implementation "io.github.inshakr2:slack-bolt-socket-mode-core:1.0.0"
    implementation "io.github.inshakr2:slack-bolt-socket-mode-spring-boot-starter:1.0.0"
}
```

### Maven
```xml
<dependencies>
  <dependency>
    <groupId>io.github.inshakr2</groupId>
    <artifactId>slack-bolt-socket-mode-core</artifactId>
    <version>1.0.0</version>
  </dependency>
  <dependency>
    <groupId>io.github.inshakr2</groupId>
    <artifactId>slack-bolt-socket-mode-spring-boot-starter</artifactId>
    <version>1.0.0</version>
  </dependency>
</dependencies>
```

## Slack App 생성 (Manifest)
Slack에서 제공하는 샘플 템플릿 흐름을 참고해 매니페스트 기반 온보딩을 지원합니다.

1. Slack App 생성 화면에서 **From an app manifest** 선택
2. [slack-bolt-socket-mode-sample/manifest.json](./slack-bolt-socket-mode-sample/manifest.json) 내용 붙여넣기
3. Bot Token / App Token 발급

환경 변수:
```bash
export SLACK_BOT_TOKEN=xoxb-...
export SLACK_APP_TOKEN=xapp-...
```

## 설정
`slack.bolt.socket-mode.*` 프로퍼티를 사용합니다.

| Key | Required | Default | Description |
|---|---|---|---|
| `slack.bolt.socket-mode.enabled` | N | `true` | starter 활성화 여부 |
| `slack.bolt.socket-mode.bot-token` | Y (`enabled=true`) | - | Slack bot token |
| `slack.bolt.socket-mode.socket-mode-enabled` | N | `true` | Socket Mode 사용 여부 |
| `slack.bolt.socket-mode.app-token` | Y (`socket-mode-enabled=true`) | - | Slack app token |
| `slack.bolt.socket-mode.socket-mode-auto-startup` | N | `true` | 앱 시작 시 Socket Mode 자동 시작 여부 |

`1.0.0`부터 starter가 Socket Mode 필수 런타임(`javax.websocket-api`, `tyrus-standalone-client`)을 기본 제공합니다.

예시:
```yaml
slack:
  bolt:
    socket-mode:
      enabled: true
      bot-token: ${SLACK_BOT_TOKEN}
      app-token: ${SLACK_APP_TOKEN}
      socket-mode-enabled: true
      socket-mode-auto-startup: true
```

## 빠른 시작

```java
@Component
public class HelloCommandHandler extends AbstractCommandHandler {
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
public class SampleShortcutHandler extends AbstractGlobalShortcutHandler {
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

- 모든 핸들러는 `BoltHandler` 계약으로 자동 수집됩니다.
- 동일 `identifier`가 중복되면 앱 시작 시 즉시 실패합니다.

## 로컬 검증
```bash
./gradlew clean test
./gradlew publishToMavenLocal -Psigning.skip=true
```

## 릴리즈
GitFlow 전략을 사용합니다.
- `release`: 배포 기준 브랜치 (태그 생성/실배포 기준)
- `prod`: 운영 기준 보호 브랜치
- `develop`: 통합 개발 브랜치
- `feature/*`, `release/*`, `hotfix/*`: 작업 브랜치

### Dry-run (로컬 퍼블리시)
```bash
./gradlew clean publishToMavenLocal -Psigning.skip=true
```

### Real release (Maven Central)
`release` 최신 커밋에 `v*` 태그를 push하면 [release-publish.yml](./.github/workflows/release-publish.yml) 워크플로가 실행됩니다.

최초 릴리즈 예시:
```bash
git checkout release
git pull origin release
git tag v1.0.0
git push origin v1.0.0
```

필수 조건:
- GitHub Environment: `release`
- Secrets
  - `SONATYPE_USERNAME`
  - `SONATYPE_PASSWORD`
  - `GPG_SIGNING_KEY_ID` (optional)
  - `GPG_SIGNING_KEY`
  - `GPG_SIGNING_PASSWORD`

자세한 절차는 [RELEASE.md](./RELEASE.md)를 참고하세요.

## CI
- `.github/workflows/build-test.yml`
  - push/PR 시 전체 모듈 빌드/테스트
- `.github/workflows/publish-dry-run.yml`
  - `workflow_dispatch`에서 로컬 퍼블리시 드라이런 수행
- `.github/workflows/release-publish.yml`
  - `release` HEAD에 생성된 `v*` 태그 push 시 승인 게이트 후 Maven Central 실배포 수행

## 보안 주의
- 실제 Slack 토큰은 저장소에 커밋하지 마세요.
- CI 비밀값(`secrets`) 또는 런타임 환경변수만 사용하세요.

## 상표 고지
- `Spring`은 VMware, Inc. 또는 그 계열사의 상표입니다.
- `Slack`은 Salesforce, Inc. 또는 그 계열사의 상표입니다.
- 본 프로젝트는 Slack/Spring과 제휴 또는 후원 관계가 없는 독립 오픈소스 프로젝트입니다.

## 배포 로드맵
- 현재 단계: Phase 2/3 구현 진행
- 릴리즈 기준 버전: `1.0.0`
