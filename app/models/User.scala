package models

import java.sql.Timestamp
import java.util.Date

import db.Entity
import org.mindrot.jbcrypt.BCrypt
import play.api.libs.json._
import play.api.libs.functional.syntax._

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

  def postPersist(id: Long): User = {
    this.copy(id = Option(id))
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

  def unapply1(user: User): Option[(Option[Long], String, String, String, Option[Timestamp], Option[Timestamp])] =
    Some((user.id, user.firstName, user.lastName, user.emailAddress, user.createdAt, user.updatedAt))

  implicit val userReads: Reads[User] = (
    (JsPath \ "firstName").read[String] and
      (JsPath \ "lastName").read[String] and
      (JsPath \ "emailAddress").read[String] and
      (JsPath \ "password").read[String]
    )(User.apply(_,_,_,_))

  /*implicit val userWrites: Writes[User] = Json.writes[User]*/

  implicit val userWrites: Writes[User] = (
    (JsPath \ "id").writeNullable[Long] and
      (JsPath \ "lastName").write[String] and
      (JsPath \ "firstName").write[String] and
      (JsPath \ "emailAddress").write[String] and
      (JsPath \ "createdAt").writeNullable[Timestamp] and
      (JsPath \ "updatedAt").writeNullable[Timestamp]
  )(unlift(User.unapply1))

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