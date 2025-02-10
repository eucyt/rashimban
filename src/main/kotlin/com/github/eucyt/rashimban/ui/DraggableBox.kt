package com.github.eucyt.rashimban.ui

import com.intellij.ui.JBColor
import java.awt.BorderLayout
import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.UUID
import javax.swing.BorderFactory
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

private const val PADDING_X = 5
private const val PADDING_Y = 5

class DraggableBox(
    val id: UUID,
    text: String?,
    onClicked: ((e: MouseEvent?) -> Unit) = { },
) : JPanel() {
    private var offsetX = 0
    private var offsetY = 0

    init {
        layout = BorderLayout()
        border = BorderFactory.createLineBorder(JBColor.DARK_GRAY)
        val label = JLabel(text, SwingConstants.CENTER)
        label.border = BorderFactory.createEmptyBorder(PADDING_Y, PADDING_X, PADDING_Y, PADDING_X)
        add(label, BorderLayout.CENTER)

        setSize(label.preferredSize.width + PADDING_X, label.preferredSize.height + PADDING_Y)

        addMouseListener(
            object : MouseAdapter() {
                override fun mousePressed(e: MouseEvent) {
                    offsetX = e.getX()
                    offsetY = e.getY()
                }

                override fun mouseClicked(e: MouseEvent?) {
                    super.mouseClicked(e)
                    onClicked(e)
                }
            },
        )

        addMouseMotionListener(
            object : MouseAdapter() {
                override fun mouseDragged(e: MouseEvent) {
                    val p: Point = location
                    setLocation(p.x + e.getX() - offsetX, p.y + e.getY() - offsetY)
                    parent.repaint()
                }
            },
        )
    }
}
