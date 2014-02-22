package controllers;

import models.User;
import models.exception.BadPasswordException;
import models.exception.NoUserFoundException;
import play.data.validation.*;
import play.mvc.Before;
import play.mvc.Controller;

public class Application extends Controller {

    @Before(unless = {"index", "login", "register"})
    public static void checkAuthentication () {
        if (!isUserLoggedIn())
            index();
    }

    @Before(only = "index")
    public static void checkAuthenticationBeforeIndex () {
        if (isUserLoggedIn()) {
            homepage();
        }
    }

    public static boolean isUserLoggedIn() {
        return session.contains("user");
    }

    public static void index() {
        render();
    }

    public static void login(@Required String username, @Required String password) {
        flash.now("form", "login");
        flash.keep();

        if(validation.hasErrors()) {
            validation.keep();
            //render("Application/index.html");
            index();
        }

        try {
            User.connect(username, password);
        }
        catch (NoUserFoundException e) {
            System.out.println("ERROR : " + e.getMessage());
            flash.error(e.getMessage());
            index();
        }
        catch (BadPasswordException e) {
            System.out.println("ERROR : " + e.getMessage());
            flash.error(e.getMessage());
            index();
        }

        session.put("user", username);
        flash.success("You've been logged!");
        homepage();
    }

    public static void register(@Valid User user,
                                @Required @MinSize(6) String password, @Equals("password") String confirmation) {
        flash.now("form", "register");
        flash.keep();

        if (validation.hasErrors()) {
            validation.keep();
            index();
        }

        if (User.find("byEmail", user.getEmail()).first() != null ){
            //TODO separate between email an username
            validation.equals("", "abcd");
            validation.keep();
            System.out.println("Already existing user");
            index();
        }
        if (User.find("byUsername", user.getUsername()).first() != null ) { 
        }

        else {
            User.register(user, password);
            user.save();

            session.put("user", user.getUsername());

            homepage();
        }
    }

    public static void logout() {
        session.clear();
        index();
    }

    public static void homepage() {
        User user = User.find("byUsername", session.get("user")).first();
        render(user);
    }

}