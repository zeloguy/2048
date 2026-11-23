package com.charles.game2048

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import kotlin.math.min

/** Draws the 4x4 board in the classic 2048 colours and reports swipe gestures. */
class BoardView(context: Context) : View(context) {

    var game: Game2048? = null
    var onSwipe: ((Game2048.Direction) -> Unit)? = null

    private val boardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#BBADA0") }
    private val emptyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#CDC1B4") }
    private val tilePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { textAlign = Paint.Align.CENTER }

    private var downX = 0f
    private var downY = 0f

    /** Classic 2048 tile background colours. */
    private fun tileColor(value: Int): Int = when (value) {
        2 -> 0xFFEEE4DA.toInt()
        4 -> 0xFFEDE0C8.toInt()
        8 -> 0xFFF2B179.toInt()
        16 -> 0xFFF59563.toInt()
        32 -> 0xFFF67C5F.toInt()
        64 -> 0xFFF65E3B.toInt()
        128 -> 0xFFEDCF72.toInt()
        256 -> 0xFFEDCC61.toInt()
        512 -> 0xFFEDC850.toInt()
        1024 -> 0xFFEDC53F.toInt()
        2048 -> 0xFFEDC22E.toInt()
        else -> 0xFF3C3A32.toInt()
    }

    private fun tileTextColor(value: Int): Int =
        if (value <= 4) Color.parseColor("#776E65") else Color.WHITE

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // The board is square: take the smaller of the two available dimensions.
        val size = min(
            MeasureSpec.getSize(widthMeasureSpec),
            MeasureSpec.getSize(heightMeasureSpec)
        )
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val g = game ?: return
        val boardSize = min(width, height).toFloat()
        val gap = boardSize * 0.03f
        val cell = (boardSize - gap * 5) / 4f
        val radius = boardSize * 0.02f

        val boardRect = RectF(0f, 0f, boardSize, boardSize)
        canvas.drawRoundRect(boardRect, radius, radius, boardPaint)

        for (row in 0 until 4) {
            for (col in 0 until 4) {
                val left = gap + col * (cell + gap)
                val top = gap + row * (cell + gap)
                val rect = RectF(left, top, left + cell, top + cell)
                val value = g.grid[row * 4 + col]
                if (value == 0) {
                    canvas.drawRoundRect(rect, radius, radius, emptyPaint)
                } else {
                    tilePaint.color = tileColor(value)
                    canvas.drawRoundRect(rect, radius, radius, tilePaint)
                    val text = value.toString()
                    textPaint.color = tileTextColor(value)
                    textPaint.textSize = when (text.length) {
                        1, 2 -> cell * 0.45f
                        3 -> cell * 0.38f
                        else -> cell * 0.30f
                    }
                    textPaint.isFakeBoldText = true
                    val y = top + cell / 2f - (textPaint.descent() + textPaint.ascent()) / 2f
                    canvas.drawText(text, left + cell / 2f, y, textPaint)
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                return true
            }
            MotionEvent.ACTION_UP -> {
                val dx = event.x - downX
                val dy = event.y - downY
                val threshold = width * 0.12f
                if (maxOf(abs(dx), abs(dy)) > threshold) {
                    val direction = if (abs(dx) > abs(dy)) {
                        if (dx > 0) Game2048.Direction.RIGHT else Game2048.Direction.LEFT
                    } else {
                        if (dy > 0) Game2048.Direction.DOWN else Game2048.Direction.UP
                    }
                    onSwipe?.invoke(direction)
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}
