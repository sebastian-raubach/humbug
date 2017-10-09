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

import jhi.humbug.gui.i18n.RB
import jhi.humbug.util.HumbugParameter
import jhi.humbug.util.HumbugParameterStore
import jhi.swtcommons.gui.viewer.AdvancedComboViewer
import org.eclipse.jface.viewers.ComboViewer
import org.eclipse.jface.viewers.LabelProvider
import org.eclipse.jface.viewers.StructuredSelection
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Composite
import java.util.*

/**
 * [GUILocaleComboViewer] extends [AdvancedComboViewer] and displays [Locale]s for the GUI.

 * @author Sebastian Raubach
 */
class GUILocaleComboViewer(parent: Composite, style: Int) : AdvancedComboViewer<Locale>(parent, style or SWT.READ_ONLY)
{
    /**
     * Returns `true` if the user changed the selection, `false` otherwise

     * @return `true` if the user changed the selection, `false` otherwise
     */
    var isChanged = false
        private set
    private var prevSelection: Locale? = null

    init
    {

        this.labelProvider = object : LabelProvider()
        {
            override fun getText(element: Any?): String
            {
                if (element is Locale)
                {
                    return getDisplayText(element as Locale?)
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
        /* Get all the supported Locales */
        val locales = RB.SUPPORTED_LOCALES

        val comp = Comparator<Locale> { o1, o2 -> o1.displayName.compareTo(o2.displayName) }

        locales.sortWith(comp)
        input = locales

        /* Select the first element (or the currently stored one) */
        val locale = HumbugParameterStore.get(HumbugParameter.locale) as Locale?

        prevSelection = locale ?: locales[0]

        selection = StructuredSelection(prevSelection)
    }

    override fun getDisplayText(item: Locale?): String
    {
        return item?.getDisplayName(item) ?: ""
    }
}
