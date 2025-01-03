package com.github.eucyt.rashimban.canvas.components

import java.awt.Graphics2D
import java.awt.Point
import java.util.UUID

interface RashimbanCanvasComponentManager {
    fun remove(uuid: UUID)

    fun draw(g: Graphics2D)

    fun contains(point: Point): RashimbanCanvasComponent?
}
