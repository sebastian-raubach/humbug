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
import jhi.swtcommons.gui.layout.GridDataUtils
import jhi.swtcommons.gui.layout.GridLayoutUtils
import jhi.swtcommons.util.StringUtils
import org.eclipse.jface.wizard.WizardPage
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.*
import java.io.File

/**
 * @author Sebastian Raubach
 */
class ImageRenameFileWizardPage : WizardPage("Image Rename Wizard Input Output Selection")
{
    private var content: Composite? = null

    var sourceFolder: File? = null
        private set

    var targetFolder: File? = null
        private set

    private lateinit var source: Text
    private lateinit var target: Text

    init
    {
        title = RB.getString(RB.DIALOG_RENAME_IMAGE_FILE_TITLE)
        description = RB.getString(RB.DIALOG_RENAME_IMAGE_FILE_DESCRIPTION)
        control = content
    }

    override fun createControl(parent: Composite)
    {
        content = Composite(parent, SWT.NONE)

        Label(content, SWT.NONE).text = RB.getString(RB.DIALOG_RENAME_IMAGE_SOURCE)
        source = Text(content, SWT.BORDER or SWT.READ_ONLY)
        val selectSource = Button(content, SWT.PUSH)
        selectSource.text = RB.getString(RB.GENERAL_BROWSE)

        selectSource.addListener(SWT.Selection) {
            val dialog = DirectoryDialog(shell)

            if (sourceFolder != null)
                dialog.filterPath = sourceFolder!!.absolutePath

            var path: String? = dialog.open()

            if (path == null)
                path = ""

            if (!StringUtils.isEmpty(path))
            {
                source.text = path
                sourceFolder = File(path)
            }

            setCompletion()
        }

        Label(content, SWT.NONE).text = RB.getString(RB.DIALOG_RENAME_IMAGE_TARGET)
        target = Text(content, SWT.BORDER or SWT.READ_ONLY)
        val selectTarget = Button(content, SWT.PUSH)
        selectTarget.text = RB.getString(RB.GENERAL_BROWSE)

        selectTarget.addListener(SWT.Selection) {
            val dialog = DirectoryDialog(shell)

            if (targetFolder != null)
                dialog.filterPath = targetFolder?.absolutePath
            else if (sourceFolder != null)
                dialog.filterPath = sourceFolder?.absolutePath

            val path: String = dialog.open() ?: ""

            if (!StringUtils.isEmpty(path))
            {
                target.text = path
                targetFolder = File(path)
            }

            setCompletion()
        }

        GridLayoutUtils.useValues(3, false).applyTo(content)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_CENTER).applyTo(source)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.END_CENTER_FALSE).applyTo(selectSource)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_CENTER).applyTo(target)
        GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.END_CENTER_FALSE).applyTo(selectTarget)

        isPageComplete = false
        control = content
    }

    private fun setCompletion()
    {
        if (sourceFolder != null && targetFolder != null)
        {
            val s = sourceFolder as File
            val t = targetFolder as File
            isPageComplete = s.exists() && s.isDirectory && t.exists() && t.isDirectory
        }
        else
        {
            isPageComplete = false
        }
    }
}
