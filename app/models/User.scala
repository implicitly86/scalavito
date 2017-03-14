package models

import java.util.UUID

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import play.api.libs.json.{Json, OFormat}

/**
  * Класс User.
  * -----------
  *
  * @param userID    уникальный ID пользователя.
  * @param loginInfo информация о логине пользователя.
  * @param firstName имя пользователя.
  * @param lastName  фамилия пользователя.
  * @param email     электронная почта пользователя.
  * @author EMurzakaev@it.ru.
  */
case class User(
                       userID: UUID,
                       loginInfo: LoginInfo,
                       firstName: String,
                       lastName: String,
                       email: Option[String]) extends Identity

/**
  * Объект-компаньен класса User.
  */
object User {

    /**
      * Конвертер объекта [User] в json и наоборот.
      */
    implicit val format: OFormat[User] = Json.format[User]
}