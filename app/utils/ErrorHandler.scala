package utils

import javax.inject.Inject

import com.mohiva.play.silhouette.api.actions.SecuredErrorHandler
import play.api.http.DefaultHttpErrorHandler
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.mvc.RequestHeader
import play.api.mvc.Results._
import play.api.routing.Router
import play.api.{Configuration, Logger, OptionalSourceMapper}

import scala.concurrent.Future

/**
  * A secured error handler.
  */
class ErrorHandler @Inject()(
                                    val messagesApi: MessagesApi,
                                    env: play.api.Environment,
                                    config: Configuration,
                                    sourceMapper: OptionalSourceMapper,
                                    router: javax.inject.Provider[Router])
        extends DefaultHttpErrorHandler(env, config, sourceMapper, router) {

    import ErrorHandler.logger

    /**
      * Вызывается, когда происходит ошибка на стороне пользователя.
      *
      * @param request    заголовок запроса.
      * @param statusCode код ошибки.
      * @param message    сообщение ошибки.
      * @return результат, отправляемый пользователю.
      */
    override def onClientError(request: RequestHeader, statusCode: Int, message: String) = {
        Future.successful({
            logger.warn(s"A client error occurred. Status: $statusCode. Message: \'$message\'. Query: \'${request.path}\'")
            Status(statusCode)(Json.obj(
                "message" -> s"Error: $message",
                "status" -> statusCode,
                "query" -> request.path
            ))
        })
    }

    /**
      * Вызывается, когда происходит ошибка на стороне сервера.
      *
      * @param request   заголовок запроса.
      * @param exception ошибка.
      * @return результат, отправляемый пользователю.
      */
    override def onServerError(request: RequestHeader, exception: Throwable) = {
        Future.successful({
            logger.error("Server error", exception)
            InternalServerError(Json.obj(
                "message" -> "server error",
                "exception message" -> exception.getLocalizedMessage
            ))
        })
    }

}

/**
  * Объект-компаньон ErrorHandler.
  *
  * Содержит handler, который используется в silhouette.SecuredAction.
  */
object ErrorHandler {

    /**
      * Логгер.
      */
    private val logger: Logger = Logger("ErrorHandler")

    /**
      * Обработчик ошибок авторизации / аутентификации.
      */
    val handler = new SecuredErrorHandler {
        /**
          * Обработчик, вызываем в случае если пользователь не аутентифицирован.
          *
          * @param request заголовок запроса.
          * @return json объект, содержащий сообщение о том, что пользователь не аутентифицирован.
          */
        override def onNotAuthenticated(implicit request: RequestHeader) = {
            logger.warn(s"Not authenticated error occurred. Request ${request.path}")
            Future.successful(Unauthorized(Json.obj("message" -> "not authenticated")))
        }

        /**
          * Обработчик, вызываем в случае если пользователь не авторизован.
          *
          * @param request заголовок запроса.
          * @return json объект, содержащий сообщение о том, что пользователь не авторизован.
          */
        override def onNotAuthorized(implicit request: RequestHeader) = {
            logger.warn(s"Not authorized error occurred. Request ${request.path}")
            Future.successful(Forbidden(Json.obj("message" -> "not authorized")))
        }
    }

}