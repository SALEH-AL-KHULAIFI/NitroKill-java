# NitroKill-java — Agent Instructions

## Mission

Maintain and repair this Android project conservatively. The primary goal is to keep the existing working application intact while making the smallest technically justified changes needed to fix real problems and produce a successful APK build.

## Before changing anything

1. Inspect the repository structure and relevant source files before editing.
2. Inspect the Gradle configuration, Android module configuration, manifest, resources, dependencies, and GitHub Actions workflows.
3. Determine the current Gradle, Android Gradle Plugin, JDK, compileSdk, minSdk, targetSdk, applicationId, and available build variants from the repository itself.
4. Establish the current failure or baseline by running the appropriate Gradle build/test commands when the environment permits it.
5. Identify the root cause before making broad changes.

## Conservative-change policy

- Do NOT rewrite the project from scratch.
- Do NOT perform broad refactoring unless it is required to fix a demonstrated problem.
- Change the minimum number of files necessary.
- Preserve working code and behavior.
- Do not change applicationId/package name unless the task explicitly requires it.
- Do not change the app name, UI, icons, resources, services, notifications, or existing features unless the requested task or a demonstrated bug requires it.
- Do not delete classes, resources, permissions, services, receivers, providers, or dependencies merely because they appear unused; verify their role first.
- Do not replace working libraries with alternatives without a concrete technical reason.
- Do not upgrade Gradle, AGP, SDK levels, or Java simply because newer versions exist. Upgrade only when required by a demonstrated compatibility/build problem.
- Do not add unnecessary dependencies.
- Do not add advertisements, analytics, Firebase/Google services, paid APIs, or other external services unless explicitly required by the task.
- Avoid formatting-only changes and unrelated cleanup.

## Error diagnosis

Prioritize real failures over cosmetic warnings.

Classify significant issues as appropriate:

- Gradle/build configuration error
- Dependency/version compatibility error
- Java compilation error
- Android resource or manifest error
- Runtime-risk bug
- GitHub Actions/CI error
- Non-blocking warning

When a build fails, locate the first meaningful/root error and trace it to its source. Do not blindly fix secondary errors one by one when they are consequences of the root cause.

## Build and verification

After important fixes, rebuild the project using the repository's Gradle Wrapper and the appropriate task, normally:

```bash
./gradlew assembleDebug
```

Also run relevant tests, lint, or validation tasks when they exist and are appropriate.

A task is not considered complete merely because the source code looks correct. Verify an actual successful build and confirm that the expected APK was produced whenever the environment allows it.

If the build fails again, diagnose the new failure and make another focused fix rather than starting a broad rewrite.

## GitHub Actions

Inspect `.github/workflows/` when CI/build automation is relevant.

- Preserve working workflows.
- Fix only the CI configuration that is actually responsible for a failure.
- Do not replace an entire workflow when a focused change is sufficient.
- Keep the workflow compatible with the project's actual Gradle/Android/JDK configuration.
- Validate YAML and build-step logic after changes.

## Protect unrelated project areas

Treat code unrelated to the current problem as protected.

In particular, avoid unnecessary changes to:

- application logic
- UI/layouts/themes
- notification behavior
- background services
- network-speed indicator logic
- icons and images
- package/application identity
- working Gradle configuration
- existing resources

Before modifying a file, establish why that file is relevant to the reported problem.

## Final review

Before completing the task:

1. Inspect the final diff.
2. Remove unrelated or accidental changes.
3. Confirm that changed files are directly justified by the task.
4. Confirm the project builds successfully when possible.
5. Confirm the generated APK path when a build succeeds.
6. Report any unverified assumptions or remaining failures explicitly.

## Final report

Provide a concise but complete report containing:

1. Problems found.
2. Root cause of each important problem.
3. Files changed.
4. What changed in each file.
5. Why each change was necessary.
6. Important project areas intentionally left unchanged.
7. Build command(s) used.
8. Build result.
9. APK output path, when available.
10. Test/lint/validation results.
11. GitHub Actions result when relevant.
12. Remaining issues that could not be verified or fixed.

## Golden rule

Prefer the smallest safe fix that solves the actual problem. A successful repair with a small, understandable diff is preferable to a large refactor.
