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

package jhi.humbug.gui.dialog.wizard

import jhi.humbug.gui.i18n.RB
import jhi.humbug.gui.viewer.DuplicateBarcodeOptionComboViewer
import jhi.humbug.gui.viewer.MissingBarcodeOptionComboViewer
import jhi.swtcommons.gui.layout.GridLayoutUtils
import org.eclipse.jface.wizard.WizardPage
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Label

/**
 * @author Sebastian Raubach
 */
class ImageRenameOptionsWizardPage : WizardPage("Image Rename Wizard Options")
{
    private lateinit var content: Composite

    init
    {
        title = RB.getString(RB.DIALOG_RENAME_IMAGE_OPTIONS_TITLE)
        description = RB.getString(RB.DIALOG_RENAME_IMAGE_OPTIONS_DESCRIPTION)
    }

    override fun createControl(parent: Composite)
    {
        content = Composite(parent, SWT.NONE)

        Label(content, SWT.NONE).text = RB.getString(RB.SETTING_BARCODE_RENAME_MISSING_TITLE)
        MissingBarcodeOptionComboViewer(content, SWT.READ_ONLY)
        Label(content, SWT.NONE).text = RB.getString(RB.SETTING_BARCODE_RENAME_DUPLICATE_TITLE)
        DuplicateBarcodeOptionComboViewer(content, SWT.READ_ONLY)

        GridLayoutUtils.useValues(1, false).applyTo(content)

        control = content
    }
}
