package com.github.eucyt.rashimban.listeners

import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.vfs.VirtualFile

class CurrentFileChangeListener(
    val onChanged: (VirtualFile) -> Unit,
) : FileEditorManagerListener {
    override fun selectionChanged(event: FileEditorManagerEvent) {
        super.selectionChanged(event)
        val newFile = event.newFile ?: return
        onChanged(newFile)
    }
}
