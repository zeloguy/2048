package com.charles.game2048

import kotlin.random.Random

/** Pure 2048 game logic: a 4x4 grid, swipe to slide and merge tiles. */
class Game2048 {

    enum class Direction { UP, DOWN, LEFT, RIGHT }

    val size = 4
    val grid = IntArray(size * size)
    var score = 0
        private set
    var won = false
        private set
    var over = false
        private set

    fun reset() {
        grid.fill(0)
        score = 0
        won = false
        over = false
        spawn()
        spawn()
    }

    /** Restore a saved game (rotation / process death). */
    fun restore(savedGrid: IntArray, savedScore: Int, savedWon: Boolean, savedOver: Boolean) {
        savedGrid.copyInto(grid)
        score = savedScore
        won = savedWon
        over = savedOver
    }

    /** Drop a 2 (90%) or a 4 (10%) into a random empty cell. */
    fun spawn() {
        val empty = ArrayList<Int>()
        for (i in grid.indices) if (grid[i] == 0) empty.add(i)
        if (empty.isEmpty()) return
        val cell = empty[Random.nextInt(empty.size)]
        grid[cell] = if (Random.nextInt(10) == 0) 4 else 2
    }

    /**
     * Slide the board in [direction], merging equal neighbours once per move.
     * Returns true if anything moved or merged.
     */
    fun move(direction: Direction): Boolean {
        var changed = false
        for (line in 0 until size) {
            val cells = lineCells(line, direction)
            val values = cells.map { grid[it] }.filter { it != 0 }
            val merged = ArrayList<Int>()
            var i = 0
            while (i < values.size) {
                if (i + 1 < values.size && values[i] == values[i + 1]) {
                    val sum = values[i] * 2
                    merged.add(sum)
                    score += sum
                    if (sum >= 2048) won = true
                    i += 2
                } else {
                    merged.add(values[i])
                    i += 1
                }
            }
            for (pos in cells.indices) {
                val value = if (pos < merged.size) merged[pos] else 0
                if (grid[cells[pos]] != value) {
                    grid[cells[pos]] = value
                    changed = true
                }
            }
        }
        return changed
    }

    /** Recompute the game-over flag. Call after spawning a new tile. */
    fun updateGameOver() {
        over = !canMove()
    }

    /** True if any move is possible (an empty cell, or two equal neighbours). */
    fun canMove(): Boolean {
        for (i in grid.indices) {
            if (grid[i] == 0) return true
            val row = i / size
            val col = i % size
            if (col + 1 < size && grid[i] == grid[i + 1]) return true
            if (row + 1 < size && grid[i] == grid[i + size]) return true
        }
        return false
    }

    /** Cell indices for one line, in the order tiles slide towards. */
    private fun lineCells(line: Int, direction: Direction): IntArray {
        val cells = IntArray(size)
        for (pos in 0 until size) {
            cells[pos] = when (direction) {
                Direction.LEFT -> line * size + pos
                Direction.RIGHT -> line * size + (size - 1 - pos)
                Direction.UP -> pos * size + line
                Direction.DOWN -> (size - 1 - pos) * size + line
            }
        }
        return cells
    }
}
