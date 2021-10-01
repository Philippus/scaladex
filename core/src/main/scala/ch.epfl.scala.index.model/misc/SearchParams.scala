package ch.epfl.scala.index.model
package misc

import ch.epfl.scala.index.model.release.Platform

object SearchParams {
  val resultsPerPage = 20
}

case class SearchParams(
    queryString: String = "",
    page: PageIndex = 0,
    sorting: Option[String] = None,
    userRepos: Set[GithubRepo] = Set(),
    total: Int = SearchParams.resultsPerPage,
    targetFiltering: Option[Platform] = None,
    cli: Boolean = false,
    topics: List[String] = Nil,
    targetTypes: List[String] = Nil,
    scalaVersions: List[String] = Nil,
    scalaJsVersions: List[String] = Nil,
    scalaNativeVersions: List[String] = Nil,
    sbtVersions: List[String] = Nil,
    contributingSearch: Boolean = false
)
