# RELEASE GUIDE

`safe-slack-bolt-spring-starter`를 Maven Central에 실배포하기 위한 운영 문서입니다.

## 1) Sonatype 준비
1. [Central Portal](https://central.sonatype.com/)에서 `io.github.inshakr2` namespace 권한을 확인합니다.
2. User Token을 발급합니다.
3. 아래 값을 확보합니다.
- `SONATYPE_USERNAME`
- `SONATYPE_PASSWORD`

## 2) GPG 키 준비
1. 배포용 GPG 키를 생성합니다.
2. 아래 값을 확보합니다.
- `GPG_SIGNING_KEY_ID`
- `GPG_SIGNING_KEY` (ASCII-armored private key)
- `GPG_SIGNING_PASSWORD`

## 3) GitHub 환경 구성
1. GitHub 저장소 Settings > Environments > `release` 생성
2. `release` 환경에 Required reviewers 설정
3. `release` 환경 Secrets 등록
- `SONATYPE_USERNAME`
- `SONATYPE_PASSWORD`
- `GPG_SIGNING_KEY_ID`
- `GPG_SIGNING_KEY`
- `GPG_SIGNING_PASSWORD`

## 4) 워크플로

### Dry-run
로컬/CI에서 아래 명령으로 서명 없이 로컬 퍼블리시만 검증합니다.
```bash
./gradlew clean publishToMavenLocal -Psigning.skip=true --no-daemon
```

### Real release
1. `main` 최신 상태에서 태그 생성
```bash
git tag v0.1.1
git push origin v0.1.1
```
2. GitHub Actions `Release Publish` 워크플로 자동 실행
3. `publish` job은 `release` 환경 승인 후 진행
4. 실행 명령
```bash
./gradlew validateReleaseCredentials publishToSonatype closeAndReleaseSonatypeStagingRepository --no-daemon
```

## 5) 사후 확인
1. Sonatype staging close/release 성공 확인
2. Maven Central 색인 반영 확인
3. GitHub Release 노트 작성

## 참고
- `safe-slack-bolt-core`, `safe-slack-bolt-spring-boot-starter`만 배포 대상
- `safe-slack-bolt-sample`은 배포 대상 제외
