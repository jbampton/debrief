package org.mwc.debrief.satc_interface.wizards;

import java.beans.PropertyDescriptor;

import org.eclipse.jface.viewers.ISelection;
import org.mwc.cmap.core.wizards.CoreEditableWizardPage;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Plottable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (xml).
 */

public class LegNameWizardPage extends CoreEditableWizardPage
{

	public static class NameHolder implements Plottable
	{
		private String _name="Pending";
		private EditorType _myEditor;

//		public static class NameInfo extends EditorType
//		{
//
//			// give it some old version id
//			static final long serialVersionUID = 1L;
//
//			public NameInfo(final NameHolder data)
//			{
//				super(data, data.getName(), "");
//			}
//
//			public PropertyDescriptor[] getPropertyDescriptors()
//			{
//				try
//				{
//					final PropertyDescriptor[] res =
//					{ prop("Name", "the name for this leg", FORMAT) };
//
//					return res;
//				}
//				catch (final IntrospectionException e)
//				{
//					return super.getPropertyDescriptors();
//				}
//
//			}
//
//		}

		public String getName()
		{
			return _name;
		}

		public void setName(String name)
		{
			_name = name;
		}

		@Override
		public boolean hasEditor()
		{
			return false;
		}

		@Override
		public EditorType getInfo()
		{
			return null;
//			if (_myEditor == null)
//				_myEditor = new NameInfo(this);
//
//			return _myEditor;
		}

		@Override
		public int compareTo(Plottable arg0)
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void paint(CanvasType dest)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public WorldArea getBounds()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean getVisible()
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void setVisible(boolean val)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public double rangeFrom(WorldLocation other)
		{
			// TODO Auto-generated method stub
			return 0;
		}

	}

	
	
	@Override
	public String getName()
	{
		return _editable.getName();
	}

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public LegNameWizardPage(final ISelection selection)
	{
		super(selection, "namePage", "Set Leg Name",
				"Use this page to specify a name for this leg", "images/scale_wizard.gif", null);
	}

	@Override
	protected Editable createMe()
	{
		if (_editable == null)
			_editable = new NameHolder();

		return _editable;
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{

		final PropertyDescriptor[] res =
		{ prop("Name", "the name for this leg", getEditable()) };

		return res;

	}

}