package controllers;

import models.User;
import play.mvc.Before;
import play.mvc.Controller;

/**
 * Created by Thibault on 25/02/14.
 */
public abstract class AbstractController extends Controller {

    @Before(unless = {"Application.index", "Application.login", "Application.register"})
    public static void checkAuthentication () {
        if (!isUserLoggedIn())
            Application.index();
    }

    @Before(only = "Application.index")
    public static void checkAuthenticationBeforeIndex () {
        if (isUserLoggedIn()) {
            Application.homepage();
        }
    }

    public static boolean isUserLoggedIn() {
        return session.contains("user") && (User.find("byUsername", session.get("user")).first() != null);
    }

    public static User getConnectedUser() {
        if (!isUserLoggedIn())
            return null;
        else
            return User.find("byUsername", session.get("user")).first();
    }
}
