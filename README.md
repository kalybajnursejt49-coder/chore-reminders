# Home chore reminders

Small Java 17 project demonstrating Bridge and Adapter with runtime delivery selection.

Run the unit tests with:

```text
mvn test
```

Run the demo with:

```text
mvn compile exec:java -Dexec.mainClass=edu.student.chores.Main
```

The UML diagram matching the implementation is in `uml.svg`; the design rationale is in `design-rationale.md`. Requests provide the notice kind, delivery format, and destination. Cadence is carried with each notice (`ONCE` or `WEEKLY`); this example describes weekly recurrence but has no scheduler. New kinds are registered with a string key and factory; no kind enum must be edited.
