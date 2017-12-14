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
import jhi.humbug.util.thread.BarcodeImageRenameThread
import jhi.swtcommons.util.DialogUtils
import jhi.swtcommons.util.ShellUtils
import org.eclipse.jface.wizard.Wizard
import java.io.File
import java.lang.reflect.InvocationTargetException

/**
 * @author Sebastian Raubach
 */
class ImageRenameWizard : Wizard()
{
    private lateinit var input: ImageRenameFileWizardPage
    private lateinit var options: ImageRenameOptionsWizardPage

    private val sourceFolder: File?
        get() = input.sourceFolder

    private val targetFolder: File?
        get() = input.targetFolder

    init
    {
        setNeedsProgressMonitor(true)
    }

    override fun getWindowTitle(): String
    {
        return RB.getString(RB.DIALOG_RENAME_IMAGE_TITLE)
    }

    override fun addPages()
    {
        input = ImageRenameFileWizardPage()
        options = ImageRenameOptionsWizardPage()

        addPage(input)
        addPage(options)

        ShellUtils.applySize(container.shell)
    }

    override fun performFinish(): Boolean
    {
        /* Start the progress dialog */
        try
        {
            container.run(true, true, BarcodeImageRenameThread(sourceFolder, targetFolder, options.getBarcodeRestriction(), options.getTryHard()))
        }
        catch (ex: InvocationTargetException)
        {
            DialogUtils.handleException(ex)
        }
        catch (ex: InterruptedException)
        {
            DialogUtils.handleException(ex)
        }

        return true
    }
}
