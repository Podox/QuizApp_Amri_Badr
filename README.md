# QuizApp (Amri Badr)

A feature-rich Android Quiz Application built with Java, featuring user authentication, dynamic question loading, and proctoring capabilities (Camera & Audio recording).

## Description

This application is designed to conduct quizzes where questions are dynamically fetched from Firebase Firestore. It includes advanced features to ensure the integrity of the quiz process, such as displaying a live camera preview of the user and recording audio while the quiz is being taken.

## Key Features

*   **User Authentication**: Secure registration and login using **Firebase Authentication**.
*   **Dynamic Content**: Questions are fetched in real-time from **Firebase Firestore** (`questions` collection).
*   **Proctoring / Monitoring**:
    *   **Camera Preview**: Displays the front-facing camera feed during the quiz.
    *   **Audio Recording**: Records ambient audio for each question (saved locally as `.3gp` files).
*   **Rich Media**: Supports image-based questions using **Glide** for efficient image loading.
*   **Score Tracking**: Calculates and displays the final score upon completion.

## Tech Stack

*   **Language**: Java
*   **Platform**: Android SDK
*   **Backend**: Firebase (Authentication, Firestore)
*   **Libraries**:
    *   `Glide`: For image loading and caching.
    *   `Firebase SDK`: For Auth and Database.
    *   `AndroidX`: For modern Android component support.

## Permissions

The app requires the following permissions to function correctly:
*   `CAMERA`: For the live camera preview.
*   `RECORD_AUDIO`: To record audio during the quiz.
*   `WRITE_EXTERNAL_STORAGE` / `READ_EXTERNAL_STORAGE`: To save the recorded audio files.

## Setup & Installation

1.  **Clone the repository**:
    ```bash
    git clone <repository-url>
    ```
2.  **Open in Android Studio**:
    Open the `QuizApp_Amri_Badr` directory in Android Studio.
3.  **Firebase Configuration**:
    *   The project relies on Firebase. Ensure `google-services.json` is present in the `app/` directory.
    *   If you are setting this up for a new project, create a project in the [Firebase Console](https://console.firebase.google.com/), add an Android app, and download the `google-services.json` file.
    *   Enable **Authentication** (Email/Password) and **Cloud Firestore** in your Firebase console.
4.  **Build and Run**:
    *   Sync project with Gradle files.
    *   Run the app on an Android Emulator or physical device.
    *   *Note: For camera and audio features, a physical device is recommended.*

## Usage

1.  **Register**: Create a new account with your email and password.
2.  **Login**: Sign in with your credentials.
3.  **Take Quiz**: Answer the questions displayed. The app will record audio and show your camera feed.
4.  **View Score**: See your total score at the end of the quiz.

## Project Structure

*   `MainActivity.java`: Handle user login.
*   `Register.java`: Handles user registration.
*   `QuizActivity.java`: The core logic for the quiz, including question fetching, answering, and proctoring features.
*   `Score.java`: Displays the final result.
*   `models/Question.java`: Data model for quiz questions.
