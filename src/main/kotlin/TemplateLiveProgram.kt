import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.draw.LineCap
import org.openrndr.extra.olive.oliveProgram
import org.openrndr.math.Vector2

fun main() = application {
    configure {
        width = 800
        height = 800
    }
    oliveProgram {
        val graph = TreeGraph(width.toDouble(), 200.0)
        graph.setup()
        extend {
            drawer.clear(ColorRGBa.WHITE)
            graph.draw(drawer)
        }
    }
}

// Core node class representing each point in the tree
class TreeNode(
    var position: Vector2 = Vector2.ZERO,
    val size: Vector2 = Vector2(40.0, 40.0),
    val value: String = "",
    var parent: TreeNode? = null,
    val children: MutableList<TreeNode> = mutableListOf()
) {

    init {
        this.children.addAll(children)
    }

    // Smooth movement parameters
    private var velocity = Vector2.ZERO
    private val damping = 0.95
    private val attractionStrength = 0.05
    var targetPosition = position.copy()

    fun add(node: TreeNode): TreeNode {
        node.parent = this
        children.add(node)
        return node
    }

    fun update(deltaTime: Double) {
        // Update position smoothly towards target
        velocity *= damping
        velocity += (targetPosition - position) * attractionStrength
        position += velocity

        // Recursively update children
        children.forEach { it.update(deltaTime) }
    }

    fun draw(drawer: Drawer) {
        // Draw connections to children
        children.forEach { child ->
            drawer.stroke = ColorRGBa.BLACK
            drawer.lineCap = LineCap.ROUND
//            drawer.lineStyle.weight = 2.0
            drawer.lineSegment(position, child.position)
            child.draw(drawer)
        }

        // Draw node
        drawer.fill = ColorRGBa.WHITE
        drawer.stroke = ColorRGBa.BLACK
        drawer.rectangle(position.x, position.y, size.x, size.y)

        // Draw node value
        drawer.fill = ColorRGBa.BLACK
        drawer.text(value, position.x + size.x / 4, position.y + size.y / 2)
    }
}

// Main program class
class TreeGraph(val width: Double, val height: Double) {
    private lateinit var root: TreeNode

    fun setup() {
        // Create sample tree structure
        root = TreeNode(Vector2(width / 2.0, height), value = "Root")

        val nodeA = root.add(TreeNode(value = "A"))
        val nodeB = root.add(TreeNode(value = "B"))
        val nodeC = nodeA.add(TreeNode(value = "C"))
        val nodeD = nodeA.add(TreeNode(value = "D"))

        updateLayout()
    }

    fun update(deltaTime: Double) {
        updateLayout()
        root.update(deltaTime)
    }


    private fun updateLayout() {
        layoutNode(root, Vector2(width / 2.0, height / 2.0))
    }

    private fun layoutNode(node: TreeNode, startPosition: Vector2) {
        val spacing = 80.0
        var xOffset = 0.0

        // Set target position for current node
        node.targetPosition = startPosition.copy()

        // Layout children
        node.children.forEachIndexed { index, child ->
            val childPosition = startPosition.copy()
            val x = childPosition.x + xOffset
            val y = childPosition.y + spacing
            val pos = Vector2(x, y)
            child.position = pos

            layoutNode(child, pos)

            xOffset += spacing
        }
    }

    fun draw(drawer: Drawer) {
        drawer.clear(ColorRGBa.WHITE)
        root.draw(drawer)
    }
}