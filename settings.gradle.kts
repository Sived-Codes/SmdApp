pluginManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://www.jitpack.io" ) }
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        maven { url = uri("https://www.jitpack.io" ) }
        mavenCentral()
    }
}

rootProject.name = "SMD"
include(":app")
