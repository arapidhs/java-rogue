# Maven Stress Test Cheat Sheet

## Configuration Summary

**POM.xml Configuration:**

```xml
<!-- Profiles - Add at root level of pom.xml -->
<profiles>
    <profile>
        <id>stress-tests</id>
        <properties>
            <!-- No groups specified means all tests run -->
            <groups></groups>
            <!-- No excluded groups means no tests are excluded -->
            <excludedGroups></excludedGroups>
        </properties>
    </profile>
    <profile>
        <id>default</id>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
        <properties>
            <groups></groups>
            <excludedGroups>stress</excludedGroups>
        </properties>
    </profile>
</profiles>

<!-- Surefire Plugin - In build/plugins section -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.2.5</version>
    <configuration>
        <groups>${groups}</groups>
        <excludedGroups>${excludedGroups}</excludedGroups>
        <argLine>-Xshare:off</argLine>
    </configuration>
</plugin>
```

**JUnit Test Annotation:**

```java
@Tag("stress")
@RepeatedTest(10000)
void testLevelGeneration(){
    // Test implementation
}
```

## Running Tests

| Command | What It Does |
|---------|-------------|
| `mvn clean install` | Runs default profile: all tests EXCEPT stress tests |
| `mvn test` | Runs default profile: all tests EXCEPT stress tests |
| `mvn clean install -Pstress-tests` | Runs stress-tests profile: ALL tests including stress tests |
| `mvn test -Pstress-tests` | Runs stress-tests profile: ALL tests including stress tests |

## Profiles Explained

1. **default profile** (active by default)
    - Excludes all tests tagged with "stress"
    - Used for regular development and CI pipelines
    - Keeps build times shorter

2. **stress-tests profile** (activated with `-Pstress-tests`)
    - Runs ALL tests, including both regular and stress tests
    - Used for thorough testing or before major releases
    - Typically takes longer to complete

## How It Works

- JUnit 5 tests are tagged using the `@Tag("stress")` annotation
- Maven Surefire plugin filters tests using the `groups` and `excludedGroups` parameters
- Profiles provide an easy way to switch between test configurations
- The configuration parameters are passed to the Surefire plugin via properties