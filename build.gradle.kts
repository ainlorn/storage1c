plugins {
    java
    id("org.springframework.boot") version "3.0.5"
    id("io.spring.dependency-management") version "1.1.0"
}

group = "com.msp31"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.security:spring-security-data")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")
    implementation("com.github.therapi:therapi-runtime-javadoc:0.15.0")
    implementation("org.liquibase:liquibase-core")
    implementation("org.eclipse.jgit:org.eclipse.jgit:6.5.0.202303070854-r")
    compileOnly("org.projectlombok:lombok")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.microsoft.sqlserver:mssql-jdbc:12.2.0.jre11")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("com.github.therapi:therapi-runtime-javadoc-scribe:0.15.0")
    annotationProcessor("org.projectlombok:lombok")
}
