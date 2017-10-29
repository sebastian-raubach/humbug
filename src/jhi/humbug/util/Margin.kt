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

import org.eclipse.swt.SWT

/**
 * [Margin] is used to determine the page margin in pixels.

 * @author Sebastian Raubach
 */
object Margin
{
    /**
     * The four possible sides of the page

     * @author Sebastian Raubach
     */
    enum class Side
    {
        TOP,
        RIGHT,
        BOTTOM,
        LEFT
    }

    /**
     * Returns the margin width/height in pixels for the given [Side]

     * @param side The [Side] of the page
     * *
     * @return The margin width/height in pixels for the given [Side]
     */
    fun getMargin(side: Int): Int
    {
        val margin = 0

        when (side)
        {
            SWT.LEFT -> return Integer.parseInt(HumbugParameterStore.getAsString(HumbugParameter.marginLeft))
            SWT.TOP -> return Integer.parseInt(HumbugParameterStore.getAsString(HumbugParameter.marginTop))
            SWT.RIGHT -> return Integer.parseInt(HumbugParameterStore.getAsString(HumbugParameter.marginRight))
            SWT.BOTTOM -> return Integer.parseInt(HumbugParameterStore.getAsString(HumbugParameter.marginBottom))
        }

        return margin
    }
}
