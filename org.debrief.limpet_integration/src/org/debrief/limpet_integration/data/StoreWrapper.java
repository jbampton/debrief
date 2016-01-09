package org.debrief.limpet_integration.data;

import info.limpet.IStore;
import info.limpet.IStoreGroup;
import info.limpet.IStore.IStoreItem;
import info.limpet.data.store.InMemoryStore;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import Debrief.Wrappers.Measurements.SupplementalDataBlock;
import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GUI.Plottables;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public class StoreWrapper implements SupplementalDataBlock, Layer
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final InMemoryStore _store;

	public StoreWrapper(InMemoryStore store)
	{
		_store = store;
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

	@Override
	public String getName()
	{
		return "Measurements";
	}

	@Override
	public boolean hasEditor()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public EditorType getInfo()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int compareTo(Plottable o)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setWrapper(Object parent)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void exportShape()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void append(Layer other)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setName(String val)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasOrderedChildren()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getLineThickness()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void add(Editable point)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void removeElement(Editable point)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Enumeration<Editable> elements()
	{
		return getElementsFor(_store);
	}

	private Enumeration<Editable> getElementsFor(InMemoryStore store)
	{
		ArrayList<Editable> res = new ArrayList<Editable>();
		Iterator<IStoreItem> iter = store.iterator();
		while (iter.hasNext())
		{
			IStore.IStoreItem storeItem = (IStore.IStoreItem) iter.next();
			final Editable thisE;

			if (storeItem instanceof IStoreGroup)
			{
				IStoreGroup group = (IStoreGroup) storeItem;
				thisE = new GroupWrapper(group);
			}
			else
			{
				thisE = new ItemWrapper(storeItem);
			}

			res.add(thisE);
		}

		return new Plottables.IteratorWrapper(res.iterator());
	}

	protected static class GroupWrapper extends BaseLayer
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private IStoreGroup _group;

		public GroupWrapper(IStoreGroup group)
		{
			_group = group;
		}

		@Override
		public Enumeration<Editable> elements()
		{
			// TODO Auto-generated method stub
			return super.elements();
		}

	}

	protected static class ItemWrapper implements Editable
	{

		private IStoreItem _item;

		public ItemWrapper(IStoreItem storeItem)
		{
			_item = storeItem;
		}

		@Override
		public String getName()
		{
			return _item.getName();
		}

		@Override
		public String toString()
		{
			return getName();
		}

		@Override
		public boolean hasEditor()
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public EditorType getInfo()
		{
			// TODO Auto-generated method stub
			return null;
		}

	}

}