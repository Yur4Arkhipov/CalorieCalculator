# CalCalc (Calorie Calculator & Nutrition Tracker)

CalCalc is an Android application designed to help users track their daily calorie intake, macronutrients (proteins, carbs, and fats), and manage their diet with ease. The app features AI-powered meal recognition, allowing users to take photos of their food to automatically estimate nutritional value.

## Features

- **Onboarding & Personalization**: Calculate your daily calorie and macronutrient goals based on your age, gender, weight, height, activity level, and goals.
- **AI-Powered Food Recognition**: Take a photo of your meal and let the AI automatically identify the food and estimate its calories and macros.
- **Manual Meal Entry & Editing**: Easily add meals manually, edit their components, and adjust the nutritional values.
- **Favorites**: Save your frequent meals to favorites for quick and easy adding later.
- **Home Dashboard**: View your daily progress, including remaining calories, macros breakdown, and a timeline of meals for the selected day.
- **Calendar & History**: Browse through your previous days and keep track of your nutrition history.
- **Profile Management**: Update your physical parameters and recalculate your goals at any time.
- **Statistics**: Track your progress over time visually.

## Architecture

The project is built using modern Android development practices and follows the **MVVM (Model-View-ViewModel)** architecture pattern, combined with **Clean Architecture** principles.

### Module Structure

The project is modularized to ensure separation of concerns, build speed optimization, and reusability:

- `app` - Main application module tying everything together.
- `core:data` - Contains local database (Room), network interactions, and repository implementations.
- `core:domain` - Contains UseCases and business logic.
- `core:designsystem` - Contains UI components, themes, colors, typography, and resources used across the app (Jetpack Compose).
- `core:util` - Helper classes and extensions.
- `feature:home` - Home screen, meal details, editing, and camera/AI integration.
- `feature:onboarding` - Initial user setup.
- `feature:profile` - User profile and goal adjustments.
- `feature:statistics` - Charts and progress tracking.

## Tech Stack

- **Kotlin** - Primary programming language.
- **Jetpack Compose** - Modern declarative UI toolkit.
- **Coroutines & Flow** - For asynchronous programming and reactive streams.
- **Hilt (Dagger)** - Dependency Injection.
- **Room** - Local SQLite database for offline caching and data persistence.
- **Navigation Component** - For in-app navigation.
- **Retrofit & OkHttp** - For network requests (AI API communication).
- **Coil** - For image loading and caching.
- **Yandex AI** - For meal recognition and nutritional estimation.