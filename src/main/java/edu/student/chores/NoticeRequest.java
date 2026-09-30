package edu.student.chores;

public record NoticeRequest(Notice notice, String format, String kind, String destination) {
}
