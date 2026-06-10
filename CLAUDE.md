# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

Kotlin Multiplatform + Compose Multiplatform weather app targeting **Android, iOS, and Desktop (JVM)**. Gradle root project name is `ToDelete`; all code lives under package `com.amonteiro.todelete`. Source comments are in French (this is a course/teaching project).

## Modules

- `shared/` — virtually everything: business logic, networking, DI, ViewModels, and the Compose UI. Most work happens here.
- `androidApp/` — Android entry point (`MainActivity`), just calls `App()`.
- `desktopApp/` — JVM/Desktop entry point (`main.kt`); calls `initKoin()` then `App()`.
- `iosApp/` — Xcode project; Swift entry point hosts `MainViewController()` from `shared/iosMain`.

## Common commands

Run apps:
- Android: `./gradlew :androidApp:assembleDebug`
- Desktop (hot reload): `./gradlew :desktopApp:hotRun --auto`
- Desktop (standard): `./gradlew :desktopApp:run`
- iOS: open `iosApp/` in Xcode and run.

Run tests (test source sets exist per platform, currently trivial placeholders):
- Android: `./gradlew :shared:testAndroidHostTest`
- Desktop/JVM: `./gradlew :shared:jvmTest`
- iOS sim: `./gradlew :shared:iosSimulatorArm64Test`

Run a single test: append `--tests "com.amonteiro.todelete.ClassName.methodName"` to the test task (works for the JVM/Android host-test tasks).

## Required local config

`local.properties` (git-ignored) must define `weather.api.key=<OpenWeatherMap key>`. The `com.github.gmazzo.buildconfig` Gradle plugin reads it at configure time and generates `com.amonteiro.todelete.BuildConfig.WEATHER_API_KEY`, available to all targets. Without it, the real API calls fail (the fake API still works for previews). `local.properties` also holds `sdk.dir` for the Android SDK.

## Architecture

MVVM with Koin dependency injection, single shared `MainViewModel`, and type-safe Compose Navigation. Data flows: `KtorWeatherApi` → `MainViewModel` StateFlows → Compose screens.

- **DI (`di/koin.kt`)** — `initKoin()` registers `apiModule` (Ktor `HttpClient` + `WeatherAPII`) and `viewModelModule` (`MainViewModel`). Platform entry points call `initKoin()` at startup. `di/koinForPreview.kt` provides `apiFakeModule`, swapped in for `@Preview` composables via `KoinApplicationPreview`.

- **API abstraction** — `domaine/WeatherAPII` is the interface. `data/remote/KtorWeatherApi` is the real OpenWeatherMap implementation; `domaine/fakeData/WeatherFakeAPI` returns static data for previews/tests. Swap implementations by changing the bound Koin module, not call sites.

- **Networking** — Ktor client with `ContentNegotiation` (kotlinx JSON, `ignoreUnknownKeys = true`), `Logging`, and a 5s `HttpTimeout`. The HTTP engine is platform-specific via source-set deps: `okhttp` on Android/JVM, `darwin` on iOS.

- **ViewModel (`presentation/viewmodel/MainViewModel`)** — exposes `dataList`, `runInProgress`, `errorMessage`, `searchText` as `MutableStateFlow`s. `loadWeathers()` runs on `Dispatchers.IO` in `viewModelScope`, catching exceptions into `errorMessage`. `loadFakeData()` seeds preview/demo data.

- **Navigation (`presentation/Navigation.kt`)** — `AppNavigation` holds a `NavHost` with `@Serializable` type-safe routes (`Routes.SearchRoute`, `Routes.DetailRoute(id)`). The `MainViewModel` is obtained once via `koinViewModel()` *outside* the NavHost so both screens share it; the detail screen looks up its entity from the shared `dataList` by id.

- **expect/actual** — `Platform.kt` (per-platform name) and the `WeatherGallery` composable in `presentation/ui/Components.kt` have platform actuals in `androidMain` / `jvmMain` / `iosMain`. UI rendering of the list is platform-specific; the rest of the UI is shared `commonMain`.

- **UI** — Compose Material3, Coil 3 (`AsyncImage`) for network images, Compose Resources (`Res.string.*`, `Res.drawable.*`) for strings/images (with `values-fr` localization). Theme lives in `presentation/ui/theme/` as `A26_04_ambientit_kotlinTheme`.

## Notes

- Several files contain a `suspend fun main()` (e.g. in `MainViewModel.kt`, `KtorWeatherApi.kt`) used as ad-hoc manual test harnesses — not app entry points.
- Dependency versions are split: `gradle/libs.versions.toml` (version catalog) for plugins and Compose/AndroidX, but most `shared` libraries (Ktor, Koin, Coil, Navigation) are hardcoded strings in `shared/build.gradle.kts`.