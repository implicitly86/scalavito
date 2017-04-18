package services

import java.util.UUID

import models.Advert

import scala.concurrent.Future

/**
  * Траит AdvertService.
  * --------------------
  *
  * Содержит методы работы с объявлениями.
  *
  * @author EMurzakaev@it.ru.
  */
trait AdvertService {

    /**
      * Сохранить объявление.
      *
      * @param advert объявление.
      * @return сохраненное объявление.
      */
    def save(advert: Advert): Future[Advert]

    /**
      * Найти объявление по идентификационному номеру.
      *
      * @param id идентификационный номер объявления.
      * @return объявление, обернутое в Option
      */
    def find(id: UUID): Future[Option[Advert]]

}
