package com.github.eucyt.rashimban.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import javax.swing.JComponent

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
        val actionGroup = DefaultActionGroup()
        
        // Add clear all action
        val clearAllAction = object : AnAction("Clear All", "Clear all files from diagram", AllIcons.Actions.GC) {
            override fun actionPerformed(e: AnActionEvent) {
                onClearAllAction()
            }
        }
        actionGroup.add(clearAllAction)
        
        // Create toolbar
        val toolbar = ActionManager.getInstance().createActionToolbar("RashimbanToolbar", actionGroup, true)
        return toolbar.component
    }
}