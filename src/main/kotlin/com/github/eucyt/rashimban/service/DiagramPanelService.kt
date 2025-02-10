package com.github.eucyt.rashimban.service

import com.github.eucyt.rashimban.listeners.GotoDeclarationListener
import com.github.eucyt.rashimban.ui.DiagramPanel
import com.intellij.openapi.actionSystem.ex.AnActionListener
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import java.awt.event.MouseEvent
import java.util.UUID

private const val CONNECTED_NODES_INITIAL_DISTANCE_X = 0
private const val CONNECTED_NODES_INITIAL_DISTANCE_Y = 50
private const val INITIAL_X = 100
private const val INITIAL_Y = 50

class DiagramPanelService(
    private val project: Project,
    private val diagramPanel: DiagramPanel,
) {
    private val files: MutableMap<UUID, VirtualFile> = mutableMapOf()

    init {
        // Add node by code jump
        val connection = project.messageBus.connect()
        val gotoDeclarationListener = GotoDeclarationListener { from, to -> addRelation(from, to) }
        connection.subscribe(AnActionListener.TOPIC, gotoDeclarationListener)
        connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, gotoDeclarationListener)
    }

    fun addRelation(
        from: VirtualFile,
        to: VirtualFile,
    ) {
        val fromBoxId = addFileDraggableBox(from)
        val toBoxId =
            addFileDraggableBox(
                to,
                INITIAL_X + CONNECTED_NODES_INITIAL_DISTANCE_X,
                INITIAL_Y + CONNECTED_NODES_INITIAL_DISTANCE_Y,
            )
        diagramPanel.addConnection(fromBoxId, toBoxId)
    }

    private fun openFile(boxId: UUID) {
        val filepath = files[boxId]?.path ?: return
        val file = LocalFileSystem.getInstance().findFileByPath(filepath) ?: return
        FileEditorManager.getInstance(project).openFile(file, true)
    }

    private fun removeFileInDiagram(boxId: UUID) {
        diagramPanel.removeDraggableBox(boxId)
        files.remove(boxId)
    }

    private fun addFileDraggableBox(
        virtualFile: VirtualFile,
        x: Int = INITIAL_X,
        y: Int = INITIAL_Y,
    ): UUID =
        getBoxId(virtualFile)
            ?: UUID.randomUUID().also {
                diagramPanel.addDraggableBox(it, virtualFile.name, x, y) { e: MouseEvent? ->
                    if (e?.button == MouseEvent.BUTTON1) {
                        openFile(it)
                    } else if (e?.button == MouseEvent.BUTTON3) {
                        removeFileInDiagram(it)
                    }
                }
                files[it] = virtualFile
            }

    private fun getBoxId(virtualFile: VirtualFile): UUID? = files.filter { it.value.url == virtualFile.url }.keys.firstOrNull()
}
