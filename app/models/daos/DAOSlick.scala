package models.daos

import play.api.db.slick.HasDatabaseConfigProvider
import slick.driver.JdbcProfile

/**
  * Траит DAOSlick.
  * ---------------
  *
  * @author EMurzakaev@it.ru.
  */
trait DAOSlick extends DBTableDefinitions with HasDatabaseConfigProvider[JdbcProfile]