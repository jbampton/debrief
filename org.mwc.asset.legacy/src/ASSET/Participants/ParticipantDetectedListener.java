/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package ASSET.Participants;

import ASSET.ScenarioType;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 *
 * @author Ian Mayo
 * @version 1.0
 */

public interface ParticipantDetectedListener extends java.util.EventListener
{
  /**
   * pass on the list of new detections
   */
  public void newDetections(ASSET.Models.Detection.DetectionList detections);

  /**
   * the scenario has restarted
   * @param scenario TODO
   */
  public void restart(ScenarioType scenario);


  public class Helper implements ParticipantDetectedListener
  {
    /**
     * the list of helpers we support
     */
    private java.util.Vector<ParticipantDetectedListener> _myListeners;

    /**
     * add the new listener
     */
    public void addListener(final ParticipantDetectedListener listener)
    {
      if (_myListeners == null)
        _myListeners = new java.util.Vector<ParticipantDetectedListener>(1, 1);

      _myListeners.add(listener);
    }

    /**
     * remove this listener
     */
    public void removeListener(final ParticipantDetectedListener listener)
    {
      _myListeners.remove(listener);
    }

    /**
     * handle the new event
     */
    public void newDetections(final ASSET.Models.Detection.DetectionList detections)
    {
      if (_myListeners != null)
      {
        final java.util.Iterator<ParticipantDetectedListener> it = _myListeners.iterator();
        while (it.hasNext())
        {
          final ParticipantDetectedListener list = (ParticipantDetectedListener) it.next();
          list.newDetections(detections);
        }
      }
    }


    /**
     * the scenario has restarted
     */
    public void restart(ScenarioType scenario)
    {

    }


  }

}