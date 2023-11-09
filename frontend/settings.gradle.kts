pluginManagement {
    repositories {
        google()
//        maven { url = uri("https://chaquo.com/maven-test") }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
//        maven("https://chaquo.com/maven")
        mavenCentral()
    }
}

rootProject.name = "SpeechBuddy"
include(":app")
