package db

import java.sql.Timestamp

/**
  * Base class for entities that can be stored in DB.
  */
trait Entity[T <: Entity[T]] {

  val id: Option[Long]

  val createdAt: Option[Timestamp]

  val updatedAt: Option[Timestamp]

  /**
    * Executed before this entity is persisted.
    */
  def prePersist: T

  /**
    * Executed before the database UPDATE operation for this entity.
    */
  def preUpdate: T
}