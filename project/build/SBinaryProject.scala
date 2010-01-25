import sbt._

class SBinaryProject(info: ProjectInfo) extends DefaultProject(info)
{
	val fmppConf = config("fmpp")
	val fmppDep = "net.sourceforge.fmpp" % "fmpp" % "0.9.13" % "fmpp"
	val sc = "org.scala-tools.testing" % "scalacheck" % "1.5" % "test"
	val compat = "org.scala-tools.sbt" %% "test-compat" % "0.4" % "test"

	def srcManaged = path("src_managed")
	override def mainScalaSourcePath = srcManaged / "main"
	override def testScalaSourcePath = srcManaged / "test"
	override def testFrameworks = new TestFramework("sbt.impl.ScalaCheckFramework") :: Nil

	def fmppArgs(output: Path) = "--ignore-temporary-files" :: "-O" :: output.absolutePath :: Nil
	def templates = sources("src")
	def testTemplates = sources("test-src")
	def fmppClasspath = configurationClasspath(fmppConf)

	override def compileAction = super.compileAction dependsOn(template)
	override def testCompileAction = super.testCompileAction dependsOn(testTemplate)

	lazy val setupManaged = task { FileUtilities.clean(srcManaged, log); FileUtilities.createDirectory(srcManaged, log) }
	lazy val template = fmppTask(fmppArgs(mainScalaSourcePath), templates) dependsOn(setupManaged)
	lazy val testTemplate = fmppTask(fmppArgs(testScalaSourcePath), testTemplates) dependsOn(setupManaged)

	def fmppTask(args: List[String], sources: PathFinder) =
		runTask(Some("fmpp.tools.CommandLine"), fmppClasspath, args ::: sources.get.map(_.absolutePath).toList)
}
