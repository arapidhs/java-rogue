Here's the updated cheat sheet with `fatjar` profile commands added:

---

# Maven Stress Test Cheat Sheet

## Running (or skipping) Tests and Building Fat JARs

| Command                                   | What It Does                                                |
|-------------------------------------------|-------------------------------------------------------------|
| `mvn clean install -DskipTests`           | Skips all tests                                             |
| `mvn clean install`                       | Runs default profile: all tests EXCEPT stress tests         |
| `mvn test`                                | Runs default profile: all tests EXCEPT stress tests         |
| `mvn clean install -Pstress-tests`        | Runs stress-tests profile: ALL tests including stress tests |
| `mvn test -Pstress-tests`                 | Runs stress-tests profile: ALL tests including stress tests |
| `mvn clean package -Pfatjar`              | Builds fat JAR only                                         |
| `mvn clean install -Pfatjar`              | Builds fat JAR and installs to local Maven repo             |
| `mvn clean install -Pfatjar,stress-tests` | Builds fat JAR and runs ALL tests including stress tests    |
| `mvn clean package -Pfatjar,stress-tests` | Builds fat JAR and runs ALL tests including stress tests    |

## Profiles

1. **default profile** (active by default)

    * Excludes all tests tagged with "stress"
    * Used for regular development and CI pipelines
    * Keeps build times shorter

2. **stress-tests profile** (activated with `-Pstress-tests`)

    * Runs ALL tests, including both regular and stress tests
    * Used for thorough testing or before major releases

3. **fatjar profile** (activated with `-Pfatjar`)

    * Builds an executable fat JAR via the Maven Shade Plugin
    * Only runs when explicitly activated
    * Can be combined with test profiles as needed

## How It Works

* JUnit 5 tests are tagged using the `@Tag("stress")` annotation
* Maven Surefire plugin filters tests using the `groups` and `excludedGroups` parameters