scalaVersion := "2.12.7"

val http4sVersion = "0.18.19"

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "io.chrisdavenport" %% "log4cats-slf4j" % "0.1.1",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)

scalacOptions ++= Seq("-Ypartial-unification")
