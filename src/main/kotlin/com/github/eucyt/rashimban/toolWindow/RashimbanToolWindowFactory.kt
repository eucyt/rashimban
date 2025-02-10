package com.github.eucyt.rashimban.toolWindow

import com.github.eucyt.rashimban.service.DiagramPanelService
import com.github.eucyt.rashimban.ui.DiagramPanel
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import javax.swing.JComponent

class RashimbanToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(
        project: Project,
        toolWindow: ToolWindow,
    ) {
        val content = ContentFactory.getInstance().createContent(getContent(project), null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    private fun getContent(project: Project): JComponent {
        val diagramPanel = DiagramPanel()
        DiagramPanelService(project, diagramPanel)
        return diagramPanel
    }
}
