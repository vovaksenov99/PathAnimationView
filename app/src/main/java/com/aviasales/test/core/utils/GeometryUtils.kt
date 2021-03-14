package com.aviasales.test.core.utils

import android.graphics.Point
import kotlin.math.acos
import kotlin.math.sqrt

fun angleBetweenTwoVectors(a: Point, b: Point): Double {
    val angleRad = acos(
        (a.x * b.x + a.y * b.y) /
                (sqrt((a.x * a.x + a.y * a.y).toDouble()) * sqrt((b.x * b.x + b.y * b.y).toDouble()))
    )
    return Math.toDegrees(angleRad) * if (a.y > b.y) -1 else 1
}

operator fun Point.div(p: Double): Point {
    return Point((x / p).toInt(), (y / p).toInt())
}

operator fun Point.times(p: Double): Point {
    return Point((x * p).toInt(), (y * p).toInt())
}
