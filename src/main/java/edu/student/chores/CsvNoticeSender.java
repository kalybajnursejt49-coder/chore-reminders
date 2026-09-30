package edu.student.chores;

public class CsvNoticeSender implements NoticeSender {
    @Override
    public void send(String destination, Notice notice) {
        String row = csv(notice.chore()) + "," + csv(notice.dueDate()) + "," + csv(notice.person())
                + "," + notice.priority();
        System.out.println("CSV to " + destination + ": " + row);
    }

    private String csv(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
