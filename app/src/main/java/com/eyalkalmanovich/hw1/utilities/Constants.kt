package com.eyalkalmanovich.hw1.utilities

object Constants {

    object GameLogic {
        const val ROWS = 8                 // obstacle rows, the last row is the car's row
        const val LANES = 3
        const val LIVES = 3
        const val TICK_DELAY_MS = 500L     // constant road speed
        const val SPAWN_EVERY_TICKS = 2    // a new obstacle every N ticks
    }

    object Signals {
        const val VIBRATION_MS = 300L
    }
}
