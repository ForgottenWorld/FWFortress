package me.architetto.fwfortress.task;

public class TaskService {

    private static TaskService taskService;

    private int positionTaskID;

    private TaskService() {
        if(taskService != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static TaskService getInstance() {
        if(taskService == null) {
            taskService = new TaskService();
        }
        return taskService;
    }

    public void schedulePositionTask() {
        this.positionTaskID = PositionTask.schedulePlayerPositionStalker();
    }
}
