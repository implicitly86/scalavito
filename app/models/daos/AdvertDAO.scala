package models.daos

import models.Advert

import scala.concurrent.Future

/**
  * Треит AdvertDAO.
  * ----------------
  *
  * @author EMurzakaev@it.ru.
  */
trait AdvertDAO {

    /**
      * Сохранить объявление.
      *
      * @param advert объявление.
      * @return объявление.
      */
    def save(advert: Advert): Future[Advert]
    /**
      * Поиск объявления по идентификационному номеру.
      *
      * @param id идентификационный номер объявления.
      * @return экземпляр Option, содержащий объявление Advert в случае успешного нахождения по id.
      */
    def find(id: Long): Future[Option[Advert]]
    /**
      * Поиск объявлений, созданных пользователем с заданным id.
      *
      * @param id идентификационный номер пользователя.
      * @return список объявлений, созданных пользователем с заданным id.
      */
    def findUserAdverts(id: Long): Future[List[Advert]]
    /**
      * Удаление объявления по id.
      *
      * @param id идентификационный номер объявления.
      * @return экземпляр Option, содержащий объявление Advert в случае успешного удаления объявления по id.
      */
    def delete(id: Long): Future[Option[Int]]
    /**
      * Удаление объявления по id.
      *
      * @param advertId идентификационный номер объявления.
      * @param userId   идентификационный номер пользователя.
      * @return экземпляр Option, содержащий объявление Advert в случае успешного удаления объявления по id.
      */
    def delete(advertId: Long, userId: Long): Future[Option[Int]]

}
