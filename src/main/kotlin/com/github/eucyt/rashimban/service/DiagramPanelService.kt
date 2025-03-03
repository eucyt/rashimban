package com.github.eucyt.rashimban.service

import com.github.eucyt.rashimban.listeners.CurrentFileChangeListener
import com.github.eucyt.rashimban.listeners.GotoDeclarationListener
import com.github.eucyt.rashimban.ui.DiagramPanel
import com.github.eucyt.rashimban.ui.DraggableBox
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
private const val INITIAL_X = 200.0
private const val INITIAL_Y = 50.0

class DiagramPanelService(
    private val project: Project,
    private val diagramPanel: DiagramPanel,
) {
    private val files: MutableMap<UUID, VirtualFile> = mutableMapOf()

    init {
        // Set listener adding node by code jump
        val connection = project.messageBus.connect()

        val gotoDeclarationListener = GotoDeclarationListener { from, to -> addRelation(from, to) }
        connection.subscribe(AnActionListener.TOPIC, gotoDeclarationListener)
        connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, gotoDeclarationListener)

        val openFileListener = CurrentFileChangeListener { virtualFile -> onCurrentFileChanged(virtualFile) }
        connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, openFileListener)
    }

    private fun addRelation(
        from: VirtualFile,
        to: VirtualFile,
    ) {
        val fromDraggableBox = addFileDraggableBox(from)
        val toDraggableBox =
            addFileDraggableBox(
                to,
            ) {
                it.setRealLocation(
                    fromDraggableBox.x + (fromDraggableBox.width - it.width) / 2 +
                        CONNECTED_NODES_INITIAL_DISTANCE_X * diagramPanel.scale,
                    fromDraggableBox.y + CONNECTED_NODES_INITIAL_DISTANCE_Y * diagramPanel.scale,
                )
            }
        diagramPanel.addConnection(fromDraggableBox.id, toDraggableBox.id)
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
        onCleated: (DraggableBox) -> Unit = {},
    ): DraggableBox {
        val boxId =
            getBoxId(virtualFile)
                ?: UUID.randomUUID().also {
                    diagramPanel
                        .addDraggableBox(it, virtualFile.name, INITIAL_X, INITIAL_Y) { e: MouseEvent? ->
                            if (e?.button == MouseEvent.BUTTON1) {
                                openFile(it)
                            } else if (e?.button == MouseEvent.BUTTON3) {
                                removeFileInDiagram(it)
                            }
                        }.also(onCleated)
                    files[it] = virtualFile
                }
        return diagramPanel.getDraggableBox(boxId)
            ?: throw IllegalStateException("Inconsistency detected between DiagramPanelService and DiagramPanel.")
    }

    private fun onCurrentFileChanged(file: VirtualFile) {
        val boxId = getBoxId(file) ?: return
        diagramPanel.setHighlightUniquely(boxId)
    }

    private fun getBoxId(virtualFile: VirtualFile): UUID? = files.filter { it.value.url == virtualFile.url }.keys.firstOrNull()
}
