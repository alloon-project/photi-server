dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.13")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.github.nefilim.kjwt:kjwt-core:0.9.0")

    implementation(project(":photi-core:domain"))
    implementation(project(":photi-core:infra"))
    implementation(project(":photi-utils"))
}
