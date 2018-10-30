import sbt._
import Keys._

val moduleName = "drt-api-export"
val typesafeConfig = "1.3.0"
val specs2 = "3.7.3"
val jodaTime = "2.9.4"

val root = Project(id = moduleName, base = file("."))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings: _*)
  .settings(
    name := moduleName,
    organization := "uk.gov.homeoffice",
    scalaVersion := "2.11.8",
    scalacOptions ++= Seq(
      "-feature",
      "-language:implicitConversions",
      "-language:higherKinds",
      "-language:existentials",
      "-language:reflectiveCalls",
      "-language:postfixOps",
      "-Yrangepos"),
    exportJars := true,
    ivyScala := ivyScala.value map {
      _.copy(overrideScalaVersion = true)
    },
    resolvers ++= Seq(
      "Artifactory Snapshot Realm" at "http://artifactory.registered-traveller.homeoffice.gov.uk/artifactory/libs-snapshot-local/",
      "Artifactory Release Realm" at "http://artifactory.registered-traveller.homeoffice.gov.uk/artifactory/libs-release-local/",
      "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
      "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
    ),
    libraryDependencies ++= Seq(
      "org.clapper" %% "grizzled-slf4j" % "1.3.2",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "org.postgresql" % "postgresql" % "42.2.2",
      "commons-dbcp" % "commons-dbcp" % "1.4",
      "com.typesafe" % "config" % typesafeConfig,
      "com.github.scopt" %% "scopt" % "3.7.0",
      "joda-time" % "joda-time" % jodaTime,
      "commons-io" % "commons-io" % "2.6",
      "io.spray" %% "spray-json" % "1.3.4",
      "org.scalikejdbc" %% "scalikejdbc" % "3.3.0"
    ),
    libraryDependencies ++= Seq(
      "com.h2database" % "h2" % "1.4.191" % Test,
      "joda-time" % "joda-time" % "2.9.4" % Test,
      "org.specs2" %% "specs2-core" % specs2 % Test,
      "org.specs2" %% "specs2-junit" % specs2 % Test,
      "org.specs2" %% "specs2-mock" % specs2 % Test
    )
  ).enablePlugins()


publishTo := {
  val artifactory = "http://artifactory.registered-traveller.homeoffice.gov.uk/"

  if (isSnapshot.value)
    Some("snapshot" at artifactory + "artifactory/libs-snapshot-local")
  else
    Some("release"  at artifactory + "artifactory/libs-release-local")
}

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

// Enable publishing the jar produced by `test:package`
publishArtifact in(Test, packageBin) := true

// Enable publishing the test API jar
publishArtifact in(Test, packageDoc) := true

// Enable publishing the test sources jar
publishArtifact in(Test, packageSrc) := true

javaOptions in run += "-Djdk.logging.allowStackWalkSearch=true"

fork in run := true

mainClass in(Compile, run) := Some("uk.gov.homeoffice.drt.Boot")

enablePlugins(JavaAppPackaging)

enablePlugins(UniversalPlugin)
