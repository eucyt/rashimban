package com.github.eucyt.rashimban.ui

import com.intellij.ui.Gray
import java.awt.Graphics
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.UUID
import javax.swing.JPanel

class DiagramPanel : JPanel() {
    private val connections: MutableSet<Pair<UUID, UUID>> = mutableSetOf()
    private var previousX = 0
    private var previousY = 0

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
                        it.setLocation(it.x + dx, it.y + dy)
                    }

                    repaint()
                }
            },
        )
    }

    fun getDraggableBox(boxId: UUID): DraggableBox? = components.filterIsInstance<DraggableBox>().find { it.id == boxId }

    fun addDraggableBox(
        boxId: UUID,
        text: String,
        x: Int,
        y: Int,
        onClicked: ((e: MouseEvent?) -> Unit),
    ): DraggableBox {
        val box = DraggableBox(boxId, text, onClicked)
        box.setLocation(x, y)
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
            g2d.drawLine(first.x + first.width / 2, first.y + first.height / 2, second.x + second.width / 2, second.y + second.height / 2)
        }
        g2d.dispose()
    }
}
