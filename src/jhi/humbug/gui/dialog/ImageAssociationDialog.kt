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

import jhi.humbug.gui.i18n.RB
import jhi.humbug.util.Resources.Images
import jhi.swtcommons.gui.dialog.I18nDialog
import jhi.swtcommons.gui.layout.GridDataUtils
import jhi.swtcommons.gui.layout.GridLayoutUtils
import jhi.swtcommons.util.OSUtils
import jhi.swtcommons.util.ShellUtils
import jhi.swtcommons.util.StringUtils
import org.eclipse.jface.dialogs.IDialogConstants
import org.eclipse.swt.SWT
import org.eclipse.swt.dnd.*
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.widgets.*
import java.io.File

/**
 * [ImageAssociationDialog] extends [I18nDialog] and allows the user to associate an image with the barcode

 * @author Sebastian Raubach
 */
class ImageAssociationDialog(parentShell: Shell, previousSelection: File) : I18nDialog(parentShell)
{
    var file: File? = null
        private set

    private var image: Label? = null
    private var theImage: Image? = null

    init
    {

        setBlockOnOpen(true)

        this.file = previousSelection
    }

    private fun updateLabel()
    {
        if (file != null && file!!.exists() && file!!.isFile)
        {
            val original = Image(null, file?.absolutePath)

            /* No idea why this is necessary, but oh well... */
            shell.display.asyncExec {
                theImage = Images.scaleImage(original, image?.size ?: Point(0, 0))
                Images.disposeImage(original)
                image?.image = theImage
            }
        }
        else
        {
            image?.image = Images.PLACEHOLDER
        }
    }

    override fun configureShell(shell: Shell)
    {
        super.configureShell(shell)
        shell.text = RB.getString(RB.DIALOG_IMAGE_ASSOCIATION_TITLE)
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

        image = Label(composite, SWT.CENTER)
        image?.addListener(SWT.Dispose) { Images.disposeImage(theImage) }

        initDnd()

        val select = Button(composite, SWT.FLAT)
        select.toolTipText = RB.getString(RB.TOOLTIP_BUTTON_SELECT)
        select.image = Images.FOLDER
        select.addListener(SWT.Selection) {
            val dialog = FileDialog(shell, SWT.OPEN)
            dialog.filterExtensions = arrayOf("*.jpg; *.jpeg; *.png")

            val path = dialog.open()

            if (!StringUtils.isEmpty(path))
            {
                file = File(path)
                updateLabel()
            }
        }

        val open = Button(composite, SWT.FLAT)
        open.toolTipText = RB.getString(RB.TOOLTIP_BUTTON_OPEN)
        open.image = Images.OPEN_IMAGE
        open.addListener(SWT.Selection) {
            if (file != null)
                OSUtils.open(file?.absolutePath ?: "")
        }

        val delete = Button(composite, SWT.FLAT)
        delete.toolTipText = RB.getString(RB.GENERAL_DELETE)
        delete.image = Images.DELETE
        delete.addListener(SWT.Selection) {
            file = null
            updateLabel()
        }

        GridLayoutUtils.useValues(3, true).applyTo(composite)

        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_BOTH).horizontalSpan(3).widthHint(Images.PLACEHOLDER.bounds.width).heightHint(Images.PLACEHOLDER.bounds.height).applyTo(image)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.CENTER_BOTTOM_FALSE).applyTo(select)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.CENTER_BOTTOM_FALSE).applyTo(open)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.CENTER_BOTTOM_FALSE).applyTo(delete)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_BOTH).applyTo(composite)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_BOTH).applyTo(parent)

        updateLabel()

        return composite
    }

    private fun initDnd()
    {
        val target = DropTarget(image, DND.DROP_DEFAULT or DND.DROP_MOVE or DND.DROP_COPY)

        val transfers = arrayOf<Transfer>(TextTransfer.getInstance(), FileTransfer.getInstance())

        target.transfer = transfers
        target.addDropListener(object : DropTargetAdapter()
        {
            override fun drop(event: DropTargetEvent?)
            {
                if (event?.data == null)
                {
                    event?.detail = DND.DROP_NONE
                    return
                }

                val files = event.data as Array<*>

                if (files.isNotEmpty())
                {
                    file = File(files[0] as String)
                    updateLabel()
                }
            }
        })
    }

}
