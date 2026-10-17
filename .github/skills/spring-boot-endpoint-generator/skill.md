---
name: spring-boot-endpoint-generator
description: Generate Spring Boot REST API endpoints, entities, DTOs, repositories, and services following clean architecture principles for Java backend projects. Use when the user asks to generate backend code, REST controllers, services, or endpoints for Spring Boot applications.
---

# Spring Boot Endpoint & Backend Generator Skill

## Overview
This skill streamlines the generation of production-ready Spring Boot 3 / Java 17 backend components (Entities, DTOs, Repositories, Services, and Controllers) ensuring clean separation of concerns and robust REST API design.

## Target Domains & Endpoints Coverage
- **Menu Management:** Create menu, update menu, delete menu, view menus (`/api/v1/menus`).
- **Menu Categories & Items:** CRUD operations for categories and items/ingredients (`/api/v1/menu-categories`, `/api/v1/menu-items`).
- **User Profile & Logout:** Customer profile get/update (`/api/v1/users/profile`), logout (`/api/v1/auth/logout`).
- **Reviews & Comments:** Add and view restaurant reviews (`/api/v1/restaurants/{id}/reviews`).

## Generation Guidelines
1. When requested, generate complete, compiling Java classes with correct package structure.
2. Ensure no business logic resides in controllers.
3. Keep code modular, DRY, and well-structured for rapid integration.

