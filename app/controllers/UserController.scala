package controllers

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import models.daos.UserDAO
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc._
import utils.auth.DefaultEnv

import scala.concurrent.Future

/**
  * Контроллер UserController.
  * --------------------------
  *
  * @author EMurzakaev@it.ru.
  */
class UserController @Inject()(
                                      silhouette: Silhouette[DefaultEnv],
                                      userDAO: UserDAO
                              ) extends Controller {

    /**
      * Поиск пользователя по id.
      *
      * @param id идентификационный номер пользователя.
      * @return пользователь, если найден, сообщение о том, что пользователь не найден, в противном случае.
      */
    def findUser(id: Long) = Action.async {
        userDAO.find(id).flatMap {
            case Some(user) => Future.successful(Ok(Json.toJson(user)))
            case None => Future.successful(BadRequest(Json.obj("message" -> s"user with id $id not found")))
        }
    }

}
