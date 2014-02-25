package controllers;

import models.User;
import models.exception.BadPasswordException;
import models.exception.NoUserFoundException;
import play.data.validation.*;
import play.mvc.Before;
import play.mvc.Controller;

public class Application extends AbstractController {

    public static void index() {
        render();
    }

    public static void login(@Required String username, @Required String password) {
        flash.now("form", "login");
        flash.keep();

        if(validation.hasErrors()) {
            validation.keep();
            index();
        }

        try {
            User.connect(username, password);
        }
        catch (NoUserFoundException | BadPasswordException e) {
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
            params.flash();
            validation.keep();
            render("@index");
        }

        else {
            User.register(user, password);
            user.save();

            //TODO : send an email.

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