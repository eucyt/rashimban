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
    private var previousX = 0
    private var previousY = 0
    private val baseFont: Font = UIManager.getFont("Label.font")

    init {
        layout = null
        background = Gray._40

        addMouseListener(
            object : MouseAdapter() {
                override fun mousePressed(e: MouseEvent) {
                    super.mousePressed(e)
                    previousX = e.x
                    previousY = e.y
                }
            },
        )

        addMouseMotionListener(
            object : MouseAdapter() {
                override fun mouseDragged(e: MouseEvent) {
                    super.mouseDragged(e)

                    val dx = e.x - previousX
                    val dy = e.y - previousY
                    previousX = e.x
                    previousY = e.y

                    // Move all components if panel is dragged
                    components.forEach {
                        if (it is JPanelForDouble) {
                            it.setLocation(it.doubleX + dx, it.doubleY + dy)
                        } else {
                            it.setLocation(it.x + dx, it.y + dy)
                        }
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
        box.setLocation(x, y)
        add(box)
        
        // Bring newly added box to the foreground
        setComponentZOrder(box, 0)
        repaint()
        
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
        super.paintComponent(g)

        // Enable antialiasing for smoother lines
        val g2d = g.create() as java.awt.Graphics2D
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON)
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, java.awt.RenderingHints.VALUE_RENDER_QUALITY)

        // paint connections
        g2d.color = Gray._255
        for (conn in connections) {
            val first = getDraggableBox(conn.first)
            val second = getDraggableBox(conn.second)
            require(first != null) { "The draggable box must be exist: boxId=${conn.first}" }
            require(second != null) { "The draggable box must be exist: boxId=${conn.second}" }
            g2d.drawLine(
                (first.doubleX + first.doubleWidth / 2).toInt(),
                (first.doubleY + first.doubleHeight / 2).toInt(),
                (second.doubleX + second.doubleWidth / 2).toInt(),
                (second.doubleY + second.doubleHeight / 2).toInt(),
            )
        }
        g2d.dispose()
    }

    private fun applyScaleToDraggableBox(
        box: DraggableBox,
        scaleRatio: Double,
    ) {
        val newX = (box.doubleX * scaleRatio)
        val newY = (box.doubleY * scaleRatio)
        val newWidth = (box.doubleWidth * scaleRatio)
        val newHeight = (box.doubleHeight * scaleRatio)
        box.setBounds(newX, newY, newWidth, newHeight)
        box.components.filterIsInstance<JLabel>().forEach { it.font = it.font.deriveFont((baseFont.size * scale).toFloat()) }
    }
}
