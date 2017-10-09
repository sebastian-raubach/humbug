/*
 * Copyright 2017 Sebastian Raubach and Paul Shaw from the
 * Information and Computational Sciences Group at JHI Dundee
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jhi.humbug.gui.viewer

import jhi.humbug.util.HumbugParameter
import jhi.humbug.util.HumbugParameterStore
import jhi.humbug.util.thread.BarcodeImageRenameThread
import jhi.swtcommons.gui.viewer.AdvancedComboViewer
import org.eclipse.jface.viewers.LabelProvider
import org.eclipse.jface.viewers.StructuredSelection
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Composite

/**
 * [MissingBarcodeOptionComboViewer] extends [AdvancedComboViewer] and displays [jhi.humbug.util.thread.BarcodeImageRenameThread.MissingBarcodeOption]s

 * @author Sebastian Raubach
 */
class MissingBarcodeOptionComboViewer
/**
 * Creates a new instance of [MissingBarcodeOptionComboViewer]

 * @param parent The parent of the viewer
 * *
 * @param style  The style bits
 */
(parent: Composite, style: Int) : AdvancedComboViewer<BarcodeImageRenameThread.MissingBarcodeOption>(parent, style or SWT.READ_ONLY)
{
    init
    {
        this.labelProvider = object : LabelProvider()
        {
            override fun getText(element: Any?): String
            {
                if (element is BarcodeImageRenameThread.MissingBarcodeOption)
                {
                    return element.text
                }

                return super.getText(element)
            }
        }

        this.addSelectionChangedListener { HumbugParameterStore.put(HumbugParameter.missingBarcodeOption, selectedItem) }

        fill()
    }

    private fun fill()
    {
        input = VALUES

        val option = HumbugParameterStore.get(HumbugParameter.missingBarcodeOption)

        if (option == null)
            selection = StructuredSelection(VALUES[0])
        else
            selection = StructuredSelection(option)
    }

    override fun getDisplayText(item: BarcodeImageRenameThread.MissingBarcodeOption): String = item.text

    companion object
    {
        private val VALUES = BarcodeImageRenameThread.MissingBarcodeOption.values()
    }
}
