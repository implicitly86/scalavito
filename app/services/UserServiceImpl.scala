package services

import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import models.User
import models.daos.UserDAO
import play.api.Logger

import scala.concurrent.Future

/**
  * Класс UserServiceImpl.
  * ----------------------
  *
  * Реализация трейта UserService.
  *
  * @author EMurzakaev@it.ru.
  */
class UserServiceImpl @Inject()(userDAO: UserDAO) extends UserService {

    /**
      * Логгер.
      */
    private val logger: Logger = Logger("UserServiceImpl")

    /**
      * Получить пользователя.
      *
      * @param loginInfo информация о логине пользователя.
      * @return пользователь, если найден, None, если не найден.
      */
    override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = {
        logger.info(s"Retrieve user with login info $loginInfo")
        userDAO.find(loginInfo)
    }

    /**
      * Сохранить пользователя.
      *
      * @param user пользователь.
      * @return сохраненный пользователь.
      */
    override def save(user: User): Future[User] = {
        logger.info(s"Save user ${user.firstName}")
        userDAO.save(user)
    }

}
