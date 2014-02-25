package controllers;

import models.User;
import org.mindrot.jbcrypt.BCrypt;
import play.data.validation.Equals;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.data.validation.Valid;

/**
 * Created by Thibault on 25/02/14.
 */
public class Users extends AbstractController {
    public static void editAccount() {
        User current = getConnectedUser();
        render(current);
    }

    public static void editInfo(User user) {
        User olduser = getConnectedUser();
        //to prevent from validation errors
        user.id = olduser.getId();
        if (!olduser.getEmail().equals(user.getEmail()))
            validation.valid(user);
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
        }

        else {
            olduser.setEmail(user.getEmail());
            olduser.setFirstname(user.getFirstname());
            olduser.setLastname(user.getLastname());
            olduser.save();
            flash.success("Your profile has been saved!");
            flash.keep();
        }
        editAccount();
    }

    public static void changePassword(@Required(message = "You must precise your old password") String oldPassword,
                                      @MinSize(value = 6, message = "Must be at least 6 chars") String newPassword,
                                      @Equals(value = "newPassword", message = "Must be the same")String confirmation) {
        User user = getConnectedUser();
        if (!BCrypt.checkpw(oldPassword, user.getHashedPassword()))
            validation.addError("oldPassword", "Wrong password!");
        if (validation.hasErrors()) {
            //Do not flash params!
        }

        else {
            user.setHashedPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
            user.save();
            flash.success("Your profile has been saved!");
            flash.keep();
        }
        editAccount();
    }
}
