package com.github.eucyt.rashimban.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * Toolbar for the Rashimban tool window.
 * Contains actions for managing the diagram.
 */
class ToolBar {
    private var onClearAllAction: () -> Unit = {}

    /**
     * Set handler for the clear all action
     */
    fun setOnClearAllAction(handler: () -> Unit) {
        onClearAllAction = handler
    }

    /**
     * Creates and returns the toolbar component with all actions.
     */
    fun createComponent(): JComponent {
        val deleteActionGroup = DefaultActionGroup()
        val clearAllAction =
            object : AnAction("Clear All", "Clear all files from diagram", AllIcons.Actions.GC) {
                override fun actionPerformed(e: AnActionEvent) {
                    onClearAllAction()
                }
            }
        deleteActionGroup.add(clearAllAction)

        val deleteToolbar = ActionManager.getInstance().createActionToolbar("RashimbanDeleteToolbar", deleteActionGroup, true)

        return JPanel(BorderLayout()).apply {
            add(deleteToolbar.component, BorderLayout.EAST)
        }
    }
}
