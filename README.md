<p align="center">
  <img src="https://sudoku-expert.com/logo-text.svg" width="100%" alt="SudokuExpert Logo">
</p>

# A Sudoku app for Android.
A app to play free and unlimited Sudoku with three difficulties to choose.

## :sparkles: Features:

- [x] **Unlimited games**: An algorithm creates unique sudokus, so you get a new one each try!
- [x] **Three difficulties**: Beginner, intermediate and expert.
- [x] **Minimalistic**: Simple and clean look!
- [x] **Notes**: Struggling to finish? Take notes like you're playing on paper.
- [x] **Notes check**: Automatic deletion of impossible notes after you insert a number.
- [x] **Mark your numbers/lines**: Mark your current row/column and also equal numbers.
- [x] **Timer**: Stop the time of your Sudoku and beat your highscore.
- [x] **Statistics**: Find information about your last games.
- [x] **Sharing**: Share your Sudoku with a friend.
 
<p align="center">
  <a href="https://play.google.com/store/apps/details?id=com.aol.philipphofer">
    <img src="https://sudoku-expert.com/download-android.png" width="25%">
  </a>
</p>

## :hammer: Build

To build this project yourself you need to follow this steps:
1. Install the Android SDK
2. Clone the project
3. Open the project with Android Studio
4. Run the `cloneGenerator.sh` script to clone the necessary C-files for Sudoku-generation from the [sudoku-expert-generator](https://github.com/hofaphil/sudoku-expert-generator) repo
5. Build the app :white_check_mark:

## :clipboard: Tests

The project contains basic unit and integration tests. They are located in the `app/src/test` and the `app/src/androidTest` directory. The tests can be run comfortably with Android Studio.
