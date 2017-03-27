package models

import play.api.libs.json.{Json, OFormat}

/**
  * Класс Advert.
  * -------------
  * Модель объявления в системе.
  *
  * @author EMurzakaev@it.ru.
  */
case class Advert(id: Option[Long], authorID: Long, title: String, description: String)

/**
  * Объект-компаньен.
  *
  * @author EMurzakaev@it.ru.
  */
object Advert {
    implicit val format: OFormat[Advert] = Json.format[Advert]
}