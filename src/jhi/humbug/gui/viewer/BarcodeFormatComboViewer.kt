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

package jhi.humbug.gui.viewer

import com.google.zxing.BarcodeFormat
import jhi.humbug.gui.i18n.RB
import jhi.humbug.util.HumbugParameter
import jhi.humbug.util.HumbugParameterStore
import jhi.swtcommons.gui.viewer.AdvancedComboViewer
import org.eclipse.jface.viewers.LabelProvider
import org.eclipse.jface.viewers.StructuredSelection
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Composite

/**
 * [BarcodeFormatComboViewer] extends [AdvancedComboViewer] and displays [BarcodeFormat]s

 * @author Sebastian Raubach
 */
class BarcodeFormatComboViewer
/**
 * Creates a new instance of [BarcodeFormatComboViewer]

 * @param parent          The parent of the viewer
 * *
 * @param style           The style bits
 * *
 * @param autoselectFirst Set to `true` if the first item should be selected initially
 */
internal constructor(parent: Composite, style: Int, private val autoselectFirst: Boolean, private val includeAll: Boolean = false) : AdvancedComboViewer<BarcodeFormat>(parent, style or SWT.READ_ONLY)
{

    /**
     * Creates a new instance of [BarcodeFormatComboViewer]

     * @param parent The parent of the viewer
     * *
     * @param style  The style bits
     */
    constructor(parent: Composite, style: Int) : this(parent, style, false)

    init
    {
        this.labelProvider = object : LabelProvider()
        {
            override fun getText(element: Any?): String
            {
                if (element is BarcodeFormat)
                {
                    return if (element == BarcodeFormat.RSS_EXPANDED)
                        RB.getString(RB.SETTING_BARCODE_RENAME_RESTRICT_TYPE_ACCEPT_ALL)
                    else
                        element.name
                }

                return super.getText(element)
            }
        }

        fill()
    }

    private fun fill()
    {
        input = if (includeAll)
            VALID_CODES_ALL
        else
            VALID_CODES

        if (autoselectFirst)
        {
            if (includeAll)
                selection = StructuredSelection(VALID_CODES_ALL[0])
            else
                selection = StructuredSelection(VALID_CODES[0])
        }
        else
            selection = StructuredSelection(HumbugParameterStore.get(HumbugParameter.barcodeFormat))
    }

    override fun getDisplayText(item: BarcodeFormat): String = item.name

    fun getBarcodeRestriction(): BarcodeFormat?
    {
        val item = selectedItem

        return if (item == BarcodeFormat.RSS_EXPANDED)
            null
        else
            item
    }

    companion object
    {
        private val VALID_CODES = arrayOf(BarcodeFormat.CODE_128, BarcodeFormat.CODE_39, BarcodeFormat.EAN_13, BarcodeFormat.EAN_8, BarcodeFormat.UPC_A, BarcodeFormat.QR_CODE)
        private val VALID_CODES_ALL = arrayOf(BarcodeFormat.RSS_EXPANDED, BarcodeFormat.CODE_128, BarcodeFormat.CODE_39, BarcodeFormat.EAN_13, BarcodeFormat.EAN_8, BarcodeFormat.UPC_A, BarcodeFormat.QR_CODE)
    }
}
