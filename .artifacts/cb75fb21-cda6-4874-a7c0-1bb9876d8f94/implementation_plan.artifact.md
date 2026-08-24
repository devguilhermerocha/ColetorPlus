# Implementation Plan - Fix Build Errors and Project Synchronization

This plan addresses the compiler errors in `AppDatabase`, synchronizes the `applicationId`, and cleans up residual references to the old project name.

## Proposed Changes

### [Database Layer]

#### [MODIFY] [AppDatabase.java](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/java/com/application/coletorplus/data/database/AppDatabase.java)
- Correct imports for `ProdutoDao` and `ValidadeDao`.
- Register all missing entities: `Usuario`, `Endereco`, `Validade`, `ProdutoEndereco`, `ProdutoValidade`.
- Restore missing abstract methods: `produtoDao()` and `validadeDao()`.
- Ensure the database name is `coletorplus_database`.

### [Build Configuration]

#### [MODIFY] [build.gradle.kts](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/build.gradle.kts)
- Update `applicationId` to `"com.application.coletorplus"`.

### [Tests]

#### [MODIFY] [ExampleInstrumentedTest.java](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/androidTest/java/com/application/coletorplus/ExampleInstrumentedTest.java)
- Update package assertion to match the new `applicationId`.

## Verification Plan

### Automated Tests
- Run `gradle_build(app:assembleDebug)` to ensure Room generated the implementations and the project compiles.
- Run `analyze_file` on `AppDatabase.java` after changes.

### Manual Verification
- Verify that the app starts correctly and the initial "admin" user is created in the database.
