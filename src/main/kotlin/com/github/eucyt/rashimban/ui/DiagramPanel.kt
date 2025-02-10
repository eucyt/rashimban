package com.github.eucyt.rashimban.ui

import com.intellij.ui.Gray
import java.awt.Graphics
import java.awt.event.MouseEvent
import java.util.UUID
import javax.swing.JPanel
import kotlin.collections.ArrayList

class DiagramPanel : JPanel() {
    private val boxes: MutableList<DraggableBox> = ArrayList()
    private val connections: MutableSet<Pair<UUID, UUID>> = mutableSetOf()

    init {
        layout = null
        background = Gray._40
    }

    fun addDraggableBox(
        id: UUID,
        text: String,
        x: Int,
        y: Int,
        onClicked: ((e: MouseEvent?) -> Unit),
    ) {
        val box = DraggableBox(id, text, onClicked)
        box.setLocation(x, y)
        boxes.add(box)
        add(box)
    }

    fun removeDraggableBox(boxId: UUID) {
        val box = boxes.find { it.id == boxId }
        remove(box)
        boxes.remove(box)
        connections.removeIf { it.first == boxId || it.second == boxId }
        repaint()
    }

    fun addConnection(
        from: UUID,
        to: UUID,
    ) {
        connections.add(Pair(from, to))
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        g.color = Gray._255
        for (conn in connections) {
            val first = boxes.find { it.id == conn.first }
            val second = boxes.find { it.id == conn.second }
            if (first == null || second == null) {
                continue
            }
            g.drawLine(first.x + first.width / 2, first.y + first.height / 2, second.x + second.width / 2, second.y + second.height / 2)
        }
    }
}
