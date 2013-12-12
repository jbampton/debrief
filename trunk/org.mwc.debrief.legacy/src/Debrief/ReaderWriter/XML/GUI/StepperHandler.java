package Debrief.ReaderWriter.XML.GUI;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import java.text.ParseException;

import Debrief.GUI.Tote.AnalysisTote;
import Debrief.GUI.Tote.Painters.SnailPainter;
import Debrief.ReaderWriter.XML.GUIHandler;
import MWC.GenericData.HiResDate;
import MWC.Utilities.ReaderWriter.XML.*;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;


public final class StepperHandler implements GUIHandler.ComponentCreator
{

  public final void makeThis(final Debrief.ReaderWriter.XML.GUIHandler.ComponentDetails details,
                             final Debrief.GUI.Views.AnalysisView _analysisView)
  {
    String val = null;

    if (_analysisView == null)
    {
      System.err.println("Analysis view missing from Stepper Handler");
      return;
    }

    // right. just do some quick checking to ensure we have the right data
    final AnalysisTote theTote = _analysisView.getTote();

    //////////////////////////////////////////////////////////////
    final String cursor = (String) details.properties.get("Cursor");
    if (cursor != null)
    {
      // set the cursor
    	theTote.getStepper().setPainter(cursor);

      //////////////////////////////////////////////////////////////
      // is this the snail cursor?
      if (cursor.equals(SnailPainter.SNAIL_NAME))
      {
        final String vector_stretch = (String) details.properties.get("VectorStretch");
        if (vector_stretch != null)
        {
          // set the cursor
          final SnailPainter sp = (SnailPainter) theTote.getStepper().getCurrentPainter();
          try {
        	  sp.setVectorStretch( MWCXMLReader.readThisDouble(vector_stretch));
          } catch (final ParseException pe) {
        	  MWC.Utilities.Errors.Trace.trace(pe,
						"Reader: Whilst reading in VectorStretch value of :"
								+ vector_stretch);
          }
        }
      }

    }

    //////////////////////////////////////////////////////////////
    final String dateFormat = (String) details.properties.get("DateFormat");
    if (dateFormat != null)
    {
      // set the cursor
      _analysisView.getTote().getStepper().setDateFormat(dateFormat);
    }

    //////////////////////////////////////////////////////////////
    final String highlighter = (String) details.properties.get("Highlighter");
    if (highlighter != null)
    {
      // set the cursor
      _analysisView.getTote().getStepper().setHighlighter(highlighter);
    }

    //////////////////////////////////////////////////////////////
    final String start_time = (String) details.properties.get("Toolbox_Start_Time");
    if (start_time != null)
    {
      HiResDate startTime = null;
      // get a date from this
      startTime = DebriefFormatDateTime.parseThis(start_time);

      // set the cursor
      _analysisView.getTote().getStepper().setToolboxStartTime(startTime);
    }

    //////////////////////////////////////////////////////////////
    final String end_time = (String) details.properties.get("Toolbox_End_Time");
    if (end_time != null)
    {
      HiResDate endTime = null;

      // get a date from this
      endTime = DebriefFormatDateTime.parseThis(end_time);

      // set the cursor
      _analysisView.getTote().getStepper().setToolboxEndTime(endTime);
    }

    //////////////////////////////////////////////////////////////
    final String tZero = (String) details.properties.get("TimeZero");
    if (tZero != null)
    {
      // get a date from this
      final HiResDate dt = DebriefFormatDateTime.parseThis(tZero);

      // set the cursor
      _analysisView.getTote().getStepper().setTimeZero(dt);
    }
    //////////////////////////////////////////////////////////////
    final String currentTime = (String) details.properties.get("CurrentTime");
    if (currentTime != null)
    {
      // and set the time
      final HiResDate dtg = DebriefFormatDateTime.parseThis(currentTime);

      // did we find a valid dtg?
      if (dtg != null)
        _analysisView.getTote().getStepper().changeTime(dtg);
    }
    //////////////////////////////////////////////////////////////
    val = (String) details.properties.get("AutoStep");
    if (val != null)
    {
      // set the auto step to this number of millis
      final int len = Integer.valueOf(val).intValue();
      _analysisView.getTote().getStepper().setAutoStep(len);
    }


    ///////////////////////////////////////////////////////////////
    val = (String) details.properties.get("StepLarge");
    if (val != null)
    {
      // set the large step to this number of millis
      try
      {
        final double len = MWCXMLReaderWriter.readThisDouble(val);
        _analysisView.getTote().getStepper().setStepLarge((long) len * 1000);
      }
      catch (final java.text.ParseException pe)
      {
        MWC.Utilities.Errors.Trace.trace(pe, "Failed reading large step size value is:" + val);
      }

    }

    ///////////////////////////////////////////////////////////////
    val = (String) details.properties.get("StepSmall");
    if (val != null)
    {
      try
      {
        // set the small step to this number of millis
        final double len = MWCXMLReaderWriter.readThisDouble(val);
        _analysisView.getTote().getStepper().setStepSmall((long) len * 1000);
      }
      catch (final java.text.ParseException pe)
      {
        MWC.Utilities.Errors.Trace.trace(pe, "Failed reading small step size value is:" + val);
      }
    }

    ///////////////////////////////////////////////////////////////
    val = (String) details.properties.get("StepLargeMicros");
    if (val != null)
    {
      // set the large step to this number of millis
      try
      {
        final double len = MWCXMLReaderWriter.readThisDouble(val);
        _analysisView.getTote().getStepper().setStepLarge((long) len);
      }
      catch (final java.text.ParseException pe)
      {
        MWC.Utilities.Errors.Trace.trace(pe, "Failed reading large step size value is:" + val);
      }

    }

    ///////////////////////////////////////////////////////////////
    val = (String) details.properties.get("StepSmallMicros");
    if (val != null)
    {
      try
      {
        // set the small step to this number of millis
        final double len = MWCXMLReaderWriter.readThisDouble(val);
        _analysisView.getTote().getStepper().setStepSmall((long) len);
      }
      catch (final java.text.ParseException pe)
      {
        MWC.Utilities.Errors.Trace.trace(pe, "Failed reading small step size value is:" + val);
      }
    }


    // just do some minor tidying here, to check we have start & end times for the slider
    if((_analysisView.getTote().getStepper().getStartTime() == null) ||
      (_analysisView.getTote().getStepper().getEndTime() == null))
    {
      _analysisView.getTote().getStepper().recalcTimes();
    }

  }


  public final GUIHandler.ComponentDetails exportThis(final Debrief.GUI.Frames.Session session)
  {
    // get the stepper
    final Debrief.GUI.Views.PlainView pv = session.getCurrentView();
    if (pv instanceof Debrief.GUI.Views.AnalysisView)
    {
      final Debrief.GUI.Tote.StepControl stepper = ((Debrief.GUI.Views.AnalysisView) pv).getTote().getStepper();

      // collate the details for this component
      final GUIHandler.ComponentDetails details = new GUIHandler.ComponentDetails();

      final MWC.GUI.StepperListener theStepper = stepper.getCurrentPainter();

      // is this the snail painter?
      if (theStepper instanceof SnailPainter)
      {
        final SnailPainter sp = (SnailPainter) theStepper;
        details.addProperty("VectorStretch", MWCXMLReader.writeThis(sp.getVectorStretch()));
      }

      details.addProperty("Cursor", stepper.getCurrentPainter().toString());

      //      details.addProperty("StepLarge", MWCXMLReader.writeThis(stepper.getStepLarge()));
      //      details.addProperty("StepSmall", MWCXMLReader.writeThis(stepper.getStepSmall()));

      // ok, we're switching to exporting the step size in microseconds
      // if we ever get the plain "StepLarge" parameter - we will assume it is millis, else
      // we will always receive the units

      details.addProperty("StepLargeMicros", MWCXMLReader.writeThis(stepper.getStepLarge()));
      details.addProperty("StepSmallMicros", MWCXMLReader.writeThis(stepper.getStepSmall()));


      details.addProperty("AutoStep", MWCXMLReader.writeThis(stepper.getAutoStep()));
      details.addProperty("DateFormat", stepper.getDateFormat());
      // the current DTG
      final HiResDate cTime = stepper.getCurrentTime();
      if (cTime != null)
        details.addProperty("CurrentTime", MWCXMLReader.writeThis(cTime));
      // the T-zero, if set
      if (stepper.getTimeZero() != null)
        details.addProperty("TimeZero", MWCXMLReader.writeThis(stepper.getTimeZero()));
      details.addProperty("Highlighter", stepper.getCurrentHighlighter().getName());
      // what's the time?
      final HiResDate theStartTime = stepper.getToolboxStartTime();
      if (theStartTime != null)
        details.addProperty("Toolbox_Start_Time", DebriefFormatDateTime.toStringHiRes(theStartTime));
      final HiResDate theEndTime = stepper.getToolboxEndTime();
      if (theEndTime != null)
        details.addProperty("Toolbox_End_Time", DebriefFormatDateTime.toStringHiRes(theEndTime));

      return details;
    }
    else
      return null;
  }

}