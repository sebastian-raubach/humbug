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

package jhi.humbug.gui.widget;

import com.google.zxing.*;
import com.google.zxing.Writer;
import com.google.zxing.client.j2se.*;
import com.google.zxing.common.*;
import com.google.zxing.oned.*;
import com.google.zxing.qrcode.*;

import org.eclipse.jface.fieldassist.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.window.*;
import org.eclipse.swt.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import java.awt.image.*;
import java.io.*;
import java.util.*;

import javax.imageio.*;

import jhi.humbug.gui.*;
import jhi.humbug.gui.dialog.*;
import jhi.humbug.gui.i18n.*;
import jhi.humbug.gui.viewer.*;
import jhi.humbug.gui.widget.listener.*;
import jhi.humbug.util.*;
import jhi.humbug.util.Resources.*;
import jhi.swtcommons.gui.layout.*;
import jhi.swtcommons.util.*;
import jhi.swtcommons.util.StringUtils;

/**
 * {@link BarcodeRow} is the main gui component of this application. Each row contains a {@link BarcodeFormatComboViewer} for the {@link
 * BarcodeFormat}, a {@link Text} to enter the barcode and a {@link Label} displaying the generated barcode.
 *
 * @author Sebastian Raubach
 */
public class BarcodeRow extends Composite
{
	public static boolean RELAYOUT = true;

	private GridData                 imageLayout;
	private Text                     barcode;
	private String                   barcodeString;
	private BarcodeFormatComboViewer barcodeViewer;
	private BarcodeFormat            barcodeFormat;
	private Label                    barcodeImage;
	private Button                   associateImage;
	private ScrolledComposite        barcodeImageWrapper;
	private ControlDecoration        barcodeDecorator;
	private File                     associatedImage;
	private BufferedImage            bufferedImage;

	private Humbug holder;

	private boolean firstRun      = true;
	private int     initialHeight = -1;

	public BarcodeRow(Composite parent, int style, final Humbug holder)
	{
		super(parent, style);

		this.holder = holder;

		Composite upDown = new Composite(this, SWT.NONE);
		Button up = new Button(upDown, SWT.PUSH);
		up.setImage(Images.UP);
		Button down = new Button(upDown, SWT.PUSH);
		down.setImage(Images.DOWN);

		up.addListener(SWT.Selection, e -> holder.move(BarcodeRow.this, -1));
		down.addListener(SWT.Selection, e -> holder.move(BarcodeRow.this, +1));

		Composite left = new Composite(this, SWT.NONE);
		barcode = new Text(left, SWT.BORDER);
		barcodeViewer = new BarcodeFormatComboViewer(left, SWT.READ_ONLY);

		barcodeFormat = barcodeViewer.getSelectedItem();

        /* Listen to events on the Text */
		barcode.addListener(SWT.Verify, new Listener()
		{
			private ConwayListener conwayListener;

			@Override
			public void handleEvent(Event event)
			{
				final String oldS = barcode.getText();
				final String newS = oldS.substring(0, event.start) + event.text + oldS.substring(event.end);

				updateBarcode(newS);

				if (ConwayListener.KEYWORD.equalsIgnoreCase(barcodeString) && conwayListener == null)
				{
					conwayListener = new ConwayListener(barcodeImage);
					barcodeImage.addListener(SWT.Paint, conwayListener);
				}
				else if (conwayListener != null)
				{
					conwayListener.stop();
					barcodeImage.removeListener(SWT.Paint, conwayListener);
					barcodeImage.redraw();
					conwayListener = null;
				}
			}
		});
		barcode.addListener(SWT.FocusIn, event -> barcode.setSelection(0, barcode.getText().length()));
		barcode.addListener(SWT.FocusOut, event -> updateBarcode(barcode.getText()));
		barcode.addListener(SWT.Traverse, event ->
		{
			if (event.detail == SWT.TRAVERSE_RETURN)
			{
				Humbug.getInstance().onEnterPressed(this);
			}
		});

        /* Add a ControlDecorator to the Text */
		barcodeDecorator = new ControlDecoration(barcode, SWT.TOP | SWT.RIGHT);
		FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
		Image img = fieldDecoration.getImage();
		barcodeDecorator.setImage(img);
		barcodeDecorator.hide();

        /* Listen for selection events on the barcode type viewer */
		barcodeViewer.addSelectionChangedListener(selectionChangedEvent ->
		{
			barcodeFormat = barcodeViewer.getSelectedItem();

			updateBarcode(barcode.getText(), true);

			holder.update();
		});

        /* Set up the scrolled parent of the image */
		barcodeImageWrapper = new ScrolledComposite(this, SWT.H_SCROLL | SWT.BORDER);
		barcodeImageWrapper.setExpandHorizontal(true);
		barcodeImageWrapper.setExpandVertical(true);

        /* Create the image itself */
		barcodeImage = new Label(barcodeImageWrapper, SWT.CENTER | SWT.DOUBLE_BUFFERED);
		barcodeImage.setBackground(Colors.WHITE);
		barcodeImage.addListener(SWT.Dispose, event -> Images.disposeImage(barcodeImage.getImage()));
		addContextMenu();

		barcodeImageWrapper.setContent(barcodeImage);
		barcodeImageWrapper.setMinSize(barcodeImage.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		Composite actionButtons = new Composite(this, SWT.NONE);
		/* Add a button to associate an image with the barcode */
		associateImage = new Button(actionButtons, SWT.FLAT);
		associateImage.setToolTipText(RB.getString(RB.TOOLTIP_BUTTON_ASSOCIATE_IMAGE));
		associateImage.setImage(Images.ADD_IMAGE);
		associateImage.addListener(SWT.Selection, event ->
		{
			ImageAssociationDialog dialog = new ImageAssociationDialog(getShell(), associatedImage);

			if (dialog.open() == Window.OK)
			{
				associatedImage = dialog.getFile();

				associateImage.setImage(associatedImage == null ? Images.ADD_IMAGE : Images.LINKED_IMAGE);
			}
		});

        /* Add a delete button */
		Button deleteButton = new Button(actionButtons, SWT.FLAT);
		deleteButton.setToolTipText(RB.getString(RB.GENERAL_DELETE));
		deleteButton.setImage(Images.DELETE);
		deleteButton.addListener(SWT.Selection, event ->
		{
			BarcodeRow.this.dispose();
			holder.onDelete(BarcodeRow.this);
		});

		/* Layout magic */
		GridLayoutUtils.useValues(4, false).marginWidth(0).applyTo(this);
		GridLayoutUtils.useValues(1, false).marginHeight(0).marginWidth(0).applyTo(upDown);
		GridLayoutUtils.useValues(1, false).marginHeight(0).applyTo(left);
		GridLayoutUtils.useValues(1, false).marginHeight(0).marginWidth(0).applyTo(actionButtons);
		GridLayoutUtils.useValues(1, false).marginHeight(0).marginWidth(0).applyTo(barcodeImageWrapper);
		GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_TOP).applyTo(this);
		GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_CENTER).applyTo(barcode);
		GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_CENTER).applyTo(barcodeViewer.getControl());
		GridDataUtils temp = GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.BEGINNING_CENTER_FALSE);

//		if(!OSUtils.isMac())
		temp.widthHint(250 * Resources.getZoomFactor() / 100);
		temp.applyTo(left);

		GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_BOTH).applyTo(barcodeImage);
		imageLayout = GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_BOTH).create();
		barcodeImageWrapper.setLayoutData(imageLayout);
		GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.END_CENTER_FALSE).applyTo(deleteButton);

        /* Make sure we only tab through the text fields */
		left.setTabList(new Control[]{barcode});
		this.setTabList(new Control[]{left});

        /* Set the focus */
		barcode.setFocus();
		barcode.forceFocus();
	}

	/**
	 * Adds a context menu to {@link #barcodeImage} to save the image to a file
	 */
	private void addContextMenu()
	{
		final Menu menu = new Menu(barcodeImage);
		MenuItem newItem = new MenuItem(menu, SWT.NONE);
		newItem.setText(RB.getString(RB.MENU_CONTEXT_IMAGE_SAVE));
		newItem.setImage(Images.SAVE);

		newItem.addListener(SWT.Selection, event ->
		{
			FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
			dialog.setFilterExtensions(new String[]{"*.png"});

			String path = dialog.open();

			if (!StringUtils.isEmpty(path))
			{
				File outputfile = new File(path);
				try
				{
					ImageIO.write(bufferedImage, "png", outputfile);
				}
				catch (IOException e)
				{
					DialogUtils.handleException(e);
				}
			}
		});

		newItem = new MenuItem(menu, SWT.NONE);
		newItem.setText(RB.getString(RB.MENU_CONTEXT_IMAGE_CLIPBOARD));
		newItem.setImage(Images.COPY);

		newItem.addListener(SWT.Selection, e -> {
			Image image = new Image(Display.getDefault(), barcodeImage.getBounds());
			GC gc = new GC(image);
			barcodeImage.print(gc);
			gc.dispose();

			Clipboard clipboard = new Clipboard(menu.getDisplay());
			ImageTransfer imageTransfer = ImageTransfer.getInstance();
			clipboard.setContents(new Object[]{image.getImageData()}, new Transfer[]{imageTransfer});
		});

        /* Only show the menu if the barcode isn't empty */
		barcodeImage.addListener(SWT.MenuDetect, event -> menu.setVisible(!StringUtils.isEmpty(getBarcode())));
	}

	public Image getImage()
	{
		Image image = new Image(Display.getDefault(), barcodeImage.getBounds());
		GC gc = new GC(image);
		barcodeImage.print(gc);
		gc.dispose();

		return image;
	}

	/**
	 * Update the barcode image
	 *
	 * @param text The barcode text
	 */
	protected void updateBarcode(String text)
	{
		updateBarcode(text, false);
	}

	protected void updateBarcode(String text, boolean force)
	{
		if (!force && !StringUtils.isEmpty(text) && text.equals(barcodeString))
			return;

		if (firstRun)
			initialHeight = barcodeImage.getSize().y - 18;

		if (barcodeFormat == BarcodeFormat.QR_CODE)
			imageLayout.heightHint = 120;
		else
			imageLayout.heightHint = 60;

		if (RELAYOUT)
		{
			barcodeImageWrapper.setLayoutData(imageLayout);
			barcodeImageWrapper.getParent().getParent().layout(true, true);
		}

		firstRun = false;
		/* Remember the selection */
		barcodeString = text;

		BitMatrix bitMatrix;
		Writer writer;
		try
		{
			/* Create the Writer */
			switch (barcodeFormat)
			{
				case CODE_128:
					writer = new Code128Writer();
					break;
				case CODE_39:
					writer = new Code39Writer();
					break;
				case EAN_13:
					writer = new EAN13Writer();
					break;
				case EAN_8:
					writer = new EAN8Writer();
					break;
				case UPC_A:
					writer = new UPCAWriter();
					break;
				case QR_CODE:
					writer = new QRCodeWriter();
					break;
				default:
					return;
			}

            /* Create the matrix and the image */
			Point size = barcodeImageWrapper.getSize();

			Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
			hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
			hints.put(EncodeHintType.MARGIN, 0); /* default = 4 */

			bitMatrix = writer.encode(text,
					barcodeFormat,
					Math.min(240 * Resources.getZoomFactor() / 100, size.x - 20),
					Math.min(120 * Resources.getZoomFactor() / 100, size.y - barcodeImageWrapper.getHorizontalBar().getSize().y - 2),
					hints);
//			bitMatrix = writer.encode(text, selectedItem, size.x - 20, size.y  - barcodeImageWrapper.getHorizontalBar().getSize().y - 2, null);
			bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            /* Convert the AWT image to a SWT image */
			ImageData data = AWTUtils.INSTANCE.convertToSWT(bufferedImage);

            /* Dispose the old image */
			Images.disposeImage(barcodeImage.getImage());

            /* And set the new one */
			if (data != null)
				barcodeImage.setImage(new Image(null, data));

			barcodeImageWrapper.setMinSize(barcodeImage.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		}
		catch (IllegalArgumentException e)
		{
			/* If something goes wrong, update the error message */
			barcodeDecorator.setDescriptionText(e.getLocalizedMessage());
			barcodeDecorator.show();
			/* Remove the image */
			bufferedImage = null;
			Images.disposeImage(barcodeImage.getImage());
			barcodeImage.setImage(null);
			barcodeImageWrapper.setMinSize(barcodeImage.computeSize(SWT.DEFAULT, SWT.DEFAULT));

			return;
		}
		catch (WriterException e)
		{
			e.printStackTrace();
		}

        /* If we reach this, everything went well, so hide the error message */
		barcodeDecorator.hide();
	}

	/**
	 * Returns the {@link BufferedImage} of the barcode
	 *
	 * @return The {@link BufferedImage} of the barcode
	 */
	public BufferedImage getBufferedImage()
	{
		return bufferedImage;
	}

	/**
	 * Returns the actual barcode text
	 *
	 * @return The actual barcode text
	 */
	public String getBarcode()
	{
		return barcodeString;
	}

	/**
	 * Sets the barcode text
	 *
	 * @param text The barcode text
	 */
	public void setBarcode(String text)
	{
		barcode.setText(text);
		updateBarcode(text);
	}

	/**
	 * Returns the associated image
	 *
	 * @return The associated image
	 */
	public File getAssociatedImage()
	{
		return associatedImage;
	}

	public void setAssociatedImage(File associatedImage)
	{
		this.associatedImage = associatedImage;
		this.associateImage.setImage(associatedImage == null ? Images.ADD_IMAGE : Images.LINKED_IMAGE);
	}

	/**
	 * Sets the {@link BarcodeFormat}
	 *
	 * @param format The {@link BarcodeFormat}
	 */
	public void setBarcodeFormat(BarcodeFormat format, boolean reveal)
	{
		barcodeFormat = format;

		if (reveal)
			barcodeViewer.setSelection(new StructuredSelection(format), reveal);
	}

	/**
	 * Forces the barcode to be redrawn
	 */
	public void forceRedraw()
	{
		updateBarcode(barcode.getText(), true);
	}

	public BarcodeFormat getBarcodeFormat()
	{
		return barcodeFormat;
	}

	@Override
	public String toString()
	{
		return barcodeString;
	}
}
