package dao

import javax.inject.{Inject, Singleton}

import db.{EntityManager, TableDefinition}
import models.User
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.MySQLDriver.api._

import scala.concurrent.ExecutionContext

class UsersTable(tag: Tag) extends TableDefinition[User](tag, "USER") {

  def firstName = column[String]("first_name")
  def lastName = column[String]("last_name")
  def email = column[String]("email_address")
  def password = column[String]("password")

  override def * =
    (id, firstName, lastName, email, password, createdAt, updatedAt) <> ((User).tupled, User.unapply)
}

@Singleton
class UsersDAO @Inject() (override val dbConfigProvider: DatabaseConfigProvider)
                         (implicit executionContext: ExecutionContext)
  extends EntityManager[UsersTable, User](dbConfigProvider) {


  def tableQuery = TableQuery[UsersTable]

}
