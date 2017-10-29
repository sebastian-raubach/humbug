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

package jhi.humbug.gui;

import com.google.zxing.*;

import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.operation.*;
import org.eclipse.jface.window.*;
import org.eclipse.jface.wizard.*;
import org.eclipse.swt.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.List;

import jhi.humbug.gui.dialog.*;
import jhi.humbug.gui.dialog.wizard.*;
import jhi.humbug.gui.i18n.*;
import jhi.humbug.gui.widget.*;
import jhi.humbug.util.*;
import jhi.humbug.util.Resources.*;
import jhi.humbug.util.thread.*;
import jhi.swtcommons.gui.*;
import jhi.swtcommons.gui.layout.*;
import jhi.swtcommons.util.*;
import jhi.swtcommons.util.PropertyReader;

/**
 * {@link Humbug} is the main class of the project. It extends {@link RestartableApplication}.
 *
 * @author Sebastian Raubach
 */
public class Humbug extends RestartableApplication
{
	private static final String APP_ID         = "4399-5140-3074-5227";
	private static final String UPDATE_ID      = "314";
	private static final String VERSION_NUMBER = "x.x.x";
	private static final String UPDATER_URL    = "https://ics.hutton.ac.uk/resources/humbug/installers/updates.xml";
	private static final String TRACKER_URL    = "https://ics.hutton.ac.uk/resources/humbug/logs/humbug.pl";

	/** Indicates whether the application is run form a jar or not */
	public static  boolean           WITHIN_JAR;
	/** The instance of {@link Humbug} */
	private static Humbug            INSTANCE;
	/** The {@link List} of {@link BarcodeRow} */
	private        List<BarcodeRow>  rows;
	/** The {@link ScrolledComposite} holding all the {@link BarcodeRow}s */
	private        ScrolledComposite scrolledContainer;
	/** The content {@link Composite} inside the {@link ScrolledComposite} */
	private        Composite         content;
	private        Composite         buttonBar;

	public static void main(String[] args)
	{
		/* Check if we are running from within a jar or the IDE */
		WITHIN_JAR = Humbug.class.getResource(Humbug.class.getSimpleName() + ".class").toString().startsWith("jar");

		new Humbug();
	}

	/**
	 * Returns the instance of {@link Humbug} (synchronized)
	 *
	 * @return The instance of {@link Humbug} (synchronized)
	 */
	public static synchronized Humbug getInstance()
	{
		if (INSTANCE != null)
			return INSTANCE;
		else
			throw new RuntimeException("No instance of Humbug available");
	}

	protected PropertyReader getPropertyReader()
	{
		return new jhi.humbug.util.PropertyReader();
	}

	protected void onPreStart()
	{
		checkForUpdate(true);
	}

	@Override
	protected void onStart()
	{
		INSTANCE = this;

		shell.setText(RB.getString(RB.APPLICATION_TITLE));
		shell.setImage(Images.LOGO);

		if (rows == null)
			rows = new ArrayList<>();
		else
			rows.forEach(BarcodeRow::dispose);

		rows.clear();

		WidgetUtils.dispose(scrolledContainer, content, buttonBar);
		scrolledContainer = null;
		content = null;
		buttonBar = null;

		scrolledContainer = new ScrolledComposite(shell, SWT.V_SCROLL | SWT.BORDER);
		scrolledContainer.setExpandHorizontal(true);
		scrolledContainer.setExpandVertical(true);

		content = new Composite(scrolledContainer, SWT.NONE);
		scrolledContainer.setContent(content);

		buttonBar = new Composite(shell, SWT.NONE);

		Button addRow = new Button(buttonBar, SWT.FLAT);
		addRow.setImage(Images.ADD);
		addRow.setToolTipText(RB.getString(RB.TOOLTIP_BUTTON_ADD));
		addRow.setText(RB.getString(RB.BUTTON_ADD));
		addRow.addListener(SWT.Selection, event -> addRow(true));

		Button clear = new Button(buttonBar, SWT.FLAT);
		clear.setImage(Images.DELETE);
		clear.setToolTipText(RB.getString(RB.TOOLTIP_BUTTON_CLEAR));
		clear.setText(RB.getString(RB.BUTTON_CLEAR));
		clear.addListener(SWT.Selection, event ->
		{
			if (!CollectionUtils.isEmpty(rows))
			{
				DialogUtils.showQuestion(RB.getString(RB.QUESTION_CONFIRM_CLEAR), result ->
				{
					if (result)
					{
						rows.forEach(BarcodeRow::dispose);
						rows.clear();

						scrolledContainer.setMinHeight(content.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
					}
				});
			}
		});

		Button savePdf = new Button(buttonBar, SWT.FLAT);
		savePdf.setImage(Images.PDF);
		savePdf.setToolTipText(RB.getString(RB.TOOLTIP_BUTTON_PDF));
		savePdf.setText(RB.getString(RB.BUTTON_PDF));
		savePdf.addListener(SWT.Selection, event ->
		{
			if (CollectionUtils.isEmpty(rows))
			{
				DialogUtils.showError(RB.getString(RB.ERROR_INPUT_EMPTY));
			}
			else if (checkInput())
			{
				FileDialog dialog = new FileDialog(shell, SWT.SAVE);
				dialog.setFilterExtensions(new String[]{"*.pdf"});
				dialog.setOverwrite(true);

				String filePath = dialog.open();

				if (!StringUtils.isEmpty(filePath))
				{
					IRunnableWithProgress op = new PdfWriterThreadXsl(rows, new File(filePath));

					/* Start the progress dialog */
					try
					{
						new ProgressMonitorDialog(content.getShell()).run(true, true, op);
					}
					catch (InvocationTargetException | InterruptedException e)
					{
						DialogUtils.handleException(e);
					}
				}
			}
			else
			{
				DialogUtils.showError(RB.getString(RB.ERROR_INPUT));
			}
		});

		GridLayoutUtils.useDefault().applyTo(scrolledContainer);
		GridLayoutUtils.useDefault().applyTo(content);
		GridLayoutUtils.useValues(3, false).applyTo(buttonBar);
		GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_BOTTOM_FALSE).applyTo(buttonBar);
		GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_BOTH).applyTo(scrolledContainer);
		GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_BOTH).applyTo(content);
		GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.BEGINNING_CENTER_FALSE).applyTo(addRow);
		GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.BEGINNING_CENTER).applyTo(clear);
		GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.END_CENTER_FALSE).applyTo(savePdf);

		addRow(true);

		createMenuBar();
	}

	/**
	 * Creates the {@link Menu}.
	 */
	private void createMenuBar()
	{
		Menu oldMenu = shell.getMenuBar();
		if (oldMenu != null && !oldMenu.isDisposed())
			oldMenu.dispose();

		Menu menuBar = new Menu(shell, SWT.BAR);
		Menu fileMenu = new Menu(menuBar);
		Menu importMenu = new Menu(menuBar);
		final Menu aboutMenu = new Menu(menuBar);

        /* File */
		MenuItem item = new MenuItem(menuBar, SWT.CASCADE);
		item.setText(RB.getString(RB.MENU_MAIN_FILE));
		item.setMenu(fileMenu);

        /* Import */
		item = new MenuItem(fileMenu, SWT.CASCADE);
		item.setText(RB.getString(RB.MENU_MAIN_FILE_IMPORT));
		item.setMenu(importMenu);

        /* Help */
		item = new MenuItem(menuBar, SWT.CASCADE);
		item.setText(RB.getString(RB.MENU_MAIN_HELP));
		item.setMenu(aboutMenu);

        /* File - Import from txt File */
		item = new MenuItem(importMenu, SWT.NONE);
		item.setText(RB.getString(RB.MENU_MAIN_FILE_IMPORT_TXT_FILE));
		item.addListener(SWT.Selection, e ->
		{
			FileDialog dialog = new FileDialog(shell, SWT.OPEN);
			dialog.setFilterExtensions(new String[]{"*.txt", "*.*"});

			String file = dialog.open();

			if (!StringUtils.isEmpty(file))
				importFromFile(file, FileUtils.ImportFileType.TXT);
		});

		/* File - Import from XML File */
		item = new MenuItem(importMenu, SWT.NONE);
		item.setText(RB.getString(RB.MENU_MAIN_FILE_IMPORT_XML_FILE));
		item.addListener(SWT.Selection, e ->
		{
			FileDialog dialog = new FileDialog(shell, SWT.OPEN);
			dialog.setFilterExtensions(new String[]{"*.xml", "*.*"});

			String file = dialog.open();

			if (!StringUtils.isEmpty(file))
				importFromFile(file, FileUtils.ImportFileType.XML);
		});

        /* File - Import from Clipboard */
		item = new MenuItem(importMenu, SWT.NONE);
		item.setText(RB.getString(RB.MENU_MAIN_FILE_IMPORT_CLIPBOARD));
		item.addListener(SWT.Selection, e ->
		{
			Clipboard cb = new Clipboard(display);
			TextTransfer transfer = TextTransfer.getInstance();
			String data = (String) cb.getContents(transfer);
			cb.dispose();

			if (data != null)
			{
				ClipboardContentDialog dialog = new ClipboardContentDialog(shell);

				if (dialog.open() == Window.OK)
				{
					data = dialog.getClipboardContent();
					BarcodeFormat format = dialog.getBarcodeFormat();

					if (!StringUtils.isEmpty(data))
						importFromClipboard(data, format);
				}
			}
			else
			{
				DialogUtils.showError(RB.getString(RB.ERROR_CLIPBOARD_EMPTY));
			}
		});

		/* File - Export as XML */
		item = new MenuItem(fileMenu, SWT.NONE);
		item.setText(RB.getString(RB.MENU_MAIN_FILE_EXPORT_XML_FILE));
		item.addListener(SWT.Selection, e ->
		{
			if (CollectionUtils.isEmpty(rows))
			{
				DialogUtils.showError(RB.getString(RB.ERROR_INPUT_EMPTY));
			}
			else if (checkInput())
			{
				FileDialog dialog = new FileDialog(shell, SWT.SAVE);
				dialog.setFilterExtensions(new String[]{"*.xml"});
				dialog.setOverwrite(true);

				String filePath = dialog.open();

				if (!StringUtils.isEmpty(filePath))
				{
					IRunnableWithProgress op = new XmlWriterThread(rows, new File(filePath));

					/* Start the progress dialog */
					try
					{
						new ProgressMonitorDialog(content.getShell()).run(true, true, op);
					}
					catch (InvocationTargetException | InterruptedException ex)
					{
						DialogUtils.handleException(ex);
					}
				}
			}
			else
			{
				DialogUtils.showError(RB.getString(RB.ERROR_INPUT));
			}
		});

		/* File - Bulk image rename */
		item = new MenuItem(fileMenu, SWT.NONE);
		item.setText(RB.getString(RB.MENU_MAIN_FILE_BULK_IMAGE_RENAME));
		item.addListener(SWT.Selection, e ->
		{
			ImageRenameWizard wizard = new ImageRenameWizard();
			WizardDialog dialog = new WizardDialog(shell, wizard);
			dialog.open();
		});

        /* File - Exit */
		addQuitMenuItemListener(RB.getString(RB.MENU_MAIN_FILE_EXIT), fileMenu, e -> shutdown());

		/* Help - Online help */
		item = new MenuItem(aboutMenu, SWT.NONE);
		item.setText(RB.getString(RB.MENU_MAIN_HELP_ONLINE_HELP));
		item.addListener(SWT.Selection, e -> OSUtils.open(RB.getString(RB.URL_ONLINE_HELP)));

        /* Help - Settings */
		addPreferencesMenuItemListener(RB.getString(RB.MENU_MAIN_HELP_SETTINGS), aboutMenu, e ->
		{
			SettingsDialog dialog = new SettingsDialog(shell);

			if (dialog.open() == Window.OK)
			{
				if (dialog.isLocaleChanged())
					onRestart();
			}
		});

		/* Help - Check for updates */
		item = new MenuItem(aboutMenu, SWT.NONE);
		item.setText(RB.getString(RB.MENU_MAIN_HELP_UPDATE));
		item.addListener(SWT.Selection, e -> checkForUpdate(false));
		item.setEnabled(WITHIN_JAR);

		/* Help - About */
		addAboutMenuItemListener(RB.getString(RB.MENU_MAIN_HELP_ABOUT), aboutMenu, e -> new AboutDialog(shell).open());

		shell.setMenuBar(menuBar);
	}

	/**
	 * Imports each row of the clipboard into a separate {@link BarcodeRow}
	 *
	 * @param data   The content of the clipboard
	 * @param format The {@link BarcodeFormat} to use
	 */
	protected void importFromClipboard(String data, BarcodeFormat format)
	{
		/* If there is only one row */
		if (rows.size() == 1)
		{
			BarcodeRow row = rows.get(0);

            /* Remove it if it's empty */
			if (StringUtils.isEmpty(row.getBarcode()))
			{
				row.dispose();
				rows.remove(row);
			}
		}

		IRunnableWithProgress op = new ClipboardImportThread(data, format);

        /* Start the progress dialog */
		try
		{
			new ProgressMonitorDialog(content.getShell()).run(true, true, op);
		}
		catch (InvocationTargetException | InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Imports each item of the input file into a separate {@link BarcodeRow}
	 *
	 * @param path The path of the file
	 */
	protected void importFromFile(String path, FileUtils.ImportFileType type)
	{
		File file = new File(path);

        /* Check the file */
		if (!file.exists())
		{
			DialogUtils.showError(RB.getString(RB.ERROR_IMPORT_FILE_NOT_EXISTS));
			return;
		}
		else if (!file.isFile())
		{
			DialogUtils.showError(RB.getString(RB.ERROR_IMPORT_FILE_IS_FOLDER));
			return;
		}

        /* If there is only one row */
		if (rows.size() == 1)
		{
			BarcodeRow row = rows.get(0);

            /* Remove it if it's empty */
			if (StringUtils.isEmpty(row.getBarcode()))
			{
				row.dispose();
				rows.remove(row);
			}
		}

		IRunnableWithProgress op = null;

		switch (type)
		{
			case TXT:
				op = new TxtFileImportThread(file);
				break;

			case XML:
				op = new XmlFileImportThread(file);
				break;
		}

        /* Start the progress dialog */
		try
		{
			new ProgressMonitorDialog(content.getShell()).run(true, true, op);
		}
		catch (InvocationTargetException | InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	private boolean checkInput()
	{
		Iterator<BarcodeRow> it = rows.iterator();

		while (it.hasNext())
		{
			BarcodeRow row = it.next();
			if (row == null)
				return false;
			else if (row.isDisposed())
				it.remove();
			else if (StringUtils.isEmpty(row.getBarcode()))
				return false;
			else if (row.getBufferedImage() == null)
				return false;
		}

		return true;
	}

	/**
	 * Adds a new empty {@link BarcodeRow} to the {@link Humbug}.
	 *
	 * @param update Should the whole GUI be updated after adding the new item?
	 * @return The newly generated {@link BarcodeRow}
	 */
	public BarcodeRow addRow(boolean update)
	{
		BarcodeRow row = new BarcodeRow(content, SWT.NONE, this);
		rows.add(row);

        /* Make sure to scroll to the activated row */
		row.addListener(SWT.Activate, event ->
		{
			Control widget = (Control) event.widget;

			Rectangle bounds = widget.getBounds();
			Rectangle area = scrolledContainer.getClientArea();
			Point origin = scrolledContainer.getOrigin();
			if (origin.x > bounds.x)
				origin.x = Math.max(0, bounds.x);
			if (origin.y > bounds.y)
				origin.y = Math.max(0, bounds.y);
			if (origin.x + area.width < bounds.x + bounds.width)
				origin.x = Math.max(0, bounds.x + bounds.width - area.width);
			if (origin.y + area.height < bounds.y + bounds.height)
				origin.y = Math.max(0, bounds.y + bounds.height - area.height);
			scrolledContainer.setOrigin(origin);
		});

		if (update)
		{
			update();

			Event event = new Event();
			event.widget = row;

			row.notifyListeners(SWT.Activate, event);
		}

		return row;
	}

	public void move(BarcodeRow row, int direction) throws IllegalArgumentException
	{
		if (!(direction == -1 || direction == 1))
			throw new IllegalArgumentException("Illegal direction: '" + direction + "'. Only +1 and -1 supported.");

		int index = rows.indexOf(row);

		if (Math.signum(direction) < 0 && index > 0 || Math.signum(direction) > 0 && index < rows.size() - 1)
		{
			BarcodeRow target = rows.get(index + direction);
			rows.remove(index);
			rows.add(index + direction, row);

			if (Math.signum(direction) < 0)
				row.moveAbove(target);
			else
				row.moveBelow(target);
		}

		content.layout(true);
	}

	/**
	 * Updates the content of the {@link Humbug}
	 */
	public void update()
	{
		shell.layout(true, true);

		BarcodeRow.RELAYOUT = false;
		for (int i = 0; i < rows.size(); i++)
		{
			if (i == rows.size() - 1)
				BarcodeRow.RELAYOUT = true;

			rows.get(i).forceRedraw();
		}

		scrolledContainer.setMinHeight(content.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
	}

	/**
	 * Deletes the given {@link BarcodeRow} from the {@link Humbug}
	 *
	 * @param row The {@link BarcodeRow} to delete
	 */
	public void onDelete(BarcodeRow row)
	{
		rows.remove(row);
		shell.layout(true, true);
		scrolledContainer.setMinHeight(content.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
	}

	private void checkForUpdate(boolean startupCall)
	{
		if (WITHIN_JAR)
		{
			/* Check if an update is available */
			Install4jUtils i4j = new Install4jUtils(APP_ID, UPDATE_ID);

			Install4jUtils.UpdateInterval interval = startupCall ? (Install4jUtils.UpdateInterval) HumbugParameterStore.INSTANCE.get(HumbugParameter.updateInterval) : Install4jUtils.UpdateInterval.STARTUP;

			i4j.setDefaultVersionNumber(VERSION_NUMBER);
			i4j.setUser(interval, HumbugParameterStore.INSTANCE.getAsString(HumbugParameter.userId), 0);
			i4j.setURLs(UPDATER_URL, TRACKER_URL);
			if (!startupCall)
			{
				i4j.setCallback(updateAvailable ->
				{
					if (!updateAvailable)
						DialogUtils.showInformation(RB.getString(RB.INFORMATION_NO_UPDATE_AVAILABLE));
				});
			}

			i4j.doStartUpCheck(Humbug.class);
		}
	}

	public void onEnterPressed(BarcodeRow barcodeRow)
	{
		if (rows.indexOf(barcodeRow) == rows.size() - 1)
			addRow(true);
	}

	@Override
	protected void onExit()
	{
	}

	@Override
	protected void initResources()
	{
		Resources.initialize();
	}

	@Override
	protected void disposeResources()
	{
		Resources.disposeResources();
	}
}
