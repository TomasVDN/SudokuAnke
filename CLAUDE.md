# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew build              # Full build
./gradlew assembleDebug      # Build debug APK
./gradlew installDebug       # Build and install debug APK on connected device
./gradlew test               # Run unit tests
./gradlew connectedAndroidTest  # Run instrumentation tests (requires device/emulator)
./gradlew lint               # Run lint checks
./gradlew clean build        # Clean rebuild
```

Run a single test class:
```bash
./gradlew test --tests "com.backend.sudoku.SudokuSolverTest"
```

## Architecture

The app uses a **traditional View-based Activity architecture** (not Compose for main UI, though Compose is available as a dependency).

### Layer Structure

**Activities** (`com.sudokuanke.activities`) — UI entry points and navigation:
- `MainActivity` — Menu: new game, load, insert manually, import via camera
- `SudokuActivity` — Core gameplay: grid, number selector, solve/undo/save/clear buttons, victory fireworks
- `LoadActivity` — Lists and loads saved games from disk
- `InsertActivity` — Manual board entry and validation; also receives boards from OCR
- `ImportActivity` — Camera capture → ML Kit OCR → passes digits to `InsertActivity`

**Frontend** (`com.frontend`) — Custom Android Views:
- `SudokuGridView` — 9×9 `GridLayout`; draws thick/thin grid lines (3×3 box borders = 6px, cell borders = 3px); manages cell selection
- `SudokuCellView` — Individual cell; visual states: selected, original (fixed), editable
- `NumberSelector` / `NumberButton` — Digit picker (1–9)
- `Undoer` — LIFO stack storing `(row, col, previousDigit, wasValid)`

**Backend — Sudoku Logic** (`com.backend.sudoku`, Java):
- `Sudoku` — Interface defining the contract
- `SudokuImpl` — Bitwise implementation: 81-element `int[]` for digits + `boolean[]` for originals; three `int`s each for rows, columns, and 3×3 boxes tracking used digits as bit flags (9 bits per int). Candidate calculation and conflict checking are O(1) via bitwise AND/XOR.
- `SudokuGenerator` — Builds a complete random board via backtracking with MRV heuristic, then removes clues while maintaining a unique solution. Difficulty by clue count: EASY 36–45, MEDIUM 27–35, HARD 22–26, EVIL 18–20.
- `SudokuSolver` — Backtracking solver with MRV; `countSolutions(limit)` is used by the generator to verify uniqueness.
- `SudokuUtil` — Serializes boards to/from 81-char strings (row-major, `0`=empty); saves/loads files under `app/files/sudokus/`.

**Backend — OCR** (`com.backend.ocr`, Java):
- `SudokuReader` — Pipeline coordinator; async result via `Callback` interface
- `CharactersReader` — ML Kit text recognition on camera image
- `SudokuReaderUtil` — Maps detected character bounding boxes to grid cell positions

### Data Flow

1. **New game**: `MainActivity` → `SudokuGenerator.generate(EVIL)` → `SudokuActivity` (board as Intent extra)
2. **OCR import**: `ImportActivity` captures image → `SudokuReader` extracts digits → `InsertActivity` for review → `SudokuActivity`
3. **Persistence**: `SudokuUtil` serializes to string; files stored in app's internal storage (`files/sudokus/`)
4. **Undo**: Every valid placement is pushed to `Undoer`; undo restores previous digit

### Key Design Decisions

- Backend (`com.backend`) is pure Java with no Android dependencies — it can be unit-tested without a device.
- The bitwise representation in `SudokuImpl` makes solver/generator fast enough for on-device use.
- Board state is passed between Activities as an Intent extra (81-char string), not shared state.