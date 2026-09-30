package edu.student.chores;

public class JsonNoticeSender implements NoticeSender {
    @Override
    public void send(String destination, Notice notice) {
        String body = "{\"chore\":\"" + notice.chore() + "\",\"dueDate\":\"" + notice.dueDate()
                + "\",\"person\":\"" + notice.person() + "\"}";
        System.out.println("JSON to " + destination + ": " + body);
    }
}
