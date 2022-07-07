package repository.group;

import model.Group;

import java.util.List;
import java.util.Optional;

public interface GroupRepository {

    Group save(Group g);

    Optional<Group> update(Group g);

    boolean delete(Long id);
    
    boolean deleteByName(String name);
    
    Optional<Group> getById(Long id);

    List<Group> getAll();

    void deleteAll();

    boolean existsWithName(String name);

    boolean existsWithId(Long id);


}
