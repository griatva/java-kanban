# Java Kanban — Lightweight Task Manager (Educational)

A compact **Kanban-style task manager** built in Java as a learning project. It supports three entities — **Task**, *
*Epic**, **SubTask** — keeps a **view history**, validates **time overlaps**, orders items by **priority (start time)**,
and exposes a minimal **JSON HTTP API**. Storage is pluggable: **in-memory** or simple **CSV** file.

> Built to practice clean OOP, collections, time APIs, JSON serialization, and HTTP server basics.

## Highlights

- CRUD for Tasks / Epics / SubTasks
- Conflict‑free scheduling: validates time intersections
- Prioritized view: tasks ordered by start time
- View history (LRU-like)
- **HTTP API** on `http://localhost:8080`:
  - `/tasks`, `/epics`, `/subtasks`
  - `/history`, `/prioritized`

## Tech & Design

- **Language:** Java 17 (works on 11+)
- **HTTP:** `com.sun.net.httpserver.HttpServer` (no frameworks)
- **JSON:** Gson with custom adapters for `LocalDateTime` and `Duration`
- **Persistence:** In-memory or CSV (`FileBackedTaskManager`)
- **Core concepts practiced:** Collections, equals/hashCode, exceptions/validation, Streams/Optional

## Testing

The project is covered with an extensive **JUnit 5** test suite (120+ tests),
including both unit and integration tests. The tests validate core business
logic such as CRUD operations, task history management, prioritized task ordering,
time-overlap validation with boundary cases, epic status and time aggregation based on subtasks,
cascading deletes, file persistence and state restoration, as well as the full HTTP API tested via `HttpClient`
and JSON serialization.

## Skills Practiced

- Designed clean models for hierarchical tasks (Epic → SubTask).
- Implemented simple persistence and idempotent CRUD.
- Built a tiny HTTP layer and JSON adapters from scratch.
- Practiced time handling and priority conflicts.
- Developed an extensive JUnit 5 test suite covering business logic, edge cases, persistence, and HTTP API behavior.

_____ 

## How to run

This is a plain Java console application (no frameworks).

To run the project:

1. Open it in an IDE (e.g. IntelliJ IDEA)
2. Locate the `Main` class
3. Run the `main` method