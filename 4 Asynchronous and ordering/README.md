# Async Depot Executor  
## Ordered Asynchronous Bus Operations

### Focus
Non-blocking API with ordered asynchronous execution.

---

## Overview

This implementation introduces asynchronous versions of depot operations.

Characteristics:

- API returns immediately
- Execution happens in worker threads
- Each bus has its own worker thread
- Only the bus worker modifies its state

---

## Architecture

- Task queues per bus
- Result abstraction for future retrieval
- Thread confinement model
- Ordered execution per bus

---

## Concurrency Guarantees

- No busy-waiting
- No data races
- Order preserved per bus
- Parallel execution across independent buses

---

## Design Principles

- Message-passing style coordination
- Worker ownership model
- Safe cross-bus transfers
- Deterministic ordering

---

## What This Demonstrates

- Asynchronous system design
- Task scheduling under constraints
- Thread confinement pattern
- Amdahl’s law awareness
