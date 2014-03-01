package controllers;

import models.Tag;
import models.Task;
import models.User;
import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.util.StringUtil;
import play.data.validation.Valid;
import play.mvc.Before;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static void creationForm() {
        render();
    }

    public static void showUsersTasks() {
        User user = getConnectedUser();
        List<Task> tasks = Task.find("byAuthor", user).fetch();
        render(tasks);
    }

    public static void createTask(@Valid Task task, boolean completed, String tags) {
        if(validation.hasErrors()) {
            params.flash();
            validation.keep();
            creationForm();
        }

        User user = getConnectedUser();
        task.setAuthor(user);

        task.setCreationDate(Calendar.getInstance().getTime());

        task.setCompleted(completed);

        Set<String> stringSet = new HashSet<>();
        Set<Tag> tagSet = new HashSet<>();
        Pattern pattern = Pattern.compile("([a-zA-Z\\d]+)(\\s*,\\s*([a-zA-Z\\d]+))?");
        Matcher matcher = pattern.matcher(tags);
        if (matcher.find()) {
            stringSet.add(matcher.group(1));
            if (matcher.group(3) != null)
                stringSet.add(matcher.group(3));
        }
        while (matcher.find())
            stringSet.add(matcher.group(1));
        for(String s : stringSet) {
            Tag tag = new Tag(s);
            tag.createIfNotExists();
            tagSet.add(tag);
        }
        task.setTags(tagSet);

        task.save();
        showUsersTasks();
    }

    public static void edit(Long id) {

    }

    public static void delete(Long id) {

    }

}
