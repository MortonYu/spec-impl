import mill._
import mill.scalalib._

object specimpl extends ScalaModule {
  def scalaVersion = "2.12.10"
  override def scalacOptions = Seq("-Xsource:2.11")
  override def ivyDeps = Agg(
    ivy"edu.berkeley.cs::chisel3:latest.integration",
    ivy"edu.berkeley.cs::firrtl:latest.integration",
  )
  object tests extends Tests {
    override def ivyDeps = Agg(ivy"com.lihaoyi::utest:latest.integration")
    def testFrameworks = Seq("utest.runner.Framework")
  }
}
