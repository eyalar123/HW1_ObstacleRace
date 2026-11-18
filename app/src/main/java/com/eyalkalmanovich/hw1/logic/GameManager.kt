package com.eyalkalmanovich.hw1.logic

import com.eyalkalmanovich.hw1.utilities.Constants
import kotlin.random.Random

/**
 * Pure game logic, no Android views here.
 * The road is a ROWS x LANES matrix of obstacles; the car lives in the last row.
 */
class GameManager(
    val rows: Int = Constants.GameLogic.ROWS,
    val lanes: Int = Constants.GameLogic.LANES,
    val maxLives: Int = Constants.GameLogic.LIVES
) {

    enum class TickResult { NONE, CRASH, GAME_OVER }

    val obstacles: Array<BooleanArray> = Array(rows) { BooleanArray(lanes) }

    var carLane: Int = lanes / 2
        private set

    var lives: Int = maxLives
        private set

    var distance: Int = 0
        private set

    private var tickCount = 0

    fun moveLeft(): TickResult = moveTo(carLane - 1)

    fun moveRight(): TickResult = moveTo(carLane + 1)

    private fun moveTo(lane: Int): TickResult {
        if (lane !in 0 until lanes) return TickResult.NONE
        carLane = lane
        return checkCollision()
    }

    /** One step of the road: obstacles move down one row, maybe a new one appears on top. */
    fun tick(): TickResult {
        for (row in rows - 1 downTo 1)
            obstacles[row - 1].copyInto(obstacles[row])
        obstacles[0].fill(false)

        if (tickCount % Constants.GameLogic.SPAWN_EVERY_TICKS == 0)
            obstacles[0][Random.nextInt(lanes)] = true

        tickCount++
        distance++
        return checkCollision()
    }

    private fun checkCollision(): TickResult {
        val carRow = obstacles[rows - 1]
        if (!carRow[carLane]) return TickResult.NONE

        carRow[carLane] = false // obstacle is destroyed by the crash
        lives--
        if (lives > 0) return TickResult.CRASH

        // Endless game: start over with full lives.
        reset()
        return TickResult.GAME_OVER
    }

    private fun reset() {
        obstacles.forEach { it.fill(false) }
        lives = maxLives
        distance = 0
        tickCount = 0
        carLane = lanes / 2
    }
}
