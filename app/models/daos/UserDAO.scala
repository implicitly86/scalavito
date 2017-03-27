package models.daos

import com.mohiva.play.silhouette.api.LoginInfo
import models.User

import scala.concurrent.Future

/**
  * Треит UserDAO.
  * --------------
  *
  * @author EMurzakaev@it.ru.
  */
trait UserDAO {

    /**
      * Поиск пользователя по данным логина.
      *
      * @param loginInfo данные логина пользователя.
      * @return пользователя, если найден, None в противном случае.
      */
    def find(loginInfo: LoginInfo): Future[Option[User]]

    /**
      * Поиск пользователя по ID.
      *
      * @param userID идентификационный номер пользователя для поиска.
      * @return пользователя, если найден, None в противном случае.
      */
    def find(userID: Long): Future[Option[User]]

    /**
      * Сохранение пользователя.
      *
      * @param user пользователь.
      * @return сохраненный пользователь.
      */
    def save(user: User): Future[User]

}
