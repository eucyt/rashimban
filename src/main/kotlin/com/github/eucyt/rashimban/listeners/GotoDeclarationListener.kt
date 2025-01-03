package com.github.eucyt.rashimban.listeners

import com.github.eucyt.rashimban.canvas.components.FileNodeManager
import com.intellij.codeInsight.navigation.actions.GotoDeclarationAction
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.AnActionResult
import com.intellij.openapi.actionSystem.ex.AnActionListener

class GotoDeclarationListener(
    private val fileNodeManager: FileNodeManager,
    private val onGotoDeclaration: () -> Unit,
) : AnActionListener {
    // TODO: If multiple declarations exist, targetFile is same as currentFile incorrectly.
    override fun afterActionPerformed(
        action: AnAction,
        event: AnActionEvent,
        result: AnActionResult,
    ) {
        super.afterActionPerformed(action, event, result)
        if (action is GotoDeclarationAction) {
            val currentFile = event.getData(com.intellij.openapi.actionSystem.CommonDataKeys.PSI_FILE)?.containingFile?.virtualFile
            val targetFile = event.getData(com.intellij.openapi.actionSystem.CommonDataKeys.PSI_ELEMENT)?.containingFile?.virtualFile

            if (currentFile == null || targetFile == null || currentFile == targetFile) return

            fileNodeManager.add(currentFile, targetFile)

            onGotoDeclaration()
        }
    }
}
