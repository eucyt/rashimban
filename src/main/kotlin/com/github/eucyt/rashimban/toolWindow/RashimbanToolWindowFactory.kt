package com.github.eucyt.rashimban.toolWindow

import com.github.eucyt.rashimban.canvas.RashimbanCanvas
import com.github.eucyt.rashimban.canvas.components.FileNodeManager
import com.github.eucyt.rashimban.listeners.GotoDeclarationListener
import com.github.eucyt.rashimban.listeners.MouseListener
import com.intellij.openapi.actionSystem.ex.AnActionListener
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class RashimbanToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(
        project: Project,
        toolWindow: ToolWindow,
    ) {
        val fileNodeManager = FileNodeManager()
        val canvas = RashimbanCanvas(project, fileNodeManager)

        val mouseListener = MouseListener(project, fileNodeManager) { canvas.repaint() }
        canvas.addMouseListener(mouseListener)
        canvas.addMouseMotionListener(mouseListener)

        // Add node by code jump
        val connection = project.messageBus.connect()
        val gotoDeclarationListener = GotoDeclarationListener(fileNodeManager) { canvas.repaint() }
        connection.subscribe(AnActionListener.TOPIC, gotoDeclarationListener)
        connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, gotoDeclarationListener)

        val content = ContentFactory.getInstance().createContent(canvas, null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true
}
