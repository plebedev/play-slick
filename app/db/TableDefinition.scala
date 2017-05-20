package db

import java.sql.Timestamp

import slick.driver.MySQLDriver.api._

/**
  * Base class for table definitions that can store [[db.Entity entity]] objects.
  */
abstract class TableDefinition[T <: Entity[T]](tag: Tag, tableName: String) extends Table[T](tag, tableName) {
  def id = column[Option[Long]]("id", O.PrimaryKey,O.AutoInc)
  def createdAt = column[Option[Timestamp]]("created_at")
  def updatedAt = column[Option[Timestamp]]("updated_at")
}
