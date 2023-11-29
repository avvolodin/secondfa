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
    libraryDependencies += ws,
    libraryDependencies += "com.github.kenglxn.qrgen" % "javase" % "2.6.0",
    libraryDependencies += "org.liquibase" % "liquibase-core" % "4.20.0",
    libraryDependencies += "org.playframework" %% "play-slick" % "6.0.0",
    libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "5.1.0",
    libraryDependencies += "org.postgresql" % "postgresql" % "42.5.4",
    libraryDependencies += "com.github.jwt-scala" %% "jwt-play-json" % "9.4.5"

  )