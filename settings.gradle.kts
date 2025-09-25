plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "photi-server"

include(
    "photi-apis",
    "photi-apis:enduser",
    "photi-batch",
    "photi-core",
    "photi-core:domain",
    "photi-utils",
)
