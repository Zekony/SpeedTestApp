package com.zekony.speed.ui.speedTestScreen.composables

import android.graphics.Paint
import android.util.Log
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zekony.speed.R
import com.zekony.speed.ui.theme.SpeedTestAppTheme

@Composable
fun SpeedProgressBar(
    modifier: Modifier = Modifier,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    secondaryColor: Color = MaterialTheme.colorScheme.secondary,
    currentValue: Float,
    circularRadius: Float = 350f
) {
    Box(
        modifier = Modifier.then(modifier)
    ) {
        CanvasCircularSlider(
            primaryColor,
            secondaryColor,
            currentValue,
            circularRadius,
        )
    }
}

@Composable
private fun CanvasCircularSlider(
    primaryColor: Color,
    secondaryColor: Color,
    currentValue: Float,
    circularRadius: Float
) {
    val animatedNumber by animateFloatAsState(
        targetValue = currentValue,
        animationSpec = tween(durationMillis = 500, easing = EaseIn),
        label = "animated value"
    )
    var highestValue by remember { mutableStateOf(100f) }
    if (currentValue > highestValue) highestValue = currentValue
    val maxValue = derivedStateOf { (highestValue * 1.15f) }

    var circleCenter: Offset


    val textString = stringResource(R.string.progress_bar_text, String.format("%.1f", animatedNumber))

    Canvas(
        modifier = Modifier.fillMaxSize(),
    ) {
        val width = size.width
        val height = size.height
        val circleThickness = width / 20f
        circleCenter = Offset(x = width / 2f, y = height / 2f)

        drawCircle(
            brush = Brush.radialGradient(
                listOf(
                    primaryColor.copy(0.45f),
                    secondaryColor.copy(0.15f)
                )
            ),
            radius = circularRadius,
            center = circleCenter
        )

        filledSliderLine(
            currentValue,
            maxValue.value,
            circleThickness,
            circularRadius,
            primaryColor
        )

        drawLines(
            circularRadius,
            primaryColor
        )

        centerText(textString, circleCenter)
    }
}

fun DrawScope.centerText(animatedNumberText: String, circleCenter: Offset) {
    drawContext.canvas.nativeCanvas.apply {
        drawText(
            animatedNumberText,
            circleCenter.x,
            circleCenter.y + 45.dp.toPx() / 3f,
            Paint().apply {
                textSize = 38.sp.toPx()
                textAlign = Paint.Align.CENTER
                color = Color.White.toArgb()
                isFakeBoldText = true
            }

        )
    }
}

private fun DrawScope.filledSliderLine(
    currentValue: Float,
    maxValue: Float,
    circleThickness: Float,
    circularRadius: Float,
    color: Color
) {
    drawArc(
        color = color,
        startAngle = 135f,
        sweepAngle = (270f / maxValue) * currentValue,
        style = Stroke(
            width = circleThickness * 1.7f,
            cap = StrokeCap.Round
        ),
        useCenter = false,
        size = Size(
            width = circularRadius * 2f,
            height = circularRadius * 2f
        ),
        topLeft = Offset(
            size.width / 2f - circularRadius,
            size.height / 2f - circularRadius,
        )
    )
}

fun DrawScope.drawLines(
    circularRadius: Float,
    lineColor: Color
) {
    for (degreeValue in -45..225 step 6) {
        val start = Offset(
            x = circularRadius / 2.4f,
            y = size.height / 2
        )

        val end = Offset(
            x = circularRadius / 1.5f,
            y = size.height / 2
        )
        rotate(
            degrees = degreeValue.toFloat()
        ) {
            drawLine(
                color = lineColor,
                start = start,
                end = end,
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}