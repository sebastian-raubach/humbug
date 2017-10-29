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

package jhi.humbug.util;

import com.google.zxing.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import jhi.humbug.gui.*;
import jhi.humbug.util.thread.*;
import jhi.swtcommons.util.*;

/**
 * {@link PropertyReader} is a wrapper around {@link Properties} to read properties.
 *
 * @author Sebastian Raubach
 */
public class PropertyReader extends jhi.swtcommons.util.PropertyReader
{
	private static final String PREFERENCES_LOCALE = "preferences.locale";

	private static final String PREFERENCES_UPDATE_INTERVAL = "preferences.update.interval";

	private static final String PREFERENCES_MARGIN_TOP    = "preferences.margin.top";
	private static final String PREFERENCES_MARGIN_RIGHT  = "preferences.margin.right";
	private static final String PREFERENCES_MARGIN_BOTTOM = "preferences.margin.bottom";
	private static final String PREFERENCES_MARGIN_LEFT   = "preferences.margin.left";

	private static final String PREFERENCES_BARCODE_PADDING = "preferences.barcode.padding";

	private static final String PREFERENCES_IMAGE_HEIGHT = "preferences.image.height";

	private static final String PREFERENCES_BARCODE_FORMAT = "preferences.barcode.format";

	private static final String PREFERENCES_MISSING_BARCODE_OPTION   = "preferences.image.rename.missing.barcode.option";
	private static final String PREFERENCES_DUPLICATE_BARCODE_OPTION = "preferences.image.rename.duplicate.barcode.option";

	private static final String INTERNAL_USER_ID = "internal.user.id";

	/** The name of the properties file (slash necessary for MacOS X) */
	private static final String PROPERTIES_FILE = "/humbug.properties";

	private static final String PROPERTIES_FOLDER_OLD = "scri-bioinf";
	private static final String PROPERTIES_FOLDER_NEW = "jhi-ics";

	private static File localFile;

	public PropertyReader()
	{
		super(PROPERTIES_FILE);
	}

	/**
	 * Loads the properties {@link File}. It will try to load the local file first (in home directory). If this file doesn't exist, it will fall back
	 * to the default within the jar (or the local file in the project during development).
	 *
	 * @throws IOException Thrown if the file interaction fails
	 */
	public void load() throws IOException
	{
		/* Move old file to new location */
		File oldFile = new File(new File(System.getProperty("user.home"), "." + PROPERTIES_FOLDER_OLD), PROPERTIES_FILE);
		File newFile = new File(new File(System.getProperty("user.home"), "." + PROPERTIES_FOLDER_NEW), PROPERTIES_FILE);

		/* If there's a new file AND an old one, just delete the old one */
		if (newFile.exists() && oldFile.exists())
		{
			oldFile.delete();
		}
		/* If there's no new one, but an old one, copy it across, then delete the old one */
		else if (!newFile.exists() && oldFile.exists())
		{
			newFile.getParentFile().mkdirs();
			Files.copy(oldFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			oldFile.delete();
		}


		localFile = newFile;

		InputStream stream = null;

		try
		{
			if (localFile.exists())
			{
				stream = new FileInputStream(localFile);
			}
			else
			{
				if (Humbug.WITHIN_JAR)
					stream = PropertyReader.class.getResourceAsStream(PROPERTIES_FILE);
				else
					stream = new FileInputStream(new File("res", PROPERTIES_FILE));
			}

			properties.load(stream);
		}
		finally
		{
			if (stream != null)
			{
				try
				{
					stream.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}

		HumbugParameterStore store = HumbugParameterStore.INSTANCE;

        /* Get the Locale of the GUI fall back on ENGLISH if necessary */
		String localeGUIString = getProperty(PREFERENCES_LOCALE);
		Locale locale;
		if (StringUtils.isEmpty(localeGUIString))
			locale = Locale.ENGLISH;
		else
			locale = new Locale(localeGUIString);
		store.put(HumbugParameter.locale, locale);
		Locale.setDefault(locale);

		/* Get the update interval and fall back on STARTUP if necessary */
		String updateIntervalString = getProperty(PREFERENCES_UPDATE_INTERVAL);
		Install4jUtils.UpdateInterval updateInterval;
		try
		{
			updateInterval = Install4jUtils.UpdateInterval.valueOf(updateIntervalString);
		}
		catch (Exception e)
		{
			updateInterval = Install4jUtils.UpdateInterval.STARTUP;
		}
		store.put(HumbugParameter.updateInterval, updateInterval);

		String marginLeft = getProperty(PREFERENCES_MARGIN_LEFT);
		if (StringUtils.isEmpty(marginLeft))
			marginLeft = "5";
		store.put(HumbugParameter.marginLeft, marginLeft, Integer.class);

		String marginTop = getProperty(PREFERENCES_MARGIN_TOP);
		if (StringUtils.isEmpty(marginTop))
			marginTop = "5";
		store.put(HumbugParameter.marginTop, marginTop, Integer.class);

		String marginRight = getProperty(PREFERENCES_MARGIN_RIGHT);
		if (StringUtils.isEmpty(marginRight))
			marginRight = "5";
		store.put(HumbugParameter.marginRight, marginRight, Integer.class);

		String marginBottom = getProperty(PREFERENCES_MARGIN_BOTTOM);
		if (StringUtils.isEmpty(marginBottom))
			marginBottom = "5";
		store.put(HumbugParameter.marginBottom, marginBottom, Integer.class);

		String barcodePadding = getProperty(PREFERENCES_BARCODE_PADDING);
		if (StringUtils.isEmpty(barcodePadding))
			barcodePadding = "10";
		store.put(HumbugParameter.barcodePadding, barcodePadding, Integer.class);

		String imageHeight = getProperty(PREFERENCES_IMAGE_HEIGHT);
		if (StringUtils.isEmpty(imageHeight))
			imageHeight = "10";
		store.put(HumbugParameter.imageHeight, imageHeight, Integer.class);

		String barcodeFormat = getProperty(PREFERENCES_BARCODE_FORMAT);
		BarcodeFormat format;
		try
		{
			format = BarcodeFormat.valueOf(barcodeFormat);
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			format = BarcodeFormat.CODE_128;
		}
		store.put(HumbugParameter.barcodeFormat, format);

		/* Get the user id */
		store.put(HumbugParameter.userId, getProperty(INTERNAL_USER_ID, SystemUtils.createGUID(32)));

		String missingBarcodeOption = getProperty(PREFERENCES_MISSING_BARCODE_OPTION);
		BarcodeImageRenameThread.MissingBarcodeOption mOption;
		try
		{
			mOption = BarcodeImageRenameThread.MissingBarcodeOption.valueOf(missingBarcodeOption);
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			mOption = BarcodeImageRenameThread.MissingBarcodeOption.COPY;
		}
		store.put(HumbugParameter.missingBarcodeOption, mOption);

		String duplicateBarcodeOption = getProperty(PREFERENCES_DUPLICATE_BARCODE_OPTION);
		BarcodeImageRenameThread.DuplicateBarcodeOption dOption;
		try
		{
			dOption = BarcodeImageRenameThread.DuplicateBarcodeOption.valueOf(duplicateBarcodeOption);
		}
		catch (IllegalArgumentException | NullPointerException e)
		{
			dOption = BarcodeImageRenameThread.DuplicateBarcodeOption.CONCATENATE;
		}
		store.put(HumbugParameter.duplicateBarcodeOption, dOption);
	}

	/**
	 * Stores the {@link Parameter}s from the {@link ParameterStore} to the {@link Properties} object and then saves it using {@link
	 * Properties#store(OutputStream, String)}.
	 *
	 * @throws IOException Thrown if the file interaction fails
	 */
	public void store() throws IOException
	{
		if (localFile == null)
			return;

		HumbugParameterStore store = HumbugParameterStore.INSTANCE;
		set(PREFERENCES_LOCALE, store.getAsString(HumbugParameter.locale));
		set(PREFERENCES_MARGIN_LEFT, store.getAsString(HumbugParameter.marginLeft));
		set(PREFERENCES_MARGIN_TOP, store.getAsString(HumbugParameter.marginTop));
		set(PREFERENCES_MARGIN_RIGHT, store.getAsString(HumbugParameter.marginRight));
		set(PREFERENCES_MARGIN_BOTTOM, store.getAsString(HumbugParameter.marginBottom));
		set(PREFERENCES_BARCODE_PADDING, store.getAsString(HumbugParameter.barcodePadding));
		set(PREFERENCES_IMAGE_HEIGHT, store.getAsString(HumbugParameter.imageHeight));
		set(PREFERENCES_BARCODE_FORMAT, store.getAsString(HumbugParameter.barcodeFormat));
		set(PREFERENCES_UPDATE_INTERVAL, store.getAsString(HumbugParameter.updateInterval));
		set(INTERNAL_USER_ID, store.getAsString(HumbugParameter.userId));
		set(PREFERENCES_MISSING_BARCODE_OPTION, store.getAsString(HumbugParameter.missingBarcodeOption));
		set(PREFERENCES_DUPLICATE_BARCODE_OPTION, store.getAsString(HumbugParameter.duplicateBarcodeOption));

		localFile.getParentFile().mkdirs();
		localFile.createNewFile();
		properties.store(new FileOutputStream(localFile), null);
	}
}
