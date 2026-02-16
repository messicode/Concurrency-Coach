# Scalable Depot  
## Fine-Grained Linearizable Concurrency

### Focus
Maximizing parallel progress while preserving linearizability.

---

## Overview

This version removes global bottlenecks and allows:

- Parallel getTickets operations
- Concurrent operations on independent buses
- Deadlock-free fine-grained synchronization

Audit functionality removed to reduce contention.

---

## Concurrency Strategy

- Fine-grained locking
- Minimal lock scope
- Deterministic lock ordering
- Deadlock avoidance

---

## Progress Guarantees

- Independent buses operate in parallel
- Reads on unaffected buses proceed concurrently
- Global reads block only when necessary

---

## Linearizability

Each operation has a well-defined linearization point.

- No partial visibility
- No inconsistent states
- Multi-ticket operations are atomic

---

## What This Demonstrates

- Scalability under concurrency
- Deadlock prevention strategies
- Fine-grained synchronization design
- Linearizability reasoning
