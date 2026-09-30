package edu.student.chores;

import java.util.Map;
import java.util.Locale;
import java.util.function.Function;

public class NoticeService {
    private final Map<String, NoticeSender> senders;
    private final Map<String, Function<NoticeSender, ChoreNotice>> notices;

    public NoticeService(Map<String, NoticeSender> senders,
                         Map<String, Function<NoticeSender, ChoreNotice>> notices) {
        this.senders = Map.copyOf(senders);
        this.notices = Map.copyOf(notices);
    }

    public void publish(NoticeRequest request) throws DeliveryException {
        NoticeSender sender = findSender(request.format());
        ChoreNotice operation = findNotice(request.kind()).apply(sender);
        operation.publish(request.destination(), request.notice());
    }

    private NoticeSender findSender(String format) {
        if (format == null) {
            throw new IllegalArgumentException("Format is required");
        }
        NoticeSender sender = senders.get(format.toUpperCase(Locale.ROOT));
        if (sender == null) {
            throw new IllegalArgumentException("Unknown format: " + format);
        }
        return sender;
    }

    private Function<NoticeSender, ChoreNotice> findNotice(String kind) {
        if (kind == null) {
            throw new IllegalArgumentException("Kind is required");
        }
        Function<NoticeSender, ChoreNotice> factory = notices.get(kind.toUpperCase(Locale.ROOT));
        if (factory == null) {
            throw new IllegalArgumentException("Unknown kind: " + kind);
        }
        return factory;
    }
}
