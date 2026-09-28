# GuessMaster

**An Android trivia game about dates.** GuessMaster shows you a famous person, a politician, a singer, or a country, and challenges you to guess its birth date (or founding date). Every wrong guess tells you whether to go earlier or later. When you land on the exact date, you win tickets based on how hard that entity is, and see a full profile of who or what you just guessed.

Built natively in **Java** for Android, with an object-oriented entity model at its core.

---

## Gameplay

1. **A welcome dialog** introduces the first entity and tells you what kind it is ("This entity is a politician!").
2. **Its name and picture** appear on screen.
3. **Enter a date** in `MM/DD/YYYY` format and tap **Submit Guess**.
4. **Get a hint** if you're wrong:
   - *"Incorrect. Try a later date."* if your guess is too early
   - *"Incorrect. Try an earlier date."* if your guess is too late
5. **Win when you're exact.** A *"BINGO!"* dialog shows the entity's full profile, a toast announces the tickets you earned, and your running total updates.
6. **Keep going.** After a win, the next entity is picked at random. Tap **Change Entity** at any time to skip to a new one.

Type `quit` to exit the game.

### Entities and rewards

Each entity has a difficulty rating, and the tickets you win are `difficulty × 100`:

| Entity | Type | Difficulty | Tickets |
|---|---|---|---|
| Justin Trudeau | Politician | 0.25 | 25 |
| Celine Dion | Singer | 0.50 | 50 |
| United States | Country | 0.10 | 10 |
| My Creator | Person | 1.00 | 100 |

---

## Features

- **Hint-driven guessing:** date comparison tells the player which direction to go, turning each guess into a step toward the answer.
- **Difficulty-scaled rewards:** harder entities pay out more tickets, and the running total is shown at the top of the screen.
- **Randomized rounds:** entities are chosen at random, both on launch and after every win or skip.
- **Rich feedback:** `AlertDialog`s for the welcome, hints, and wins, plus `Toast` notifications for game start, ticket awards, and invalid input.
- **Dynamic images:** each entity is shown with its own picture, with a fallback icon for any entity without one.
- **Robust input validation:** guesses pass through two layers of checks (details below), so bad input produces a friendly message instead of a crash.
- **Modern Android UI:** a Material 3 day/night theme and an edge-to-edge layout that respects system bar insets.

---

## Architecture

### Entity class hierarchy

The game is built around an abstract `Entity` class, with specialized subclasses that each carry their own data and behaviour:

```
                 Entity  (abstract)
          name · date · difficulty
         ┌───────────┴───────────┐
       Person                 Country
       + gender               + capital
     ┌────┴──────┐
 Politician    Singer
 + party       + debut album
               + album release date
```

- **Polymorphism:** `entityType()` is abstract, so each subclass describes itself. The welcome dialog calls it without knowing which kind of entity it's showing.
- **Chained `toString()`:** each subclass extends its parent's description (`super.toString() + ...`). The win dialog prints a complete profile: name, date, gender, party, debut album, release date, or capital, depending on the type.
- **Deep copies through `clone()`:** each class implements a copy constructor and a covariant `clone()` override (`Politician.clone()` returns a `Politician`). Entities are cloned when they're added to the game, so the game owns independent copies.
- **Encapsulation with defensive copying:** every `Date` is copied on the way in and on the way out (`getBorn()` returns a new `Date`), so no outside code can change an entity's internal state.

### Date model

A custom `Date` class stores month, day, and year with validated setters. It can be built from month names, month numbers, or a `MM/DD/YYYY` string, and provides chronological comparison (`precedes`, `equals`), which drives the hint logic.

### Input validation

Every guess goes through two independent checks before it's compared:

1. **Strict format parsing:** a non-lenient `SimpleDateFormat("MM/dd/yyyy")` rejects anything that isn't a real calendar date in the expected format, followed by explicit range checks on the month, day, and year.
2. **Construction guard:** the input is then parsed into a `Date` inside a `try`/`catch`, so any remaining malformed input is caught and reported with a toast.

### Game flow

`GuessMaster` is the single `Activity`. It builds the entity list in `onCreate`, shows the welcome dialog, and wires up the two buttons:

- **Submit Guess** validates the input and compares the date. It then either shows a hint or awards tickets and moves to the next entity.
- **Change Entity** clears the input and picks a random new entity.

---

## Getting Started

**Requirements:** Android Studio (Iguana or newer), with the Android SDK for API 34. The app runs on Android 8.0 (API 26) and above.

1. Clone the repository and open the folder in Android Studio.
2. Let Gradle sync. The Gradle wrapper and version catalog pin every dependency.
3. Run the `app` configuration on an emulator or a connected device.

From the command line:

```bash
./gradlew assembleDebug     # build the APK
./gradlew installDebug      # install on a connected device or emulator
```

---

## Project Structure

```
app/src/main/
├── java/com/example/guess_master_3/
│   ├── GuessMaster.java    main Activity: game loop, dialogs, validation, UI wiring
│   ├── Entity.java         abstract base class
│   ├── Person.java         extends Entity (+ gender)
│   ├── Politician.java     extends Person (+ party)
│   ├── Singer.java         extends Person (+ debut album and release date)
│   ├── Country.java        extends Entity (+ capital)
│   └── Date.java           validated date model with comparison
└── res/
    ├── layout/activity_guess_master.xml
    └── drawable/           entity images and fallback icon
```

---

## Tech Stack

**Java 8** · **Android SDK 34** (min SDK 26) · **AndroidX AppCompat** · **Material Components 3** · **Gradle (Kotlin DSL) with a version catalog**

---

## Author

**Arjun Guliya**
