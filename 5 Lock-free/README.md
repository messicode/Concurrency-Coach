# Lock-Free Depot  
## Linearizable Non-Blocking Depot Implementation

### Focus
Implementing a lock-free universal construction.

---

## Overview

This version removes:

- synchronized
- locks
- blocking primitives

The system is fully linearizable and non-blocking.

---

## Concurrency Model

- Lock-free algorithm
- Atomic state transitions
- Retry-based updates
- Universal construction principles

Only single-threaded data structures are used internally.

---

## Guarantees

- Linearizability
- Lock-freedom
- No partial visibility
- No deadlocks
- No blocking synchronization

---

## Why This Is Advanced

- Strong progress guarantees
- Correct under arbitrary thread interleavings
- No reliance on JVM locking
- Pure non-blocking design

---

## What This Demonstrates

- Expert-level concurrency understanding
- Lock-free algorithm construction
- Formal reasoning about progress guarantees
