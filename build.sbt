name := """dagger-seed"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"

libraryDependencies += "com.google.dagger" % "dagger" % "2.23.2"

libraryDependencies += "com.google.dagger" % "dagger-compiler" % "2.23.2"

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"

lazy val processAnnotations = taskKey[Unit]("my test task")

processAnnotations := {
  def failIfNonZeroExitStatus(command: String, message: => String, log: Logger): Unit = {
    import scala.sys.process._
    val result = command.!

    if (result != 0) {
      log.error(message)
      sys.error(s"Failed running command: $command")
    }
  }
  
  val log = streams.value.log

  log.info("Processing annotations ...")

  val classpath =
    ((products in Compile).value ++ ((dependencyClasspath in Compile).value.files)).mkString(":")
//  val destinationDirectory = ((classDirectory in Compile).value).getParentFile./("routes")./("main")
  val destinationDirectory = ((sourceManaged in Compile).value)
  val processor = "dagger.internal.codegen.ComponentProcessor"
  val classesToProcess = Seq("controllers.HomeController").mkString(" ")

  val command = s"javac -cp $classpath -proc:only -XprintRounds -s ${destinationDirectory} $classesToProcess"

  failIfNonZeroExitStatus(command, "Failed to process annotations.", log)

  log.info("Done processing annotations.")
}

