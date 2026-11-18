# Obstacle Race – HW1

**Author:** Eyal Kalmanovich

An endless obstacle-avoidance racing game for Android (Kotlin).

## Features
- Three-lane road
- Car that moves left and right (arrow buttons)
- Obstacles (stone blocks) on the road
- Constant driving speed – the obstacles come to you
- Crash notification: Toast message + vibration
- 3 lives (hearts at the top)
- Endless game – after losing all lives the game restarts with 3 lives

## Implementation notes
- No Canvas and no x/y position changes: the road is a grid of `ImageView`s
  (8 rows × 3 lanes, sized with layout weights). Movement is shown by toggling cell visibility.
- `logic/GameManager.kt` – game state and rules (obstacle matrix, car lane, lives, collisions).
- `utilities/SignalManager.kt` – Toast + vibration.
- `MainActivity.kt` – builds the grid, runs the game loop with a `Handler` (pauses in `onPause`), and renders the state.

## Demo video
_Add link here_
