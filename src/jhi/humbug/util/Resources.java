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

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

import java.io.*;
import java.util.*;

import jhi.humbug.gui.*;

/**
 * {@link Resources} contains other classes to handle {@link Font}s, {@link Image}s and {@link Color}s
 *
 * @author Sebastian Raubach
 */
public class Resources
{
	private static int zoomFactor = 200;

	/**
	 * Returns the zoom factor that was selected during application start <p> The return value will be one of the following values: <ul> <li>100: 1.0x
	 * zoom</li> <li>150: 1.5x zoom</li> <li>200: 2.0x zoom</li> </ul>
	 *
	 * @return The zoom factor that was selected during application start
	 */
	public static int getZoomFactor()
	{
		return zoomFactor;
	}

	/**
	 * Initializes the {@link Resources} class. Will load the necessary {@link Image}s and {@link Color}s
	 */
	public static void initialize()
	{
		Images.initialize();
		Colors.initialize();
	}

	/**
	 * Disposes all {@link Image}s that were loaded during execution (if they haven't already been disposed)
	 */
	public static void disposeResources()
	{
		Images.disposeAll();
		Colors.disposeAll();
	}

	/**
	 * {@link Fonts} is a utility class to handle {@link Font}s
	 *
	 * @author Sebastian Raubach
	 */
	public static class Fonts
	{
		/**
		 * Applies the given {@link Font} size to the given {@link Control}. A new {@link Font} instance is created internally, but disposed via a
		 * {@link Listener} for {@link SWT#Dispose} attached to the {@link Control}.
		 *
		 * @param control  The {@link Control}
		 * @param fontSize The {@link Font} size
		 */
		public static void applyFontSize(Control control, int fontSize)
		{
			/* Increase the font size */
			FontData[] fontData = control.getFont().getFontData();
			fontData[0].setHeight(fontSize);
			final Font font = new Font(control.getShell().getDisplay(), fontData[0]);
			control.setFont(font);

			control.addListener(SWT.Dispose, event -> {
				if (!font.isDisposed())
					font.dispose();
			});
		}
	}

	/**
	 * {@link Images} is a utility class to handle {@link Image}s
	 *
	 * @author Sebastian Raubach
	 */
	public static class Images
	{
		private static final String             IMAGE_PATH  = "img";
		/** Image cache */
		private static final Map<String, Image> IMAGE_CACHE = new HashMap<>();
		public static Image LOGO;
		public static Image PLACEHOLDER;
		public static Image DELETE;
		public static Image ADD;
		public static Image PDF;
		public static Image SAVE;
		public static Image COPY;
		public static Image ADD_IMAGE;
		public static Image LINKED_IMAGE;
		public static Image FOLDER;
		public static Image OPEN_IMAGE;
		public static Image UP;
		public static Image DOWN;
		public static Image TWITTER_LOGO;
		public static Image EMAIL;
		public static Image WEB;
		public static Image JHI_LOGO;

		private static void initialize()
		{
			JHI_LOGO = loadImage("res/jhi.png");

			PLACEHOLDER = loadImage("res/placeholder.png");

			LOGO = loadImage("res/logo.png");
			ADD = loadImage("icons/add.png");
			DELETE = loadImage("icons/cross.png");
			SAVE = loadImage("icons/disk.png");
			EMAIL = loadImage("icons/email.png");
			FOLDER = loadImage("icons/folder.png");
			OPEN_IMAGE = loadImage("icons/image.png");
			COPY = loadImage("icons/copy.png");
			UP = loadImage("icons/up.png");
			DOWN = loadImage("icons/down.png");
			ADD_IMAGE = loadImage("icons/image-add.png");
			LINKED_IMAGE = loadImage("icons/image-link.png");
			PDF = loadImage("icons/pdf.png");
			TWITTER_LOGO = loadImage("icons/twitter.png");
			WEB = loadImage("icons/world.png");
		}

		public static Image scaleImage(final Image original, final Point size)
		{
			/*
			 * Since the size can actually reach 0, but the Image constructor fails
             * for 0, we catch this here
             */
			if (size.x <= 0 || size.y <= 0)
				return null;

            /* Create a new Image in the correct size */
			Point newSize = getAspectRatioSize(size, original.getBounds(), true, true);
			Image newScaled = new Image(Display.getDefault(), newSize.x, newSize.y);
			GC gc = new GC(newScaled);
			gc.setAntialias(SWT.ON);
			gc.setInterpolation(SWT.HIGH);
			/* Scale the original image to the new size */
			gc.drawImage(original, 0, 0, original.getBounds().width, original.getBounds().height, 0, 0, newSize.x, newSize.y);
			/* You create it, you dispose it */
			gc.dispose();

			return newScaled;
		}

		public static Point getAspectRatioSize(Point targetSize, Rectangle bounds, boolean stayWithin, boolean dontUpscale)
		{
			float w = targetSize.x / (1f * bounds.width);
			float h = targetSize.y / (1f * bounds.height);

			float targetWidth = bounds.width;
			float targetHeight = bounds.height;

			if (stayWithin ? w < h : w > h)
			{
				targetWidth *= w;
				targetHeight *= w;
			}
			else
			{
				targetWidth *= h;
				targetHeight *= h;
			}

			if (dontUpscale && (targetWidth > bounds.width || targetHeight > bounds.height))
				return new Point(bounds.width, bounds.height);
			else
				return new Point(Math.round(targetWidth), Math.round(targetHeight));
		}

		/**
		 * Returns a copy of the given {@link Image} with the given alpha value
		 *
		 * @param image The original {@link Image}
		 * @param alpha The alpha value
		 * @return The copy of the given {@link Image} with the given alpha value
		 */
		@SuppressWarnings("unused")
		public static Image applyAlpha(Image image, int alpha)
		{
			String name = image.toString() + "_" + alpha;

			Image result = IMAGE_CACHE.get(name);

			if (result == null)
			{
				ImageData data = image.getImageData();
				data.alpha = alpha;
				result = new Image(null, data);

				IMAGE_CACHE.put(name, result);
			}

			return result;
		}

		/**
		 * Loads and returns the {@link Image} with the given name
		 *
		 * @param name The image name
		 * @return The {@link Image} object
		 */
		public static Image loadImage(String name)
		{
			/* Check if we've already created that Image before */
			Image newImage = IMAGE_CACHE.get(name);
			if (newImage == null || newImage.isDisposed())
			{
				/* If not, try to load it */
				try
				{
					/* Check if this code is in the jar */
					if (Humbug.WITHIN_JAR)
					{
						newImage = new Image(null, (ImageDataProvider) zoom ->
						{
							zoomFactor = Math.min(zoomFactor, zoom);

							String path = IMAGE_PATH + "/" + zoom + "-" + name;

							InputStream stream = Humbug.class.getClassLoader().getResourceAsStream(path);
							if (stream == null)
							{
								/* Check if the base image exists (zoom=100) */
								path = IMAGE_PATH + "/100-" + name;
								stream = Humbug.class.getClassLoader().getResourceAsStream(path);

								/* If that doesn't exist, try without a zoom level */
								if (stream == null)
								{
									path = IMAGE_PATH + "/" + name;
									stream = Humbug.class.getClassLoader().getResourceAsStream(path);
								}
							}

							Image image = new Image(null, stream);
							ImageData data = image.getImageData();
							image.dispose();

							return data;
						});
					}
					/* Else, we aren't using a jar */
					else
					{
						newImage = new Image(null, (ImageFileNameProvider) zoom ->
						{
							zoomFactor = Math.min(zoomFactor, zoom);

							String path = IMAGE_PATH + "/" + zoom + "-" + name;

							/* Try to fall back */
							if (!new File(path).exists())
							{
								/* Check if the base image exists (zoom=100) */
								path = IMAGE_PATH + "/100-" + name;

								/* If that doesn't exist, try without a zoom level */
								if (!new File(path).exists())
									path = IMAGE_PATH + "/" + name;
							}

							return path;
						});
					}

                    /* Remember that we loaded this image */
					IMAGE_CACHE.put(name, newImage);
				}
				catch (SWTException ex)
				{
					return null;
				}
			}

			return newImage;
		}

		/**
		 * Disposes all cached image resources
		 */
		private static void disposeAll()
		{
			IMAGE_CACHE.values()
					   .stream()
					   .filter(img -> !img.isDisposed())
					   .forEach(Image::dispose);
			IMAGE_CACHE.clear();
		}

		/**
		 * Disposed the given {@link Image} after checking for <code>null</code> and {@link Image#isDisposed()}
		 *
		 * @param oldImage The {@link Image} to dispose
		 */
		public static void disposeImage(Image oldImage)
		{
			if (oldImage != null && !oldImage.isDisposed())
				oldImage.dispose();
		}
	}

	/**
	 * {@link Colors} is a utility class to handle {@link Color}s
	 *
	 * @author Sebastian Raubach
	 */
	public static class Colors
	{
		/** Color cache */
		private static final Map<String, Color> COLOR_CACHE = new HashMap<>();
		public static Color DARK_GREY;
		public static Color WHITE;

		private static void initialize()
		{
			DARK_GREY = loadColor("#444444");
			WHITE = loadColor("#FFFFFF");
		}

		/**
		 * Loads and returns the {@link Color} with the given hex
		 *
		 * @param color The color hex
		 * @return The {@link Color} object
		 */
		public static Color loadColor(String color)
		{
			Color newColor = COLOR_CACHE.get(color);
			if (newColor == null || newColor.isDisposed())
			{
				java.awt.Color col;
				try
				{
					col = java.awt.Color.decode(color);
				}
				catch (Exception e)
				{
					col = java.awt.Color.WHITE;
				}
				int red = col.getRed();
				int blue = col.getBlue();
				int green = col.getGreen();

				newColor = new Color(null, red, green, blue);

				COLOR_CACHE.put(color, newColor);
			}

			return newColor;
		}

		/**
		 * Disposes all cachec color resources
		 */
		private static void disposeAll()
		{
			COLOR_CACHE.values()
					   .stream()
					   .filter(color -> !color.isDisposed())
					   .forEach(Color::dispose);
			COLOR_CACHE.clear();
		}
	}
}
