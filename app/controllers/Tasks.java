package controllers;

import models.Tag;
import models.Task;
import models.User;
import models.constants.TaskVisibility;
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
    @Before(only = {"editTask", "editionForm", "delete", "complete"})
    public static void checkOwner() {
        Long id = params.get("id", Long.class);
        Task task = Task.findById(id);
        if (task == null)
            notFound("This task does not exist!");
        if (!task.getAuthor().equals(getConnectedUser()))
            forbidden("You cannot edit or delete a task that is not yours!");

    }

    public static void creationForm() {
        render();
    }

    public static void complete(long id) {
        Task task = Task.findById(id);
        task.setCompleted(true);
        task.save();
        showUsersTasks();
    }

    public static void showOthersTasks(String username) {
        User user = User.find("byUsername", username).first();
        if (user == null)
            notFound("User : " + username + " does not exist");
        List<Task> tasks = Task.find("byAuthorAndVisibility", user, TaskVisibility.PUBLIC).fetch();
        boolean isFriend = Users.isFriend(username);

        render(username, tasks, isFriend);
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

        Set<Tag> tagSet = new HashSet<Tag>();
        for(String s : extractTagsAsString(tags)) {
            Tag tag = Tag.createIfNotExists(s);
            tagSet.add(tag);
        }
        task.setTags(tagSet);

        task.save();
        showUsersTasks();
    }

    public static void editionForm(Long id) {
        Task task = Task.findById(id);
        String tags = tagsToString(task.getTags());
        render(task, tags);
    }

    public static void editTask(Long id, @Valid Task task, boolean completed, String tags) {
        if(validation.hasErrors()) {
            params.flash();
            validation.keep();
            editionForm(task.getId());
        }

        Task oldTask = Task.findById(id);

        oldTask.setTitle(task.getTitle());

        oldTask.setDescription(task.getDescription());

        oldTask.setDueDate(task.getDueDate());

        oldTask.setPriority(task.getPriority());

        oldTask.setCompleted(completed);

        Set<Tag> tagSet = new HashSet<Tag>();
        for(String s : extractTagsAsString(tags)) {
            Tag tag = Tag.createIfNotExists(s);
            tagSet.add(tag);
        }
        oldTask.setTags(tagSet);

        oldTask.save();
        showUsersTasks();
    }

    public static void delete(Long id) {
        Task t = Task.findById(id);
        t._delete();
        flash.success("Your task" + t.getTitle() + "has been deleted!");
        flash.keep();
        showUsersTasks();
    }

    public static Set<String> extractTagsAsString(String tags) {
        Set<String> stringSet = new HashSet<String>();
        Pattern pattern = Pattern.compile("([a-zA-Z\\d]+)(\\s*,\\s*([a-zA-Z\\d]+))?");
        Matcher matcher = pattern.matcher(tags);
        if (matcher.find()) {
            stringSet.add(matcher.group(1));
            if (matcher.group(3) != null)
                stringSet.add(matcher.group(3));
        }
        while (matcher.find())
            stringSet.add(matcher.group(1));

        return stringSet;
    }

    public static String tagsToString(Set<Tag> tags) {
        StringBuilder builder = new StringBuilder();
        for (Tag tag : tags) {
            builder.append(tag.getName()).append(", ");
        }
        if (tags.size() > 1)
            builder.delete(builder.length()-2, builder.length());

        return builder.toString();
    }

}
