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

import jhi.humbug.util.HumbugParameter
import jhi.humbug.util.HumbugParameterStore
import jhi.swtcommons.gui.viewer.AdvancedComboViewer
import jhi.swtcommons.util.Install4jUtils
import org.eclipse.jface.viewers.ComboViewer
import org.eclipse.jface.viewers.LabelProvider
import org.eclipse.jface.viewers.StructuredSelection
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Composite

/**
 * [UpdateIntervalComboViewer] extends [AdvancedComboViewer] and displays [jhi.swtcommons.util.Install4jUtils.UpdateInterval]s

 * @author Sebastian Raubach
 */
class UpdateIntervalComboViewer(parent: Composite, style: Int) : AdvancedComboViewer<Install4jUtils.UpdateInterval>(parent, style or SWT.READ_ONLY)
{
    /**
     * Returns `true` if the user changed the selection, `false` otherwise

     * @return `true` if the user changed the selection, `false` otherwise
     */
    var isChanged = false
        private set
    private var prevSelection: Install4jUtils.UpdateInterval? = null

    init
    {
        this.labelProvider = object : LabelProvider()
        {
            override fun getText(element: Any?): String
            {
                if (element is Install4jUtils.UpdateInterval)
                {
                    return getDisplayText(element)
                }
                return super.getText(element)
            }
        }

        fill()

        /* Listen for selection changes */
        this.addSelectionChangedListener {
            val selection = selectedItem

            if (selection != prevSelection)
                isChanged = true
        }
    }

    /**
     * Fill the [ComboViewer]
     */
    private fun fill()
    {
        /* Get all the supported update intervals */
        val items = Install4jUtils.UpdateInterval.values()
        input = items

        /* Select the first element (or the currently stored one) */
        val toSelect = HumbugParameterStore.get(HumbugParameter.updateInterval)

        if (toSelect != null)
            prevSelection = toSelect as Install4jUtils.UpdateInterval
        else
            prevSelection = Install4jUtils.UpdateInterval.STARTUP
        selection = StructuredSelection(prevSelection!!)
    }

    override fun getDisplayText(item: Install4jUtils.UpdateInterval): String = item.resource
}
