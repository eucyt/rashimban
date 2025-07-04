package com.github.eucyt.rashimban.service

import com.github.eucyt.rashimban.listeners.CodeJumpListener
import com.github.eucyt.rashimban.listeners.CurrentFileChangeListener
import com.github.eucyt.rashimban.ui.DiagramPanel
import com.github.eucyt.rashimban.ui.DraggableBox
import com.intellij.openapi.actionSystem.ex.AnActionListener
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import java.awt.event.MouseEvent
import java.io.File
import java.util.UUID

private const val CONNECTED_NODES_INITIAL_DISTANCE_X = 0
private const val CONNECTED_NODES_INITIAL_DISTANCE_Y = 50
private const val INITIAL_X = 200.0
private const val INITIAL_Y = 50.0

class DiagramPanelService(
    private val project: Project,
    private val diagramPanel: DiagramPanel,
    var isFileAddingEnabled: Boolean = true,
) {
    private val files: MutableMap<UUID, VirtualFile> = mutableMapOf()

    fun clearAllFiles() {
        if (files.isEmpty()) return

        val confirmResult =
            Messages.showYesNoDialog(
                "Are you sure you want to clear all files from the diagram?",
                "Clear All Files",
                "Yes",
                "No",
                Messages.getQuestionIcon(),
            )

        if (confirmResult == Messages.NO) return

        // Make a copy of the keys to avoid concurrent modification
        val boxIds = files.keys.toList()
        boxIds.forEach { boxId ->
            diagramPanel.removeDraggableBox(boxId)
        }
        files.clear()
        diagramPanel.repaint()
    }

    init {
        // Set listener adding node by code jump
        val connection = project.messageBus.connect()

        val codeJumpListener = CodeJumpListener { from, to -> addRelation(from, to) }
        connection.subscribe(AnActionListener.TOPIC, codeJumpListener)
        connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, codeJumpListener)

        val openFileListener = CurrentFileChangeListener { virtualFile -> onCurrentFileChanged(virtualFile) }
        connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, openFileListener)
    }

    private fun addRelation(
        from: VirtualFile,
        to: VirtualFile,
    ) {
        if (!isFileAddingEnabled) return

        val fromDraggableBox = addFileDraggableBox(from)
        val toDraggableBox =
            addFileDraggableBox(
                to,
            ) {
                it.setLocation(
                    fromDraggableBox.doubleX + (fromDraggableBox.doubleWidth - it.doubleWidth) / 2 +
                        CONNECTED_NODES_INITIAL_DISTANCE_X * diagramPanel.scale,
                    fromDraggableBox.doubleY + CONNECTED_NODES_INITIAL_DISTANCE_Y * diagramPanel.scale,
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

    fun exportAsMermaid() {
        if (files.isEmpty()) {
            Messages.showInfoMessage(
                "No files in the diagram to export.",
                "Export Diagram"
            )
            return
        }

        val mermaidContent = generateMermaidDiagram()
        
        // Show save dialog to user
        val outputFile = File(project.basePath ?: System.getProperty("user.home"), "diagram.md")
        
        try {
            outputFile.writeText(mermaidContent)
            Messages.showInfoMessage(
                "Diagram exported successfully to: ${outputFile.absolutePath}",
                "Export Successful"
            )
        } catch (e: Exception) {
            Messages.showErrorDialog(
                "Failed to export diagram: ${e.message}",
                "Export Failed"
            )
        }
    }

    private fun generateMermaidDiagram(): String {
        val sb = StringBuilder()
        sb.appendLine("# Diagram")
        sb.appendLine()
        sb.appendLine("```mermaid")
        sb.appendLine("graph TD")
        
        // Get all connections from the diagram panel
        val connectionSet = diagramPanel.getConnections()
        
        // Create node definitions with sanitized names
        val nodeNames = mutableMapOf<UUID, String>()
        val usedNames = mutableSetOf<String>()
        files.forEach { (uuid, virtualFile) ->
            var sanitizedName = sanitizeNodeName(virtualFile.name)
            var counter = 1
            while (usedNames.contains(sanitizedName)) {
                sanitizedName = "${sanitizeNodeName(virtualFile.name)}_$counter"
                counter++
            }
            usedNames.add(sanitizedName)
            nodeNames[uuid] = sanitizedName
            sb.appendLine("    $sanitizedName[\"${virtualFile.name}\"]")
        }
        
        // Add connections
        connectionSet.forEach { (fromId, toId) ->
            val fromName = nodeNames[fromId]
            val toName = nodeNames[toId]
            if (fromName != null && toName != null) {
                sb.appendLine("    $fromName --> $toName")
            }
        }
        
        sb.appendLine("```")
        return sb.toString()
    }

    private fun sanitizeNodeName(fileName: String): String {
        // Remove file extension and sanitize for Mermaid
        val nameWithoutExt = fileName.substringBeforeLast('.')
        return nameWithoutExt
            .replace("[^a-zA-Z0-9_]".toRegex(), "_")
            .let { if (it.isEmpty()) "node" else it }
    }
}
