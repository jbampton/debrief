package org.mwc.asset.sensormonitor.views;

import java.beans.*;

import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.ViewPart;
import org.mwc.asset.core.ASSETPlugin;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.property_support.EditableWrapper;
import org.mwc.cmap.core.ui_support.PartMonitor;

import ASSET.Models.SensorType;
import ASSET.Models.Sensor.Initial.InitialSensor.InitialSensorComponentsEvent;
import ASSET.Models.Sensor.Lookup.LookupSensor;
import ASSET.Models.Sensor.Lookup.LookupSensor.LookupSensorComponentsEvent;
import MWC.GUI.Editable;
import MWC.GenericData.WorldDistance;
import MWC.Utilities.TextFormatting.GeneralFormat;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class SensorMonitor extends ViewPart
{
	private Table _table;

	/**
	 * who we're listening to.
	 */
	private PartMonitor _myPartMonitor;

	private Action _trackParticipant;

	private ISelectionChangedListener _selectionChangeListener;

	private SensorType _mySensor;

	private PropertyChangeListener _sensorCalcListener;

	private long _lastTime = -1;

	private Action _newWindow;

	/*
	 * The content provider class is responsible for providing objects to the
	 * view. It can wrap existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view, or
	 * ignore it and always show the same content (like Task List, for example).
	 */

	class ViewContentProvider implements IStructuredContentProvider
	{
		public void inputChanged(Viewer v, Object oldInput, Object newInput)
		{
		}

		public void dispose()
		{
		}

		public Object[] getElements(Object parent)
		{
			return new String[] { "One", "Two", "Three" };
		}
	}

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider
	{
		public String getColumnText(Object obj, int index)
		{
			return getText(obj);
		}

		public Image getColumnImage(Object obj, int index)
		{
			return getImage(obj);
		}

		public Image getImage(Object obj)
		{
			return PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	/**
	 * The constructor.
	 */
	public SensorMonitor()
	{
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{
		// Composite holder = new Composite(parent,SWT.NONE);
		// holder.setLayout(new FillLayout());

		_table = new Table(parent, SWT.NONE);
		_table.setHeaderVisible(true);

		_table.addDisposeListener(new DisposeListener(){
			public void widgetDisposed(DisposeEvent e)
			{
				widgetClosing();
			}});

		makeActions();
		hookContextMenu();
		contributeToActionBars();

		listenToMyParts();
	}

	protected void widgetClosing()
	{
		// we're closing - stop listening
		if(_mySensor != null)
		{
			_mySensor.removeSensorCalculationListener(_sensorCalcListener);
			_mySensor = null;
			_sensorCalcListener = null;			
		}
		
		_myPartMonitor.ditch();
	}

	protected void testCall()
	{
		TableItem t1 = new TableItem(_table, SWT.NONE);
		t1.setText(new String[] { "a", "b", "c" });
	}

	private void listenToMyParts()
	{
		_selectionChangeListener = new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				newItemSelected(event);
			}
		};

		_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow().getPartService());
		_myPartMonitor.addPartListener(ISelectionProvider.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						ISelectionProvider iS = (ISelectionProvider) part;
						iS.addSelectionChangedListener(_selectionChangeListener);
					}
				});
		_myPartMonitor.addPartListener(ISelectionProvider.class, PartMonitor.DEACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part, IWorkbenchPart parentPart)
					{
						ISelectionProvider iS = (ISelectionProvider) part;
						iS.removeSelectionChangedListener(_selectionChangeListener);
					}
				});

		// ok we're all ready now. just try and see if the current part is valid
		_myPartMonitor.fireActivePart(getSite().getWorkbenchWindow().getActivePage());
	}

	private void hookContextMenu()
	{

	}

	private void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager)
	{

	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		manager.add(_trackParticipant);
		manager.add(_newWindow);
	}

	private void makeActions()
	{
		_trackParticipant = new Action("Track", SWT.TOGGLE)
		{
		};
		_trackParticipant.setText("Sync");
		_trackParticipant.setChecked(true);
		_trackParticipant.setToolTipText("Follow selected participant");
		_trackParticipant.setImageDescriptor(CorePlugin
				.getImageDescriptor("icons/follow_selection.gif"));

		_newWindow = new Action("New monitor", SWT.NONE)
		{
			public void run()
			{
				super.run();

				// ok, open a new view
				CorePlugin.openView(ASSETPlugin.SENSOR_MONITOR, "" + System.currentTimeMillis(),
						IWorkbenchPage.VIEW_VISIBLE);
			}

		};
		_newWindow.setText("New monitor");
		_newWindow.setToolTipText("Open a new sensor monitor");
		_newWindow.setImageDescriptor(CorePlugin.getImageDescriptor("icons/window_new.png"));

	}

	protected void newItemSelected(SelectionChangedEvent event)
	{

		if (_trackParticipant.isChecked())
		{
			// right, let's have a look at it.
			ISelection theSelection = event.getSelection();

			// get the first element
			if (theSelection instanceof StructuredSelection)
			{
				StructuredSelection sel = (StructuredSelection) theSelection;
				Object first = sel.getFirstElement();
				// hmm, is it adaptable?
				if (first instanceof EditableWrapper)
				{
					EditableWrapper ew = (EditableWrapper) first;
					Editable ed = ew.getEditable();
					if (ed instanceof SensorType)
					{
						updateSensor((SensorType) ed);
					}
				}
			}
		}
	}

	public void updateSensor(final SensorType sensor)
	{
		// is this different to our current one?
		if (sensor != _mySensor)
		{
			if (_mySensor != null)
			{
				_mySensor.removeSensorCalculationListener(_sensorCalcListener);
			}
		}

		if (_sensorCalcListener == null)
		{
			_sensorCalcListener = new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent evt)
				{
					processNewDetection(evt);
				}
			};
		}

		_mySensor = sensor;
		_mySensor.addSensorCalculationListener(_sensorCalcListener);

		// and update our title
		this.setPartName(sensor.getName());

		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				if (!_table.isDisposed())
				{
					// ok, now sort out our table
					if (sensor instanceof LookupSensor)
					{
						// ok - do our sensor headings.
						generateCol(_table, "Name", 130);
						generateCol(_table, "State", 60);
						generateCol(_table, "RP (m)", 60);
						generateCol(_table, "RI (m)", 60);
						generateCol(_table, "Actual (m)", 60);
					}
					else
					{
						generateCol(_table, "Name", 130);
						generateCol(_table, "Loss", 60);
						generateCol(_table, "Bk Noise", 60);
						generateCol(_table, "OS Noise", 60);
						generateCol(_table, "Tgt Noise", 60);
						generateCol(_table, "RD", 60);
						generateCol(_table, "DI", 60);
						generateCol(_table, "SE", 60);
					}
				}
			}
		});
	}

	private void generateCol(Table table, String name, int wid)
	{
		TableColumn col = new TableColumn(table, SWT.LEFT);
		col.setText(name);
		col.setWidth(wid);
	}

	/**
	 * ok, extract the relevant bits
	 * 
	 * @param evt
	 *          the event that triggered us.
	 */
	protected void processNewDetection(PropertyChangeEvent evt)
	{
		String[] fields = null;
		final long newTime;

		if (evt.getNewValue() instanceof LookupSensorComponentsEvent)
		{
			// sort out the lookup fields
			LookupSensorComponentsEvent ev = (LookupSensorComponentsEvent) evt.getNewValue();
			fields = new String[] { ev.getTgtName(), ev.getStateString(), f(ev.getRP()),
					f(ev.getRI()), f(ev.getActual()) };

			newTime = ev.getTime();
		}
		else
		{
			if (evt.getNewValue() instanceof InitialSensorComponentsEvent)
			{
				// sort out the component fields
				InitialSensorComponentsEvent ev = (InitialSensorComponentsEvent) evt
						.getNewValue();
				fields = new String[] { ev.getTgtName(), f(ev.getLoss()), f(ev.getBkNoise()),
						f(ev.getOsNoise()), f(ev.getTgtNoise()), f(ev.getRd()), f(ev.getDi()),
						f(ev.getSE()) };
				newTime = ev.getTime();
			}
			else
				newTime = -1;
		}

		if (fields != null)
		{
			final String[] finalFields = fields;
			Display.getDefault().asyncExec(new Runnable()
			{
				public void run()
				{
					if (!_table.isDisposed())
					{
						// is this a new DTG?
						if (newTime > _lastTime)
						{
							// clear the table before we add new items
							_table.removeAll();
							_lastTime = newTime;
						}
						TableItem item1 = new TableItem(_table, SWT.NONE);
						item1.setText(finalFields);
					}
				}
			});
		}
	}

	private String f(WorldDistance val)
	{
		return GeneralFormat.formatOneDecimalPlace(val.getValueIn(WorldDistance.METRES));
	}

	private String f(double val)
	{
		return GeneralFormat.formatOneDecimalPlace(val);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus()
	{
		_table.setFocus();
	}
}