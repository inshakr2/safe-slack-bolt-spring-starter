# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.1.0] - 2026-03-04

### Added
- Added a Modal DSL for faster and more consistent modal input composition with:
  - `SlackModalBuilder`
  - `ModalFieldKey`
  - `ModalOption`
  - `ModalFieldType`
- Added modal open helper APIs with:
  - `SlackModalOpener` (open / openResult / openOrAck)
  - `SlackModalOpenResult` (explicit success/failure handling)
- Added failure payload customization for modal open failures with:
  - `SlackModalFailureInfo` (`code`, `message`)
  - `SlackModalFailurePayload` (`code`, `message`, `error`, `warning`)

### Changed
- Updated sample handler usage to show end-to-end modal flow with `SlackModalBuilder` and `SlackModalOpener`.
- Updated `README.md` and `docs/readme/README.en.md` with the latest modal DSL usage guidance.

### Fixed
- Strengthened fail-fast validation for modal definitions (blank/invalid values, constraint violations, duplicate identifiers) to reduce runtime errors.

[Unreleased]: https://github.com/inshakr2/slack-bolt-socket-mode-spring-boot-starter/compare/v1.1.0...develop
[1.1.0]: https://github.com/inshakr2/slack-bolt-socket-mode-spring-boot-starter/compare/v1.0.0...v1.1.0
