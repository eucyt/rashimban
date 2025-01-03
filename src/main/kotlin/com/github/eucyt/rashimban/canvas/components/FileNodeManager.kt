package com.github.eucyt.rashimban.canvas.components

import com.intellij.openapi.vfs.VirtualFile
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Point
import java.util.UUID

private const val INITIAL_X = 100
private const val INITIAL_Y = 100
private const val RANDOM_RANGE_X = 30
private const val RANDOM_RANGE_Y = 30

class FileNodeManager : RashimbanCanvasComponentManager {
    private val fileNodes: MutableList<FileNode> = mutableListOf()
    private val edges: MutableMap<UUID, MutableSet<UUID>> = mutableMapOf()

    fun add(
        from: VirtualFile,
        to: VirtualFile,
    ) {
        edges.computeIfAbsent(add(from).nodeId) { mutableSetOf() }.add(add(to).nodeId)
    }

    override fun remove(uuid: UUID) {
        fileNodes.removeIf { it.nodeId == uuid }
        edges.remove(uuid)
        edges.forEach { it.value.remove(uuid) }
    }

    override fun contains(point: Point) = fileNodes.find { it.contains(point.x, point.y) }

    override fun draw(g: Graphics2D) {
        g.color = Color.LIGHT_GRAY
        edges.forEach { (fromNodeId, toSet) ->
            run {
                val from = find(fromNodeId) ?: throw IllegalArgumentException("FileNode does not exist: $fromNodeId")
                toSet.forEach {
                    val to = find(it) ?: throw IllegalArgumentException("FileNode does not exist: $it")
                    g.drawLine(
                        from.x + (from.width / 2),
                        from.y + (from.height / 2),
                        to.x + (to.width / 2),
                        to.y + (to.height / 2),
                    )
                }
            }
        }
        fileNodes.map { it.draw(g) }
    }

    private fun add(virtualFile: VirtualFile): FileNode =
        find(virtualFile)
            ?: FileNode(
                virtualFile,
                INITIAL_X + (0..RANDOM_RANGE_X).random(),
                INITIAL_Y + (0..RANDOM_RANGE_Y).random(),
            ).also { fileNodes.add(it) }

    private fun find(virtualFile: VirtualFile): FileNode? = fileNodes.find { it.virtualFile.url == virtualFile.url }

    private fun find(nodeId: UUID): FileNode? = fileNodes.find { it.nodeId == nodeId }
}
