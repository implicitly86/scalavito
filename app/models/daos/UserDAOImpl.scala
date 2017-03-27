package models.daos

import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import models.User
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.cache.CacheApi

import scala.concurrent.Future

/**
  * Класс UserDAOImpl.
  * ------------------
  *
  * @author EMurzakaev@it.ru.
  */
class UserDAOImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, val cache: CacheApi) extends UserDAO with DAOSlick {

    import driver.api._

    /**
      * Логгер.
      */
    private val logger: Logger = Logger("UserDAOImpl")

    /**
      * Поиск пользователя по данным логина.
      *
      * @param loginInfo данные логина пользователя.
      * @return пользователя, если найден, None в противном случае.
      */
    def find(loginInfo: LoginInfo): Future[Option[User]] = {
        val userQuery = for {
            dbLoginInfo <- loginInfoQuery(loginInfo)
            dbUserLoginInfo <- slickUserLoginInfos.filter(_.loginInfoId === dbLoginInfo.id)
            dbUser <- slickUsers.filter(_.id === dbUserLoginInfo.userID)
        } yield dbUser
        val query = userQuery.result.statements.headOption.get
        logger.info(s"Find user with login info $loginInfo \n\t\tQuery : $query")
        val userInCache = cache.get[User](query)
        if (userInCache != null) {
            logger.info(s"User in cache $userInCache")
            Future.successful(Some(userInCache))
        } else {
            logger.info("User in cache not present")
            db.run(userQuery.result.headOption).map { dbUserOption =>
                dbUserOption.map { dbUser =>
                    val user = User(dbUser.userID, loginInfo, dbUser.firstName, dbUser.lastName, dbUser.email)
                    cache.set(query, user, 30)
                    user
                }
            }
        }
    }

    /**
      * Поиск пользователя по ID.
      *
      * @param userID идентификационный номер пользователя для поиска.
      * @return пользователя, если найден, None в противном случае.
      */
    def find(userID: Long): Future[Option[User]] = {
        val query = for {
            dbUser <- slickUsers.filter(_.id === userID)
            dbUserLoginInfo <- slickUserLoginInfos.filter(_.userID === dbUser.id)
            dbLoginInfo <- slickLoginInfos.filter(_.id === dbUserLoginInfo.loginInfoId)
        } yield (dbUser, dbLoginInfo)
        db.run(query.result.headOption).map { resultOption =>
            resultOption.map {
                case (user, loginInfo) =>
                    User(
                        user.userID,
                        LoginInfo(loginInfo.providerID, loginInfo.providerKey),
                        user.firstName,
                        user.lastName,
                        user.email)
            }
        }
    }

    /**
      * Сохранение пользователя.
      *
      * @param user пользователь.
      * @return сохраненный пользователь.
      */
    def save(user: User): Future[User] = {
        val dbUser = DBUser(None, user.firstName, user.lastName, user.email)
        val dbLoginInfo = DBLoginInfo(None, user.loginInfo.providerID, user.loginInfo.providerKey)
        // We don't have the LoginInfo id so we try to get it first.
        // If there is no LoginInfo yet for this user we retrieve the id on insertion.
        val loginInfoAction = {
            val retrieveLoginInfo = slickLoginInfos.filter(
                info => info.providerID === user.loginInfo.providerID &&
                        info.providerKey === user.loginInfo.providerKey).result.headOption
            val insertLoginInfo = slickLoginInfos.returning(slickLoginInfos.map(_.id)).
                    into((info, id) => info.copy(id = Some(id))) += dbLoginInfo
            for {
                loginInfoOption <- retrieveLoginInfo
                loginInfo <- loginInfoOption.map(DBIO.successful(_)).getOrElse(insertLoginInfo)
            } yield loginInfo
        }
        // combine database actions to be run sequentially
        val actions = (for {
            user <- slickUsers.returning(slickUsers.map(_.id)) += dbUser
            loginInfo <- loginInfoAction
            _ <- slickUserLoginInfos += DBUserLoginInfo(user, loginInfo.id.get)
        } yield ()).transactionally
        // run actions and return user afterwards
        db.run(actions).map(_ => user)
    }

}
