package com.github.eucyt.rashimban.ui

import com.intellij.ui.Gray
import java.awt.Font
import java.awt.Graphics
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent
import java.util.UUID
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.UIManager

private const val MIN_SCALE = 0.1
private const val MAX_SCALE = 2.0
private const val SCALE_FACTOR_PER_FRAME = 0.025f

class DiagramPanel : JPanel() {
    var scale = 1.0
    private val connections: MutableSet<Pair<UUID, UUID>> = mutableSetOf()
    private var lastX = 0
    private var lastY = 0
    private val baseFont: Font = UIManager.getFont("Label.font")

    init {
        layout = null
        background = Gray._40

        addMouseListener(
            object : MouseAdapter() {
                override fun mousePressed(e: MouseEvent) {
                    super.mousePressed(e)
                    lastX = e.x
                    lastY = e.y
                }
            },
        )

        addMouseMotionListener(
            object : MouseAdapter() {
                override fun mouseDragged(e: MouseEvent) {
                    super.mouseDragged(e)

                    val dx = e.x - lastX
                    val dy = e.y - lastY
                    lastX = e.x
                    lastY = e.y

                    // Move all components if panel is dragged
                    components.forEach {
                        it.setLocation(it.x + dx, it.y + dy)
                    }

                    repaint()
                }
            },
        )

        addMouseWheelListener { e: MouseWheelEvent ->
            val oldScale = scale
            val scaleFactor = if (e.preciseWheelRotation < 0) (1f + SCALE_FACTOR_PER_FRAME) else (1F - SCALE_FACTOR_PER_FRAME)
            scale = (scale * scaleFactor).coerceIn(MIN_SCALE, MAX_SCALE)

            components.forEach {
                if (it is DraggableBox) {
                    applyScaleToDraggableBox(it, scale / oldScale)
                }
            }

            repaint()
        }
    }

    fun getDraggableBox(boxId: UUID): DraggableBox? = components.filterIsInstance<DraggableBox>().find { it.id == boxId }

    fun addDraggableBox(
        boxId: UUID,
        text: String,
        x: Double,
        y: Double,
        onClicked: ((e: MouseEvent?) -> Unit),
    ): DraggableBox {
        val box = DraggableBox(boxId, text, onClicked)
        applyScaleToDraggableBox(box, scale)
        box.setRealLocation(x, y)
        add(box)
        return box
    }

    fun removeDraggableBox(boxId: UUID) {
        remove(getDraggableBox(boxId))
        connections.removeIf { it.first == boxId || it.second == boxId }
        repaint()
    }

    fun addConnection(
        fromBoxId: UUID,
        toBoxId: UUID,
    ) {
        require(getDraggableBox(fromBoxId) != null) { "The draggable box must be exist: boxId=$fromBoxId" }
        require(getDraggableBox(toBoxId) != null) { "The draggable box must be exist: boxId=$toBoxId" }
        connections.add(Pair(fromBoxId, toBoxId))
    }

    fun setHighlightUniquely(boxId: UUID) {
        components.filterIsInstance<DraggableBox>().forEach { it.setHighlight(false) }
        getDraggableBox(boxId)?.setHighlight(true)
    }

    override fun paintComponent(g: Graphics) {
        // paint connections
        super.paintComponent(g)
        g.color = Gray._255

        // Enable antialiasing for smoother lines
        val g2d = g.create() as java.awt.Graphics2D
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, java.awt.RenderingHints.VALUE_RENDER_QUALITY)

        for (conn in connections) {
            val first = getDraggableBox(conn.first)
            val second = getDraggableBox(conn.second)
            require(first != null) { "The draggable box must be exist: boxId=${conn.first}" }
            require(second != null) { "The draggable box must be exist: boxId=${conn.second}" }
            g2d.drawLine(first.x + first.width / 2, first.y + first.height / 2, second.x + second.width / 2, second.y + second.height / 2)
        }
        g2d.dispose()
    }

    private fun applyScaleToDraggableBox(
        box: DraggableBox,
        scaleRatio: Double,
    ) {
        val bounds = box.getRealBounds()
        val newX = (bounds.x * scaleRatio)
        val newY = (bounds.y * scaleRatio)
        val newWidth = (bounds.width * scaleRatio)
        val newHeight = (bounds.height * scaleRatio)
        box.setRealBounds(newX, newY, newWidth, newHeight)
        box.components.filter { it is JLabel }.forEach { it.font = it.font.deriveFont((baseFont.size * scale).toFloat()) }
    }
}
