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

package jhi.humbug.util;

import com.google.zxing.*;

import org.simpleframework.xml.*;

import java.io.*;
import java.net.*;
import java.util.*;

import jhi.humbug.gui.widget.*;

/**
 * @author Sebastian Raubach
 */
@Root(name = "barcode-list", strict = false)
public class BarcodeCollection
{
	@ElementList(name = "item", inline = true, required = false)
	private List<BarcodeItem> items = new ArrayList<>();

	public BarcodeItem add(BarcodeRow row)
	{
		String barcode = row.getBarcode();
		File image = row.getAssociatedImage();
		BarcodeFormat format = row.getBarcodeFormat();

		BarcodeItem item = new BarcodeItem(barcode, image, format);
		items.add(item);

		return item;
	}

	public List<BarcodeItem> getItems()
	{
		return items;
	}

	public void setItems(List<BarcodeItem> items)
	{
		this.items = items;
	}

	@Root(name = "item", strict = false)
	public static class BarcodeItem
	{
		@Element
		private String        name;
		@Element(required = false)
		private String        image;
		@Element(required = false)
		private String        barcode;
		@Element
		private BarcodeFormat format;

		@SuppressWarnings("unused")
		public BarcodeItem()
		{
		}

		public BarcodeItem(String name, File image, BarcodeFormat format)
		{
			this.name = name;
			this.image = image != null ? image.toURI().toString() : null;
			this.format = format;
		}

		public String getName()
		{
			return name;
		}

		@SuppressWarnings("unused")
		public void setName(String name)
		{
			this.name = name;
		}

		public File getImage()
		{
			try
			{
				try
				{
					return new File(new URI(image));
				}
				catch (URISyntaxException e)
				{
					return new File(image);
				}
			}
			catch (NullPointerException e)
			{
				return null;
			}
		}

		public void setImage(File image)
		{
			this.image = image.toURI().toString();
		}

		public File getBarcode()
		{
			try
			{
				return new File(new URI(barcode));
			}
			catch (NullPointerException | URISyntaxException e)
			{
				return null;
			}
		}

		public void setBarcode(File barcode)
		{
			this.barcode = barcode.toURI().toString();
		}

		public BarcodeFormat getFormat()
		{
			return format;
		}

		@SuppressWarnings("unused")
		public void setFormat(BarcodeFormat format)
		{
			this.format = format;
		}
	}
}
