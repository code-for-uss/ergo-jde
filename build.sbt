ThisBuild / scalaVersion := "2.12.10"

ThisBuild / scalacOptions ++= Seq(
  "-Xlint",
  "-Ywarn-dead-code",
  "-Ywarn-value-discard",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-deprecation",
  "-feature",
  "-unchecked"
)

resolvers ++= Seq(
  "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "SonaType" at "https://oss.sonatype.org/content/groups/public",
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
)

updateOptions := updateOptions.value.withLatestSnapshots(false)

lazy val commonSettings: Def.Setting[Seq[ModuleID]] = libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-collection-compat" % "2.1.3",
  "org.bouncycastle" % "bcprov-jdk15on" % "1.+",
  "org.ergoplatform" %% "ergo-appkit" % "4.0.3",
  "com.squareup.okhttp3" % "mockwebserver" % "3.14.9" % Test,
  "org.scalatest" %% "scalatest" % "3.0.8" % Test,
  "org.scalacheck" %% "scalacheck" % "1.14.+" % Test,
  "org.mockito" % "mockito-core" % "2.23.4" % Test
)

lazy val Kiosk = Project("kiosk", file("kiosk")).settings(
  commonSettings
)

lazy val JDE = Project("jde", file("jde"))
  .dependsOn(Kiosk)
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-json" % "2.9.2"
    )
  )

lazy val myMainClass = "cli.CLI"
lazy val myJarName = "jde.jar"

lazy val root = Project("ErgoJDE", file("."))
  .dependsOn(JDE)
  .settings(
    libraryDependencies += "javax.servlet" % "servlet-api" % "2.5" % "provided", // for servlet
    Compile / mainClass := Some(myMainClass),
    assembly / mainClass := Some(myMainClass),
    assembly / assemblyJarName := myJarName,
    assembly / assemblyMergeStrategy := {
      case PathList("reference.conf")           => MergeStrategy.concat
      case PathList("META-INF", xs @ _*)        => MergeStrategy.discard
      case x if x.endsWith("module-info.class") => MergeStrategy.discard
      case x                                    => MergeStrategy.first
    }
  )

enablePlugins(JettyPlugin)
