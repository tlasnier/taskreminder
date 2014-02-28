package models;

import models.constants.TaskPriority;
import models.constants.TaskVisibility;
import play.data.binding.As;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

/**
 * Created by Thibault on 08/01/14.
 */
@Entity
public class Task extends Model{
    @Required
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    @Enumerated(EnumType.STRING)
    private TaskVisibility visibility;

    @ManyToOne
    private User author;

    @As("dd/MM/yyyy")
    private Date creationDate;

    @As("dd/MM/yyyy")
    private Date dueDate;

    @ManyToMany
    private Set<Tag> tags;

    private boolean completed;

    public boolean isDueDatePassed() {
        return (dueDate != null) && (dueDate.before(Calendar.getInstance().getTime()));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public TaskVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(TaskVisibility visibility) {
        this.visibility = visibility;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
