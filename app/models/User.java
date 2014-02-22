package models;

import models.exception.BadPasswordException;
import models.exception.NoUserFoundException;
import org.apache.commons.lang.RandomStringUtils;
import org.mindrot.jbcrypt.BCrypt;
import play.data.validation.*;
import play.db.jpa.Model;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Created by Thibault on 08/01/14.
 */

@Entity
public class User extends Model{
    @Unique(message = "This email is already linked to an account")
    @Column(unique = true)
    @Email(message = "This must be a valid email")
    @Required
    private String email;

    @Unique(message = "This username is already used")
    @Column(unique = true)
    @Required
    @MinSize(value = 3, message = "Username min length is 3")
    @MaxSize(value = 15, message = "Username max length is 15")
    private String username;

    private String hashedPassword;

    private String salt;

    private String firstname;

    private String lastname;

    public static User connect(String username, String password) throws NoUserFoundException, BadPasswordException {
        User user = User.find("byUsername", username).first();
        if (user == null) {
            throw new NoUserFoundException("User " + username + " was not found");
        }

        if (BCrypt.checkpw(password, user.getHashedPassword())) {
            return user;
        }
        else {
            throw new BadPasswordException("Wrong password for user " + username);
        }
    }

    public static void register(User newUser, String password) {
        newUser.setSalt(BCrypt.gensalt());
        newUser.setHashedPassword(BCrypt.hashpw(password, newUser.getSalt()));
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}