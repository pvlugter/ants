import sbt._
import spde._

class AntsProject(info: ProjectInfo) extends DefaultSpdeProject(info) with AkkaProject with AssemblyProject {
  override def spdeSourcePath = mainSourcePath / "spde"
}

trait AssemblyProject extends BasicScalaProject {
  def assemblyExclude(base: PathFinder) = (base / "META-INF" ** "*") +++ (base / "logback.xml")
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
