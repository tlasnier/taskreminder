package models;

import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by Thibault on 22/01/14.
 */
@Entity
public class Tag extends Model{
    @Column(unique = true)
    @Required
    private String name;

    public Tag(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void createIfNotExists() {
        Tag inBase = Tag.find("byName", name).first();
        if (inBase == null)
            save();
    }

}
