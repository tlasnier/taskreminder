package controllers;

import models.Task;
import models.User;
import play.data.validation.Valid;
import play.mvc.Before;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Thibault on 25/02/14.
 */
public class Tasks extends AbstractController {
    @Before(only = {"edit", "delete"})
    public static void checkOwner() {
        Long id = params.get("id", Long.class);
        Task task = Task.findById(id);
        if (!task.getAuthor().equals(getConnectedUser()))
            forbidden("You cannot edit or delete a task that is not yours!");

    }

    public static void showUsersTasks() {
        User user = getConnectedUser();
        List<Task> tasks = Task.find("byAuthor", user).fetch();
        render(tasks);
    }

    public static void createTask(@Valid Task task) {
        User user = getConnectedUser();
        task.setAuthor(user);
        task.setCreationDate(Calendar.getInstance().getTime());
        task.save();
    }

    public static void edit(Long id) {

    }

    public static void delete(Long id) {

    }

}
