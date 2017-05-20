package controllers

import javax.inject._

import dao.UsersDAO
import models.User
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() (
                                 usersDao: UsersDAO
                               ) extends Controller {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action.async { implicit request =>
    import scala.concurrent.ExecutionContext.Implicits.global

    val user = User("a", "b", "c", "d")
    val result = usersDao.insert(user)
    result.map(id => Ok(views.html.index("Inserted user with id: " + id)))
  }
}
