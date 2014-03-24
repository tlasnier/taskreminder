package controllers;

import models.User;
import org.mindrot.jbcrypt.BCrypt;
import play.data.validation.Equals;
import play.data.validation.MinSize;
import play.data.validation.Required;

import java.util.*;

/**
 * Created by Thibault on 25/02/14.
 */
public class Users extends AbstractController {
    public static void editAccount() {
        User current = getConnectedUser();
        render(current);
    }

    public static boolean isFriend(String username) {
        User user = User.find("byUsername", username).first();
        User connectedUser = getConnectedUser();
        if (user == null)
            return false;
        else
            return connectedUser.getFriends().contains(user);
    }

    public static void addFriend(String username) {
        User user = User.find("byUsername", username).first();
        if (user == null)
            notFound("User : " + username + " does not exist");
        User connectedUser = getConnectedUser();
        connectedUser.addFriend(user);
        connectedUser.save();
        Tasks.showOthersTasks(username);
    }

    public static void removeFriend(String username) {
        User user = User.find("byUsername", username).first();
        if (user == null)
            notFound("User : " + username + " does not exist");
        User connectedUser = getConnectedUser();
        connectedUser.removeFriend(user);
        connectedUser.save();
        Tasks.showOthersTasks(username);
    }

    public static void showFriends(String search) {
        User user = getConnectedUser();

        List<String> searchResult = null;
        if (!"".equals(search))
            searchResult = User.find("select username from User where username like ? order by username", "%"+search+"%").fetch();

        List<User> friends = new ArrayList<User>(user.getFriends());
        Collections.sort(friends, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o1.getUsername().compareTo(o2.getUsername());
            }
        });

        render(friends, searchResult);
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
