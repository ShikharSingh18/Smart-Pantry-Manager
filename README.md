# Smart Pantry Manager

A native Android application, written in Java, that helps reduce food waste by tracking the ingredients a user actually has at home and suggesting only the recipes they can cook **right now**, with zero shopping required.

## Download

Grab the latest APK directly from the [Releases page](https://github.com/ShikharSingh18/Smart-Pantry-Manager/releases/latest). Enable "Install from unknown sources" on your Android device, then open the downloaded APK to install. Optionally you may build from the source files.

## The Concept

Most recipe apps show you what you *could* make if you bought a few more things. Smart Pantry Manager does the opposite: it applies a **strict-matching rule** against your current pantry, so a recipe only ever appears in your suggestions if every single ingredient it needs (in at least the quantity it needs) is already sitting in your kitchen. No partial matches, no "almost there" recipes cluttering the list, no wasted trips to the store.

## Core Features

- **Pantry management**: add, edit, and delete ingredients (name, quantity, unit, optional expiry date), all persisted locally.
- **Pantry List screen**: a live RecyclerView of every ingredient currently tracked, with an expiring-soon banner that filters the list on tap, sort options (alphabetical / soonest-expiring), and an empty-state message when the pantry is empty.
- **Suggested Recipes screen**: runs the strict-matching algorithm against the current pantry and shows only recipes that can genuinely be made in full, right now.
- **Recipe Detail screen**: full ingredient list (with quantities) and step-by-step method for a selected recipe.
- **Settings screen**: toggle for expiring-soon alerts, with a live icon that reflects the toggle state.
- **16 pre-seeded recipes**, each with real per-ingredient quantities and units, loaded automatically on first run.

## The Strict-Matching Algorithm

Implemented in `IngredientMatcher.java`. For a recipe to be suggested:

1. Every required ingredient must exist somewhere in the pantry.
2. Where the pantry item's unit and the recipe's required unit can be reconciled (e.g. both measured in grams, or both in pieces), the pantry quantity must be **greater than or equal to** the required quantity.
3. Ingredient names are normalized (lowercase, whitespace-trimmed, naive singular/plural stripping) so "tomato" and "tomatoes" are treated as the same ingredient, a deliberate, as per assignment requirements.
4. If a recipe is missing even one ingredient, or doesn't have enough of it, it is excluded entirely from the suggestions list as no partial matches are ever shown.

**Known limitation:** true cross-unit conversion (e.g. converting grams to pieces) is out of scope. If a pantry item's unit can't be reconciled with the recipe's required unit after normalisation, that specific ingredient falls back to a presence-only check rather than failing outright.

## Database Choice: SQLite

This app uses **SQLite via `SQLiteOpenHelper`**, not Firebase or PostgreSQL. Reasoning:

- The app's data is inherently single-user and local — there's no requirement for multi-device sync or real-time collaboration, so a cloud backend would add complexity without adding value.
- SQLite works fully offline, which matches a pantry-tracking use case where a user shouldn't need an internet connection just to check what's in their kitchen.
- It's the persistence approach covered in depth in the module's own content on Activities, Adapters, and persistent data, making it the most defensible and explainable choice for this assignment.
- Two tables are used: `pantry_items` (the user's current ingredients, full CRUD) and `recipes` (a fixed, seeded collection, read-only after first launch).

## Screens

| Screen | Purpose |
|---|---|
| Pantry List (`MainActivity`) | View, sort, and filter all current pantry ingredients |
| Add/Edit Ingredient | Create a new ingredient or edit an existing one, with input validation and a date picker for expiry |
| Suggested Recipes | Strict-matched recipes based on the current pantry |
| Recipe Detail | Full ingredient list and preparation steps for one recipe |
| Settings | Toggle expiring-soon alerts |

## Tech Stack

- **Language:** Java
- **IDE:** Android Studio
- **Database:** SQLite (`SQLiteOpenHelper`)
- **UI:** Material Components, ConstraintLayout/LinearLayout, RecyclerView with custom Adapters
- **Font:** Manrope (custom, applied app-wide via `themes.xml`)

## Setup & Run Instructions

1. Clone the repository:
   ```
   git clone https://github.com/ShikharSingh18/Smart-Pantry-Manager.git
   ```
2. Open the project folder in **Android Studio** (latest stable release recommended).
3. Let Gradle sync automatically, and all data is stored locally via SQLite so there is no "cloud fetching".
4. Select an emulator (or connect an Android phone via wireless/wired connection) and click **Run**.
5. On first launch, the app automatically seeds its recipe database with 16 recipes — no manual setup step is needed.

No external services, API keys, or network connection are required to run or test this app.

Alternatively, skip building entirely and install directly from the [prebuilt APK release](https://github.com/ShikharSingh18/Smart-Pantry-Manager/releases/latest).

## Project Structure (Key Files)

```
app/src/main/java/com/shikharsingh/smartpantrymanager/
├── MainActivity.java              # Pantry list, banner, sort, filter
├── AddEditIngredientActivity.java # Add/edit ingredient with validation + date picker
├── SuggestedRecipesActivity.java  # Runs strict-matching against the pantry
├── RecipeDetailActivity.java      # Full recipe view
├── SettingsActivity.java          # Expiring-soon alerts toggle
├── DatabaseHelper.java            # SQLite schema, seeding, and CRUD
├── IngredientMatcher.java         # Strict-matching + normalization logic
├── PantryItem.java / Recipe.java / RecipeIngredient.java  # Data models
├── PantryAdapter.java / RecipeAdapter.java                # RecyclerView adapters
└── ExpiryUtils.java               # Shared date-comparison helper
```

## Author

Shikhar Singh
402502915@my.richfield.ac.za
Richfield Graduate Institute of Technology
Mobile App Development 700
