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

import jhi.swtcommons.util.OSUtils
import jhi.swtcommons.util.StringUtils
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.CLabel
import org.eclipse.swt.graphics.Color
import org.eclipse.swt.graphics.Cursor
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.widgets.Composite

/**
 * [Hyperlink] is a wrapper for [CLabel] that takes care of the [Cursor] and [Color]. It also handles [SWT.MouseUp]
 * events and uses the tooltip to open the given URL/email using [OSUtils.open]

 * @author Sebastian Raubach
 */
class Hyperlink
/**
 * Creates a new instance of [Hyperlink] with the given parent [Composite] and the given style bits

 * @param parent The parent [Composite]
 * *
 * @param style  The style bits
 */
(parent: Composite, style: Int)
{
    /**
     * Returns the contained [CLabel]

     * @return The contained [CLabel]
     */
    val control: CLabel = CLabel(parent, style)
    private var isEmail = false

    init
    {
        control.cursor = parent.shell.display.getSystemCursor(SWT.CURSOR_HAND)
        control.foreground = parent.shell.display.getSystemColor(SWT.COLOR_LINK_FOREGROUND)
        control.addListener(SWT.MouseUp) {
            var link = control.toolTipText

            if (!StringUtils.isEmpty(link))
            {
                if (isEmail)
                    link = "mailto:$link"
                OSUtils.open(link)
            }
        }
    }

    /**
     * Set if the [Hyperlink] represents an email address or not

     * @param isEmail Set to `true` of this [Hyperlink] represents an email address
     */
    fun setIsEmail(isEmail: Boolean)
    {
        this.isEmail = isEmail
    }

    /**
     * Set the label's text. The value `null` clears it.

     * @param text the text to be displayed in the label or null
     * *
     * @see CLabel.setText
     */
    fun setText(text: String)
    {
        control.text = text
    }

    /**
     * Sets the receiver's tool tip text to the argument, which may be null indicating that the default tool tip for the control will be shown.

     * @param text the new tool tip text (or `null`)
     * *
     * @see CLabel.setToolTipText
     */
    fun setToolTipText(text: String)
    {
        control.toolTipText = text
    }

    fun setImage(image: Image)
    {
        control.image = image
    }
}
