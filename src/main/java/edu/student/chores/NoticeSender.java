package edu.student.chores;

public interface NoticeSender {
    void send(String destination, Notice notice) throws DeliveryException;
}
