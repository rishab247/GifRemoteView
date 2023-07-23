package com.rishabaggarwal.gifremoteview.utils

import android.content.res.Resources
import android.util.TypedValue
import java.math.RoundingMode
import java.text.DecimalFormat

val Number.toPx get() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    Resources.getSystem().displayMetrics)

fun formatData(data: Float):Float{
    val df = DecimalFormat("#.####")
    df.roundingMode = RoundingMode.DOWN
    return df.format(data).toFloat()
}
fun formatData(data: Double):Double{
    val df = DecimalFormat("#.####")
    df.roundingMode = RoundingMode.DOWN
    return df.format(data).toDouble()
}