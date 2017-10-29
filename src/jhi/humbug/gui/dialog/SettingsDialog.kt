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

package jhi.humbug.gui.dialog

import jhi.humbug.gui.i18n.RB
import jhi.humbug.gui.viewer.BarcodeFormatComboViewer
import jhi.humbug.gui.viewer.GUILocaleComboViewer
import jhi.humbug.gui.viewer.UpdateIntervalComboViewer
import jhi.humbug.gui.widget.PageMarginWidget
import jhi.humbug.util.HumbugParameter
import jhi.humbug.util.HumbugParameterStore
import jhi.swtcommons.gui.dialog.I18nDialog
import jhi.swtcommons.gui.layout.GridDataUtils
import jhi.swtcommons.gui.layout.GridLayoutUtils
import jhi.swtcommons.util.DialogUtils
import jhi.swtcommons.util.OSUtils
import jhi.swtcommons.util.ShellUtils
import org.eclipse.jface.dialogs.IDialogConstants
import org.eclipse.swt.SWT
import org.eclipse.swt.graphics.Point
import org.eclipse.swt.widgets.*

/**
 * [SettingsDialog] extends [I18nDialog] and contains the settings of this application.

 * @author Sebastian Raubach
 */
class SettingsDialog(parentShell: Shell) : I18nDialog(parentShell)
{
    private lateinit var page: PageMarginWidget
    private lateinit var padding: Spinner
    private lateinit var imageHeight: Spinner
    private lateinit var barcodeType: BarcodeFormatComboViewer
    private lateinit var localeComboViewer: GUILocaleComboViewer
    private lateinit var updateIntervalComboViewer: UpdateIntervalComboViewer
    private lateinit var composite: Composite

    var isLocaleChanged = false
        private set

    init
    {
        setBlockOnOpen(true)
    }

    override fun configureShell(shell: Shell)
    {
        super.configureShell(shell)
        shell.text = RB.getString(RB.DIALOG_SETTINGS_TITLE)
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

    override fun create()
    {
        super.create()

        if (OSUtils.isUnix())
            shell.display.timerExec(250) { composite.setSize(composite.size.x - 1, composite.size.y - 1) }
    }


    override fun createDialogArea(parent: Composite): Control
    {
        composite = super.createDialogArea(parent) as Composite

        val folder = TabFolder(composite, SWT.FLAT)

        var item = TabItem(folder, SWT.NONE)
        item.text = RB.getString(RB.DIALOG_SETTINGS_GENERAL_TITLE)
        item.control = createGeneralTab(folder)

        item = TabItem(folder, SWT.NONE)
        item.text = RB.getString(RB.DIALOG_SETTINGS_PAGE_LAYOUT_TITLE)
        item.control = createPageLayoutTab(folder)

        folder.addListener(SWT.Selection) { page.update() }

        GridLayoutUtils.useValues(1, false).applyTo(composite)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_BOTH).applyTo(parent)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_BOTH).applyTo(folder)

        return composite
    }


    private fun createGeneralTab(folder: TabFolder): Control
    {
        val parent = Composite(folder, SWT.NONE)

        val localeGroup = Group(parent, SWT.NONE)
        localeGroup.text = RB.getString(RB.DIALOG_SETTINGS_GENERAL_LOCALE_TITLE)

        val localeMessage = Label(localeGroup, SWT.NONE)
        localeMessage.text = RB.getString(RB.DIALOG_SETTINGS_GENERAL_LOCALE_MESSAGE)

        localeComboViewer = GUILocaleComboViewer(localeGroup, SWT.NONE)

        val restart = Label(localeGroup, SWT.NONE)
        restart.text = RB.getString(RB.DIALOG_SETTINGS_RESTART_REQUIRED)

        val updateGroup = Group(parent, SWT.NONE)
        updateGroup.text = RB.getString(RB.DIALOG_SETTINGS_GENERAL_UPDATE_TITLE)

        val updateMessage = Label(updateGroup, SWT.NONE)
        updateMessage.text = RB.getString(RB.DIALOG_SETTINGS_GENERAL_UPDATE_MESSAGE)

        updateIntervalComboViewer = UpdateIntervalComboViewer(updateGroup, SWT.NONE)


        val barcodeTypeGroup = Group(parent, SWT.NONE)
        barcodeTypeGroup.text = RB.getString(RB.DIALOG_SETTINGS_BARCODE_TYPE_TITLE)

        val message = Label(barcodeTypeGroup, SWT.NONE)
        message.text = RB.getString(RB.DIALOG_SETTINGS_BARCODE_TYPE_MESSAGE)

        barcodeType = BarcodeFormatComboViewer(barcodeTypeGroup, SWT.NONE)

        GridLayoutUtils.useDefault().applyTo(parent)
        GridLayoutUtils.useDefault().applyTo(localeGroup)
        GridLayoutUtils.useDefault().applyTo(updateGroup)
        GridLayoutUtils.useDefault().applyTo(barcodeTypeGroup)

        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_BOTH).applyTo(parent)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_TOP).applyTo(localeGroup)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_TOP).applyTo(updateGroup)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_TOP).applyTo(barcodeTypeGroup)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_TOP).applyTo(localeComboViewer.combo)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_TOP).applyTo(updateIntervalComboViewer.combo)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_TOP).applyTo(barcodeType.combo)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.BEGINNING_CENTER_FALSE).applyTo(message)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.BEGINNING_CENTER_FALSE).applyTo(localeMessage)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.BEGINNING_CENTER_FALSE).applyTo(updateMessage)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.END_CENTER_FALSE).applyTo(restart)

        return parent
    }

    private fun createPageLayoutTab(folder: TabFolder): Control
    {
        val parent = Composite(folder, SWT.NONE)
        val marginGroup = Group(parent, SWT.NONE)
        marginGroup.text = RB.getString(RB.DIALOG_SETTINGS_MARGIN_TITLE)

        Label(marginGroup, SWT.WRAP).text = RB.getString(RB.DIALOG_SETTINGS_MARGIN_MESSAGE)

        page = PageMarginWidget(marginGroup, SWT.NONE)

        val paddingGroup = Group(parent, SWT.NONE)
        paddingGroup.text = RB.getString(RB.DIALOG_SETTINGS_PADDING_TITLE)

        Label(paddingGroup, SWT.WRAP).text = RB.getString(RB.DIALOG_SETTINGS_PADDING_MESSAGE)

        padding = Spinner(paddingGroup, SWT.BORDER)
        padding.minimum = 0
        padding.maximum = 30

        val paddingValue = Integer.parseInt(HumbugParameterStore.getAsString(HumbugParameter.barcodePadding))
        padding.selection = paddingValue

        val imageHeightGroup = Group(parent, SWT.NONE)
        imageHeightGroup.text = RB.getString(RB.DIALOG_SETTINGS_IMAGE_HEIGHT_TITLE)

        Label(imageHeightGroup, SWT.WRAP).text = RB.getString(RB.DIALOG_SETTINGS_IMAGE_HEIGHT_MESSAGE)

        imageHeight = Spinner(imageHeightGroup, SWT.BORDER)
        imageHeight.minimum = 0
        imageHeight.maximum = 100

        val imageHeightValue = Integer.parseInt(HumbugParameterStore.getAsString(HumbugParameter.imageHeight))
        imageHeight.selection = imageHeightValue

        GridLayoutUtils.useValues(1, false).applyTo(parent)
        GridLayoutUtils.useValues(1, false).applyTo(marginGroup)
        GridLayoutUtils.useValues(1, false).applyTo(paddingGroup)
        GridLayoutUtils.useValues(1, false).applyTo(imageHeightGroup)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_BOTH).applyTo(parent)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_BOTH).applyTo(marginGroup)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_BOTH).applyTo(paddingGroup)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_BOTH).applyTo(imageHeightGroup)

        return parent
    }

    override fun okPressed()
    {
        isLocaleChanged = localeComboViewer.isChanged

        HumbugParameterStore.put(HumbugParameter.marginLeft, page.getMargin(SWT.LEFT))
        HumbugParameterStore.put(HumbugParameter.marginTop, page.getMargin(SWT.TOP))
        HumbugParameterStore.put(HumbugParameter.marginRight, page.getMargin(SWT.RIGHT))
        HumbugParameterStore.put(HumbugParameter.marginBottom, page.getMargin(SWT.BOTTOM))
        HumbugParameterStore.put(HumbugParameter.barcodePadding, padding.selection)
        HumbugParameterStore.put(HumbugParameter.imageHeight, imageHeight.selection)
        HumbugParameterStore.put(HumbugParameter.barcodeFormat, barcodeType.selectedItem)
        HumbugParameterStore.put(HumbugParameter.updateInterval, updateIntervalComboViewer.selectedItem)

        if (isLocaleChanged)
        {
            DialogUtils.showQuestion(RB.getString(RB.DIALOG_SETTINGS_DO_RESTART)) { result ->
                isLocaleChanged = result

                /* Store in the ParameterStore */
                if (result)
                    HumbugParameterStore.put(HumbugParameter.locale, localeComboViewer.selectedItem)

                super@SettingsDialog.okPressed()
            }
        }
        else
        {
            super.okPressed()
        }
    }
}
