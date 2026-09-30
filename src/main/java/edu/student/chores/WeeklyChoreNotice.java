package edu.student.chores;

public class WeeklyChoreNotice extends ChoreNotice {
    public WeeklyChoreNotice(NoticeSender sender) {
        super(sender);
    }

    @Override
    public void publish(String destination, Notice notice) throws DeliveryException {
        Notice weekly = new Notice(notice.chore(), notice.dueDate(), notice.person(), notice.priority(), "WEEKLY");
        sender.send(destination, weekly);
    }
}
