package net.minecraft.game.entity.mob.ai;

public class Task {
    public Action action;
    final TaskList tasks;
    public int priority;

    public Task(TaskList tasks, int priority, Action task) {
        this.tasks = tasks;
        this.priority = priority;
        this.action = task;
    }
}
