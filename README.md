# Concurrency Coach  

A five-stage concurrency engineering project that incrementally builds a correct, scalable, and finally non-blocking ticket management system.

This repository demonstrates advanced knowledge of:

- Thread safety
- Linearizability
- Reentrant locks
- Fine-grained synchronization
- Asynchronous execution models
- Lock-free universal constructions
- Java Memory Model guarantees

---

## 📌 Problem Domain

The system models a **touristic bus depot** where:

- Tickets are issued
- Tickets are boarded onto buses
- Tickets are transferred between buses
- Tickets are used or expired
- Capacity constraints must never be violated
- All operations must be atomic
- System must remain correct under concurrency

Each stage strengthens concurrency guarantees and scalability.

---

## 🧠 Learning Progression

| Project | Name | Core Focus | Blocking | Progress Guarantee |
|----------|------|------------|----------|-------------------|
| 1 | Atomic Depot | Thread-safe atomic operations | Yes | Thread-safe |
| 2 | Reentrant Lock Engine | Custom reentrant mutual exclusion | Yes | Deadlock-free |
| 3 | Scalable Depot | Fine-grained locking & linearizability | Yes | Parallel progress |
| 4 | Async Depot Executor | Asynchronous ordered execution | Non-blocking API | Parallel async progress |
| 5 | Lock-Free Depot | Universal construction | No | Lock-free |

---

## 🏗 System Model

### Core Components

- **Depot** – Central coordination system
- **Bus** – Capacity-constrained container of tickets
- **Ticket** – Stateful object

### Ticket Lifecycle

ISSUED → IN_CIRCULATION → USED → EXPIRED


Properties:

- USED and EXPIRED are terminal states
- No ticket may re-enter circulation
- No partial state transitions are observable

---

## 🔒 Concurrency Guarantees Across Projects

- No data races
- Linearizability
- No partial visibility of operations
- Capacity safety
- Status monotonicity
- Transfer atomicity
- Lock-freedom (Assignment 5)

---

## 📁 Repository Structure

/1 Thread safety
/2 Reentrant locks
/3 Synchronize and scale
/4 Asynchronous and ordered execution
/5 Lock-free and atomic



Each folder contains:
- Implementation
- Design explanation
- Concurrency guarantees
- Trade-off discussion

---

## 🎯 What This Repository Demonstrates

- Practical implementation of synchronization primitives
- Deep understanding of concurrency correctness
- Ability to scale systems safely
- Lock-free algorithm design
- Strong systems reasoning under the Java Memory Model

---

## ⚙️ Language & Environment

- Java
- No external concurrency libraries (where restricted)
- Designed for correctness under arbitrary thread interleavings



Disclaimer: Due to time constraints this Readme text is generated with the help of ChatGPT.
