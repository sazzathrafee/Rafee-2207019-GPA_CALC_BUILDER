# Student Grading System (GPA Calculator)

A JavaFX desktop application that allows students to add courses, record grades, and calculate their overall GPA interactively.

## Features

### Home Screen
- Welcome screen with "Start GPA Calculator" button to begin

### Course Entry Screen
- **Target Credits Input**: Set the total credit limit before adding courses
- **Add Courses**: Input course details including:
  - Course Name
  - Course Code (must be 4 digits at end, no spaces - e.g., CSE2200)
  - Course Credit (supports decimal values like 3.0 or 3.5)
  - Teacher 1 Name
  - Teacher 2 Name
  - Grade (A+, A, A-, B+, B, B-, C+, C, C-, D+, D, F)

### Input Validations
- Target credits must be set before adding courses
- Course code must end with exactly 4 digits with no spaces
- Credit must be a valid positive number (decimal allowed)
- Multiple decimal points and invalid formats are rejected
- Total entered credits must not exceed target credits

### Course Management
- **Table View**: Displays all added courses with details
- **Edit**: Click Edit button to modify any course entry
- **Delete**: Click Delete button to remove courses
- **Real-time Credit Tracking**: Shows current credits vs target credits

### GPA Calculation
- **Calculate GPA Button**: Activates when:
  - At least one course is added
  - Total credits ≤ target credits (exact match not required)
- **Weighted GPA Formula**: 
  - GPA = Σ(grade_point × credit) / Σ(credit)
- **Grade Point Scale**:
  - A+ = 4.0, A = 3.75, A- = 3.5
  - B+ = 3.25, B = 3.0, B- = 2.75
  - C+ = 2.5, C = 2.25, C- = 2.0
  - D+ = 1.75, D = 1.5, F = 0.0

### Result Screen
- Displays all entered courses in a formatted table
- Shows calculated weighted GPA (formatted to 2 decimal places)
- Award form style presentation

## Project Structure
- **Layout**: BorderPane, GridPane, VBox, HBox with proper spacing and alignment
- **FXML + Controllers**: Clean separation of UI and logic
- **Model Classes**: Course model for data management
- **CSS Styling**: Custom styles for visual design
- **Validation**: Comprehensive input validation with error messages
# Rafee-2207019-GPA_CALC_BUILDER