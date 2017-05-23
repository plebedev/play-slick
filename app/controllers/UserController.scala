package controllers

import javax.inject.Inject

import dao.UsersDAO
import models.User
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future

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

    request.body.validate[User].map{
      case (user) => {
        val result = usersDao.insert(User(user.firstName, user.lastName, user.emailAddress, user.password))
        result.map(user => Ok(Json.toJson(user)))
      }
    }.recoverTotal{
      e => Future(BadRequest("Detected error:"+ JsError.toJson(e)))
    }
  }
}
