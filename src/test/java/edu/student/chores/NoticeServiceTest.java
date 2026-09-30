package edu.student.chores;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NoticeServiceTest {
    private final Notice plants = new Notice("Water the plants", "2026-09-28", "Alex", 2);
    private final Notice bins = new Notice("Take out the bins", "2026-09-28", "Sam", 1);

    private Map<String, Function<NoticeSender, ChoreNotice>> kinds() {
        return Map.of("ONE_TIME", OneTimeChoreNotice::new, "WEEKLY", WeeklyChoreNotice::new);
    }

    @Test
    void oneTimeNoticeDelegatesToSender() throws Exception {
        NoticeSender sender = mock(NoticeSender.class);

        new OneTimeChoreNotice(sender).publish("home-screen", plants);

        verify(sender).send("home-screen", plants);
    }

    @Test
    void weeklyNoticeAddsWeeklyLabel() throws Exception {
        NoticeSender sender = mock(NoticeSender.class);

        new WeeklyChoreNotice(sender).publish("home-screen", bins);

        verify(sender).send("home-screen", new Notice("Weekly: Take out the bins", "2026-09-28", "Sam", 1));
    }

    @Test
    void serviceChoosesSenderByFormat() throws Exception {
        NoticeSender json = mock(NoticeSender.class);
        NoticeSender csv = mock(NoticeSender.class);
        NoticeService service = new NoticeService(Map.of("JSON", json, "CSV", csv), kinds());

        service.publish(new NoticeRequest(plants, "csv", "ONE_TIME", "laundry-room"));

        verify(csv).send("laundry-room", plants);
        verify(json, never()).send(anyString(), any());
    }

    @Test
    void serviceChoosesWeeklyNotice() throws Exception {
        NoticeSender json = mock(NoticeSender.class);
        NoticeService service = new NoticeService(Map.of("JSON", json), kinds());

        service.publish(new NoticeRequest(bins, "JSON", "WEEKLY", "home-screen"));

        verify(json).send("home-screen", new Notice("Weekly: Take out the bins", "2026-09-28", "Sam", 1));
    }

    @Test
    void newSenderAndNewKindNeedNoChangeInService() throws Exception {
        NoticeSender xml = mock(NoticeSender.class);
        Map<String, Function<NoticeSender, ChoreNotice>> custom =
                Map.of("MONTHLY", sender -> new ChoreNotice(sender) {
                    @Override
                    public void publish(String destination, Notice notice) throws DeliveryException {
                        sender.send(destination, new Notice("Custom: " + notice.chore(), notice.dueDate(),
                                notice.person(), notice.priority()));
                    }
                });
        NoticeService service = new NoticeService(Map.of("XML", xml), custom);

        service.publish(new NoticeRequest(plants, "xml", "MONTHLY", "home-screen"));

        verify(xml).send("home-screen", new Notice("Custom: Water the plants", "2026-09-28", "Alex", 2));
    }

    @Test
    void unknownFormatIsRejected() {
        NoticeService service = new NoticeService(Map.of("JSON", mock(NoticeSender.class)), kinds());

        assertThrows(IllegalArgumentException.class,
                () -> service.publish(new NoticeRequest(plants, "PDF", "ONE_TIME", "home-screen")));
    }

    @Test
    void missingFormatIsRejected() {
        NoticeService service = new NoticeService(Map.of("JSON", mock(NoticeSender.class)), kinds());

        assertThrows(IllegalArgumentException.class,
                () -> service.publish(new NoticeRequest(plants, null, "ONE_TIME", "home-screen")));
    }

    @Test
    void adapterCallsOldScreenAndSucceeds() throws Exception {
        OldHomeScreen screen = mock(OldHomeScreen.class);
        when(screen.display(any(), anyInt(), anyString())).thenReturn(0);

        new HomeScreenAdapter(screen).send("home-screen", plants);

        verify(screen).display("Water the plants|2026-09-28|Alex".getBytes(), 2, "home-screen");
    }

    @Test
    void adapterTranslatesDestinationErrorCode() {
        OldHomeScreen screen = mock(OldHomeScreen.class);
        when(screen.display(any(), anyInt(), anyString())).thenReturn(41);

        DeliveryException exception = assertThrows(DeliveryException.class,
                () -> new HomeScreenAdapter(screen).send("home-screen", plants));

        assertEquals("Destination is missing", exception.getMessage());
        assertFalse(exception.getMessage().contains("41"));
    }

    @Test
    void adapterTranslatesEmptyMessageErrorCode() {
        OldHomeScreen screen = mock(OldHomeScreen.class);
        when(screen.display(any(), anyInt(), anyString())).thenReturn(12);

        DeliveryException exception = assertThrows(DeliveryException.class,
                () -> new HomeScreenAdapter(screen).send("home-screen", plants));

        assertEquals("Reminder text is empty", exception.getMessage());
    }

    @Test
    void adapterTranslatesUnknownErrorCode() {
        OldHomeScreen screen = mock(OldHomeScreen.class);
        when(screen.display(any(), anyInt(), anyString())).thenReturn(99);

        DeliveryException exception = assertThrows(DeliveryException.class,
                () -> new HomeScreenAdapter(screen).send("home-screen", plants));

        assertEquals("Home screen rejected the reminder", exception.getMessage());
    }

    @Test
    void adapterTranslatesOldScreenException() {
        OldHomeScreen screen = mock(OldHomeScreen.class);
        when(screen.display(any(), anyInt(), anyString())).thenThrow(new OldHomeScreenException("boom"));

        DeliveryException exception = assertThrows(DeliveryException.class,
                () -> new HomeScreenAdapter(screen).send("home-screen", plants));

        assertEquals("Home screen is not available", exception.getMessage());
        assertEquals("boom", exception.getCause().getMessage());
    }

    @Test
    void destinationErrorCanBeReachedThroughService() {
        OldHomeScreen screen = new OldHomeScreen();
        NoticeService service = new NoticeService(Map.of("SCREEN", new HomeScreenAdapter(screen)), kinds());

        DeliveryException exception = assertThrows(DeliveryException.class,
                () -> service.publish(new NoticeRequest(plants, "screen", "ONE_TIME", "")));

        assertEquals("Destination is missing", exception.getMessage());
    }

    @Test
    void jsonEscapesTextAndIncludesPriority() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream original = System.out;
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            new JsonNoticeSender().send("home-screen", new Notice("He said \"go\"", "today", "A\\B", 3));
        } finally {
            System.setOut(original);
        }

        assertEquals("JSON to home-screen: {\"chore\":\"He said \\\"go\\\"\",\"dueDate\":\"today\",\"person\":\"A\\\\B\",\"priority\":3}",
                output.toString(StandardCharsets.UTF_8).strip());
    }

    @Test
    void csvQuotesFieldsAndIncludesPriority() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream original = System.out;
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            new CsvNoticeSender().send("home-screen", new Notice("Sweep, then mop", "today", "Sam \"S\"", 2));
        } finally {
            System.setOut(original);
        }

        assertEquals("CSV to home-screen: \"Sweep, then mop\",today,\"Sam \"\"S\"\"\",2",
                output.toString(StandardCharsets.UTF_8).strip());
    }
}

