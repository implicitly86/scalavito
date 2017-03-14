package utils.auth

import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import models.User

/**
  * Траит DefaultEnv.
  * -----------------
  * Определяет окружение для Silhouette.
  *
  * @author EMurzakaev@it.ru.
  */
trait DefaultEnv extends Env {
    type I = User
    type A = JWTAuthenticator
}
