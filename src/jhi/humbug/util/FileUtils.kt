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

package jhi.humbug.util

import jhi.humbug.gui.Humbug
import java.io.*

/**
 * [FileUtils] contains methods to read/write to [File]s.

 * @author Sebastian Raubach
 */
object FileUtils
{
    enum class ImageFileType
    {
        JPG,
        JPEG,
        PNG,
        GIF,
        TIFF
    }

    enum class ImportFileType
    {
        XML,
        TXT
    }

    fun isImage(name: String): Boolean = ImageFileType.values().any {
        name.toLowerCase().endsWith(it.name.toLowerCase())
    }

    fun createUniqueFile(parent: File, filename: String, extension: String): File
    {
        var target = File(parent, filename + "." + extension)
        var counter = 1
        while (target.exists())
            target = File(parent, filename + "-" + counter++ + "." + extension)

        return target
    }

    fun createUniqueFolder(parent: File, folder: String): File
    {
        var target = File(parent, folder)
        var counter = 1
        while (target.exists())
            target = File(parent, folder + "-" + counter++)

        return target
    }

    @Throws(IOException::class)
    fun readLicense(path: String): String
    {
        val builder = StringBuilder()

        val br: BufferedReader
        if (Humbug.WITHIN_JAR)
            br = BufferedReader(InputStreamReader(FileUtils::class.java.getResourceAsStream("/" + path), "UTF-8"))
        else
            br = BufferedReader(FileReader(path))

        br.lines().forEachOrdered { s -> builder.append(s).append("\n") }

        br.close()

        return builder.toString()
    }
}
