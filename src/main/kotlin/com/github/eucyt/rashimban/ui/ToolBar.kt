package com.github.eucyt.rashimban.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionUpdateThread
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
class ToolBar(
    private var isRecordingActive: Boolean,
) {
    private var onClearAllAction: () -> Unit = {}
    private var onStartAction: () -> Unit = {}
    private var onStopAction: () -> Unit = {}

    /**
     * Set handler for the clear all action
     */
    fun setOnClearAllAction(handler: () -> Unit) {
        onClearAllAction = handler
    }

    /**
     * Set handler for the start action
     */
    fun setOnStartAction(handler: () -> Unit) {
        onStartAction = handler
    }

    /**
     * Set handler for the stop action
     */
    fun setOnStopAction(handler: () -> Unit) {
        onStopAction = handler
    }

    /**
     * Creates and returns the toolbar component with all actions.
     */
    fun createComponent(): JComponent {
        // Create action toolbar for control buttons (Start/Stop)
        val recordingActionGroup = DefaultActionGroup()

        val startAction =
            object : AnAction("Start", "Start adding files to diagram", AllIcons.Actions.Execute) {
                override fun actionPerformed(e: AnActionEvent) {
                    onStartAction()
                    isRecordingActive = true
                    e.presentation.isEnabled = false
                    val stopActionFromGroup =
                        recordingActionGroup.childActionsOrStubs.find {
                            it.templateText == "Stop"
                        }
                    stopActionFromGroup?.templatePresentation?.isEnabled = true
                }

                override fun update(e: AnActionEvent) {
                    e.presentation.isEnabled = !isRecordingActive
                }

                override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.EDT
            }

        val stopAction =
            object : AnAction("Stop", "Stop adding files to diagram", AllIcons.Actions.Suspend) {
                override fun actionPerformed(e: AnActionEvent) {
                    onStopAction()
                    isRecordingActive = false
                    e.presentation.isEnabled = false
                    val startActionFromGroup =
                        recordingActionGroup.childActionsOrStubs.find {
                            it.templateText == "Start"
                        }
                    startActionFromGroup?.templatePresentation?.isEnabled = true
                }

                override fun update(e: AnActionEvent) {
                    e.presentation.isEnabled = isRecordingActive
                }

                override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.EDT
            }

        recordingActionGroup.add(startAction)
        recordingActionGroup.add(stopAction)
        val recordingToolbar = ActionManager.getInstance().createActionToolbar("RashimbanControlToolbar", recordingActionGroup, true)

        // Create action toolbar for delete button
        val deleteActionGroup = DefaultActionGroup()

        val clearAllAction =
            object : AnAction("Clear All", "Clear all files from diagram", AllIcons.Actions.GC) {
                override fun actionPerformed(e: AnActionEvent) {
                    onClearAllAction()
                }

                override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.EDT
            }

        deleteActionGroup.add(clearAllAction)
        val deleteToolbar = ActionManager.getInstance().createActionToolbar("RashimbanDeleteToolbar", deleteActionGroup, true)

        // Create panel with both toolbars
        val panel = JPanel(BorderLayout())

        // Set target components to avoid the warning
        recordingToolbar.targetComponent = panel
        deleteToolbar.targetComponent = panel

        panel.add(recordingToolbar.component, BorderLayout.WEST)
        panel.add(deleteToolbar.component, BorderLayout.EAST)

        return panel
    }
}
