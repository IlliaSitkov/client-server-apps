package repository.group;

import model.Group;
import utils.Utils;

import java.util.List;
import java.util.Objects;

public class GroupRepositoryImpl implements GroupRepository {

    private List<Group> groups;

    private static volatile GroupRepositoryImpl groupRepository;

    private GroupRepositoryImpl() {
        this.groups = Utils.getEmptySynchronizedList();
    }

    public static GroupRepositoryImpl getInstance() {
        GroupRepositoryImpl repository = groupRepository;
        if (repository != null) {
            return repository;
        }
        synchronized (GroupRepositoryImpl.class) {
            if (groupRepository == null) {
                groupRepository = new GroupRepositoryImpl();
            }
            return groupRepository;
        }
    }


     /*
     some methods are not synchronized as they make
     use of already synchronized list
     (not all methods of the synchronized list are synchronized though)
     */

    @Override
    public Group save(Group g) {
        groups.add(g);
        return g;
    }

    @Override
    public Group update(Group g) {
        delete(g.getId());
        save(g);
        return g;
    }

    @Override
    public synchronized void delete(Long id) {
        groups.removeIf(g -> Objects.equals(g.getId(), id));
    }

    @Override
    public synchronized Group getById(Long id) {
        return groups.stream().filter(g -> Objects.equals(g.getId(), id)).findFirst().orElseThrow();
    }

    @Override
    public synchronized List<Group> getAll() {
        return groups;
    }

    @Override
    public synchronized void deleteAll() {
        groups = Utils.getEmptySynchronizedList();
    }

    @Override
    public synchronized boolean existsWithName(String name) {
        return groups.stream().anyMatch(g -> g.getName().equalsIgnoreCase(name));
    }
}
