ThisBuild / scalaVersion := "2.13.12"

ThisBuild / version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """secondfa""",
    resolvers +="jitpack.io" at "https://jitpack.io",
    libraryDependencies ++= Seq(
      guice,
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
    ),
    libraryDependencies += "com.github.kenglxn.qrgen" % "javase" % "2.6.0"
  )