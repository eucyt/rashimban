package com.github.eucyt.rashimban.ui

import com.intellij.ui.Gray
import java.awt.Graphics
import java.awt.event.MouseEvent
import java.util.UUID
import javax.swing.JPanel

class DiagramPanel : JPanel() {
    private val connections: MutableSet<Pair<UUID, UUID>> = mutableSetOf()

    init {
        layout = null
        background = Gray._40
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

    override fun paintComponent(g: Graphics) {
        // paint connections
        super.paintComponent(g)
        g.color = Gray._255
        for (conn in connections) {
            val first = getDraggableBox(conn.first)
            val second = getDraggableBox(conn.second)
            require(first != null) { "The draggable box must be exist: boxId=${conn.first}" }
            require(second != null) { "The draggable box must be exist: boxId=${conn.second}" }
            g.drawLine(first.x + first.width / 2, first.y + first.height / 2, second.x + second.width / 2, second.y + second.height / 2)
        }
    }
}
