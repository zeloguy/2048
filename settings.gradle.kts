pluginManagement {
    repositories {
        maven { url = uri("/home/hatch/workspace/android-toolchain/maven-repo") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("/home/hatch/workspace/android-toolchain/maven-repo") }
    }
}
rootProject.name = "Game2048"
include(":app")
