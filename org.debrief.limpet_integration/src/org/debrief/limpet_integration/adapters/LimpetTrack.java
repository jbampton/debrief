package org.debrief.limpet_integration.adapters;

import info.limpet.IStoreItem;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.Iterator;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.PlainWrapper;

public class LimpetTrack extends CoreLimpetTrack
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private TrackWrapper _myTrack;

  public LimpetTrack(TrackWrapper track)
  {
    super(track.getName(), false);

    _myTrack = track;

    // setup listeners
    _myTrack.addPropertyChangeListener(PlainWrapper.LOCATION_CHANGED,
        new PropertyChangeListener()
        {
          @Override
          public void propertyChange(PropertyChangeEvent evt)
          {
            Iterator<IStoreItem> children = children().iterator();
            while (children.hasNext())
            {
              IStoreItem iStoreItem = (IStoreItem) children.next();
              iStoreItem.fireDataChanged();
            }
          }
        });
    
    init(false);

  }

  public TrackWrapper getTrack()
  {
    return _myTrack;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.debrief.limpet_integration.adapters.CoreLimpetTrack#getLocations()
   */
  @Override
  Enumeration<Editable> getLocations()
  {
    return _myTrack.getPositions();
  }

}
