package controllers

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import models.daos.AdvertDAOImpl
import models.{Advert, AdvertForm}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import utils.ErrorHandler.handler
import utils.auth.DefaultEnv

import scala.concurrent.Future

/**
  * Контроллер AdvertController.
  * ----------------------------
  *
  * @author EMurzakaev@it.ru.
  */
class AdvertController @Inject()(
                                        silhouette: Silhouette[DefaultEnv],
                                        advertDAO: AdvertDAOImpl
                                ) extends Controller {

    /**
      * Сохранение объявления.
      *
      * Парсится json, если проходит валидацию - сохраняется объвления,
      * сообщение о невалидном json в противном случае.
      *
      * @return сохраненное объявление в случае успеха.
      */
    def saveAdvert = silhouette.SecuredAction(handler).async(parse.json) { implicit request =>
        val userID = request.identity.userID.getOrElse(-1l)
        request.body.validate[AdvertForm].map { form =>
            val data = Advert(None, userID, form.title, form.description)
            advertDAO.save(data).map(advert => Ok(Json.toJson(advert)))
        }.recoverTotal {
            _ => Future.successful(Ok(Json.obj("message" -> "not valid json object")))
        }
    }
    /**
      * Поиск объявления по id.
      *
      * @param id идентификационный номер объявления.
      * @return объявление, если найдено, сообщение о том, что объявление не найдено, в противном случае.
      */
    def findAdvert(id: Long) = Action.async {
        advertDAO.find(id).flatMap {
            case Some(advert) => Future.successful(Ok(Json.toJson(advert)))
            case None => Future.successful(NotFound(
                Json.obj("message" -> s"advert with id $id not found"))
            )
        }
    }
    /**
      * Найти обявления пользователя.
      *
      * @param id идентификационный номер пользователя.
      * @return список
      */
    def findUserAdverts(id: Long) = Action.async {
        advertDAO.findUserAdverts(id).flatMap {
            case head :: tail => Future.successful(Ok(Json.toJson(List(head) ++ tail)))
            case Nil => Future.successful(NotFound(
                Json.obj("message" -> s"adverts created by user with id $id not found"))
            )
        }
    }
    /**
      * Удалить объявление с заданным id.
      *
      * Удаление произойдет успешно, если удаляемое
      * объявление принадлежит залогиненному пользователю.
      *
      * @param id идентификационный номер объявления.
      */
    def deleteAdvert(id: Long) = silhouette.SecuredAction(handler).async { implicit request =>
        val userID = request.identity.userID.getOrElse(-1l)
        advertDAO.findUserAdverts(userID).flatMap { list =>
            if (list.exists(_.id.contains(id))) {
                advertDAO.delete(id).map {
                    case Some(_) => Ok
                    case None => BadRequest
                }
            } else {
                Future.successful(BadRequest)
            }
        }
    }

}
