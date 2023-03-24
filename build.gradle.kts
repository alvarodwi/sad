plugins {
  id("com.github.ben-manes.versions") version "0.46.0"
  id("org.jmailen.kotlinter") version "3.13.0"
  id("io.gitlab.arturbosch.detekt").version("1.22.0")

  id("com.android.application") version "7.4.2" apply false
  id("com.android.library") version "7.4.2" apply false
  id("org.jetbrains.kotlin.android") version "1.8.10" apply false
  id("com.google.dagger.hilt.android") version "2.45" apply false
  id("androidx.navigation.safeargs") version "2.5.3" apply false
  id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
}

allprojects {
  // apply ktlint
  apply(plugin = "org.jmailen.kotlinter")

  // Ktlint configuration for sub-projects
  kotlinter {
    ignoreFailures = false
    reporters = arrayOf("checkstyle", "plain")
    experimentalRules = false
    disabledRules = emptyArray()
  }
}

subprojects {
  apply(plugin = "io.gitlab.arturbosch.detekt")

  detekt {
    config = files("${project.rootDir}/detekt.yml")
    parallel = true
  }
}

tasks.register("clean", Delete::class) {
  delete(rootProject.buildDir)
}

// ben-manes versions checking
fun isNonStable(version: String): Boolean {
  val stableKeyword = listOf("RELEASE", "FINAL", "GA").any {
    version.toUpperCase()
      .contains(it)
  }
  val regex = "^[0-9,.v-]+(-r)?$".toRegex()
  val isStable = stableKeyword || regex.matches(version)
  return isStable.not()
}

tasks.withType<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask> {
  // reject all non stable versions
  rejectVersionIf {
    isNonStable(candidate.version)
  }

  // optional parameters
  checkForGradleUpdate = true
  outputFormatter = "json"
  outputDir = "build/dependencyUpdates"
  reportfileName = "report"
}