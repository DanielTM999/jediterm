val upstreamVersion = rootProject.projectDir.resolve("VERSION").readText().trim()
val orionRevision = providers.gradleProperty("orionRevision").getOrElse("1")
val publishGroup = providers.gradleProperty("group").getOrElse("dtm.ide")
val publishVersion = providers.gradleProperty("version").getOrElse("$upstreamVersion-orion.$orionRevision")

allprojects {
  version = publishVersion
  group = publishGroup
  layout.buildDirectory = rootProject.projectDir.resolve(".gradleBuild/" + project.name)
}

subprojects {
  repositories {
    mavenCentral()
  }
}
