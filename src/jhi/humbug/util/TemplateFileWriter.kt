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

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException

/**
 * @author Sebastian Raubach
 */
object TemplateFileWriter
{
    data class TemplatePlaceholders(var cellSpacing: Int = 1, var marginTop: Int = 2, var marginRight: Int = 2, var marginBottom: Int = 2, var marginLeft: Int = 2, var maxImageHeight: Int? = null)
    {
        fun getCellSpacing(): String = "${cellSpacing}mm"

        fun getMarginTop(): String = "${marginTop}mm"

        fun getMarginRight(): String = "${marginRight}mm"

        fun getMarginBottom(): String = "${marginBottom}mm"

        fun getMarginLeft(): String = "${marginLeft}mm"

        fun getMaxImageHeight(): String = if (maxImageHeight != null) "${maxImageHeight}mm" else "scale-to-fit"
    }

    @Throws(IOException::class)
    fun write(br: BufferedReader, bw: BufferedWriter, placeholders: TemplatePlaceholders)
    {
        br.forEachLine { s ->
            run {
                bw.write(replace(s, placeholders))
                bw.newLine()
            }
        }
    }

    private fun replace(pLine: String, placeholders: TemplatePlaceholders): String
    {
        var line = pLine
        line = line.replace("{{cell-spacing}}", placeholders.getCellSpacing())
        line = line.replace("{{margin-top}}", placeholders.getMarginTop())
        line = line.replace("{{margin-right}}", placeholders.getMarginRight())
        line = line.replace("{{margin-bottom}}", placeholders.getMarginBottom())
        line = line.replace("{{margin-left}}", placeholders.getMarginLeft())
        line = line.replace("{{max-image-height}}", placeholders.getMaxImageHeight())
        line = line.replace("{{spacing}}", placeholders.getCellSpacing())

        return line
    }
}
