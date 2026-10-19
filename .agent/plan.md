# Project Plan

Create an Android app using Jetpack Compose and Material 3 with adaptive responsive design (large screen, tablet, foldable, phone List-Detail flow). 

Features:
1. Dessert Recipes & Journaling app features:
- Grid/List of recipe/journal cards with title, image, date, mood.
- Large-screen friendly List-Detail adaptive layout (sliding pane layout or ListDetailPaneScaffold for tablets/phones).
- Detail view with large hero image, ingredients/instructions, journal content, date, mood.
- Ability to create new entries/recipes with title, date, content, mood selection dropdown, and tranquil nature scene header images.
- Modern Material 3 UI aesthetics.

## Project Brief

# Project Brief: Dessert Recipe & Journaling App

## Features
- **Recipe & Journal Feed**: Grid or list display showing recipe and journal entries with title, cover image, date, and mood.
- **Adaptive List-Detail Layout**: Responsive layout built for phone, foldable, tablet, and desktop screens, transitioning smoothly between single-pane list view and dual-pane list-detail view.
- **Detail View**: Comprehensive view featuring a hero image, ingredient list, preparation steps, personal journal entries, date, and mood rating.
- **Entry Creation**: Interface for adding new dessert recipes and journal entries with title, date, content, mood selector dropdown, and custom header image selection.

## High-Level Tech Stack
- **Language**: Kotlin
- **UI Toolkit**: Jetpack Compose with Material Design 3
- **Navigation & Adaptive Strategy**: **Jetpack Navigation 3** (state-driven navigation) paired with **Compose Material 3 Adaptive** (`ListDetailPaneScaffold`) for dynamic multi-pane layouts across screen sizes
- **Architecture & Concurrency**: ViewModel, StateFlow, and Kotlin Coroutines

## Implementation Steps

