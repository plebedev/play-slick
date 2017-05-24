package controllers

import javax.inject.Inject

import dao.UsersDAO
import models.User
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Controller, Result}
import play.api.Logger

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * The controller to handle CRUD operations for [[models.User]].
  */
class UserController @Inject() (
                                 usersDao: UsersDAO
                               ) extends Controller {

  /**
    * @return
    */
  def create = Action.async(parse.json) { implicit request =>
    import scala.concurrent.ExecutionContext.Implicits.global

    request.body.validate[User].map {
      case (user) => {
        val result = usersDao.persist(User(user.firstName, user.lastName, user.emailAddress, user.password))
        result.map(user => Ok(Json.toJson(user)))
      }.recover {
        case e => {
          logger.error("Error inserting user.", e)
          BadRequest("Error inserting user.")
        }
      }
    }.recoverTotal{
      e => Future(BadRequest("Detected error:"+ JsError.toJson(e)))
    }
  }

  def update(id: Long) = Action.async(parse.json) { implicit request =>
    import scala.concurrent.ExecutionContext.Implicits.global

    request.body.validate[User].map {
      case (user) => {
        val currentUser: Option[User] = Await.result(usersDao.find(id), Duration.Inf)

        val result: Future[Result] = currentUser match {
          case None => Future(NotFound("User not found."))
          case Some(u) => {
              usersDao.persist(User(user.firstName, user.lastName, user.emailAddress, user.password)
                .copy(id = Option(id), createdAt = u.createdAt))
                .map(user => Ok(Json.toJson(user)))}
                .recover {
                  case e => {
                    logger.error("Error updating user.", e)
                    BadRequest("Error updating user.")
                  }
                }
        }

        result
      }
    }.recoverTotal{
      e => Future(BadRequest("Detected error:"+ JsError.toJson(e)))
    }
  }

  val logger: Logger = Logger(this.getClass())
}
