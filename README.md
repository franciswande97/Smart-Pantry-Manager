# Smart Pantry Manager

An Android app (Java) that helps reduce food waste. You record the ingredients you
already have at home, and the app suggests recipes you can cook using **only** those
ingredients, with no shopping trip required.

Built for Mobile App Development 700 (Richfield Graduate Institute of Technology).

## The core idea: strict matching

A recipe is suggested only if **every** ingredient it needs is in the pantry, in at
least the required quantity. A recipe missing one ingredient, or needing more than
you have, is excluded from the main list. The matching also copes with real-world
messiness:

- Plural and singular names ("Tomato" / "Tomatoes", "Egg" / "Eggs")
- Different units of the same kind (1 kg satisfies 200 g; 1 l satisfies 300 ml)
- Duplicate pantry entries (they are added together)
- Spoon and cup measures vs grams for common staples (e.g. 2 tbsp sugar vs 30 g sugar)

## Features

- Pantry management: add, edit and delete ingredients (name, quantity, unit, optional expiry date)
- Pantry list screen (RecyclerView with a custom adapter) with input validation on the form
- 20 pre-loaded recipes, seeded on first run
- Suggested Recipes screen (strict matches) and a Recipe Detail screen
- "Almost there" list: recipes missing exactly one ingredient, kept separate from the strict list
- Recipes that use soon-to-expire ingredients are shown first
- Settings screen: toggle for expiring-soon highlighting (saved with SharedPreferences)
- Friendly message when no recipes match
- Bottom navigation bar

## Database: SQLite via Room

I chose **SQLite using the Room persistence library** because:

- The data is **relational**: a recipe has many ingredients, modelled with a foreign key.
- It is **local and offline**, so the app works without internet, accounts or a server.
- Room checks SQL queries **at compile time**, which catches mistakes early.
- Data **persists** across app restarts, and there is nothing extra to host or configure.

Tables: `pantry_items`, `recipes`, `recipe_ingredients`.

## Setup and run

1. Install **Android Studio** (a recent stable version).
2. Clone this repository and open the project folder in Android Studio.
3. Wait for the Gradle sync to finish.
4. Run on an emulator or a physical Android phone (API 24 or higher): press the green **Run** button.
5. On first launch the 20 recipes are created automatically. To re-seed, uninstall the app and run again.

### Run the unit tests

Right-click `RecipeMatcherTest` (in `app/src/test/...`) and choose **Run**.

## Project structure

| File | Purpose |
|---|---|
| `MainActivity` | Host Activity with the bottom navigation bar |
| `PantryFragment`, `PantryAdapter` | Pantry list screen |
| `AddEditIngredientActivity` | Add/edit form with validation |
| `SuggestedRecipesFragment`, `RecipeAdapter` | Suggested recipes screen |
| `RecipeDetailActivity` | Ingredients and method for one recipe |
| `SettingsFragment`, `AppPreferences` | Settings screen and saved preference |
| `AppDatabase`, `*Dao`, entities | Room database layer |
| `DatabaseSeeder` | Pre-loads the recipe collection |
| `RecipeMatcher`, `IngredientNormalizer`, `UnitConverter`, `DensityTable` | Strict-matching logic |

## Known limitations

- The density table is approximate and only covers common staples. Other ingredients must be entered in the same kind of unit the recipe uses.
- Ingredient names are matched after cleaning plurals, but spelling mistakes are not corrected.
- The list redraws fully on change (`notifyDataSetChanged`); `DiffUtil` would be a future improvement.

## Out of scope

No Google Maps, mapping SDKs or location/GPS features, in line with the assignment brief.
