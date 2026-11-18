package com.eyalkalmanovich.hw1

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.eyalkalmanovich.hw1.logic.GameManager
import com.eyalkalmanovich.hw1.logic.GameManager.TickResult
import com.eyalkalmanovich.hw1.utilities.Constants
import com.eyalkalmanovich.hw1.utilities.SignalManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView

class MainActivity : AppCompatActivity() {

    private lateinit var gameManager: GameManager
    private lateinit var signalManager: SignalManager

    private lateinit var main_LBL_distance: MaterialTextView
    private lateinit var main_IMG_hearts: Array<AppCompatImageView>
    private lateinit var main_LAY_grid: LinearLayout
    private lateinit var main_FAB_left: FloatingActionButton
    private lateinit var main_FAB_right: FloatingActionButton

    /** main_IMG_obstacles[row][lane] */
    private lateinit var main_IMG_obstacles: Array<Array<AppCompatImageView>>
    private lateinit var main_IMG_cars: Array<AppCompatImageView>

    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false

    private val gameLoop = object : Runnable {
        override fun run() {
            handleResult(gameManager.tick())
            refreshUI()
            handler.postDelayed(this, Constants.GameLogic.TICK_DELAY_MS)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        gameManager = GameManager()
        signalManager = SignalManager(this)
        findViews()
        buildGrid()
        initViews()
        refreshUI()
    }

    override fun onResume() {
        super.onResume()
        startGame()
    }

    override fun onPause() {
        super.onPause()
        stopGame()
    }

    private fun startGame() {
        if (isRunning) return
        isRunning = true
        handler.postDelayed(gameLoop, Constants.GameLogic.TICK_DELAY_MS)
    }

    private fun stopGame() {
        isRunning = false
        handler.removeCallbacks(gameLoop)
    }

    private fun findViews() {
        main_LBL_distance = findViewById(R.id.main_LBL_distance)
        main_IMG_hearts = arrayOf(
            findViewById(R.id.main_IMG_heart0),
            findViewById(R.id.main_IMG_heart1),
            findViewById(R.id.main_IMG_heart2)
        )
        main_LAY_grid = findViewById(R.id.main_LAY_grid)
        main_FAB_left = findViewById(R.id.main_FAB_left)
        main_FAB_right = findViewById(R.id.main_FAB_right)
    }

    /**
     * Builds a ROWS x LANES grid of ImageViews (weights, no Canvas).
     * Objects "move" by toggling visibility of the cells, not by changing x/y.
     */
    private fun buildGrid() {
        val rows = gameManager.rows
        val lanes = gameManager.lanes
        val padding = resources.getDimensionPixelSize(R.dimen.cell_padding)

        fun newImage(resId: Int) = AppCompatImageView(this).apply {
            setImageResource(resId)
            setPadding(padding, padding, padding, padding)
            visibility = View.INVISIBLE
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        val cars = ArrayList<AppCompatImageView>()
        main_IMG_obstacles = Array(rows) { row ->
            val rowLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
            }
            main_LAY_grid.addView(rowLayout)

            Array(lanes) {
                val cell = FrameLayout(this).apply {
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                }
                rowLayout.addView(cell)

                val obstacle = newImage(R.drawable.rock)
                cell.addView(obstacle)

                if (row == rows - 1) {
                    val car = newImage(R.drawable.car)
                    cell.addView(car)
                    cars.add(car)
                }
                obstacle
            }
        }
        main_IMG_cars = cars.toTypedArray()
    }

    private fun initViews() {
        main_FAB_left.setOnClickListener { onMove(gameManager.moveLeft()) }
        main_FAB_right.setOnClickListener { onMove(gameManager.moveRight()) }
    }

    private fun onMove(result: TickResult) {
        handleResult(result)
        refreshUI()
    }

    private fun handleResult(result: TickResult) {
        when (result) {
            TickResult.NONE -> Unit
            TickResult.CRASH -> {
                signalManager.toast(resources.getQuantityString(R.plurals.crash_message, gameManager.lives, gameManager.lives))
                signalManager.vibrate()
            }
            TickResult.GAME_OVER -> {
                signalManager.toast(getString(R.string.game_over_message))
                signalManager.vibrate(Constants.Signals.VIBRATION_MS * 3)
            }
        }
    }

    private fun refreshUI() {
        // obstacles
        for (row in 0 until gameManager.rows)
            for (lane in 0 until gameManager.lanes)
                main_IMG_obstacles[row][lane].visibility =
                    if (gameManager.obstacles[row][lane]) View.VISIBLE else View.INVISIBLE

        // car
        main_IMG_cars.forEachIndexed { lane, car ->
            car.visibility = if (lane == gameManager.carLane) View.VISIBLE else View.INVISIBLE
        }

        // hearts
        main_IMG_hearts.forEachIndexed { index, heart ->
            heart.visibility = if (index < gameManager.lives) View.VISIBLE else View.INVISIBLE
        }

        // distance
        main_LBL_distance.text = getString(R.string.distance_format, gameManager.distance)
    }
}
