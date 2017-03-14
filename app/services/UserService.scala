package services

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import models.User

import scala.concurrent.Future

/**
  * Траит UserService.
  * ------------------
  *
  * @author EMurzakaev@it.ru.
  */
trait UserService extends IdentityService[User] {

    /**
      * Получить пользователя.
      *
      * @param loginInfo информация о логине пользователя.
      * @return пользователь, если найден, None, если не найден.
      */
    def retrieve(loginInfo: LoginInfo): Future[Option[User]]

    /**
      * Сохранить пользователя.
      *
      * @param user пользователь.
      * @return сохраненный пользователь.
      */
    def save(user: User): Future[User]

}
