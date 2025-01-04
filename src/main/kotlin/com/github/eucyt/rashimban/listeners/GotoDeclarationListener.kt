package com.github.eucyt.rashimban.listeners

import com.github.eucyt.rashimban.canvas.components.FileNodeManager
import com.intellij.codeInsight.navigation.actions.GotoDeclarationAction
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.AnActionResult
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.ex.AnActionListener
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.vfs.VirtualFile

private const val JUMP_TIMEOUT_MS = 5_000

// This listener should subscribe to AnActionListener.TOPIC and FileEditorManagerListener.FILE_EDITOR_MANAGER
class GotoDeclarationListener(
    private val fileNodeManager: FileNodeManager,
    private val onGotoDeclaration: () -> Unit,
) : AnActionListener,
    FileEditorManagerListener {
    private var isJumping = false
    private var latestSourceFile: VirtualFile? = null
    private var latestGotoDeclarationActionAt: Long = 0

    override fun beforeActionPerformed(
        action: AnAction,
        event: AnActionEvent,
    ) {
        isJumping = false
        if (action !is GotoDeclarationAction) return

        isJumping = true
        latestSourceFile = event.getData(CommonDataKeys.VIRTUAL_FILE)
        latestGotoDeclarationActionAt = System.currentTimeMillis()
        println("beforeActionPerformed: $latestSourceFile")
    }

    override fun afterActionPerformed(
        action: AnAction,
        event: AnActionEvent,
        result: AnActionResult,
    ) {
        if (action !is GotoDeclarationAction) return

        val targetFile = event.getData(CommonDataKeys.PSI_ELEMENT)?.containingFile?.virtualFile ?: return
        println("afterActionPerformed: $targetFile")

        // It cannot distinguish whether the jump occurred within the same file
        // or if there were multiple declarations or usages, making it unable to correctly identify the targetFile.
        if (targetFile == latestSourceFile) return

        fileNodeManager.add(latestSourceFile!!, targetFile)
        onGotoDeclaration()
        isJumping = false
    }

    // If there are multiple declarations or usages, the targetFile cannot be obtained
    // at the point of GotoDeclarationAction. Therefore, it is necessary to retrieve the file opened after the action.
    override fun selectionChanged(event: FileEditorManagerEvent) {
        if (!isJumping) return
        val newFile = event.newFile ?: return

        if (latestSourceFile == newFile) {
            isJumping = false
            return
        }

        /*
         * HACK: Check timeout to avoid false positive.
         *
         * In the below case, newFile may not be declarations or usages but just other file opened manually.
         *
         * 1. Go to declarations or usages and cancel displayed popup menu. Or jump occurred within the same file.
         * 2. Open other file manually before going to other declarations or usages.
         */
        if (System.currentTimeMillis() - latestGotoDeclarationActionAt > JUMP_TIMEOUT_MS) {
            isJumping = false
            return
        }

        fileNodeManager.add(latestSourceFile!!, newFile)
        onGotoDeclaration()
        isJumping = false
    }
}
