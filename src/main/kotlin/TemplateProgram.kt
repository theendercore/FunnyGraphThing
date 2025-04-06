import org.openrndr.ApplicationBuilder
import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.ColorBuffer
import org.openrndr.draw.loadFont
import org.openrndr.extra.color.presets.DIM_GRAY
import org.openrndr.math.Vector2
import java.io.File

val DATA_FOLDER = File("./data")

val bubbles = mutableListOf(
    Bubble("apple"),
    Bubble("stick"),
)

fun main() = application {
    configure {
        width = 768
        height = 576
    }
    loadData()
    graphApp()
}



fun loadData() = DATA_FOLDER.listFiles()?.forEach { file ->
    if (file.isFile) {
        println(file.absolutePath)
    }
}

fun ApplicationBuilder.graphApp() = program {
    val font = loadFont("data/fonts/default.otf", 12.0)

    extend {
        drawer.clear(ColorRGBa.DIM_GRAY)
        drawer.fontMap = font

        // Draw bubbles
        bubbles.forEachIndexed { idx, bubble ->
            drawer.fill = ColorRGBa.TRANSPARENT
            drawer.stroke = ColorRGBa.WHITE
            drawer.circle(Vector2(60.0, (idx + 1) * 60.0), 20.0)

            drawer.fill = ColorRGBa.WHITE
            drawer.text(bubble.id, 46.0, (idx + 1) * 61.0)

            // Draw image if present
            bubble.image?.let { image -> drawer.image(image) }
        }
    }
}

data class Bubble(val id: String, val image: ColorBuffer? = null)
