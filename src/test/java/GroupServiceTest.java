import model.Group;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import repository.group.GroupRepository;
import repository.group.GroupRepositoryImpl;
import service.group.GroupService;
import service.group.GroupServiceImpl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GroupServiceTest {

    private final GroupService groupService = GroupServiceImpl.getInstance();

    @AfterEach
    public void removeAll() {
        groupService.deleteAllGroups();
    }

    @Test
    public void createGroup_whenOneThread_thenShouldExist() {
        Group g = groupService.createGroup("Group", "New Group");
        Assertions.assertDoesNotThrow(() -> {
            groupService.getGroupById(g.getId());
        });
    }

    @Test
    public void createDeleteGroup_whenOneThread_thenShouldThrow() {
        Group g = groupService.createGroup("Group", "New Group");
        groupService.deleteGroup(g.getId());
        Assertions.assertThrows(Exception.class, () -> groupService.getGroupById(g.getId()+1));
    }


    @Test
    public void createEqualGroups_whenFewThreads_thenOnlyOneCreated() {

        int times = 20;
        int nThreads = 5;

        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        for (int i = 0; i < times; i++) {
            executorService.execute(() -> {
                try {
                    groupService.createGroup("Group", "New Group");
                } catch (RuntimeException e) {

                }
            });
        }
        executorService.shutdown();
        Assertions.assertEquals(1, groupService.getAllGroups().size());
    }

    @Test
    public void updateGroupCreateEqualGroups_whenFewThreads_thenOnlyTwoExist() {
        Group g = groupService.createGroup("Group", "New Group");

        int times = 20;
        int nThreads = 5;

        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        executorService.execute(() -> groupService.updateGroup(g.getId(),"Group1", "NewDesc"));
        for (int i = 0; i < times; i++) {
            executorService.execute(() -> {
                try {
                    groupService.createGroup("Group", "New Group");
                } catch (RuntimeException e) {

                }
            });
        }
        executorService.shutdown();
        Assertions.assertEquals(2, groupService.getAllGroups().size());
    }


    @Test
    public void createGroups_whenManyThreads_thenAllSaved() throws InterruptedException {
        GroupRepository repository = GroupRepositoryImpl.getInstance();
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 1000; i++) {
            executorService.execute(() -> repository.save(new Group("Group", "New Group")));
        }
        executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
        Assertions.assertEquals(1000, groupService.getAllGroups().size());
    }




}
