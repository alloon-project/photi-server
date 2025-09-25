dependencies {
    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation(project(":photi-core:domain"))

    testImplementation("org.springframework.batch:spring-batch-test")
}