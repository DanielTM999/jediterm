val upstreamVersion = rootProject.projectDir.resolve("VERSION").readText().trim()
val orionRevision = providers.gradleProperty("orionRevision").getOrElse("1")

allprojects {
  version = "$upstreamVersion-orion.$orionRevision"
  group = "dtm.ide"
  layout.buildDirectory = rootProject.projectDir.resolve(".gradleBuild/" + project.name)
}

subprojects {
  repositories {
    mavenCentral()
  }
}
