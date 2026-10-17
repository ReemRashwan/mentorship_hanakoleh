---
name: code-review-evaluator
description: Evaluate and review team projects and codebases based on the Mentorship Evaluation Model (100 Points rubric covering Architecture, Code Quality, Functionality, Git, Testing, DevOps, and Bonus). Use when the user asks to review code, evaluate a repository, or grade a team project.
---

# Code Review & Evaluation Model Skill

## Overview
This skill guides the evaluation of software engineering projects and codebases using the structured 100-point Mentorship Evaluation Model.

## Evaluation Categories & Weights
1. **Architecture (25%)**
   - Separation of concerns (Controller, Service, Repository layers).
   - Clean folder and feature/domain-based structure.
   - Scalability and loose coupling.
   - *Red Flags:* Everything in one file, spaghetti code, hardcoded values.

2. **Code Quality (20%)**
   - Readability and small focused functions.
   - Clear and meaningful naming conventions (no vague names like temp, data, x).
   - Clean code principles (DRY, avoiding deep nesting).
   - *Red Flags:* Copy-paste code, long unreadable functions.

3. **Functionality (20%)**
   - Requirements coverage and expected API behavior.
   - Edge cases handling and input validations.
   - *Red Flags:* Happy-path-only code, unhandled errors or crashes.

4. **Git Usage (10%)**
   - Meaningful commits and logical changes.
   - Small, focused Pull Requests with clear descriptions.
   - Branching strategy (feature branches, no direct commits to main).
   - *Red Flags:* One big commit at the end, no PR usage, messy history.

5. **Testing (10%)**
   - Unit tests covering core logic and services.
   - Reasonable coverage focusing on critical logic.
   - *Red Flags:* No tests at all, dummy tests that don't verify logic.

6. **DevOps Awareness (10%)**
   - Docker support (Dockerfile, docker-compose).
   - CI/CD automated build and test pipelines.
   - *Red Flags:* App runs only locally, no automation.

7. **Bonus (5%)**
   - Performance optimizations, caching (Redis), advanced error handling, elegant solutions.

---

