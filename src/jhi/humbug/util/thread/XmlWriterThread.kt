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

import jhi.humbug.gui.i18n.RB
import jhi.humbug.gui.widget.BarcodeRow
import jhi.humbug.util.BarcodeCollection
import jhi.swtcommons.util.DialogUtils
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.core.runtime.NullProgressMonitor
import org.eclipse.jface.operation.IRunnableWithProgress
import org.simpleframework.xml.core.Persister
import java.io.File
import java.lang.reflect.InvocationTargetException


/**
 * [XmlWriterThread] implements [IRunnableWithProgress]. It is used to export the [BarcodeRow]s to a File

 * @author Sebastian Raubach
 */
class XmlWriterThread
/**
 * Writes the given [Collection] of [BarcodeRow]s to the [File]

 * @param barcodes The [Collection] of [BarcodeRow]s to export
 * *
 * @param file     The [File] to write the barcodes to
 */
(private val barcodes: Collection<BarcodeRow>, private val file: File) : IRunnableWithProgress
{
    @Throws(InvocationTargetException::class, InterruptedException::class)
    override fun run(pMonitor: IProgressMonitor?)
    {
        /* Ensure there is a monitor of some sort */
        val monitor = pMonitor ?: NullProgressMonitor()

        /* Tell the user what you are doing */
        monitor.beginTask(RB.getString(RB.THREAD_EXPORT_TITLE), IProgressMonitor.UNKNOWN)

        val collection = BarcodeCollection()

        for (row in barcodes)
            collection.add(row)

        val serializer = Persister()

        try
        {
            serializer.write(collection, file)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
            DialogUtils.handleException(e)
        }

        DialogUtils.showInformation(RB.getString(RB.INFORMATION_SAVE, file.absolutePath))
    }
}
