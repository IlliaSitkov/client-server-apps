package repository.group;

import exceptions.GroupNotFoundException;
import model.Group;
import utils.Utils;

import java.util.List;
import java.util.Objects;

public class GroupRepositoryFakeImpl implements GroupRepository {

    private List<Group> groups;

    private static volatile GroupRepositoryFakeImpl groupRepository;

    private GroupRepositoryFakeImpl() {
        this.groups = Utils.getEmptySynchronizedList();
    }

    public static GroupRepositoryFakeImpl getInstance() {
        GroupRepositoryFakeImpl repository = groupRepository;
        if (repository != null) {
            return repository;
        }
        synchronized (GroupRepositoryFakeImpl.class) {
            if (groupRepository == null) {
                groupRepository = new GroupRepositoryFakeImpl();
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
        return groups.stream().filter(g -> Objects.equals(g.getId(), id)).findFirst().orElseThrow(() -> new GroupNotFoundException(id));
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

    @Override
    public boolean existsWithId(Long id) {
        return groups.stream().anyMatch(g -> g.getId().equals(id));
    }
}
