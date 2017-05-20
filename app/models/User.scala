package models

import java.sql.Timestamp
import java.util.Date

import db.Entity
import org.mindrot.jbcrypt.BCrypt

case class User (override val id: Option[Long],
                firstName: String,
                lastName: String,
                emailAddress: String,
                password: String,
                override val createdAt: Option[Timestamp],
                override val updatedAt: Option[Timestamp])
  extends Entity[User] {

  /**
    * @inheritdoc
    */
  def prePersist: User = {
    val ts = Option(new Timestamp(new Date().getTime))
    this.copy(createdAt = ts, updatedAt = ts)
  }

  /**
    * @inheritdoc
    */
  def preUpdate: User = {
    this.copy(updatedAt = Option(new Timestamp(new Date().getTime)))
  }
}

object User extends ((Option[Long], String, String, String, String, Option[Timestamp], Option[Timestamp]) => User){

  def apply(firstName: String, lastName: String, emailAddress: String, password: String): User = {

    new User(None, firstName, lastName, emailAddress, hashPassword(password), None, None)
  }

  /**
    * Returns true if the given password matches the given hash, false otherwise.
    */
  def validate(password: String, user: User): Boolean = {
    BCrypt.checkpw(password, user.password)
  }

  /**
    * Hashes the given password.
    */
  private def hashPassword(password: String): String = {
    BCrypt.hashpw(password, BCrypt.gensalt())
  }
}