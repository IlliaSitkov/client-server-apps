package service.group;

import model.Group;

import java.util.List;

public interface GroupService {

    Group createGroup(String name, String description);

    Group updateGroup(Long groupId, String name, String description);

    void deleteGroup(Long groupId);

    Group getGroupById(Long groupId);

    List<Group> getAllGroups();

    void deleteAllGroups();
}
