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

package jhi.humbug.gui.widget.listener;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

import java.util.*;

import jhi.humbug.util.*;

/**
 * @author Sebastian Raubach
 */
public class ConwayListener implements Listener
{
	public static final String KEYWORD = "humbug";

	private static final int DURATION = 10000;

	private static final int SPEED = 100;

	private Cell[][] cells;
	private Label    control;
	private Point    controlSize;
	private int      top;
	private int      left;
	private int      width;
	private int      height;
	private int nrOfColumns = 40;
	private int nrOfRows    = 8;

	private boolean isRunning = false;
	private boolean isStopped = false;

	private Runnable runnable;

	private Image image;
	private Color background;

	public ConwayListener(final Label control)
	{
		this.image = control.getImage();
		this.background = control.getBackground();
		control.setImage(null);
		control.setBackground(null);
		controlSize = control.getSize();
		this.control = control;

		this.width = controlSize.x;
		this.height = controlSize.y;

		this.top = (controlSize.y - height) / 2;
		this.left = (controlSize.x - width) / 2;

		Cell.width = Math.min(width / nrOfColumns, height / nrOfRows);
		Cell.height = Math.min(width / nrOfColumns, height / nrOfRows);

		this.width = Cell.width * nrOfColumns;
		this.height = Cell.width * nrOfRows;

		cells = new Cell[nrOfRows][nrOfColumns];

		for (int i = 0; i < nrOfRows; i++)
		{
			for (int j = 0; j < nrOfColumns; j++)
			{
				Cell cell = new Cell();
				cells[i][j] = cell;
			}
		}

		control.addListener(SWT.Resize, event -> {
			control.removeListener(SWT.Resize, this);
			stop();
			control.redraw();
		});

		control.getDisplay().timerExec(DURATION, () -> {
			if (!isStopped)
				stop();
		});

		runnable = new Runnable()
		{
			@Override
			public void run()
			{
				boolean[][] living = new boolean[nrOfRows][nrOfColumns];
				for (int i = 0; i < nrOfRows; i++)
				{
					for (int j = 0; j < nrOfColumns; j++)
					{
						int top = (j > 0 ? j - 1 : nrOfColumns - 1);
						int btm = (j < nrOfColumns - 1 ? j + 1 : 0);
						int lft = (i > 0 ? i - 1 : nrOfRows - 1);
						int rgt = (i < nrOfRows - 1 ? i + 1 : 0);
						int neighbors = 0;
						if (cells[i][top].isLiving()) neighbors++;
						if (cells[i][btm].isLiving()) neighbors++;
						if (cells[lft][top].isLiving()) neighbors++;
						if (cells[lft][btm].isLiving()) neighbors++;
						if (cells[lft][j].isLiving()) neighbors++;
						if (cells[rgt][j].isLiving()) neighbors++;
						if (cells[rgt][top].isLiving()) neighbors++;
						if (cells[rgt][btm].isLiving()) neighbors++;
						living[i][j] = cells[i][j].isAlive(neighbors);
					}
				}
				for (int i = 0; i < nrOfRows; i++)
				{
					for (int j = 0; j < nrOfColumns; j++)
					{
						cells[i][j].setAlive(living[i][j]);
					}
				}

				if (!isStopped && control != null && !control.isDisposed())
				{
					control.redraw();

					Display.getDefault().timerExec(SPEED, this);
				}
			}
		};
	}

	public void stop()
	{
		isStopped = true;
		if (control != null && !control.isDisposed())
		{
			if (image != null && !image.isDisposed())
				control.setImage(image);
			if (background != null && !background.isDisposed())
				control.setBackground(background);
			control.removeListener(SWT.Paint, ConwayListener.this);
			control.redraw();
		}
	}

	@Override
	public void handleEvent(Event event)
	{
		if (isStopped)
		{
			return;
		}

		if (!isRunning)
		{
			isRunning = true;
			Display.getDefault().timerExec(SPEED, runnable);
		}

		Widget widget = event.widget;

		if (widget instanceof Control && widget == control)
		{
			GC gc = event.gc;

			controlSize = control.getSize();
			this.top = (controlSize.y - height) / 2;
			this.left = (controlSize.x - width) / 2;

			gc.setBackground(Resources.Colors.WHITE);
			gc.fillRectangle(0, 0, controlSize.x, controlSize.y);

			gc.setBackground(Resources.Colors.DARK_GREY);
			gc.setForeground(Resources.Colors.DARK_GREY);

			int x = left;
			int y = top;
			for (int i = 0; i < nrOfRows; i++)
			{
				for (int j = 0; j < nrOfColumns; j++)
				{
					if (cells[i][j].isLiving())
						gc.fillRectangle(x, y, Cell.width - 1, Cell.height - 1);
					else
						gc.drawRectangle(x, y, Cell.width - 2, Cell.height - 2);
					x += Cell.width;
				}

				x = left;
				y += Cell.height;
			}
		}
	}

	private static class Cell
	{
		public static  Random random = new Random();
		private static int    width  = 6;
		private static int    height = 6;
		private boolean isLiving;

		public Cell()
		{
			isLiving = random.nextBoolean();
		}

		public boolean isAlive(int neighbors)
		{
			boolean alive = false;
			if (this.isLiving)
			{
				if (neighbors < 2)
				{
					alive = false;
				}
				else if (neighbors == 2 || neighbors == 3)
				{
					alive = true;
				}
				else if (neighbors > 3)
				{
					alive = false;
				}
			}
			else
			{
				if (neighbors == 3)
				{
					alive = true;
				}
			}
			return alive;
		}

		public void setAlive(boolean alive)
		{
			isLiving = alive;
		}

		public boolean isLiving()
		{
			return this.isLiving;
		}
	}
}
