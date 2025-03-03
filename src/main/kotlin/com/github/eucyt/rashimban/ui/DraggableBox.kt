package com.github.eucyt.rashimban.ui

import com.intellij.ui.JBColor
import java.awt.BorderLayout
import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.geom.Rectangle2D
import java.util.UUID
import javax.swing.BorderFactory
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

private const val PADDING_X = 5
private const val PADDING_Y = 5
private val DEFAULT_COLOR = JBColor.DARK_GRAY
private val HIGHLIGHT_COLOR = JBColor.GRAY

class DraggableBox(
    val id: UUID,
    text: String?,
    onClicked: ((e: MouseEvent?) -> Unit) = { },
) : JPanel() {
    private var offsetX = 0
    private var offsetY = 0

    // To prevent rounding error, use double positions instead of int ones
    private var realX: Double
    private var realY: Double
    private var realWidth: Double
    private var realHeight: Double

    init {
        layout = BorderLayout()
        border = BorderFactory.createLineBorder(DEFAULT_COLOR, 1, true)
        val label = JLabel(text, SwingConstants.CENTER)
        label.border = BorderFactory.createEmptyBorder(PADDING_Y, PADDING_X, PADDING_Y, PADDING_X)
        add(label, BorderLayout.CENTER)

        setSize(label.preferredSize.width + PADDING_X, label.preferredSize.height + PADDING_Y)

        realX = this.x.toDouble()
        realY = this.y.toDouble()
        realWidth = this.width.toDouble()
        realHeight = this.height.toDouble()

        addMouseListener(
            object : MouseAdapter() {
                override fun mousePressed(e: MouseEvent) {
                    super.mousePressed(e)
                    offsetX = e.x
                    offsetY = e.y
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
                    super.mouseDragged(e)

                    val p: Point = location
                    setRealLocation((p.x + e.x - offsetX).toDouble(), (p.y + e.y - offsetY).toDouble())
                    parent.repaint()
                }
            },
        )
    }

    fun setHighlight(isHighlight: Boolean) {
        val color = if (isHighlight) HIGHLIGHT_COLOR else DEFAULT_COLOR
        border = BorderFactory.createLineBorder(color, 1, true)
        (getComponent(0) as JLabel).foreground = color
    }

    override fun setLocation(
        x: Int,
        y: Int,
    ) {
        super.setLocation(x, y)
        realX = this.x.toDouble()
        realY = this.y.toDouble()
    }

    override fun setLocation(p: Point) {
        super.setLocation(p)
        realX = this.x.toDouble()
        realY = this.y.toDouble()
    }

    fun setRealLocation(
        x: Double,
        y: Double,
    ) {
        realX = x
        realY = y
        setLocation(x.toInt(), y.toInt())
    }

    fun getRealBounds() = Rectangle2D.Double(realX, realY, realWidth, realHeight)

    fun setRealBounds(
        x: Double,
        y: Double,
        width: Double,
        height: Double,
    ) {
        realX = x
        realY = y
        realWidth = width
        realHeight = height
        setBounds(x.toInt(), y.toInt(), width.toInt(), height.toInt())
    }
}
