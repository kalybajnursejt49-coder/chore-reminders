package edu.student.chores;

public class OneTimeChoreNotice extends ChoreNotice {
    public OneTimeChoreNotice(NoticeSender sender) {
        super(sender);
    }

    @Override
    public void publish(String destination, Notice notice) throws DeliveryException {
        sender.send(destination, notice);
    }
}
