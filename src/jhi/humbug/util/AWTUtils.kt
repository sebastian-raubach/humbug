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

import org.eclipse.swt.graphics.ImageData
import org.eclipse.swt.graphics.PaletteData
import org.eclipse.swt.graphics.RGB
import java.awt.image.BufferedImage
import java.awt.image.DirectColorModel
import java.awt.image.IndexColorModel

/**
 * [AWTUtils] contains methods to interact with AWT/Swing objects.

 * @author Sebastian Raubach
 */
object AWTUtils
{
    /**
     * Converts the given [BufferedImage] to [ImageData]

     * @param bufferedImage The [BufferedImage]
     * *
     * @return The generated [ImageData]
     */
    fun convertToSWT(bufferedImage: BufferedImage): ImageData?
    {
        if (bufferedImage.colorModel is DirectColorModel)
        {
            val colorModel = bufferedImage.colorModel as DirectColorModel
            val palette = PaletteData(colorModel.redMask, colorModel.greenMask, colorModel.blueMask)
            val data = ImageData(bufferedImage.width, bufferedImage.height, colorModel.pixelSize, palette)
            val raster = bufferedImage.raster

            val pixelArray = IntArray(3)

            for (y in 0 until data.height)
            {
                for (x in 0 until data.width)
                {
                    raster.getPixel(x, y, pixelArray)
                    val pixel = palette.getPixel(RGB(pixelArray[0], pixelArray[1], pixelArray[2]))
                    data.setPixel(x, y, pixel)
                }
            }

            return data
        }
        else if (bufferedImage.colorModel is IndexColorModel)
        {
            val colorModel = bufferedImage.colorModel as IndexColorModel
            val size = colorModel.mapSize
            val reds = ByteArray(size)
            val greens = ByteArray(size)
            val blues = ByteArray(size)

            colorModel.getReds(reds)
            colorModel.getGreens(greens)
            colorModel.getBlues(blues)

            val rgbs = arrayOfNulls<RGB>(size)

            for (i in rgbs.indices)
            {
                rgbs[i] = RGB(reds[i].toInt() and 255, greens[i].toInt() and 255, blues[i].toInt() and 255)
            }

            val palette = PaletteData(*rgbs)
            val data = ImageData(bufferedImage.width, bufferedImage.height, colorModel.pixelSize, palette)
            data.transparentPixel = colorModel.transparentPixel
            val raster = bufferedImage.raster

            val pixelArray = IntArray(1)

            for (y in 0 until data.height)
            {
                for (x in 0 until data.width)
                {
                    raster.getPixel(x, y, pixelArray)
                    data.setPixel(x, y, pixelArray[0])
                }
            }

            return data
        }
        return null
    }
}
