package services

import java.util.UUID

import models.{Advert, Category}

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

    /**
      * Получить список объявлений в заданной категории.
      *
      * @param category категория объявлений.
      * @return список объявлений в заданной категории.
      */
    def advertsWithCategory(category: Category): Future[List[Advert]]

}
