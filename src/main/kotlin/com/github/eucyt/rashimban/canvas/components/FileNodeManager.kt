package com.github.eucyt.rashimban.canvas.components

import com.intellij.openapi.vfs.VirtualFile
import java.awt.Color
import java.awt.Graphics2D
import java.awt.Point
import java.util.UUID

private const val CONNECTED_NODES_INITIAL_DISTANCE_X = 0
private const val CONNECTED_NODES_INITIAL_DISTANCE_Y = 50
private const val INITIAL_X = 100
private const val INITIAL_Y = 50

class FileNodeManager {
    private val fileNodes: MutableList<FileNode> = mutableListOf()
    private val edges: MutableMap<UUID, MutableSet<UUID>> = mutableMapOf()

    fun add(
        from: VirtualFile,
        to: VirtualFile,
    ) {
        val fromFileNode = addFileNode(from)
        val toFileNode =
            addFileNode(
                to,
                fromFileNode.centerX + CONNECTED_NODES_INITIAL_DISTANCE_X,
                fromFileNode.centerY + CONNECTED_NODES_INITIAL_DISTANCE_Y,
            )
        addEdge(fromFileNode.nodeId, toFileNode.nodeId)
    }

    fun remove(uuid: UUID) {
        fileNodes.removeIf { it.nodeId == uuid }
        edges.remove(uuid)
        edges.forEach { it.value.remove(uuid) }
    }

    // Latest node is at the top
    fun contains(point: Point) = fileNodes.reversed().find { it.contains(point.x, point.y) }

    fun draw(
        g: Graphics2D,
        currentFile: VirtualFile?,
    ) {
        g.color = Color.LIGHT_GRAY
        edges.forEach { (fromNodeId, toSet) ->
            run {
                val from = find(fromNodeId) ?: throw IllegalArgumentException("FileNode does not exist: $fromNodeId")
                toSet.forEach {
                    val to = find(it) ?: throw IllegalArgumentException("FileNode does not exist: $it")
                    g.drawLine(from.centerX, from.centerY, to.centerX, to.centerY)
                }
            }
        }
        fileNodes.map { it.draw(g, currentFile) }
    }

    private fun addFileNode(
        virtualFile: VirtualFile,
        x: Int = INITIAL_X,
        y: Int = INITIAL_Y,
    ): FileNode =
        find(virtualFile)
            ?: FileNode(
                virtualFile,
                x,
                y,
            ).also { fileNodes.add(it) }

    private fun addEdge(
        fromNodeId: UUID,
        toNodeId: UUID,
    ) {
        edges.computeIfAbsent(fromNodeId) { mutableSetOf() }.add(toNodeId)
    }

    private fun find(virtualFile: VirtualFile): FileNode? = fileNodes.find { it.virtualFile.url == virtualFile.url }

    private fun find(nodeId: UUID): FileNode? = fileNodes.find { it.nodeId == nodeId }
}
