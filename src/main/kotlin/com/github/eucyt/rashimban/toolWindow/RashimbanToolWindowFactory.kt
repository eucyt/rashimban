package com.github.eucyt.rashimban.toolWindow

import com.github.eucyt.rashimban.service.DiagramPanelService
import com.github.eucyt.rashimban.ui.DiagramPanel
import com.github.eucyt.rashimban.ui.ToolBar
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.ContentFactory
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel

class RashimbanToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(
        project: Project,
        toolWindow: ToolWindow,
    ) {
        val contentPanel = createContentPanel(project)
        val content = ContentFactory.getInstance().createContent(contentPanel, null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    private fun createContentPanel(project: Project): JComponent {
        val panel = JPanel(BorderLayout())

        val diagramPanel = DiagramPanel()
        val diagramPanelService = DiagramPanelService(project, diagramPanel)

        val toolbar = ToolBar()
        toolbar.setOnClearAllAction {
            diagramPanelService.clearAllFiles()
        }

        panel.add(toolbar.createComponent(), BorderLayout.NORTH)
        panel.add(JBScrollPane(diagramPanel), BorderLayout.CENTER)

        return panel
    }
}
