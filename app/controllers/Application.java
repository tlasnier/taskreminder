package controllers;

import models.User;
import models.exception.BadPasswordException;
import models.exception.NoUserFoundException;
import play.data.validation.Email;
import play.data.validation.Equals;
import play.data.validation.MinSize;
import play.data.validation.Required;
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
        if(validation.hasErrors()) {
            validation.keep();
            index();
        }

        try {
            User.connect(username, password);
        } catch (NoUserFoundException e) {
            System.out.println("ERROR : " + e.getMessage());
            index();
        } catch (BadPasswordException e) {
            System.out.println("ERROR : " + e.getMessage());
            index();
        }
        session.put("user", username);

        homepage();
    }

    public static void register(@Required @Email String email, @Required @MinSize(3) String username,
                                @Required @MinSize(6) String password, @Equals("password") String confirmation,
                                String firstname, String lastname) {

        if (validation.hasErrors()) {
            validation.keep();
            index();
        }

        if (User.find("byEmail", email).first() != null || User.find("byUsername", username).first() != null ) {
            //TODO what if there is another user with same email?
            validation.equals("","abcd");
            validation.keep();
            System.out.println("Already existing user");
            index();
        }

        else {
            User user = new User();
            user.setEmail(email);
            user.setUsername(username);
            user.setFirstname(firstname);
            user.setLastname(lastname);
            User.register(user, password);
            user.save();

            session.put("user", username);

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