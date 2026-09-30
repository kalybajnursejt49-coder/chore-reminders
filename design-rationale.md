# Design rationale

## Problem

This program sends reminders about chores at home. A reminder can be for a one-time task, like watering the plants today, or a weekly task, like taking out the bins every week. It can be sent as JSON, CSV, or shown on an old home screen.

## Why both patterns are needed

Bridge separates the kind of reminder from the way it is delivered. `OneTimeChoreNotice` and `WeeklyChoreNotice` are the two refined abstractions. Both use the `NoticeSender` interface. This lets a new reminder type or delivery method be added without creating a class for every possible combination.

The old home screen has a different method: it expects message bytes, an urgency number, and the screen name in that order. It reports errors with integer codes and an unchecked `OldHomeScreenException`, while the other senders use `NoticeSender` and its `DeliveryException`. `HomeScreenAdapter` converts the reminder into bytes, calls the old method, and translates every error into a `DeliveryException` with a plain message. No error code and no old exception reaches the caller. The old screen class is not modified.

Bridge alone cannot make the old screen work with `NoticeSender`. Adapter alone can connect the old screen, but it does not separate one-time and weekly reminder behavior from the delivery methods.

## Required complexity module

The chosen module is **dynamic implementor selection**. `NoticeService` looks up the sender by the format in each request and the reminder type by the kind in each request. The caller does not directly choose a concrete sender.

## Open/Closed Principle

`NoticeService` gets two maps in its constructor: format to sender, and kind to a function that creates a `ChoreNotice` from a sender. A new delivery method is one new `NoticeSender` class and one new map entry. A new reminder type is one new `ChoreNotice` subclass, one new `NoticeKind` value and one new map entry. `NoticeService` and the existing classes do not change. The test `newSenderAndNewKindNeedNoChangeInService` shows this.

## Limitation

The format is a string, so an invalid format is only found when the request is handled. For a larger program, an enum would reduce this risk.
