package edu.student.chores;

public abstract class ChoreNotice {
    protected final NoticeSender sender;

    protected ChoreNotice(NoticeSender sender) {
        this.sender = sender;
    }

    public abstract void publish(String destination, Notice notice) throws DeliveryException;
}
