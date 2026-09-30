package edu.student.chores;

import java.nio.charset.StandardCharsets;

public class HomeScreenAdapter implements NoticeSender {
    private final OldHomeScreen screen;

    public HomeScreenAdapter(OldHomeScreen screen) {
        this.screen = screen;
    }

    @Override
    public void send(String destination, Notice notice) throws DeliveryException {
        String message = notice.chore() + "|" + notice.dueDate() + "|" + notice.person();
        int code;
        try {
            code = screen.display(message.getBytes(StandardCharsets.UTF_8), notice.priority(), destination);
        } catch (OldHomeScreenException exception) {
            throw new DeliveryException("Home screen is not available");
        }
        if (code != 0) {
            throw new DeliveryException(describe(code));
        }
    }

    private String describe(int code) {
        return switch (code) {
            case 12 -> "Reminder text is empty";
            case 41 -> "Destination is missing";
            default -> "Home screen rejected the reminder";
        };
    }
}
