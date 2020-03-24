ThisBuild / resolvers ++= Seq(
  "Apache Development Snapshot Repository" at "https://repository.apache.org/content/repositories/snapshots/",
  Resolver.mavenLocal
)

name := "demo2"

version := "1.0"

organization := "org.example"

ThisBuild / scalaVersion := "2.12.11"


val myDependencies = Seq(
    "org.influxdb" % "influxdb-java" % "2.15" % "compile",
    "org.scala-lang" % "scala-library" % "2.12.11" % "compile",
)

lazy val root = (project in file(".")).
  settings(
    libraryDependencies ++= myDependencies
  )

assembly / mainClass := Some("org.example.Job")

// make run command include the provided dependencies
Compile / run  := Defaults.runTask(Compile / fullClasspath,
  Compile / run / mainClass,
  Compile / run / runner
).evaluated

// stays inside the sbt console when we press "ctrl-c" while a Flink programme executes with "run" or "runMain"
Compile / run / fork := true
Global / cancelable := true

// exclude Scala library from assembly
assembly / assemblyOption  := (assembly / assemblyOption).value.copy(includeScala = true)
