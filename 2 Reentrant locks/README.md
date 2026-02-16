# Reentrant Lock Engine  
## Custom High-Contention Reentrant Lock Implementation

### Focus
Building a correct, deadlock-free, reentrant lock from scratch.

---

## Overview

This project implements a custom lock that satisfies:

- Mutual exclusion
- Deadlock freedom
- Reentrancy
- Unlock integrity
- Proper happens-before relationships

Assignment 1 Depot was modified to use this lock.

---

## Lock Guarantees

1. No two threads own the lock simultaneously
2. A thread may reenter the lock
3. Unlocking without ownership throws IllegalMonitorStateException
4. Unlocking an unlocked lock throws IllegalMonitorStateException
5. Memory visibility is preserved

---

## Design Details

- Thread ownership tracking
- Reentrancy counter
- Fast isReentered() implementation
- Optimized for high contention
- No data races

---

## Performance Focus

Optimized for:

- Short lock-unlock sections
- High contention scenarios
- Minimal memory operations in isReentered()

---

## What This Demonstrates

- Deep understanding of Java Memory Model
- Synchronization primitive construction
- Performance-aware concurrency design
