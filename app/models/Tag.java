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

    public static Tag createIfNotExists(String tagName) {
        Tag inBase = Tag.find("byName", tagName).first();
        if (inBase == null)
            return new Tag(tagName).save();
        else
            return inBase;
    }

}
