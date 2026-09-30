package edu.student.chores;

import java.util.Map;

public class Main {
    public static void main(String[] args) throws DeliveryException {
        NoticeService service = new NoticeService(
                Map.of("JSON", new JsonNoticeSender(),
                        "CSV", new CsvNoticeSender(),
                        "SCREEN", new HomeScreenAdapter(new OldHomeScreen())),
                Map.of(NoticeKind.ONE_TIME, OneTimeChoreNotice::new,
                        NoticeKind.WEEKLY, WeeklyChoreNotice::new));

        Notice plants = new Notice("Water the plants", "2026-09-28", "Alex", 2);
        Notice bins = new Notice("Take out the bins", "2026-09-28", "Sam", 1);

        service.publish(new NoticeRequest(plants, "json", NoticeKind.ONE_TIME));
        service.publish(new NoticeRequest(bins, "csv", NoticeKind.WEEKLY));
        service.publish(new NoticeRequest(bins, "screen", NoticeKind.WEEKLY));
        System.out.println("Done");
    }
}
