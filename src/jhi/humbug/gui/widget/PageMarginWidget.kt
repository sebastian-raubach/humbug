/*
 *  Copyright 2017 Information and Computational Sciences,
 *  The James Hutton Institute.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package jhi.humbug.gui.widget

import jhi.humbug.util.HumbugParameter
import jhi.humbug.util.HumbugParameterStore
import jhi.humbug.util.Margin
import jhi.swtcommons.gui.layout.GridDataUtils
import jhi.swtcommons.gui.layout.GridLayoutUtils
import org.eclipse.swt.SWT
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.*

/**
 * The [PageMarginWidget] is used to select the page margins

 * @author Sebastian Raubach
 */
class PageMarginWidget(parent: Composite, style: Int) : Composite(parent, style)
{
    private val top: Spinner
    private val left: Spinner
    private val page: Composite
    private val right: Spinner
    private val bottom: Spinner
    private val selectionListener: Listener
    private val pageWidgetHeight: Int
    private val pageWidgetWidth: Int

    init
    {
        Label(this, SWT.NONE)
        top = Spinner(this, SWT.BORDER)
        top.data = Margin.Side.TOP
        top.minimum = 0
        top.maximum = 100
        Label(this, SWT.NONE)

        left = Spinner(this, SWT.BORDER)
        left.data = Margin.Side.LEFT
        left.minimum = 0
        left.maximum = 100

        page = Composite(this, SWT.BORDER)
        page.background = Display.getDefault().getSystemColor(SWT.COLOR_WHITE)
        val border = Composite(page, SWT.BORDER)
        border.background = Display.getDefault().getSystemColor(SWT.COLOR_WHITE)

        right = Spinner(this, SWT.BORDER)
        right.data = Margin.Side.RIGHT
        right.minimum = 0
        right.maximum = 100

        Label(this, SWT.NONE)
        bottom = Spinner(this, SWT.BORDER)
        bottom.data = Margin.Side.BOTTOM
        bottom.minimum = 0
        bottom.maximum = 100
        Label(this, SWT.NONE)

        selectionListener = Listener { event ->
            val layout = page.layout as GridLayout

            val margin = getMargin(event.widget.data)

            when (event.widget.data)
            {
                Margin.Side.BOTTOM -> layout.marginBottom = margin
                Margin.Side.TOP -> layout.marginTop = margin
                Margin.Side.LEFT -> layout.marginLeft = margin
                Margin.Side.RIGHT -> layout.marginRight = margin
            }

            page.layout(true, true)
        }

        GridLayoutUtils.useDefault().marginHeight(0).marginWidth(0).applyTo(page)

        top.addListener(SWT.Selection, selectionListener)
        right.addListener(SWT.Selection, selectionListener)
        bottom.addListener(SWT.Selection, selectionListener)
        left.addListener(SWT.Selection, selectionListener)

        val marginLeft = Integer.parseInt(HumbugParameterStore.getAsString(HumbugParameter.marginLeft))
        val marginTop = Integer.parseInt(HumbugParameterStore.getAsString(HumbugParameter.marginTop))
        val marginRight = Integer.parseInt(HumbugParameterStore.getAsString(HumbugParameter.marginRight))
        val marginBottom = Integer.parseInt(HumbugParameterStore.getAsString(HumbugParameter.marginBottom))

        top.selection = marginTop
        right.selection = marginRight
        bottom.selection = marginBottom
        left.selection = marginLeft

        pageWidgetHeight = Math.min(200.0, Display.getDefault().primaryMonitor.bounds.height * 0.8).toInt()
        pageWidgetWidth = Math.round(pageWidgetHeight * A4_WIDTH / A4_HEIGHT)

        GridLayoutUtils.useValues(3, false).marginHeight(0).marginWidth(0).applyTo(this)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.CENTER_BOTH).applyTo(this)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.CENTER_BOTH_FALSE).heightHint(pageWidgetHeight).widthHint(pageWidgetWidth).applyTo(page)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_BOTH).applyTo(border)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.END_CENTER_FALSE).applyTo(left)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.CENTER_TOP).applyTo(top)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.BEGINNING_CENTER_FALSE).applyTo(right)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.CENTER_BOTTOM).applyTo(bottom)

        /* Trigger the initial margins */
        Display.getDefault().asyncExec { this.update() }
    }

    /**
     * Returns the margin in pixels (for the preview) for the given side (as the [Object])

     * @param data The given [Side] from the widget's data
     * *
     * @return The margin in pixels
     */
    private fun getMargin(data: Any): Int
    {
        return when (data)
        {
            Margin.Side.BOTTOM -> Math.round(bottom.selection * (pageWidgetHeight / A4_HEIGHT))
            Margin.Side.TOP -> Math.round(top.selection * (pageWidgetHeight / A4_HEIGHT))
            Margin.Side.LEFT -> Math.round(left.selection * (pageWidgetWidth / A4_WIDTH))
            Margin.Side.RIGHT -> Math.round(right.selection * (pageWidgetWidth / A4_WIDTH))
            else -> 0
        }
    }

    override fun update()
    {
        val event = Event()
        event.widget = top
        selectionListener.handleEvent(event)
        event.widget = left
        selectionListener.handleEvent(event)
        event.widget = right
        selectionListener.handleEvent(event)
        event.widget = bottom
        selectionListener.handleEvent(event)
    }

    /**
     * Returns the selected percentage value for the given [Side]

     * @param side The [Side]
     * *
     * @return The selected percentage value for the given [Side]
     */
    fun getMargin(side: Int): Int
    {
        return when (side)
        {
            SWT.LEFT -> left.selection
            SWT.TOP -> top.selection
            SWT.RIGHT -> right.selection
            SWT.BOTTOM -> bottom.selection
            else -> 0
        }

    }

    companion object
    {
        private val A4_WIDTH = 210f
        private val A4_HEIGHT = 297f
    }
}
