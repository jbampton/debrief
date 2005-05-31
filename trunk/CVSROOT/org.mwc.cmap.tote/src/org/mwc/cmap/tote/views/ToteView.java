package org.mwc.cmap.tote.views;

import java.beans.*;
import java.util.*;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.ViewPart;
import org.mwc.cmap.core.DataTypes.Temporal.*;
import org.mwc.cmap.core.DataTypes.TrackData.*;
import org.mwc.cmap.core.ui_support.PartMonitor;
import org.mwc.cmap.tote.calculations.CalculationLoaderManager;

import Debrief.Tools.Tote.*;
import MWC.GenericData.HiResDate;

/**
 * View which provides a track tote. The track tote is a table of values who are
 * calculated using the current status of one or more vessel tracks
 * <p>
 */

public class ToteView extends ViewPart
{

	private Action _followTimeToggle;

	// Extension point tag and attributes in plugin.xml
	private static final String EXTENSION_POINT_ID = "ToteCalculation";

	private static final String EXTENSION_TAG = "calculation";

	private static final String EXTENSION_TAG_LABEL_ATTRIB = "name";

	private static final String EXTENSION_TAG_ICON_ATTRIB = "icon";

	private static final String EXTENSION_TAG_CLASS_ATTRIB = "class";

	// Plug-in ID from <plugin> tag in plugin.xml
	private static final String PLUGIN_ID = "org.mwc.cmap.tote";
	/**
	 * the table content provider (containing both the calculations and the
	 * tracks)
	 */
	private IStructuredContentProvider _content;

	/**
	 * helper application to help track creation/activation of new plots
	 */
	private PartMonitor _myPartMonitor = null;

	/**
	 * the listener we use to track time changes
	 */
	private PropertyChangeListener _temporalListener = null;

	/**
	 * where we get our track data from
	 */
	TrackDataProvider _trackData = null;

	/**
	 * where we get/store what the current set of calcs are
	 */
	ToteCalculationProvider _toteCalcs = null;

	/**
	 * our current set of calculations
	 */
	Vector _myCalculations = null;

	/**
	 * the temporal dataset controlling the narrative entry currently displayed
	 */
	private TimeProvider _myTemporalDataset;

	/**
	 * the "write" interface for the plot which tracks the narrative, where
	 * avaialable
	 */
	private ControllableTime _controllableTime;

	/**
	 * the editor currently providing our narrative
	 */
	protected IEditorPart _currentEditor;

	/**
	 * helper object which loads plugin file-loaders
	 */
	private CalculationLoaderManager _loader;

	private Label _tempStatus;

	private ToteLabelProvider _labelProvider;

	private SWTTote _myTote;

	/**
	 * The constructor.
	 */
	public ToteView()
	{
		_myCalculations = new Vector(0, 1);
	}

	/**
	 * This is a callback that will allow us to create the _tableViewer and
	 * initialize it.
	 */
	public void createPartControl(Composite parent)
	{
		// _tempStatus = new Label(parent, SWT.NONE);
		// _tempStatus.setText("pending");
		//		
		_myTote = new SWTTote();
		_myTote.createControl(parent);

		// _tableViewer = new TableViewer(createTableWithColumns(parent));
		// _content = new ToteContentProvider();
		// _tableViewer.setContentProvider(_content);
		// _labelProvider = new ToteLabelProvider();
		// _tableViewer.setLabelProvider(_labelProvider);
		// _tableViewer.setInput(this);
		// _tableViewer.setSorter(new NameSorter());

		// Create Action instances
		createViewActions();

		makeActions();
		hookContextMenu();
		// hookDoubleClickAction();
		contributeToActionBars();

		// try to add ourselves to listen out for page changes
		// getSite().getWorkbenchWindow().getPartService().addPartListener(this);

		_myPartMonitor = new PartMonitor(getSite().getWorkbenchWindow()
				.getPartService());
		_myPartMonitor.addPartListener(TrackDataProvider.class,
				PartMonitor.ACTIVATED, new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						TrackDataProvider provider = (TrackDataProvider) part;

						// is this different to our current one?
						if (provider != _trackData)
							storeDetails(provider, parentPart);
					}
				});

		// unusually, we are also going to track the open event for narrative data
		// so that we can start off with some data
		_myPartMonitor.addPartListener(TrackDataProvider.class, PartMonitor.OPENED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						TrackDataProvider provider = (TrackDataProvider) part;

						// is this different to our current one?
						if (provider != _trackData)
							storeDetails(provider, parentPart);
					}

				});

		_myPartMonitor.addPartListener(TrackDataProvider.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						// implementation here.
						TrackDataProvider provider = (TrackDataProvider) part;

						// is this our current provider?
						if (_trackData == provider)
							_trackData = null;
					}
				});
		_myPartMonitor.addPartListener(TimeProvider.class, PartMonitor.ACTIVATED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						// just check we're not already looking at it
						if (part != _myTemporalDataset)
						{
							// implementation here.
							_myTemporalDataset = (TimeProvider) part;
							if (_temporalListener == null)
							{
								_temporalListener = new PropertyChangeListener()
								{
									public void propertyChange(PropertyChangeEvent event)
									{
										// ok, use the new time
										HiResDate newDTG = (HiResDate) event.getNewValue();
										timeUpdated(newDTG);
									}
								};
							}
							_myTemporalDataset.addListener(_temporalListener,
									TimeProvider.TIME_CHANGED_PROPERTY_NAME);
						}
					}
				});
		_myPartMonitor.addPartListener(TimeProvider.class, PartMonitor.OPENED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						// implementation here.
						_myTemporalDataset = (TimeProvider) part;
						if (_temporalListener == null)
						{
							_temporalListener = new PropertyChangeListener()
							{
								public void propertyChange(PropertyChangeEvent event)
								{
									// ok, use the new time
									HiResDate newDTG = (HiResDate) event.getNewValue();
									timeUpdated(newDTG);
								}
							};
						}
						_myTemporalDataset.addListener(_temporalListener,
								TimeProvider.TIME_CHANGED_PROPERTY_NAME);
					}
				});
		_myPartMonitor.addPartListener(TimeProvider.class, PartMonitor.CLOSED,
				new PartMonitor.ICallback()
				{
					public void eventTriggered(String type, Object part,
							IWorkbenchPart parentPart)
					{
						_myTemporalDataset.removeListener(_temporalListener,
								TimeProvider.TIME_CHANGED_PROPERTY_NAME);
					}
				});

		// ok we're all ready now. just try and see if the current part is valid
		_myPartMonitor.fireActivePart(getSite().getWorkbenchWindow()
				.getActivePage());

	}


	/**
	 * @param parent
	 *          what we have to fit into
	 */
	private static Table createTableWithColumns(Composite parent)
	{
		Table table = new Table(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI
				| SWT.FULL_SELECTION);

		TableLayout layout = new TableLayout();
		table.setLayout(layout);

		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		String[] STD_HEADINGS = { "Calculation", "Primary", "Sec 1", "Sec 2" };

		layout.addColumnData(new ColumnWeightData(5, 40, true));
		TableColumn tc0 = new TableColumn(table, SWT.NONE);
		tc0.setText(STD_HEADINGS[0]);
		tc0.setAlignment(SWT.LEFT);
		tc0.setResizable(true);
		//
		// layout.addColumnData(new ColumnWeightData(10, true));
		// TableColumn tc1 = new TableColumn(table, SWT.NONE);
		// tc1.setText(STD_HEADINGS[1]);
		// tc1.setAlignment(SWT.LEFT);
		// tc1.setResizable(true);
		//
		// layout.addColumnData(new ColumnWeightData(10, true));
		// TableColumn tc2 = new TableColumn(table, SWT.NONE);
		// tc2.setText(STD_HEADINGS[2]);
		// tc2.setAlignment(SWT.LEFT);
		// tc2.setResizable(true);
		//
		// layout.addColumnData(new ColumnWeightData(10, true));
		// TableColumn tc3 = new TableColumn(table, SWT.NONE);
		// tc3.setText(STD_HEADINGS[3]);
		// tc3.setAlignment(SWT.LEFT);
		// tc3.setResizable(true);
		return table;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite,
	 *      org.eclipse.ui.IMemento)
	 */
	public void init(IViewSite site, IMemento memento) throws PartInitException
	{
		// let the parent do its bits
		super.init(site, memento);

		// ok - declare and load the supplemental plugins which can load datafiles
		initialiseCalcLoaders();

		toteCalculation[] calcs = _loader.findCalculations();
		for (int i = 0; i < calcs.length; i++)
		{
			toteCalculation thisCalc = calcs[i];
			_myCalculations.add(thisCalc);
		}
	}

	private void createViewActions()
	{

		// // -------------------------------------------------------
		// // Toggle filter action
		// filterToggleAction = new Action("Only show Type_1", Action.AS_CHECK_BOX)
		// {
		//
		// public void run()
		// {
		// // Use default political type for simplicity
		// if (isChecked())
		// {
		// if (filter == null)
		// filter = new Type1_Filter();
		// _tableViewer.addFilter(filter);
		// }
		// else
		// _tableViewer.removeFilter(filter);
		// }
		// };
		// filterToggleAction.setToolTipText("Hide anything other than type_1");
		// filterToggleAction.setImageDescriptor(PlatformUI.getWorkbench()
		// .getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_REDO));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	public void dispose()
	{
		super.dispose();

		if (_myPartMonitor != null)
		{
			// and stop listening for part activity
			_myPartMonitor.dispose(getSite().getWorkbenchWindow().getPartService());
		}
		// also stop listening for time events
		if (_controllableTime != null)
		{
			_myTemporalDataset.removeListener(_temporalListener,
					TimeProvider.TIME_CHANGED_PROPERTY_NAME);
		}
	}

	private void hookContextMenu()
	{
		// MenuManager menuMgr = new MenuManager("#PopupMenu");
		// menuMgr.setRemoveAllWhenShown(true);
		// menuMgr.addMenuListener(new IMenuListener()
		// {
		// public void menuAboutToShow(IMenuManager manager)
		// {
		// ToteView.this.fillContextMenu(manager);
		// }
		// });
		// Menu menu = menuMgr.createContextMenu(_tableViewer.getControl());
		// _tableViewer.getControl().setMenu(menu);
		// getSite().registerContextMenu(menuMgr, _tableViewer);
	}

	private void contributeToActionBars()
	{
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager)
	{
		manager.add(new Separator());

	}

	private void fillContextMenu(IMenuManager manager)
	{
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

		manager.add(_followTimeToggle);
	}

	private void fillLocalToolBar(IToolBarManager manager)
	{
		// manager.add(action1);
		// manager.add(action2);
		manager.add(_followTimeToggle);
	}

	private void makeActions()
	{
		_followTimeToggle = new Action("Debug", Action.AS_PUSH_BUTTON)
		{
			public void run()
			{
			}

		};
		_followTimeToggle.setText("Debug");
		_followTimeToggle.setChecked(true);
		_followTimeToggle.setToolTipText("Do Ian's debug operation");
		_followTimeToggle.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_UNDO_HOVER));
	}

	/**
	 * Passing the focus request to the _tableViewer's control.
	 */
	public void setFocus()
	{
		// _tableViewer.getControl().setFocus();
	}

	/**
	 * @param part
	 * @param parentPart
	 */
	private void storeDetails(TrackDataProvider part, IWorkbenchPart parentPart)
	{
		// hmm - are we already looking at this one?
		if (part != _trackData)
		{

			// ok, store it
			_trackData = part;

			WatchableList pri = _trackData.getPrimaryTrack();
			if (pri != null)
			{
				_myTote.setPrimary(pri);

				WatchableList[] secs = _trackData.getSecondaryTracks();
				if (secs != null)
				{
					for (int i = 0; i < secs.length; i++)
					{
						WatchableList thisSec = secs[i];
						_myTote.setSecondary(thisSec);
					}
				}
			}

			// cool, and update
			_myTote.updateToteMembers();
			
			_myTote.getPanel().layout(true);
		}
	}

	// //////////////////////////////
	// temporal data management
	// //////////////////////////////

	/**
	 * the data we are looking at has updated. If we're set to follow that time,
	 * update ourselves
	 */
	private void timeUpdated(HiResDate newDTG)
	{
		_myTote.newTime(null, newDTG, null);
		
		_myTote.updateToteInformation();
//		if (!_tableViewer.getTable().isDisposed())
//		{
//			_tableViewer.refresh(true);
//			_labelProvider.setDTG(newDTG);
//		}
//		else
//			System.out.println("not updating. table is disposed");
	}

	// //////////////////////////////
	// selection listener bits
	// //////////////////////////////

	/**
	 * 
	 */
	private void initialiseCalcLoaders()
	{
		// hey - sort out our plot readers
		_loader = new CalculationLoaderManager(EXTENSION_POINT_ID, EXTENSION_TAG,
				PLUGIN_ID)
		{

			public toteCalculation createInstance(
					IConfigurationElement configElement, String label)
			{
				// get the attributes
				label = configElement.getAttribute(EXTENSION_TAG_LABEL_ATTRIB);
				String icon = configElement.getAttribute(EXTENSION_TAG_ICON_ATTRIB);

				// create the instance
				toteCalculation res = null;

				// create the instance
				res = new CalculationLoaderManager.DeferredCalculation(configElement,
						label, icon);

				// and return it.
				return res;
			}

		};
	}

	public class ToteContentProvider implements IStructuredContentProvider
	{

		public ToteContentProvider()
		{
		}

		public Object[] getElements(Object inputElement)
		{
			// System.out.println("Tote TABLE: returning new elements");
			// return new Object[]{"a", "b", "c"};
			return _myCalculations.toArray();
		}

		public void dispose()
		{
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
			System.out.println("input changed!!");

		}
	}

	public class ToteLabelProvider implements ITableLabelProvider
	{
		/**
		 * the DTG we're updating for.
		 */
		private HiResDate _theDTG;

		/**
		 * store the new DTG (ready for our updates)
		 * 
		 * @param theDTG
		 */
		public void setDTG(HiResDate theDTG)
		{
			_theDTG = theDTG;
		}

		public Image getColumnImage(Object element, int columnIndex)
		{
			return null;
		}

		public String getColumnText(Object element, int columnIndex)
		{
			String res = "";

			if (_theDTG != null)
			{

				toteCalculation tc = (toteCalculation) element;

				if (columnIndex == 0)
					res = tc.getTitle();
				else
				{
					if (_trackData != null)
					{
						WatchableList _thePrimary = _trackData.getPrimaryTrack();
						WatchableList[] secLists = _trackData.getSecondaryTracks();

						// check that we've got a primary
						if (_thePrimary != null)
						{

							// so, we the calculations have been added to the tote list
							// in order going across the page

							// get the primary ready,
							Watchable[] list = _thePrimary.getNearestTo(_theDTG);
							Watchable pw = null;
							if (list.length > 0)
								pw = list[0];

							// are we only looking at the primary?
							if (columnIndex == 1)
							{
								res = tc.update(null, pw, _theDTG);
							}
							else
							{
								if (secLists != null)
								{
									if (columnIndex - 2 < secLists.length)
									{
										// prepare the list of secondary watchables
										WatchableList wList = secLists[columnIndex - 2];
										list = wList.getNearestTo(_theDTG);

										Watchable nearest = null;
										if (list.length > 0)
											nearest = list[0];
										res = tc.update(pw, nearest, _theDTG);
									}
								}
							}

						}
					}
				}
			}
			return res;
		}

		public void addListener(ILabelProviderListener listener)
		{
		}

		public void dispose()
		{
		}

		public boolean isLabelProperty(Object element, String property)
		{
			return true;
		}

		public void removeListener(ILabelProviderListener listener)
		{
		}

	}
}
