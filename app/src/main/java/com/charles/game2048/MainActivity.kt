package com.charles.game2048

import android.app.Activity
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

/** Classic 2048: swipe to slide the tiles, merge equals, reach 2048. */
class MainActivity : Activity() {

    private lateinit var game: Game2048
    private lateinit var boardView: BoardView
    private lateinit var scoreView: TextView
    private lateinit var bestView: TextView
    private lateinit var statusView: TextView
    private lateinit var prefs: SharedPreferences
    private var best = 0
    private var winShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("game2048", MODE_PRIVATE)
        best = prefs.getInt("best", 0)

        game = Game2048()
        val savedGrid = savedInstanceState?.getIntArray("grid")
        if (savedGrid != null && savedGrid.size == 16) {
            game.restore(
                savedGrid,
                savedInstanceState.getInt("score", 0),
                savedInstanceState.getBoolean("won", false),
                savedInstanceState.getBoolean("over", false)
            )
            winShown = savedInstanceState.getBoolean("winShown", false)
        } else {
            game.reset()
        }

        val density = resources.displayMetrics.density
        fun dp(value: Int) = (value * density).toInt()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#FAF8EF"))
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }

        // Header: title on the left, score and best on the right.
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val title = TextView(this).apply {
            text = "2048"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 56f)
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.parseColor("#776E65"))
        }
        val scoreBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#BBADA0"))
            setPadding(dp(12), dp(6), dp(12), dp(6))
        }
        val scoreLabel = TextView(this).apply {
            text = "SCORE"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.parseColor("#EEE4DA"))
            gravity = Gravity.CENTER
        }
        scoreView = TextView(this).apply {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }
        scoreBox.addView(scoreLabel)
        scoreBox.addView(scoreView)

        val bestBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#BBADA0"))
            setPadding(dp(12), dp(6), dp(12), dp(6))
        }
        val bestLabel = TextView(this).apply {
            text = "BEST"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.parseColor("#EEE4DA"))
            gravity = Gravity.CENTER
        }
        bestView = TextView(this).apply {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }
        bestBox.addView(bestLabel)
        bestBox.addView(bestView)

        val headerSpacer = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, 1, 1f)
        }
        val scoreParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { marginEnd = dp(8) }
        header.addView(title)
        header.addView(headerSpacer)
        header.addView(scoreBox, scoreParams)
        header.addView(bestBox)
        root.addView(header)

        // Sub-header: tagline + New Game button.
        val subHeader = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(8), 0, dp(8))
        }
        val tagline = TextView(this).apply {
            text = "Join the numbers and get to the 2048 tile!"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTextColor(Color.parseColor("#776E65"))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        val newGameButton = Button(this).apply {
            text = "New Game"
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#8F7A66"))
            setOnClickListener { newGame() }
        }
        subHeader.addView(tagline)
        subHeader.addView(newGameButton)
        root.addView(subHeader)

        // Board.
        boardView = BoardView(this).apply {
            this.game = this@MainActivity.game
            onSwipe = { direction -> onSwipe(direction) }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        }
        root.addView(boardView)

        // Status line under the board.
        statusView = TextView(this).apply {
            text = "Swipe to play"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.parseColor("#776E65"))
            gravity = Gravity.CENTER
            setPadding(0, dp(12), 0, 0)
        }
        root.addView(statusView)

        setContentView(root)
        refresh()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntArray("grid", game.grid)
        outState.putInt("score", game.score)
        outState.putBoolean("won", game.won)
        outState.putBoolean("over", game.over)
        outState.putBoolean("winShown", winShown)
    }

    private fun onSwipe(direction: Game2048.Direction) {
        if (game.over) return
        if (game.move(direction)) {
            game.spawn()
            game.updateGameOver()
            if (game.score > best) {
                best = game.score
                prefs.edit().putInt("best", best).apply()
            }
            refresh()
        }
    }

    private fun newGame() {
        game.reset()
        winShown = false
        refresh()
    }

    private fun refresh() {
        scoreView.text = game.score.toString()
        bestView.text = best.toString()
        statusView.text = when {
            game.over -> "Game over! Tap New Game to try again."
            game.won && !winShown -> {
                winShown = true
                "You win! Keep going for a higher score."
            }
            else -> "Swipe to play"
        }
        boardView.invalidate()
    }
}
