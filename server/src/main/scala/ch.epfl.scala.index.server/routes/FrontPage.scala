package ch.epfl.scala.index
package server
package routes

import model.misc.UserInfo
import TwirlSupport._
import com.softwaremill.session.SessionDirectives._
import com.softwaremill.session.SessionOptions._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import scala.concurrent.ExecutionContext

class FrontPage(dataRepository: DataRepository,
                session: GithubUserSession)(implicit ec: ExecutionContext) {
  import session.implicits._

  private def frontPage(userInfo: Option[UserInfo]) = {
    import dataRepository._
    val topicsF = topics()
    val targetTypesF = targetTypes()
    val scalaVersionsF = scalaVersions()
    val scalaJsVersionsF = scalaJsVersions()
    val scalaNativeVersionsF = scalaNativeVersions()
    val sbtVersionsF = sbtVersions()
    val mostDependedUponF = mostDependedUpon()
    val latestProjectsF = latestProjects()
    val latestReleasesF = latestReleases()
    val totalProjectsF = totalProjects()
    val totalReleasesF = totalReleases()
    val contributingProjectsF = contributingProjects()

    for {
      topics <- topicsF
      targetTypes <- targetTypesF
      scalaVersions <- scalaVersionsF
      scalaJsVersions <- scalaJsVersionsF
      scalaNativeVersions <- scalaNativeVersionsF
      sbtVersions <- sbtVersionsF
      mostDependedUpon <- mostDependedUponF
      latestProjects <- latestProjectsF
      latestReleases <- latestReleasesF
      totalProjects <- totalProjectsF
      totalReleases <- totalReleasesF
      contributingProjects <- contributingProjectsF
    } yield {

      def query(label: String)(xs: String*): String =
        xs.map(v => s"$label:$v").mkString("search?q=", " OR ", "")

      val ecosystems = Map(
        "Akka" -> query("topics")("akka",
                                  "akka-http",
                                  "akka-persistence",
                                  "akka-streams"),
        "Scala.js" -> "search?targets=scala.js_0.6",
        "Spark" -> query("depends-on")("apache/spark-streaming",
                                       "apache/spark-graphx",
                                       "apache/spark-hive",
                                       "apache/spark-mllib",
                                       "apache/spark-sql"),
        "Typelevel" -> "typelevel"
      )

      views.html.frontpage(
        topics,
        targetTypes,
        scalaVersions,
        scalaJsVersions,
        scalaNativeVersions,
        sbtVersions,
        latestProjects,
        mostDependedUpon,
        latestReleases,
        userInfo,
        ecosystems,
        totalProjects,
        totalReleases,
        contributingProjects
      )
    }
  }

  val routes: Route =
    pathSingleSlash {
      optionalSession(refreshable, usingCookies) { userId =>
        complete(frontPage(session.getUser(userId).map(_.user)))
      }
    }
}
