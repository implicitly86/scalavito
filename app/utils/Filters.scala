package utils

import javax.inject.Inject

import play.api.http.HttpFilters
import play.api.mvc.EssentialFilter
import play.filters.csrf.CSRFFilter
import play.filters.gzip.GzipFilter
import play.filters.headers.SecurityHeadersFilter

/**
  * Provides filters.
  */
class Filters @Inject()(gzipFilter: GzipFilter, csrfFilter: CSRFFilter, securityHeadersFilter: SecurityHeadersFilter, requestFilter: RequestFilter) extends HttpFilters {

    override def filters: Seq[EssentialFilter] = Seq(gzipFilter, csrfFilter, securityHeadersFilter, requestFilter)

}
