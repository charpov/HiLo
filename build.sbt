val scalaTest  = "org.scalatest"     %% "scalatest"        % "3.2.19"
val scalactic  = "org.scalactic"     %% "scalactic"        % "3.2.19"
val apacheLang = "org.apache.commons" % "commons-lang3"    % "3.16.0"

ThisBuild / scalaVersion := "3.4.2"

ThisBuild / scalacOptions ++= Seq(
  "-source:future",  // Source version
  "-deprecation",    // Emit warning and location for usages of deprecated APIs.
  "-encoding:utf-8", // Specify character encoding used by source files.
  "-feature",        // Emit warning for usages of features that should be imported explicitly.
  "-unchecked",      // Enable detailed unchecked (erasure) warnings.
  "-Wunused:linted", // Check unused imports and variables.
)

ThisBuild / javacOptions ++= Seq("-deprecation", "-Xlint")

lazy val hilo = (project in file(".")).settings(
  name    := "hilo",
  version := "1.0.0",
  libraryDependencies ++= Seq(
    scalaTest % Test,
    apacheLang,
    scalactic,
  )
)
