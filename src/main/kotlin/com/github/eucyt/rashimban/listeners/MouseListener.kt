package com.github.eucyt.rashimban.listeners

import com.github.eucyt.rashimban.canvas.components.FileNode
import com.github.eucyt.rashimban.canvas.components.FileNodeManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

class MouseListener(
    private val project: Project,
    private val fileNodeManager: FileNodeManager,
    private val repaint: () -> Unit,
) : MouseAdapter() {
    private var lastMousePosition: Point? = null
    private var selectedNode: FileNode? = null

    override fun mousePressed(e: MouseEvent) {
        selectedNode = fileNodeManager.contains(Point(e.x, e.y))
        lastMousePosition = e.point
    }

    override fun mouseReleased(e: MouseEvent) {
        selectedNode = null
        lastMousePosition = null
    }

    // Move node by drag node
    override fun mouseDragged(e: MouseEvent) {
        selectedNode?.let { node ->
            lastMousePosition?.let { lastPos ->
                val dx = e.x - lastPos.x
                val dy = e.y - lastPos.y
                node.x += dx
                node.y += dy
                lastMousePosition = e.point
                repaint()
            }
        }
    }

    override fun mouseClicked(e: MouseEvent) {
        if (e.button == MouseEvent.BUTTON1) {
            // Open file by click node
            selectedNode = fileNodeManager.contains(Point(e.x, e.y)) ?: return
            val file = LocalFileSystem.getInstance().findFileByPath(selectedNode!!.virtualFile.path) ?: return
            FileEditorManager.getInstance(project).openFile(file, true)
        } else if (e.button == MouseEvent.BUTTON3) {
            // Delete node by right click
            selectedNode = fileNodeManager.contains(Point(e.x, e.y)) ?: return
            fileNodeManager.remove(selectedNode!!.nodeId)
            repaint()
        }
    }
}
