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

package jhi.humbug.util.thread

import com.google.zxing.BarcodeFormat
import jhi.humbug.gui.Humbug
import jhi.humbug.gui.i18n.RB
import jhi.humbug.gui.widget.BarcodeRow
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jface.operation.IRunnableWithProgress
import org.eclipse.swt.dnd.Clipboard
import org.eclipse.swt.widgets.Display
import java.lang.reflect.InvocationTargetException

/**
 * [ClipboardImportThread] implements [IRunnableWithProgress]. It is used to read the content of the system [Clipboard], parse it
 * and add a new [BarcodeRow] for each read line.

 * @author Sebastian Raubach
 */
class ClipboardImportThread(private val clipboard: String, private val format: BarcodeFormat) : IRunnableWithProgress
{

    @Throws(InvocationTargetException::class, InterruptedException::class)
    override fun run(pMonitor: IProgressMonitor?)
    {
        val monitor = pMonitor ?: NullProgressMonitor()

        val newLine = "[\\r\\n]+"
        val lines = clipboard.split(newLine.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val workload = lines.size

        /* Tell the user what you are doing */
        monitor.beginTask(RB.getString(RB.THREAD_IMPORT_TITLE), workload)

        var counter = 0

        for (line in lines)
        {
            if (monitor.isCanceled)
            {
                monitor.done()
                break
            }

            monitor.subTask(RB.getString(RB.THREAD_IMPORT_ROW, ++counter))

            Display.getDefault().syncExec {
                val row = Humbug.getInstance().addRow(false)
                row.barcode = line
                row.setBarcodeFormat(format, false)
            }

            monitor.worked(1)
        }

        Display.getDefault().syncExec { Humbug.getInstance().update() }
    }
}
