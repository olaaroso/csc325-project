# MacroManager
Online Calorie/Macronutrient Tracker

Group: 4

Names: Pablo Orellana, Olamide Aroso, Josh Iehle, and Japneet Singh

Our group project is an online calorie and macronutrient tracker that gives people a convenient way to keep track of what they eat every day. The platform will be a web based application where users can make a personal profile, set their daily calorie and macro targets, and log foodEntries or meals either from a searchable database or by entering custom values.

As users add entries, the app will show how each meal affects their daily totals in real time. To make this clear, we want to include visual summaries like progress bars, pie charts, trend graphs, and other simple displays so people can quickly understand their progress, the tracker will also let users save meals they use often, edit or delete past entries, and export their history as a CSV file if they want to look at their data in more detail.

The goal of our project is to create something that feels easy to use while still giving meaningful feedback, so people can make better choices throughout the day without the process feeling overwhelming. 

## World Assumptions:
Users have access to the internet and suitable devices (e.g., computers, smartphones) for running the application.
Users will use a desktop or laptop computer for initial sign-in and data synchronization.
Users are seeking dietary tracking and general nutritional insights, not medical advice; the application provides information only and does not replace professional guidance.
Food nutritional values are sourced from either user entries or third-party APIs, and are assumed to be sufficiently accurate for tracking purposes.
Users are responsible for entering portion sizes (grams, ounces, cups) correctly, and for ensuring the accuracy of unit conversions.

## User Requirements:
### Functional requirements:
The application must provide a user-friendly interface
Create and authenticate user accounts (via email/password, with optional Google OAuth using Firebase).
Perform full CRUD operations on foodEntries and meals: search existing foodEntries, add custom foodEntries (name, serving size, calories, macros), and create meals as groups of foodEntries.
Log meals into a daily diary with timestamps and portion sizes.
Set and edit daily calorie and macronutrient targets.
Provide real-time daily totals and progress visualizations.
View nutrition history by day, week, or month, with the option to export data as CSV.
Edit or delete past entries as needed.

### Non Functional requirements:
Ensure data privacy through secure authentication and encrypted storage of user information.
The application must provide a responsive, low-latency user interface.
The system should be cross-platform and accessible on Windows, macOS, and Linux.
Code should be maintainable, using Maven (or Gradle) and well-structured packages.

## Specifications and Interface Needs:
Login / Signup – Secure user authentication with email/password and Google OAuth
Dashboard / Home – Displays today’s calorie total, macronutrient breakdown with progress rings, quick food entry, and recent meals/entries
Food Search / Library – Searchable database with pagination, macro columns, “add to meal” option, and filters by meal type (breakfast, lunch, dinner, snack)
Custom Food Form – Add custom foodEntries with fields for name, serving size, units, calories, protein, carbohydrates, fat, and optional photo
Meal Builder – Combine multiple foodEntries into a single meal with quantity adjustments and automatic macro totals
History and Reports – Calendar/date picker for past entries, detailed daily views, weekly summary charts, and CSV export
Settings – Options for measurement units (metric/imperial), daily calorie and macro targets, and theme preferences (light/dark mode)

## Program and Hardware:
Java and JavaFX for the main application 
SceneBuilder for designing user interfaces 
Maven or Gradle for build and dependency management 
IntelliJ IDEA as the primary IDE 
JavaFX built-in charts or a lightweight charting library for visualizations  
Firebase for database and authentication services 
Firestore for storing images or additional cloud data

## Team meetings:
The team will meet every Tuesday and Thursday at 7:00 PM, will be held virtually via zoom.




