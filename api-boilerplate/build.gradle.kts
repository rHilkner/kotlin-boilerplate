import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.5.4"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.5.31"
	kotlin("plugin.spring") version "1.5.31"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	// Springframework dependencies
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	// Kotlin and Commons
	implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.31")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.31")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.5")
	implementation("commons-validator:commons-validator:1.7")
	implementation("commons-io:commons-io:2.6")
	implementation("com.google.code.gson:gson:2.7")

	// API Monitor
	implementation("io.micrometer:micrometer-registry-prometheus:1.7.3")
	implementation("io.github.mweirauch:micrometer-jvm-extras:0.2.2")

	// SMTP
	implementation("com.sun.mail:javax.mail:1.6.2")
	implementation("org.springframework.boot:spring-boot-starter-mail:1.2.0.RELEASE")

	// Lombok
	compileOnly("org.projectlombok:lombok:1.18.8")
	annotationProcessor("org.projectlombok:lombok:1.18.8")
	testCompileOnly("org.projectlombok:lombok:1.18.8")
	testAnnotationProcessor("org.projectlombok:lombok:1.18.8")

	// DBs and Testing
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("com.h2database:h2:1.3.148")
	testImplementation("io.mockk:mockk:1.4.1")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
