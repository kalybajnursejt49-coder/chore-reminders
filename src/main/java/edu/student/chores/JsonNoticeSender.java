package edu.student.chores;

public class JsonNoticeSender implements NoticeSender {
    @Override
    public void send(String destination, Notice notice) {
        String body = "{\"chore\":" + JsonStrings.quote(notice.chore())
                + ",\"dueDate\":" + JsonStrings.quote(notice.dueDate())
                + ",\"person\":" + JsonStrings.quote(notice.person())
                + ",\"priority\":" + notice.priority()
                + ",\"cadence\":" + JsonStrings.quote(notice.cadence()) + "}";
        System.out.println("JSON to " + destination + ": " + body);
    }
}
