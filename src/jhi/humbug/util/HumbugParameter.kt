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

package jhi.humbug.util

import com.google.zxing.BarcodeFormat
import jhi.humbug.util.thread.BarcodeImageRenameThread
import jhi.swtcommons.util.Install4jUtils
import java.util.*

/**
 * @author Sebastian Raubach
 */
enum class HumbugParameter constructor(private val type: Class<*>) : jhi.swtcommons.util.Parameter
{
    locale(Locale::class.java),
    updateInterval(Install4jUtils.UpdateInterval::class.java),
    marginLeft(Int::class.java),
    marginTop(Int::class.java),
    marginRight(Int::class.java),
    marginBottom(Int::class.java),
    barcodePadding(Int::class.java),
    imageHeight(Int::class.java),
    barcodeFormat(BarcodeFormat::class.java),
    missingBarcodeOption(BarcodeImageRenameThread.MissingBarcodeOption::class.java),
    duplicateBarcodeOption(BarcodeImageRenameThread.DuplicateBarcodeOption::class.java),
    userId(String::class.java);

    override fun getType(): Class<*>
    {
        return type
    }
}
