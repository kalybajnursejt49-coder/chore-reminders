package edu.student.chores;

import java.util.Map;
import java.util.function.Function;

public class NoticeService {
    private static final String DESTINATION = "home-screen";

    private final Map<String, NoticeSender> senders;
    private final Map<NoticeKind, Function<NoticeSender, ChoreNotice>> notices;

    public NoticeService(Map<String, NoticeSender> senders,
                         Map<NoticeKind, Function<NoticeSender, ChoreNotice>> notices) {
        this.senders = senders;
        this.notices = notices;
    }

    public void publish(NoticeRequest request) throws DeliveryException {
        NoticeSender sender = findSender(request.format());
        ChoreNotice operation = findNotice(request.kind()).apply(sender);
        operation.publish(DESTINATION, request.notice());
    }

    private NoticeSender findSender(String format) {
        if (format == null) {
            throw new IllegalArgumentException("Format is required");
        }
        NoticeSender sender = senders.get(format.toUpperCase());
        if (sender == null) {
            throw new IllegalArgumentException("Unknown format: " + format);
        }
        return sender;
    }

    private Function<NoticeSender, ChoreNotice> findNotice(NoticeKind kind) {
        if (kind == null) {
            throw new IllegalArgumentException("Kind is required");
        }
        Function<NoticeSender, ChoreNotice> factory = notices.get(kind);
        if (factory == null) {
            throw new IllegalArgumentException("Unknown kind: " + kind);
        }
        return factory;
    }
}
