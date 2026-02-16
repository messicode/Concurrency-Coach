# Atomic Depot  
## Thread-Safe All-or-Nothing Bus Ticket System

### Focus
Ensuring atomic multi-object operations under concurrency.

---

## Overview

This project implements a thread-safe depot system where:

- Multiple tickets can be modified in one operation
- All operations are atomic
- No partial state is ever observable
- Ticket lifecycle is strictly enforced

Audit functionality is included.

---

## Concurrency Model

- Synchronized critical sections
- Atomic validation-then-commit pattern
- Global consistency enforcement

---

## Correctness Guarantees

1. Bus capacity is never exceeded
2. No ticket appears in two buses simultaneously
3. Tickets are never "in transit"
4. USED and EXPIRED states are terminal
5. All operations are all-or-nothing
6. Audit logs fully explain system state

---

## Design Decisions

- Pre-validate all tickets before mutation
- Apply state changes only after full validation
- Log actions in deterministic order
- Enforce strict state machine transitions

---

## What This Demonstrates

- Atomicity across multiple objects
- State machine enforcement
- Thread-safe design
- Transaction-style concurrency reasoning
