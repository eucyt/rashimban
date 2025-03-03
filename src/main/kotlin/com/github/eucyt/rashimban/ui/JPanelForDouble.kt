package com.github.eucyt.rashimban.ui

import java.awt.Point
import java.awt.Rectangle
import javax.swing.JPanel

open class JPanelForDouble : JPanel() {
    // To prevent rounding error, use double positions instead of int ones
    var doubleX: Double = 0.0
        get() = if (field == 0.0) super.getX().toDouble() else field
        private set
    var doubleY: Double = 0.0
        get() = if (field == 0.0) super.getY().toDouble() else field
        private set
    var doubleWidth: Double = 0.0
        get() = if (field == 0.0) super.getWidth().toDouble() else field
        private set
    var doubleHeight: Double = 0.0
        get() = if (field == 0.0) super.getHeight().toDouble() else field
        private set

    init {
        doubleX = super.getX().toDouble()
        doubleY = super.getY().toDouble()
        doubleWidth = super.getWidth().toDouble()
        doubleHeight = super.getHeight().toDouble()
    }

    // HACK: Prohibit some functions using Int to prevent rounding error
    @Deprecated("Use doubleX instead", ReplaceWith("doubleX"), level = DeprecationLevel.ERROR)
    override fun getX() = super.getX()

    @Deprecated("Use doubleY instead", ReplaceWith("doubleY"), level = DeprecationLevel.ERROR)
    override fun getY() = super.getY()

    @Deprecated("Use doubleWidth instead", ReplaceWith("doubleX and doubleY"), level = DeprecationLevel.ERROR)
    override fun getLocation(): Point = super.getLocation()

    @Deprecated("Use doubleWidth instead", ReplaceWith("doubleWidth"), level = DeprecationLevel.ERROR)
    override fun getWidth() = super.getWidth()

    @Deprecated("Use doubleHeight instead", ReplaceWith("doubleHeight"), level = DeprecationLevel.ERROR)
    override fun getHeight() = super.getHeight()

    @Deprecated(
        "Use double(X|Y|Width|Height) instead",
        ReplaceWith("double(X|Y|Width|Height)"),
        level = DeprecationLevel.ERROR,
    )
    override fun getBounds(): Rectangle = super.getBounds()

    @Deprecated(
        "Use setLocation(x: Double, y: Double) instead",
        ReplaceWith("setLocation(x: Double, y: Double)"),
        level = DeprecationLevel.ERROR,
    )
    override fun setLocation(
        x: Int,
        y: Int,
    ) = super.setLocation(x, y)

    @Deprecated(
        "Use setLocation(x: Double, y: Double) instead",
        ReplaceWith("setLocation(x: Double, y: Double)"),
        level = DeprecationLevel.ERROR,
    )
    override fun setLocation(p: Point): Unit = super.setLocation(p)

    @Deprecated(
        "Use setBounds(x: Double, y: Double, width: Double, height: Double) instead",
        ReplaceWith("setBounds(x: Double, y: Double, width: Double, height: Double)"),
        level = DeprecationLevel.ERROR,
    )
    override fun setBounds(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
    ) = super.setBounds(x, y, width, height)

    fun setLocation(
        x: Double,
        y: Double,
    ) {
        doubleX = x
        doubleY = y
        super.setLocation(x.toInt(), y.toInt())
    }

    fun setBounds(
        x: Double,
        y: Double,
        width: Double,
        height: Double,
    ) {
        doubleX = x
        doubleY = y
        doubleWidth = width
        doubleHeight = height
        super.setBounds(x.toInt(), y.toInt(), width.toInt(), height.toInt())
    }
}
