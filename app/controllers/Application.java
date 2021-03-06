package controllers;

import models.User;
import models.exception.BadPasswordException;
import models.exception.NoUserFoundException;
import notifiers.Mails;
import play.data.validation.*;

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
        } catch (NoUserFoundException e) {
            System.out.println("ERROR : " + e.getMessage());
            flash.error(e.getMessage());
            index();
        } catch (BadPasswordException e) {
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
            index();
        }

        else {
            User.register(user, password);
            user.save();

            Mails.welcome(user);
            //TODO account validation

            session.put("user", user.getUsername());

            homepage();
        }
    }

    public static void logout() {
        session.clear();
        index();
    }

    public static void homepage() {
        Tasks.showUsersTasks();
    }

}