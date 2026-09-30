# Design rationale

## Problem

This program sends reminders about chores at home. A reminder can be for a one-time task, like watering the plants today, or a weekly task, like taking out the bins every week. It can be sent as JSON, CSV, or shown on an old home screen.

## Why both patterns are needed

Bridge separates reminder cadence from delivery format. `OneTimeChoreNotice` sends a notice with cadence `ONCE`; `WeeklyChoreNotice` sets cadence to `WEEKLY`. The cadence is carried in `Notice` and encoded by the JSON and CSV senders (and included in the legacy screen message). The implementations therefore vary the reminder semantics while using the same delivery interface, without making a sender class for every cadence and format combination.

The old home screen has a different method: it expects message bytes, an urgency number, and the screen name in that order. It reports errors with integer codes and an unchecked `OldHomeScreenException`, while the other senders use `NoticeSender` and its `DeliveryException`. `HomeScreenAdapter` converts the reminder into bytes, calls the old method, and translates every error into a `DeliveryException` with a plain message. No error code and no old exception reaches the caller. The old screen class is not modified.

Bridge alone cannot make the old screen work with `NoticeSender`. Adapter alone can connect the old screen, but it does not separate one-time and weekly reminder behavior from the delivery methods.

## Required complexity module

The chosen module is **dynamic implementor selection**. `NoticeService` looks up the sender by the format in each request and the reminder type by the kind in each request. The caller does not directly choose a concrete sender.

## Open/Closed Principle

`NoticeService` gets two maps in its constructor: format to sender, and a string kind key to a function that creates a `ChoreNotice` from a sender. There is no closed `NoticeKind` enum to edit. A new reminder type is one new `ChoreNotice` subclass plus a registry entry at application composition. The service does not change. A new delivery method is one new `NoticeSender` implementation plus a sender registry entry. The test `newSenderAndNewKindNeedNoChangeInService` registers `MONTHLY` and `XML` together without changing the service.

The request carries its destination, so the service can route to any configured target. The legacy adapter can therefore receive a blank destination and translate the old screen's error code 41 through the service path.

JSON strings are escaped according to JSON string rules. CSV fields containing commas, quotes, or line breaks are quoted and embedded quotes are doubled. Both output formats include priority and cadence. `Notice` rejects null chore, date, person, or cadence values when constructed.

## Limitation

Format and kind are registry keys, so misspellings are detected only when a request is handled. Also, weekly cadence is metadata for the receiving system: this example has no scheduler and does not create future jobs. Adding scheduling would require a separate component that consumes the cadence and due date.
