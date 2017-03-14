package models

/**
  * Треит Category.
  * ---------------
  *
  * Категория объявлений.
  *
  * @author EMurzakaev@it.ru.
  */
sealed trait Category

/**
  * Категория автомобилей.
  *
  * @param brand марка авто.
  * @param model модель.
  * @param year  год выпуска.
  */
case class Auto(brand: String, model: String, year: Int) extends Category

/**
  * Категория недвижимости.
  *
  * @param houseType тип недвижимости.
  */
case class House(houseType: String) extends Category

/**
  * Категория работы.
  */
case class Work() extends Category

/**
  * Категория прочих объявлений.
  */
case class Other() extends Category