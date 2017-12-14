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

package jhi.humbug.gui.dialog;

import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

import java.util.*;

import jhi.swtcommons.gui.dialog.*;
import jhi.swtcommons.gui.layout.*;
import jhi.swtcommons.util.*;

/**
 * @author Sebastian Raubach
 */
public abstract class ListDialog extends I18nDialog
{
	private TableViewer list;

	private String                     title;
	private String                     message;
	private IStructuredContentProvider contentProvider;
	private ILabelProvider             labelProvider;
	private Object                     input;
	private Map<Integer, Listener> listeners = new HashMap<>();

	protected ListDialog(Shell shell)
	{
		super(shell);

		setBlockOnOpen(true);
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	}

	@Override
	protected Point getInitialLocation(Point initialSize)
	{
		return ShellUtils.getLocationCenteredTo(getParentShell(), initialSize);
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = (Composite) super.createDialogArea(parent);

		if (!StringUtils.isEmpty(title))
			getShell().setText(title);

		if (!StringUtils.isEmpty(message))
			new Label(composite, SWT.NONE).setText(message);

		list = new TableViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		list.setContentProvider(contentProvider);
		list.setLabelProvider(labelProvider);
		list.setInput(input);
		list.addDoubleClickListener(e -> {
			Object item = ((IStructuredSelection) e.getSelection()).getFirstElement();
			onItemDoubleClicked(item);
		});

		if (listeners.size() > 0)
		{
			for (Map.Entry<Integer, Listener> entry : listeners.entrySet())
			{
				list.getTable().addListener(entry.getKey(), entry.getValue());
			}
		}

		GridLayoutUtils.useDefault()
					   .applyTo(parent);
		GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_BOTH)
					 .heightHint(400)
					 .applyTo(parent);
		GridDataUtils.usePredefined(GridDataUtils.GridDataStyle.FILL_BOTH)
					 .applyTo(list.getControl());

		return composite;
	}

	public void addListener(int event, Listener listener)
	{
		listeners.put(event, listener);
	}

	public void setInput(Object input)
	{
		this.input = input;
	}

	public void setContentProvider(IStructuredContentProvider provider)
	{
		this.contentProvider = provider;
	}

	public void setLabelProvider(ILabelProvider provider)
	{
		this.labelProvider = provider;
	}

	protected abstract void onItemDoubleClicked(Object item);

	public IStructuredSelection getSelection()
	{
		return list.getStructuredSelection();
	}
}
