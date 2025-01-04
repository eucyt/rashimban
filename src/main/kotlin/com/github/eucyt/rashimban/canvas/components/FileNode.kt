package com.github.eucyt.rashimban.canvas.components

import com.intellij.openapi.vfs.VirtualFile
import java.awt.Color
import java.awt.Graphics2D
import java.util.UUID

private const val PADDING = 5

class FileNode(
    val virtualFile: VirtualFile,
    var x: Int,
    var y: Int,
) {
    val nodeId: UUID = UUID.randomUUID()
    var width: Int = 0
    var height: Int = 0

    fun draw(
        g: Graphics2D,
        currentFile: VirtualFile?,
    ) {
        val fm = g.fontMetrics
        val text = virtualFile.name
        width = fm.stringWidth(text) + (PADDING * 2)
        height = fm.height + (PADDING * 2)

        val isCurrentFile = currentFile?.path == virtualFile.path

        g.color = Color.DARK_GRAY
        g.fillRoundRect(x, y, width, height, PADDING, PADDING)

        g.color = if (isCurrentFile) Color.GRAY else Color.LIGHT_GRAY
        g.drawRoundRect(x, y, width, height, PADDING, PADDING)
        g.drawString(text, x + PADDING, y + fm.ascent + PADDING)
    }

    fun contains(
        xx: Int,
        yy: Int,
    ): Boolean = (x <= xx && xx <= x + width) && (y <= yy && yy <= y + height)
}
