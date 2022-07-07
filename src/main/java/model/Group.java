package model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import utils.Utils;

@Getter
@EqualsAndHashCode
public class Group {

    private final Long id;

    @Setter
    private String name;
    @Setter
    private String description;


    public Group(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Group(String name, String description) {
        this(Utils.generateId(),name,description);
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
