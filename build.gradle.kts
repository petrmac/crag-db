plugins {
    java
    groovy
    id("org.springframework.boot") version "3.3.4"
    id("io.spring.dependency-management") version "1.1.6"
    id("com.netflix.dgs.codegen") version "6.3.0"
}

group = "com.petrmacek"
version = "0.0.1-SNAPSHOT"

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
extra["axonVersion"] = "4.10.0"
extra["googleProtobufVersion"] = "4.28.2"
extra["mapstructVersion"] = "1.6.2"


dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-neo4j")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("com.netflix.graphql.dgs:graphql-dgs-spring-graphql-starter")

    implementation("org.springframework.modulith:spring-modulith-starter-core")

    // https://mvnrepository.com/artifact/org.axonframework/axon-spring-boot-starter
    implementation("org.axonframework:axon-spring-boot-starter:${property("axonVersion")}")

    implementation("org.axonframework.extensions.reactor:axon-reactor-spring-boot-starter:${property("axonVersion")}")

    // https://mvnrepository.com/artifact/org.mapstruct/mapstruct
    implementation("org.mapstruct:mapstruct:${property("mapstructVersion")}")


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
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("com.squareup.okhttp3:mockwebserver")
    testImplementation("com.squareup.okhttp3:okhttp")
    testImplementation("org.axonframework:axon-test:${property("axonVersion")}")

    // https://mvnrepository.com/artifact/com.google.protobuf/protobuf-java
    testImplementation("com.google.protobuf:protobuf-java:3.23.4")
    testImplementation("ch.qos.logback:logback-classic:1.3.11")
    testImplementation("org.slf4j:slf4j-api:2.0.16")

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
