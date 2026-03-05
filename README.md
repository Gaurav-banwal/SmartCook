Smart Cook: AI-Powered Recipe Companion

Smart Cook is a modern Android application designed to eliminate "what should I cook today?" fatigue. By leveraging the Gemini API, the app analyzes your current pantry inventory to suggest creative, delicious, and healthy recipes, ensuring you make the most of what you already have.
🚀 Features
Currently Implemented

    Secure Authentication: Firebase-powered login and registration system.

    Inventory Management: A dedicated module to add, update, and track available ingredients in your kitchen using Room Database.

    Modern UI Architecture: Built entirely with Jetpack Compose for a fluid, reactive user interface.

    Core Navigation: Established structure for Home, Inventory, Favorites, and Settings.

In Progress / Remaining

    Gemini AI Integration: Implementation of the recipe generation logic based on live inventory data.

    Spoonacular API Integration: Enhancing the UI with high-quality ingredient and dish imagery.

    Favorites System: Ability to save and categorize AI-generated recipes for offline access.

    Advanced Filtering: Sorting recipes by preparation time, dietary restrictions, or calorie count.

    UI Polishing: Transitioning from "functional" to "production-ready" with custom animations and Material 3 components.

🛠 Tech Stack

    Language: Kotlin

    UI Framework: Jetpack Compose

    Database: Room (Local Storage)

    Backend: Firebase (Auth)

    AI: Google Gemini API

    Networking: Retrofit / Ktor (for API calls)

    Architecture: MVVM (Model-View-ViewModel)

📋 Roadmap to "Resume Ready" (May 2026)

To ensure the project is at a professional standard for internship applications, the following milestones are prioritized:

    Phase 1: The Core Loop – Finalize the prompt engineering for Gemini to ensure recipe accuracy and formatting.

    Phase 2: Data Persistence – Fully integrate the Favorites module so users don't lose great recipes.

    Phase 3: Visual Appeal – Connect ingredient image APIs to make the inventory look professional.

    Phase 4: Optimization – Implement proper error handling for API quotas and offline states.

⚙️ Setup & Installation

    Clone the repository:
    Bash

    git clone https://github.com/your-username/smart-cook.git

    Open the project in Android Studio (Ladybug or newer).

    Add your google-services.json for Firebase.

    Add your Gemini API Key to your local.properties or secrets file.

    Sync Gradle and run on an emulator or physical device.

🤝 Contributing

Since this is a personal project aimed at skill development, suggestions are welcome! Feel free to open an issue or submit a pull request.
