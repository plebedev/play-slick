package db

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

/**
  * Base class that provides access to common database operation.
  */
abstract class EntityManager[T <: TableDefinition[E], E <: Entity[E]]
                                      (protected val dbConfigProvider: DatabaseConfigProvider)
                                      (implicit executionContext: ExecutionContext)
                              extends HasDatabaseConfigProvider[JdbcProfile]
{
  import driver.api._

  def tableQuery: TableQuery[T]

  /**
    * Inserts the given entity into a table defined by [[EntityManager.tableQuery]].
    * @param entity an entity to be inserted
    * @return a future whose value is the ID of the inserted record
    */
  def insert(entity: E): Future[E] = {
    val prepersistEntity: E = entity.prePersist
    db.run((tableQuery returning tableQuery.map(_.id)) += prepersistEntity)
      .map(res => prepersistEntity.postPersist(res.get))
  }
}
