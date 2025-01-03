package com.github.eucyt.rashimban.canvas

import com.github.eucyt.rashimban.canvas.components.FileNodeManager
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import javax.swing.JPanel

class RashimbanCanvas(
    private val fileNodeManager: FileNodeManager,
) : JPanel() {
    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2d = g as Graphics2D
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        fileNodeManager.draw(g2d)
    }
}
