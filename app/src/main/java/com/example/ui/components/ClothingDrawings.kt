package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun ClothingVectorIcon(
    category: String,
    colorHex: String,
    modifier: Modifier = Modifier
) {
    val garmentColor = try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: Exception) {
        Color.DarkGray
    }

    val outlineColor = if (colorHex.uppercase() == "#FAFAFA" || colorHex.uppercase() == "#FFFFFF") {
        Color(0xFF8E8E93) // Use darker borders for light clothes
    } else {
        Color.White.copy(alpha = 0.5f)
    }

    Canvas(
        modifier = modifier
            .aspectRatio(1f)
            .fillMaxSize()
    ) {
        val w = size.width
        val h = size.height

        when (category.lowercase()) {
            "shirt" -> {
                // Draw collared buttondown shirt
                val path = Path().apply {
                    moveTo(w * 0.3f, h * 0.15f)
                    lineTo(w * 0.45f, h * 0.22f)
                    lineTo(w * 0.55f, h * 0.22f)
                    lineTo(w * 0.7f, h * 0.15f)
                    // Sleeves left
                    lineTo(w * 0.85f, h * 0.35f)
                    lineTo(w * 0.75f, h * 0.4f)
                    lineTo(w * 0.7f, h * 0.35f)
                    // Torso left side
                    lineTo(w * 0.7f, h * 0.9f)
                    // Bottom
                    lineTo(w * 0.3f, h * 0.9f)
                    // Torso right side
                    lineTo(w * 0.3f, h * 0.35f)
                    lineTo(w * 0.25f, h * 0.4f)
                    lineTo(w * 0.15f, h * 0.35f)
                    close()
                }
                drawPath(path, color = garmentColor)
                drawPath(path, color = outlineColor, style = Stroke(width = 3.dp.toPx()))

                // Collar folds
                drawLine(
                    color = outlineColor,
                    start = Offset(w * 0.30f, h * 0.18f),
                    end = Offset(w * 0.45f, h * 0.28f),
                    strokeWidth = 3.dp.toPx()
                )
                drawLine(
                    color = outlineColor,
                    start = Offset(w * 0.70f, h * 0.18f),
                    end = Offset(w * 0.55f, h * 0.28f),
                    strokeWidth = 3.dp.toPx()
                )

                // Placket buttons vertical line
                drawLine(
                    color = outlineColor.copy(alpha = 0.8f),
                    start = Offset(w * 0.5f, h * 0.25f),
                    end = Offset(w * 0.5f, h * 0.9f),
                    strokeWidth = 2.dp.toPx()
                )
                // Small button dots
                drawCircle(color = outlineColor, radius = 2.dp.toPx(), center = Offset(w * 0.5f, h * 0.45f))
                drawCircle(color = outlineColor, radius = 2.dp.toPx(), center = Offset(w * 0.5f, h * 0.6f))
                drawCircle(color = outlineColor, radius = 2.dp.toPx(), center = Offset(w * 0.5f, h * 0.75f))
            }
            "t-shirt" -> {
                // Classic crewneck tee
                val path = Path().apply {
                    moveTo(w * 0.35f, h * 0.18f)
                    // Neck cutout curve
                    quadraticTo(w * 0.5f, h * 0.23f, w * 0.65f, h * 0.18f)
                    // Right shoulder
                    lineTo(w * 0.82f, h * 0.25f)
                    // Right sleeve
                    lineTo(w * 0.92f, h * 0.42f)
                    lineTo(w * 0.78f, h * 0.48f)
                    // Armpit right
                    lineTo(w * 0.74f, h * 0.42f)
                    // Torso right
                    lineTo(w * 0.74f, h * 0.88f)
                    // Hem bottom
                    lineTo(w * 0.26f, h * 0.88f)
                    // Torso left
                    lineTo(w * 0.26f, h * 0.42f)
                    // Armpit left
                    lineTo(w * 0.22f, h * 0.48f)
                    // Left sleeve
                    lineTo(w * 0.08f, h * 0.42f)
                    lineTo(w * 0.18f, h * 0.25f)
                    close()
                }
                drawPath(path, color = garmentColor)
                drawPath(path, color = outlineColor, style = Stroke(width = 3.dp.toPx()))

                // Crewneck collar ring rim
                val neckPath = Path().apply {
                    moveTo(w * 0.35f, h * 0.18f)
                    quadraticTo(w * 0.5f, h * 0.23f, w * 0.65f, h * 0.18f)
                }
                drawPath(neckPath, color = outlineColor, style = Stroke(width = 2.5.dp.toPx()))
            }
            "pants" -> {
                // Pleated formal trousers
                val path = Path().apply {
                    moveTo(w * 0.28f, h * 0.15f)
                    lineTo(w * 0.72f, h * 0.15f)
                    // Hip right
                    lineTo(w * 0.78f, h * 0.35f)
                    // Leg right outer
                    lineTo(w * 0.72f, h * 0.88f)
                    // Cuff right
                    lineTo(w * 0.53f, h * 0.88f)
                    // Crotch inner right
                    lineTo(w * 0.5f, h * 0.48f)
                    // Crotch inner left
                    lineTo(w * 0.47f, h * 0.88f)
                    // Cuff left
                    lineTo(w * 0.28f, h * 0.88f)
                    // Leg left outer
                    lineTo(w * 0.22f, h * 0.35f)
                    close()
                }
                drawPath(path, color = garmentColor)
                drawPath(path, color = outlineColor, style = Stroke(width = 3.dp.toPx()))

                // Belt loops & zip placket lines
                drawLine(
                    color = outlineColor,
                    start = Offset(w * 0.5f, h * 0.15f),
                    end = Offset(w * 0.5f, h * 0.33f),
                    strokeWidth = 2.5.dp.toPx()
                )
                // Waistband line
                drawLine(
                    color = outlineColor,
                    start = Offset(w * 0.26f, h * 0.22f),
                    end = Offset(w * 0.74f, h * 0.22f),
                    strokeWidth = 2.dp.toPx()
                )
                // Left & right tuck crease line
                drawLine(color = outlineColor.copy(alpha = 0.5f), start = Offset(w * 0.38f, h * 0.22f), end = Offset(w * 0.38f, h * 0.85f), strokeWidth = 1.dp.toPx())
                drawLine(color = outlineColor.copy(alpha = 0.5f), start = Offset(w * 0.62f, h * 0.22f), end = Offset(w * 0.62f, h * 0.85f), strokeWidth = 1.dp.toPx())
            }
            "jeans" -> {
                // Classic rugged jeans layout
                val path = Path().apply {
                    moveTo(w * 0.28f, h * 0.15f)
                    lineTo(w * 0.72f, h * 0.15f)
                    lineTo(w * 0.76f, h * 0.35f)
                    lineTo(w * 0.70f, h * 0.88f)
                    lineTo(w * 0.52f, h * 0.88f)
                    lineTo(w * 0.5f, h * 0.48f)
                    lineTo(w * 0.48f, h * 0.88f)
                    lineTo(w * 0.30f, h * 0.88f)
                    lineTo(w * 0.24f, h * 0.35f)
                    close()
                }
                drawPath(path, color = garmentColor)
                drawPath(path, color = outlineColor, style = Stroke(width = 3.dp.toPx()))

                // Stitch detailing (Jeans highlight)
                // Pocket lines
                drawArc(
                    color = outlineColor,
                    startAngle = 0f,
                    sweepAngle = 100f,
                    useCenter = false,
                    topLeft = Offset(w * 0.22f, h * 0.18f),
                    size = Size(w * 0.15f, h * 0.12f),
                    style = Stroke(width = 2.dp.toPx())
                )
                drawArc(
                    color = outlineColor,
                    startAngle = 80f,
                    sweepAngle = 100f,
                    useCenter = false,
                    topLeft = Offset(w * 0.63f, h * 0.18f),
                    size = Size(w * 0.15f, h * 0.12f),
                    style = Stroke(width = 2.dp.toPx())
                )
                // Pocket rivets
                drawCircle(color = Color(0xFFD7C49E), radius = 1.5.dp.toPx(), center = Offset(w * 0.35f, h * 0.24f))
                drawCircle(color = Color(0xFFD7C49E), radius = 1.5.dp.toPx(), center = Offset(w * 0.65f, h * 0.24f))
            }
            "shorts" -> {
                // Stylish tailored hot-weather shorts
                val path = Path().apply {
                    moveTo(w * 0.28f, h * 0.15f)
                    lineTo(w * 0.72f, h * 0.15f)
                    // Hip right
                    lineTo(w * 0.76f, h * 0.32f)
                    // Leg right outer edge
                    lineTo(w * 0.71f, h * 0.52f)
                    // Cuff right
                    lineTo(w * 0.52f, h * 0.52f)
                    // Crotch inner right
                    lineTo(w * 0.5f, h * 0.40f)
                    // Crotch inner left
                    lineTo(w * 0.48f, h * 0.52f)
                    // Cuff left
                    lineTo(w * 0.29f, h * 0.52f)
                    // Leg left outer edge
                    lineTo(w * 0.24f, h * 0.32f)
                    close()
                }
                drawPath(path, color = garmentColor)
                drawPath(path, color = outlineColor, style = Stroke(width = 3.dp.toPx()))

                // Waistband line
                drawLine(
                    color = outlineColor,
                    start = Offset(w * 0.26f, h * 0.22f),
                    end = Offset(w * 0.74f, h * 0.22f),
                    strokeWidth = 2.dp.toPx()
                )
                // Fly line
                drawLine(
                    color = outlineColor,
                    start = Offset(w * 0.5f, h * 0.15f),
                    end = Offset(w * 0.5f, h * 0.30f),
                    strokeWidth = 2.5.dp.toPx()
                )
            }
            "hoodie" -> {
                // Heavy loose hoodie
                val path = Path().apply {
                    // Hood outline
                    moveTo(w * 0.42f, h * 0.22f)
                    quadraticTo(w * 0.55f, h * 0.05f, w * 0.58f, h * 0.22f)
                    lineTo(w * 0.78f, h * 0.28f)
                    // Sleeves
                    lineTo(w * 0.90f, h * 0.58f)
                    lineTo(w * 0.80f, h * 0.65f)
                    lineTo(w * 0.72f, h * 0.48f)
                    // Torso
                    lineTo(w * 0.72f, h * 0.85f)
                    // Bottom ribbing
                    lineTo(w * 0.28f, h * 0.85f)
                    lineTo(w * 0.28f, h * 0.48f)
                    lineTo(w * 0.20f, h * 0.65f)
                    lineTo(w * 0.10f, h * 0.58f)
                    lineTo(w * 0.22f, h * 0.28f)
                    close()
                }
                drawPath(path, color = garmentColor)
                drawPath(path, color = outlineColor, style = Stroke(width = 3.dp.toPx()))

                // Draw Hood lining scoop inner
                val innerHood = Path().apply {
                    moveTo(w * 0.42f, h * 0.22f)
                    quadraticTo(w * 0.5f, h * 0.30f, w * 0.58f, h * 0.22f)
                    quadraticTo(w * 0.5f, h * 0.10f, w * 0.42f, h * 0.22f)
                }
                drawPath(innerHood, color = outlineColor.copy(alpha = 0.3f))
                drawPath(innerHood, color = outlineColor, style = Stroke(width = 2.dp.toPx()))

                // Draw Kangaroo Pocket pouch in front
                val pocket = Path().apply {
                    moveTo(w * 0.38f, h * 0.62f)
                    lineTo(w * 0.62f, h * 0.62f)
                    lineTo(w * 0.68f, h * 0.78f)
                    lineTo(w * 0.32f, h * 0.78f)
                    close()
                }
                drawPath(pocket, color = outlineColor, style = Stroke(width = 2.dp.toPx()))

                // Draws dangling hood drawstrings
                drawLine(color = outlineColor, start = Offset(w * 0.47f, h * 0.26f), end = Offset(w * 0.47f, h * 0.42f), strokeWidth = 2.dp.toPx())
                drawLine(color = outlineColor, start = Offset(w * 0.53f, h * 0.26f), end = Offset(w * 0.53f, h * 0.40f), strokeWidth = 2.dp.toPx())
            }
            "jacket" -> {
                // Classic smart blazer style
                val path = Path().apply {
                    moveTo(w * 0.3f, h * 0.16f)
                    lineTo(w * 0.42f, h * 0.2f)
                    lineTo(w * 0.58f, h * 0.2f)
                    lineTo(w * 0.7f, h * 0.16f)
                    // Sleeves
                    lineTo(w * 0.86f, h * 0.44f)
                    lineTo(w * 0.76f, h * 0.48f)
                    lineTo(w * 0.72f, h * 0.38f)
                    // Torso sides
                    lineTo(w * 0.72f, h * 0.90f)
                    // Bottom hem
                    lineTo(w * 0.28f, h * 0.90f)
                    // Torso left side
                    lineTo(w * 0.28f, h * 0.38f)
                    lineTo(w * 0.24f, h * 0.48f)
                    lineTo(w * 0.14f, h * 0.44f)
                    close()
                }
                drawPath(path, color = garmentColor)
                drawPath(path, color = outlineColor, style = Stroke(width = 3.dp.toPx()))

                // Wide lapel lines (Double blazer shawl collar)
                val leftLapel = Path().apply {
                    moveTo(w * 0.3f, h * 0.16f)
                    lineTo(w * 0.43f, h * 0.38f)
                    lineTo(w * 0.5f, h * 0.65f)
                    lineTo(w * 0.41f, h * 0.38f)
                }
                drawPath(leftLapel, color = outlineColor, style = Stroke(width = 2.dp.toPx()))

                val rightLapel = Path().apply {
                    moveTo(w * 0.7f, h * 0.16f)
                    lineTo(w * 0.57f, h * 0.38f)
                    lineTo(w * 0.5f, h * 0.65f)
                    lineTo(w * 0.59f, h * 0.38f)
                }
                drawPath(rightLapel, color = outlineColor, style = Stroke(width = 2.dp.toPx()))

                // Main buttons
                drawCircle(color = outlineColor, radius = 2.5.dp.toPx(), center = Offset(w * 0.46f, h * 0.58f))
                drawCircle(color = outlineColor, radius = 2.5.dp.toPx(), center = Offset(w * 0.46f, h * 0.68f))
            }
            "shoes" -> {
                // Sleek premium minimal trainers
                val path = Path().apply {
                    moveTo(w * 0.15f, h * 0.62f)
                    // Ankle curve collar
                    quadraticTo(w * 0.35f, h * 0.32f, w * 0.48f, h * 0.35f)
                    // Lace tongue line
                    lineTo(w * 0.75f, h * 0.58f)
                    // Toe cap curve
                    quadraticTo(w * 0.92f, h * 0.68f, w * 0.88f, h * 0.78f)
                    // Sole line
                    lineTo(w * 0.15f, h * 0.78f)
                    close()
                }
                drawPath(path, color = garmentColor)
                drawPath(path, color = outlineColor, style = Stroke(width = 3.dp.toPx()))

                // Flat sole outsole
                val sole = Path().apply {
                    moveTo(w * 0.15f, h * 0.74f)
                    lineTo(w * 0.86f, h * 0.74f)
                    lineTo(w * 0.88f, h * 0.78f)
                    lineTo(w * 0.15f, h * 0.78f)
                    close()
                }
                drawPath(sole, color = outlineColor.copy(alpha = 0.4f))
                drawPath(sole, color = outlineColor, style = Stroke(width = 2.dp.toPx()))

                // Lacing diagonal strokes
                drawLine(color = outlineColor, start = Offset(w * 0.48f, h * 0.45f), end = Offset(w * 0.58f, h * 0.50f), strokeWidth = 2.dp.toPx())
                drawLine(color = outlineColor, start = Offset(w * 0.54f, h * 0.49f), end = Offset(w * 0.64f, h * 0.54f), strokeWidth = 2.dp.toPx())
                drawLine(color = outlineColor, start = Offset(w * 0.60f, h * 0.53f), end = Offset(w * 0.70f, h * 0.58f), strokeWidth = 2.dp.toPx())
            }
            "accessories" -> {
                // Elegant luxury dress watch layout
                // Watch strap vertical bands
                drawRoundRect(
                    color = outlineColor.copy(alpha = 0.5f),
                    topLeft = Offset(w * 0.44f, h * 0.15f),
                    size = Size(w * 0.12f, h * 0.7f),
                    cornerRadius = CornerRadius(2.dp.toPx()),
                    style = Stroke(width = 2.dp.toPx())
                )
                // Watch circular dial case
                drawCircle(
                    color = garmentColor,
                    radius = w * 0.22f,
                    center = Offset(w * 0.5f, h * 0.5f)
                )
                drawCircle(
                    color = outlineColor,
                    radius = w * 0.22f,
                    center = Offset(w * 0.5f, h * 0.5f),
                    style = Stroke(width = 3.dp.toPx())
                )
                // Inner bezel ring
                drawCircle(
                    color = outlineColor.copy(alpha = 0.6f),
                    radius = w * 0.16f,
                    center = Offset(w * 0.5f, h * 0.5f),
                    style = Stroke(width = 1.5.dp.toPx())
                )
                // Dynamic watch hands
                drawLine(color = outlineColor, start = Offset(w * 0.5f, h * 0.5f), end = Offset(w * 0.5f, h * 0.38f), strokeWidth = 2.dp.toPx())
                drawLine(color = outlineColor, start = Offset(w * 0.5f, h * 0.5f), end = Offset(w * 0.62f, h * 0.5f), strokeWidth = 2.dp.toPx())
            }
            else -> {
                // Sleek Modern Hanger placeholder drawing for custom categories
                val pathHanger = Path().apply {
                    // Hook
                    moveTo(w * 0.5f, h * 0.2f)
                    quadraticTo(w * 0.55f, h * 0.12f, w * 0.5f, h * 0.08f)
                    quadraticTo(w * 0.45f, h * 0.12f, w * 0.5f, h * 0.2f)
                    // Hanger shoulders
                    lineTo(w * 0.15f, h * 0.45f)
                    lineTo(w * 0.85f, h * 0.45f)
                    close()
                }
                drawPath(pathHanger, color = garmentColor.copy(alpha = 0.5f))
                drawPath(pathHanger, color = outlineColor, style = Stroke(width = 3.dp.toPx()))
                
                // Draped outline shape
                val pathDrape = Path().apply {
                    moveTo(w * 0.22f, h * 0.45f)
                    lineTo(w * 0.78f, h * 0.45f)
                    lineTo(w * 0.7f, h * 0.82f)
                    lineTo(w * 0.3f, h * 0.82f)
                    close()
                }
                drawPath(pathDrape, color = garmentColor)
                drawPath(pathDrape, color = outlineColor, style = Stroke(width = 2.dp.toPx()))
            }
        }
    }
}
