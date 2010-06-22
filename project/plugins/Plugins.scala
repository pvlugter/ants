import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
  val akkaPlugin = "se.scalablesolutions.akka" % "akka-sbt-plugin" % "0.10"

  val databinderRepo = "Databinder Repository" at "http://databinder.net/repo"
  val spdeSbt = "us.technically.spde" % "spde-sbt-plugin" % "0.4.1"
}
