package service.group;

import exceptions.NameNotUniqueException;
import model.Group;
import repository.group.GroupRepository;
import repository.group.GroupRepositoryImpl;
import repository.product.ProductRepository;
import repository.product.ProductRepositoryImpl;
import utils.Utils;

import java.util.List;

public class GroupServiceImpl implements GroupService{

    private final GroupRepository groupRepository;

    private final ProductRepository productRepository;

    private static volatile GroupServiceImpl groupService;

    private GroupServiceImpl() {
        this.groupRepository = GroupRepositoryImpl.getInstance();
        this.productRepository = ProductRepositoryImpl.getInstance();
    }

    public static GroupServiceImpl getInstance() {
        GroupServiceImpl service = groupService;
        if (service != null) {
            return service;
        }
        synchronized (GroupServiceImpl.class) {
            if (groupService == null) {
                groupService = new GroupServiceImpl();
            }
            return groupService;
        }
    }

    @Override
    public synchronized Group createGroup(String name, String description) {
        String pName = Utils.processString(name);
        String pDescription = Utils.processString(description);
        validateParams(pName, pDescription);
        Group g = new Group(pName, pDescription);
        return groupRepository.save(g);
    }

    @Override
    public synchronized Group updateGroup(Long groupId, String name, String description) {
        String pName = Utils.processString(name);
        String pDescription = Utils.processString(description);
        validateParams(pName, pDescription);
        Group group = getGroupById(groupId);
        group.setDescription(pDescription);
        group.setName(pName);
        return groupRepository.update(group);
    }

    @Override
    public synchronized void deleteGroup(Long groupId) {
        groupRepository.delete(groupId);
        productRepository.deleteOfGroup(groupId);
    }

    @Override
    public synchronized Group getGroupById(Long groupId) {
        return groupRepository.getById(groupId);
    }

    @Override
    public synchronized List<Group> getAllGroups() {
        return groupRepository.getAll();
    }

    @Override
    public synchronized void deleteAllGroups() {
        getAllGroups().stream().map(Group::getId).forEach(productRepository::deleteOfGroup);
        groupRepository.deleteAll();
    }

    private void validateNameIsUnique(String name) {
        if (groupRepository.existsWithName(name)) {
            throw new NameNotUniqueException();
        }
    }

    private void validateParams(String pName, String pDescription) {
        validateNameIsUnique(pName);
        Utils.validateString(pName, true, 100);
        Utils.validateString(pDescription, false, 255);
    }




}


