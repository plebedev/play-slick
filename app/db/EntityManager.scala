package db

import play.api.Logger
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
  def persist(entity: E): Future[E] = {
    entity.id match {
      case None => insert(entity)
      case Some(_) => update(entity)
    }

  }

  /**
    * Returns an entity with a given id
    */
  def find(id: Long): Future[Option[E]] = {
    db.run(tableQuery.filter(_.id === id).result.headOption)
  }

  private def insert(entity: E): Future[E] = {
    val preinserttEntity: E = entity.prePersist

    logger.error("Insert: createdAt: " + preinserttEntity.createdAt.get.toString)
    logger.error("Insert: updatedAt: " + preinserttEntity.updatedAt.get.toString)
    db.run((tableQuery returning tableQuery.map(_.id)) += preinserttEntity)
      .map(res => preinserttEntity.postPersist(res.get))
  }

  private def update(entity: E): Future[E] = {
    val preupdateEntity: E = entity.preUpdate
    logger.error("Update: updatedAt: " + preupdateEntity.updatedAt.get.toString)
    db.run(tableQuery.update(preupdateEntity)).map(res => preupdateEntity)
  }

  val logger: Logger = Logger(this.getClass())
}
