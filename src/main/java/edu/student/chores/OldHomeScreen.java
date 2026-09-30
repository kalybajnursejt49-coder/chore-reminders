package edu.student.chores;

public class OldHomeScreen {
    public int display(byte[] data, int urgency, String screenName) {
        if (screenName == null || screenName.isBlank()) {
            return 41;
        }
        if (data == null || data.length == 0) {
            return 12;
        }
        return 0;
    }
}
