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

import jhi.humbug.gui.Humbug
import jhi.humbug.gui.i18n.RB
import jhi.humbug.gui.widget.BarcodeRow
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jface.operation.IRunnableWithProgress
import org.eclipse.swt.widgets.Display
import java.io.File
import java.lang.reflect.InvocationTargetException

/**
 * [TxtFileImportThread] implements [IRunnableWithProgress]. It is used to read a given .txt [File] and parse each line into a
 * [BarcodeRow]

 * @author Sebastian Raubach
 */
class TxtFileImportThread(private val file: File) : IRunnableWithProgress
{

    @Throws(InvocationTargetException::class, InterruptedException::class)
    override fun run(pMonitor: IProgressMonitor?)
    {
        val monitor = pMonitor ?: NullProgressMonitor()

        /* Tell the user what you are doing */
        monitor.beginTask(RB.getString(RB.THREAD_IMPORT_TITLE), IProgressMonitor.UNKNOWN)

        val lines = file.readLines()

        var counter = 0
        lines.forEach { s ->
            run {
                if (monitor.isCanceled)
                {
                    monitor.done()
                    return@forEach
                }

                monitor.subTask(RB.getString(RB.THREAD_IMPORT_ROW, ++counter))

                Display.getDefault().syncExec {
                    val row = Humbug.getInstance().addRow(false)
                    row.barcode = s
                }
            }
        }

        Display.getDefault().syncExec { Humbug.getInstance().update() }
    }
}
