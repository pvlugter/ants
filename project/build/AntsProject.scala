import sbt._
import spde._

class AntsProject(info: ProjectInfo) extends DefaultSpdeProject(info) with AssemblyProject {
  val jBoss =  "jBoss" at "http://repository.jboss.org/maven2"
  val multiverse = "Multiverse releases" at "http://multiverse.googlecode.com/svn/maven-repository/releases/"
  val guiceyFruit = "GuiceyFruit" at "http://guiceyfruit.googlecode.com/svn/repo/releases/"
  val databinder = "Databinder" at "http://databinder.net/repo"
  val akkaRepo = "Akka Maven Repository" at "http://scalablesolutions.se/akka/repository"
  val javaNet = "Java.Net" at "http://download.java.net/maven/2"
  val scalaToolsSnapshots = ScalaToolsSnapshots
  
  val akkaCore = "se.scalablesolutions.akka" %% "akka-core" % "0.9"

  override def spdeSourcePath = mainSourcePath / "spde"
}

trait AssemblyProject extends BasicScalaProject {
  def assemblyExclude(base: PathFinder) = base / "META-INF" ** "*"
  def assemblyOutputPath = outputPath / assemblyJarName
  def assemblyJarName = artifactID + "-assembly-" + version + ".jar"
  def assemblyTemporaryPath = outputPath / "assemblage"
  def assemblyClasspath = runClasspath
  def assemblyExtraJars = mainDependencies.scalaJars
  
  lazy val expandLibs = expandLibsAction
 
  def expandLibsAction = expandLibsTask(assemblyTemporaryPath, assemblyClasspath, assemblyExtraJars) dependsOn(compile)
 
  def expandLibsTask(tempDir: Path, classpath: PathFinder, extraJars: PathFinder): Task = task {
    val libs = classpath.get.filter(ClasspathUtilities.isArchive)
    for (jar <- (libs ++ extraJars.get)) {
      log.info(jar.toString)
      FileUtilities.unzip(jar, tempDir, log) //.left.foreach(error)
    }
    None
  }
  
  def assemblyFiles = descendents(assemblyTemporaryPath ##, "*") --- assemblyExclude(assemblyTemporaryPath ##)
 
  def assemblyPackagePaths = packagePaths +++ assemblyFiles
 
  lazy val assembly = assemblyAction
 
  def assemblyAction = assemblyTask(assemblyPackagePaths) dependsOn(compile, expandLibs)
 
  def assemblyTask(packagePaths: PathFinder) =
    packageTask(packagePaths, assemblyOutputPath, packageOptions)
}
