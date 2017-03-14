package controllers

import java.util.UUID

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{Clock, Credentials, PasswordHasher}
import com.mohiva.play.silhouette.api.{LoginEvent, LoginInfo, SignUpEvent, Silhouette}
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.{LoginForm, SignUpForm, User}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import play.api.{Configuration, Logger}
import services.UserService
import utils.auth.DefaultEnv

import scala.concurrent.Future

/**
  * Класс AuthController.
  * ---------------------
  *
  * @author EMurzakaev@it.ru.
  */
class AuthController @Inject()(
                                      silhouette: Silhouette[DefaultEnv],
                                      userService: UserService,
                                      authInfoRepository: AuthInfoRepository,
                                      credentialsProvider: CredentialsProvider,
                                      configuration: Configuration,
                                      clock: Clock,
                                      passwordHasher: PasswordHasher) extends Controller {

    /**
      * Логгер.
      */
    private val logger: Logger = Logger("AuthController")

    /**
      * Аутентификация пользователя по логину/паролю.
      *
      * @return JSON с JWT токеном, в случае успешного входа, иначе с сообщением об ошибке.
      */
    def login = Action.async(parse.json) { implicit request =>
        request.body.validate[LoginForm].map { data =>
            credentialsProvider.authenticate(Credentials(data.email, data.password)).flatMap { loginInfo =>
                userService.retrieve(loginInfo).flatMap {
                    case Some(user) => silhouette.env.authenticatorService.create(loginInfo).map {
                        /*
                        case authenticator if data.rememberMe =>
                            val c = configuration.underlying
                            authenticator.copy(
                                expirationDateTime = clock.now + c.as[FiniteDuration]("silhouette.authenticator.rememberMe.authenticatorExpiry"),
                                idleTimeout = c.getAs[FiniteDuration]("silhouette.authenticator.rememberMe.authenticatorIdleTimeout")
                            )
                        */
                        case authenticator => authenticator
                    }.flatMap { authenticator =>
                        logger.info(s"User ${user.email} successfully logged in.")
                        silhouette.env.eventBus.publish(LoginEvent(user, request))
                        silhouette.env.authenticatorService.init(authenticator).map { token =>
                            Ok(Json.obj("token" -> token))
                        }
                    }
                    case None => Future.failed(new IdentityNotFoundException("Couldn't find user"))
                }
            }.recover {
                case e: ProviderException =>
                    logger.warn(s"Unsuccessful login with email ${data.email}.")
                    Unauthorized(Json.obj("message" -> "invalid.credentials"))
            }
        }.recoverTotal {
            case error => Future.successful(BadRequest(Json.obj("message" -> "Not valid json request body.")))
        }
    }

    def signup = Action.async(parse.json) { implicit request =>
        request.body.validate[SignUpForm].map { data =>
            val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
            userService.retrieve(loginInfo).flatMap {
                case Some(_) =>
                    Future.successful(BadRequest(Json.obj("message" -> "user.exists")))
                case None =>
                    val authInfo = passwordHasher.hash(data.password)
                    val user = User(
                        userID = UUID.randomUUID(),
                        loginInfo = loginInfo,
                        firstName = data.firstName,
                        lastName = data.lastName,
                        email = Some(data.email)
                    )
                    for {
                        user <- userService.save(user)
                        authInfo <- authInfoRepository.add(loginInfo, authInfo)
                        authenticator <- silhouette.env.authenticatorService.create(loginInfo)
                        token <- silhouette.env.authenticatorService.init(authenticator)
                    } yield {
                        silhouette.env.eventBus.publish(SignUpEvent(user, request))
                        silhouette.env.eventBus.publish(LoginEvent(user, request))
                        Ok(Json.obj("token" -> token))
                    }
            }
        }.recoverTotal {
            case error =>
                Future.successful(Unauthorized(Json.obj("message" -> "invalid.data")))
        }
    }

}
