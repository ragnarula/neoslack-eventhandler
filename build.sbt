name := "EventHandler"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies += "com.amazonaws" % "aws-java-sdk" % "1.11.86"

// https://github.com/typesafehub/scala-logging
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.7"

// https://mvnrepository.com/artifact/com.typesafe.akka/akka-actor_2.11
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.16"

// https://mvnrepository.com/artifact/com.typesafe/config
libraryDependencies += "com.typesafe" % "config" % "1.3.1"

// http://www.scalatest.org/install
libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.1"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

// https://mvnrepository.com/artifact/com.typesafe.akka/akka-testkit_2.11
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.4.16" % "test"

libraryDependencies += "org.scalamock" %% "scalamock-scalatest-support" % "3.4.2" % Test

// https://mvnrepository.com/artifact/org.scala-lang.modules/scala-pickling_2.11
libraryDependencies += "org.scala-lang.modules" %% "scala-pickling" % "0.10.1"

// https://mvnrepository.com/artifact/com.typesafe.play/play-json_2.11
libraryDependencies += "com.typesafe.play" % "play-json_2.11" % "2.5.12"
