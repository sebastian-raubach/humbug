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

package jhi.humbug.util.thread

import jhi.humbug.gui.Humbug
import jhi.humbug.gui.i18n.RB
import jhi.humbug.gui.widget.BarcodeRow
import jhi.humbug.util.BarcodeCollection
import jhi.swtcommons.util.DialogUtils
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jface.operation.IRunnableWithProgress
import org.eclipse.swt.widgets.Display
import org.simpleframework.xml.core.Persister
import java.io.File

/**
 * [XmlFileImportThread] implements [IRunnableWithProgress]. It is used to read a given .xml [File] and parse each item into a
 * [BarcodeRow]

 * @author Sebastian Raubach
 */
class XmlFileImportThread(private val file: File) : IRunnableWithProgress
{
    override fun run(pMonitor: IProgressMonitor?)
    {
        val monitor = pMonitor ?: NullProgressMonitor()

        /* Tell the user what you are doing */
        monitor.beginTask(RB.getString(RB.THREAD_IMPORT_TITLE), IProgressMonitor.UNKNOWN)

        val serializer = Persister()

        try
        {
            val coll = serializer.read(BarcodeCollection::class.java, file)

            var counter = 0
            BarcodeRow.RELAYOUT = false
            for (item in coll.items)
            {
                if (monitor.isCanceled)
                {
                    monitor.done()
                    break
                }

                monitor.subTask(RB.getString(RB.THREAD_IMPORT_ROW, ++counter))

                Display.getDefault().syncExec {
                    val row = Humbug.getInstance().addRow(false)
                    row.setBarcodeFormat(item.format, false)
                    row.barcode = item.name
                    row.associatedImage = item.getImage()
                }
            }
            BarcodeRow.RELAYOUT = true
        }
        catch (e: Exception)
        {
            DialogUtils.handleException(e)
        }

        Display.getDefault().syncExec { Humbug.getInstance().update() }
    }
}
