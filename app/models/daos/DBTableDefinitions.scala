package models.daos

import com.mohiva.play.silhouette.api.LoginInfo
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape.proveShapeOf

/**
  * Траит DBTableDefinitions.
  * -------------------------
  *
  * @author EMurzakaev@it.ru.
  */
trait DBTableDefinitions {

    protected val driver: JdbcProfile

    import driver.api._

    // table query definitions
    val slickUsers = TableQuery[Users]
    val slickLoginInfos = TableQuery[LoginInfos]
    val slickUserLoginInfos = TableQuery[UserLoginInfos]
    val slickPasswordInfos = TableQuery[PasswordInfos]

    // queries used in multiple places
    def loginInfoQuery(loginInfo: LoginInfo) =
        slickLoginInfos.filter(dbLoginInfo => dbLoginInfo.providerID === loginInfo.providerID && dbLoginInfo.providerKey === loginInfo.providerKey)

    case class DBUser(userID: String, firstName: String, lastName: String, email: Option[String])

    class Users(tag: Tag) extends Table[DBUser](tag, "user") {
        def id = column[String]("userID", O.PrimaryKey)

        def firstName = column[String]("firstName")

        def lastName = column[String]("lastName")

        def email = column[Option[String]]("email")

        def * = (id, firstName, lastName, email) <> (DBUser.tupled, DBUser.unapply)
    }

    case class DBLoginInfo(id: Option[Long], providerID: String, providerKey: String)

    class LoginInfos(tag: Tag) extends Table[DBLoginInfo](tag, "logininfo") {
        def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

        def providerID = column[String]("providerID")

        def providerKey = column[String]("providerKey")

        def * = (id.?, providerID, providerKey) <> (DBLoginInfo.tupled, DBLoginInfo.unapply)
    }

    case class DBUserLoginInfo(userID: String, loginInfoId: Long)

    class UserLoginInfos(tag: Tag) extends Table[DBUserLoginInfo](tag, "userlogininfo") {
        def userID = column[String]("userID")

        def loginInfoId = column[Long]("loginInfoId")

        def * = (userID, loginInfoId) <> (DBUserLoginInfo.tupled, DBUserLoginInfo.unapply)
    }

    case class DBPasswordInfo(hasher: String, password: String, salt: Option[String], loginInfoId: Long)

    class PasswordInfos(tag: Tag) extends Table[DBPasswordInfo](tag, "passwordinfo") {
        def hasher = column[String]("hasher")

        def password = column[String]("password")

        def salt = column[Option[String]]("salt")

        def loginInfoId = column[Long]("loginInfoId")

        def * = (hasher, password, salt, loginInfoId) <> (DBPasswordInfo.tupled, DBPasswordInfo.unapply)
    }

}
