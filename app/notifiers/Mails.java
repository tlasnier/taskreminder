package notifiers;

import models.User;
import play.mvc.*;

public class Mails extends Mailer {

    public static void welcome(User user) {
        setSubject("Welcome %s", user.getUsername());
        addRecipient(user.getEmail());
        setFrom("Task Reminder <task.reminder.notifier@gmail.com>");
        send(user);
    }
}