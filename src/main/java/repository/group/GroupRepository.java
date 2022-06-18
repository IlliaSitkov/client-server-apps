package repository.group;

import model.Group;

import java.util.List;

public interface GroupRepository {

    Group save(Group g);

    Group update(Group g);

    void delete(Long id);

    Group getById(Long id);

    List<Group> getAll();

    void deleteAll();

    boolean existsWithName(String name);


}
