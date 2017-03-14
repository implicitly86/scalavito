package controllers

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import play.api.mvc._
import utils.ErrorHandler.handler
import utils.auth.DefaultEnv

class Application @Inject()(silhouette: Silhouette[DefaultEnv]) extends Controller {

    def index = Action {
        Ok("Nothing to show")
    }

    def lol = silhouette.SecuredAction(handler) {
        Ok
    }

}
