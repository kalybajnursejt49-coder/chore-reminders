package edu.student.chores;

public class OneTimeChoreNotice extends ChoreNotice {
    public OneTimeChoreNotice(NoticeSender sender) {
        super(sender);
    }

    @Override
    public void publish(String destination, Notice notice) throws DeliveryException {
        Notice oneTime = new Notice(notice.chore(), notice.dueDate(), notice.person(), notice.priority(), "ONCE");
        sender.send(destination, oneTime);
    }
}
