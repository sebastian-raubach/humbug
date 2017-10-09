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

package jhi.humbug.gui.dialog

import com.google.zxing.BarcodeFormat
import jhi.humbug.gui.i18n.RB
import jhi.humbug.gui.viewer.BarcodeFormatComboViewer
import jhi.swtcommons.gui.dialog.I18nDialog
import jhi.swtcommons.gui.layout.GridDataUtils
import jhi.swtcommons.gui.layout.GridLayoutUtils
import jhi.swtcommons.util.ShellUtils
import jhi.swtcommons.util.StringUtils
import org.eclipse.jface.dialogs.IDialogConstants
import org.eclipse.swt.SWT
import org.eclipse.swt.dnd.Clipboard
import org.eclipse.swt.dnd.TextTransfer
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.widgets.*

/**
 * [ClipboardContentDialog] extends [I18nDialog] and shows the content of the system clipboard. The user can edit the test and then click
 * ok to start the import process

 * @author Sebastian Raubach
 */
class ClipboardContentDialog(parentShell: Shell) : I18nDialog(parentShell)
{
    var clipboardContent: String? = null
        private set
    var barcodeFormat: BarcodeFormat? = null
        private set
    private var barcodeFormatViewer: BarcodeFormatComboViewer? = null
    private var text: Text? = null

    init
    {

        setBlockOnOpen(true)
    }

    override fun configureShell(shell: Shell)
    {
        super.configureShell(shell)
        shell.text = RB.getString(RB.DIALOG_CLIPBOARD_TITLE)
    }

    override fun createButtonsForButtonBar(parent: Composite)
    {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true)
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false)
    }

    override fun getInitialLocation(initialSize: Point): Point
    {
        /* Center the dialog based on the parent */
        return ShellUtils.getLocationCenteredTo(parentShell, initialSize)
    }

    override fun createDialogArea(parent: Composite): Control
    {
        val composite = super.createDialogArea(parent) as Composite

        text = Text(composite, SWT.MULTI or SWT.BORDER or SWT.V_SCROLL)

        val cb = Clipboard(Display.getDefault())
        val transfer = TextTransfer.getInstance()
        val data = cb.getContents(transfer) as String

        if (!StringUtils.isEmpty(data))
            text?.text = data

        barcodeFormatViewer = BarcodeFormatComboViewer(composite, SWT.READ_ONLY)

        GridLayoutUtils.useDefault()
                .applyTo(parent)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_BOTH)
                .heightHint(400)
                .applyTo(parent)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_BOTH)
                .applyTo(text)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_BOTTOM_FALSE)
                .applyTo((barcodeFormatViewer as BarcodeFormatComboViewer).control)

        return composite
    }

    override fun okPressed()
    {
        clipboardContent = text?.text
        barcodeFormat = barcodeFormatViewer?.selectedItem

        super.okPressed()
    }

}
