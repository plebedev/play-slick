name := """play-slick-simple"""
organization := "com.allstuffaround"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.11"

libraryDependencies += filters
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % Test
libraryDependencies += "com.typesafe.play" %% "play-slick" % "2.0.2"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "2.0.2"
libraryDependencies += "org.mindrot" % "jbcrypt" % "0.4"
libraryDependencies += "com.h2database" % "h2" % "1.2.140"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.allstuffaround.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.allstuffaround.binders._"
