package models.daos

import javax.inject.Inject

import models.Advert
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future


/**
  * Класс AdvertDAOImpl.
  * --------------------
  *
  * @author EMurzakaev@it.ru.
  */
class AdvertDAOImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends AdvertDAO with DAOSlick {

    import driver.api._

    /**
      * Логгер.
      */
    private[this] val logger: Logger = Logger("UserDAOImpl")
    /**
      * Сохранить объявление.
      *
      * @param advert объявление.
      * @return объявление.
      */
    override def save(advert: Advert): Future[Advert] = {
        logger.debug(s"Save advert $advert")
        val dbAdvert = DBAdvert(None, advert.authorID, advert.title, advert.description)
        val request = (slickAdverts returning slickAdverts.map(_.id))
                .into((info, id) => info.copy(id = Some(id))) += dbAdvert
        db.run(request).map(result => Advert(result.id, result.authorID, result.title, result.description))
    }
    /**
      * Поиск объявления по идентификационному номеру.
      *
      * @param id идентификационный номер объявления.
      * @return экземпляр Option, содержащий объявление Advert в случае успешного нахождения по id.
      */
    override def find(id: Long): Future[Option[Advert]] = {
        logger.debug(s"Find advert with id $id")
        val request = slickAdverts.filter(_.id === id).result.headOption
        db.run(request).map { dbAdvert =>
            dbAdvert.map { advert =>
                Advert(advert.id, advert.authorID, advert.title, advert.description)
            }
        }
    }
    /**
      * Поиск объявлений, созданных пользователем с заданным id.
      *
      * @param id идентификационный номер пользователя.
      * @return список объявлений, созданных пользователем с заданным id.
      */
    override def findUserAdverts(id: Long): Future[List[Advert]] = {
        logger.debug(s"Find adverts with authorID $id")
        val request = slickAdverts.filter(_.author === id).result
        db.run(request).map { dbAdverts =>
            dbAdverts.map { dbAdvert =>
                Advert(dbAdvert.id, dbAdvert.authorID, dbAdvert.title, dbAdvert.description)
            }.toList
        }
    }
    /**
      * Удаление объявления по id.
      *
      * @param id идентификационный номер объявления.
      * @return экземпляр Option, содержащий число удаленных записей (должна быть одна запись).
      */
    override def delete(id: Long): Future[Option[Int]] = {
        logger.debug(s"Delete advert with id $id")
        val request = slickAdverts.filter(_.id === id).delete
        db.run(request).flatMap {
            case 0 => Future.successful(None)
            case x => Future.successful(Some(x))
        }
    }
    /**
      * Удаление объявления по id.
      *
      * @param advertId идентификационный номер объявления.
      * @param userId   идентификационный номер пользователя.
      * @return экземпляр Option, содержащий объявление Advert в случае успешного удаления объявления по id.
      */
    override def delete(advertId: Long, userId: Long) = {
        logger.debug(s"Delete advert with advert id $advertId and user id $userId")
        val request = slickAdverts.filter(x => x.id === advertId && x.author === userId).delete
        db.run(request).flatMap {
            case 0 => Future.successful(None)
            case x => Future.successful(Some(x))
        }
    }

}
