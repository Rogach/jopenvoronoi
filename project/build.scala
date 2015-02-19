import sbt._
import Keys._
import sbtassembly.AssemblyKeys._
import spray.revolver.RevolverPlugin._

object build extends Build {

  lazy val root = Project("jopenvoronoi", file("."))
    .settings(Revolver.settings:_*)
    .settings (
      scalaVersion := "2.11.4",
      scalacOptions ++= Seq(
        "-deprecation",
        "-unchecked",
        "-feature",
        "-language:postfixOps",
        "-language:reflectiveCalls",
        "-language:implicitConversions",
        "-Xlint"
      ),
      javacOptions ++= Seq("-Xlint:all"),
      javaOptions += "-ea",

      libraryDependencies ++= Seq(
        "org.scalatest" %% "scalatest" % "2.2.2" % "test"
      ),

      Revolver.reColors := Seq("blue", "green", "magenta", "cyan"),
      mainClass in assembly := Some("org.rogach.jopenvoronoi.Main"),

      name := "jopenvoronoi",
      version := "0.0.1",
      organization := "org.rogach"
    )
}
