plugins {
    java
    groovy
    jacoco
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
    id("com.netflix.dgs.codegen") version "6.3.0"
    id("com.google.cloud.tools.jib") version "3.4.3"
    id("org.shipkit.shipkit-changelog") version "2.0.1"
    id("org.shipkit.shipkit-github-release") version "2.0.1"
    id("org.shipkit.shipkit-auto-version") version "2.0.11"
}

group = "com.petrmacek"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

extra["netflixDgsVersion"] = "9.1.2"
extra["springModulithVersion"] = "1.2.4"
extra["spockBomVersion"] = "2.4-M4-groovy-4.0"
extra["axonVersion"] = "4.10.1"
extra["axonReactorVersion"] = "4.10.0"
extra["googleProtobufVersion"] = "4.28.2"
extra["mapstructVersion"] = "1.6.2"


dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-neo4j")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework:spring-aspects")

    implementation("com.netflix.graphql.dgs:graphql-dgs-spring-graphql-starter")
    implementation("com.netflix.graphql.dgs:graphql-dgs-extended-scalars")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("name.nkonev.multipart-spring-graphql:multipart-spring-graphql:1.+")
    implementation("com.netflix.graphql.dgs:graphql-dgs-spring-boot-micrometer")

    implementation("org.springframework.modulith:spring-modulith-starter-core")

    // https://mvnrepository.com/artifact/org.axonframework/axon-spring-boot-starter
    implementation("org.axonframework:axon-spring-boot-starter:${property("axonVersion")}")

    // https://mvnrepository.com/artifact/org.axonframework.extensions.mongo/axon-mongo
    implementation("org.axonframework:axon-tracing-opentelemetry:${property("axonVersion")}")

    // https://mvnrepository.com/artifact/org.axonframework.extensions.mongo/axon-mongo
    // implementation("org.axonframework.extensions.mongo:axon-mongo:${property("axonVersion")}")

//    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    // https://mvnrepository.com/artifact/org.axonframework/axon-micrometer
    implementation("org.axonframework:axon-micrometer:${property("axonVersion")}")

    implementation("org.axonframework.extensions.reactor:axon-reactor-spring-boot-starter:${property("axonReactorVersion")}")

    // https://mvnrepository.com/artifact/org.mapstruct/mapstruct
    implementation("org.mapstruct:mapstruct:${property("mapstructVersion")}")

    // https://mvnrepository.com/artifact/io.micrometer/micrometer-registry-prometheus
    implementation("io.micrometer:micrometer-registry-prometheus:1.13.5")

    // https://mvnrepository.com/artifact/com.google.protobuf/protobuf-java
    implementation("com.google.protobuf:protobuf-java:3.23.4")

    implementation("org.liquibase:liquibase-core")

    compileOnly("org.projectlombok:lombok")


    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.mapstruct:mapstruct-processor:${property("mapstructVersion")}")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    testAnnotationProcessor("org.mapstruct:mapstruct-processor:${property("mapstructVersion")}")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.netflix.graphql.dgs:graphql-dgs-spring-graphql-starter-test")
    testImplementation("org.springframework.modulith:spring-modulith-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // https://mvnrepository.com/artifact/org.neo4j.test/neo4j-harness
    testImplementation("org.neo4j.test:neo4j-harness:5.24.1")

    testImplementation("org.testcontainers:spock")
    testImplementation("org.spockframework:spock-core")
    testImplementation("org.spockframework:spock-spring")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-neo4j")

    testImplementation("org.testcontainers:testcontainers")
    testImplementation("com.squareup.okhttp3:mockwebserver")
    testImplementation("com.squareup.okhttp3:okhttp")
    testImplementation("org.axonframework:axon-test:${property("axonVersion")}")

    testImplementation("ch.qos.logback:logback-classic:1.3.11")
    testImplementation("org.slf4j:slf4j-api:2.0.16")
    testImplementation("io.projectreactor:reactor-test")

}

dependencyManagement {
    imports {
        mavenBom("org.springframework.modulith:spring-modulith-bom:${property("springModulithVersion")}")
        mavenBom("com.netflix.graphql.dgs:graphql-dgs-platform-dependencies:${property("netflixDgsVersion")}")
        mavenBom(("org.spockframework:spock-bom:${property("spockBomVersion")}"))
    }
}

tasks.withType<com.netflix.graphql.dgs.codegen.gradle.GenerateJavaTask> {
    generateClientv2 = true
    packageName = "com.petrmacek.cragdb.generated"
}


tasks.withType<Test> {
    useJUnitPlatform()
}

jib {
    from {
        image = "eclipse-temurin:21" // Use your desired base image
    }
    to {
        image = "petrmac/crag-db:${project.version}" // Target image
        auth {
            username = System.getenv("DOCKER_REGISTRY_USER") ?: ""
            password = System.getenv("DOCKER_REGISTRY_PASSWORD") ?: ""
        }
    }
    container {
        jvmFlags = listOf("-Xms512m", "-Xmx1024m")
        ports = listOf("3000")  // Expose container ports
        environment = mapOf("ENV" to "production")
    }
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(true)
    }
}

afterEvaluate {
    tasks.jacocoTestReport {
        classDirectories.setFrom(
            fileTree("build/classes/java/main") {
                exclude("**/excluded/package/**") // Adjust exclusions as needed
            }
        )
        sourceDirectories.setFrom(files("src/main/java"))
        executionData.setFrom(
            fileTree(layout.buildDirectory.dir("jacoco")) {
                include("**/test.exec")
            }
        )
    }
}

val newTag = project.findProperty("shipkit-auto-version.new-tag") ?: "v0.0.1"
val previousTag = project.findProperty("shipkit-auto-version.previous-tag") ?: "v0.0.0"


tasks.named("generateChangelog", org.shipkit.changelog.GenerateChangelogTask::class) {
    // Load version properties from shipkit-auto-version
    previousRevision = previousTag.toString()
    githubToken = System.getenv("GITHUB_TOKEN")
    repository = "petrmac/crag-db"
}

tasks.named("githubRelease", org.shipkit.github.release.GithubReleaseTask::class) {
    dependsOn("generateChangelog")
    newTagRevision = newTag.toString()
    githubToken = System.getenv("GITHUB_TOKEN")
    repository = "petrmac/crag-db"
    changelog = tasks.named("generateChangelog").get().outputs.files.singleFile
}
