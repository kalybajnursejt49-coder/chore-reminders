package edu.student.chores;

public class CsvNoticeSender implements NoticeSender {
    @Override
    public void send(String destination, Notice notice) {
        String row = notice.chore() + "," + notice.dueDate() + "," + notice.person();
        System.out.println("CSV to " + destination + ": " + row);
    }
}
