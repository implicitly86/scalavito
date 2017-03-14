package models

import java.util.UUID

import play.api.libs.json.{Json, OFormat}

/**
  * Класс Advert.
  * -------------
  * Модель объявления в системе.
  *
  * @author EMurzakaev@it.ru.
  */
case class Advert(id: UUID, author: String, category: Category, title: String, description: String)

/**
  * Объект-компаньен.
  *
  * @author EMurzakaev@it.ru.
  */
object Advert {
    implicit val format: OFormat[Advert] = Json.format[Advert]
}