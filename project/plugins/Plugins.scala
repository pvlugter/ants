import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
  val databinderRepo = "Databinder Repository" at "http://databinder.net/repo"
  val spdeSbt = "us.technically.spde" % "spde-sbt-plugin" % "0.4.1"
}
