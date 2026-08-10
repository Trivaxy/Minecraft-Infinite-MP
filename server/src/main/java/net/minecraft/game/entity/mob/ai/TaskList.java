package net.minecraft.game.entity.mob.ai;

import java.util.ArrayList;

public class TaskList {
    public ArrayList<Task> tasks = new ArrayList<>();
    private ArrayList<Task> runningTasks = new ArrayList<>();

    public void addTask(int pirority, Action task) {
        this.tasks.add(new Task(this, pirority, task));
    }

    public void removeTask(int task) {
        this.tasks.remove(task);
    }

    public void onUpdateTasks() {
        ArrayList<Task> taskList = new ArrayList<>();

        for (Task task : this.tasks) {
            boolean alreadyHasTask = this.runningTasks.contains(task);
            if (alreadyHasTask) {
                if (this.canUpdateTasks(task) && task.action.continueExecuting()) {
                    continue;
                }

                task.action.stop();
                this.runningTasks.remove(task);
            }

            if (this.canUpdateTasks(task) && task.action.canExecute()) {
                taskList.add(task);
                this.runningTasks.add(task);
            }
        }

        for (Task currTask : taskList) {
            currTask.action.start();
        }

        for (Task currTask : this.runningTasks) {
            currTask.action.tick();
        }
    }

    private boolean canUpdateTasks(Task task) {
        for (Task currTask : this.tasks) {
            if (currTask != task) {
                if (task.priority >= currTask.priority) {
                    if (this.runningTasks.contains(currTask) && !this.areTasksCompatible(task, currTask)) {
                        return false;
                    }
                } else if (this.runningTasks.contains(currTask) && !currTask.action.isContinuous()) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean areTasksCompatible(Task task1, Task task2) {
        return (task1.action.getMutexBits() & task2.action.getMutexBits()) == 0;
    }
}
