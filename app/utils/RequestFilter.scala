package utils

import javax.inject.Inject

import akka.stream.Materializer
import play.api.Logger
import play.api.http.HeaderNames._
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class RequestFilter @Inject()(implicit val mat: Materializer, ec: ExecutionContext) extends Filter {

    def apply(nextFilter: RequestHeader => Future[Result])
             (requestHeader: RequestHeader): Future[Result] = {
        val origin = requestHeader.headers.get(ORIGIN).getOrElse("*")
        if (requestHeader.method == "OPTIONS") {
            val response = Ok.withHeaders(
                ACCESS_CONTROL_ALLOW_ORIGIN -> origin,
                ACCESS_CONTROL_ALLOW_METHODS -> "POST, GET, OPTIONS, PUT, DELETE",
                ACCESS_CONTROL_MAX_AGE -> "3600",
                ACCESS_CONTROL_ALLOW_HEADERS -> s"$ORIGIN, X-Requested-With, $CONTENT_TYPE, $ACCEPT, $AUTHORIZATION, X-Auth-Token, Csrf-Token",
                ACCESS_CONTROL_ALLOW_CREDENTIALS -> "true"
            )
            Logger.info(s"RequestFilter: ${requestHeader.method} ${response.header.status} ${requestHeader.uri}")
            Future.successful(response)
        } else {
            nextFilter(requestHeader).map { result =>
                //Logger.info(s"RequestFilter: ${requestHeader.method} ${result.header.status} ${requestHeader.uri}")
                result.withHeaders(
                    ACCESS_CONTROL_ALLOW_ORIGIN -> origin,
                    ACCESS_CONTROL_ALLOW_CREDENTIALS -> "true"
                )
            }
        }
    }

}