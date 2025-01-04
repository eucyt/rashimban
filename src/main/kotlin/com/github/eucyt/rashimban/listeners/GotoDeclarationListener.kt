package com.github.eucyt.rashimban.listeners

import com.github.eucyt.rashimban.canvas.components.FileNodeManager
import com.intellij.codeInsight.navigation.actions.GotoDeclarationAction
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.AnActionResult
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.ex.AnActionListener
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.vfs.VirtualFile

class GotoDeclarationListener(
    private val fileNodeManager: FileNodeManager,
    private val onGotoDeclaration: () -> Unit,
) : AnActionListener,
    FileEditorManagerListener {
    private var isJumping = false
    private var sourceFile: VirtualFile? = null

    override fun beforeActionPerformed(
        action: AnAction,
        event: AnActionEvent,
    ) {
        isJumping = false
        if (action !is GotoDeclarationAction) return
        isJumping = true
        sourceFile = event.getData(CommonDataKeys.VIRTUAL_FILE)
    }

    override fun afterActionPerformed(
        action: AnAction,
        event: AnActionEvent,
        result: AnActionResult,
    ) {
        if (action !is GotoDeclarationAction) return
        val targetFile = event.getData(CommonDataKeys.PSI_ELEMENT)?.containingFile?.virtualFile ?: return
        if (targetFile != sourceFile) {
            fileNodeManager.add(sourceFile!!, targetFile)
            onGotoDeclaration()
            isJumping = false
        }
    }

    // HACK: If multiple declarations or usages are found, targetFile is same as sourceFile in GotoDeclarationAction.
    // So, we should get targetFile after fileOpened.
    override fun fileOpened(
        source: FileEditorManager,
        file: VirtualFile,
    ) {
        if (!isJumping) return
        sourceFile?.let { src ->
            if (src != file) {
                fileNodeManager.add(src, file)
                onGotoDeclaration()
            }
        }
        isJumping = false
        sourceFile = null
    }
}
