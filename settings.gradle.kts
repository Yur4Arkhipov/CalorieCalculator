pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "CalorieCalculator"
include(":app")
include(":feature")
include(":feature:onboarding")
include(":core")
include(":core:designsystem")
include(":core:data")
include(":feature:home")
include(":core:util")
include(":feature:statistics")
include(":feature:profile")
