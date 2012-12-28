package com.planetmayo.debrief.satc.model.contributions;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.planetmayo.debrief.satc.model.GeoPoint;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

@SuppressWarnings("deprecation")
public class LocationForecastContributionTest extends ForecastContributionTestBase
{

	@Override
	protected Map<String, Object> getPropertiesForTest()
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(LocationForecastContribution.ACTIVE, true);
		map.put(LocationForecastContribution.FINISH_DATE, new Date(6));
		map.put(LocationForecastContribution.START_DATE, new Date(2));
		map.put(LocationForecastContribution.WEIGHT, 7);
		map.put(LocationForecastContribution.ESTIMATE, new GeoPoint(1, -1));		
		map.put(LocationForecastContribution.LIMIT, 1d);
		return map;
	}

	@Override
	protected BaseContribution createContribution()
	{
		LocationForecastContribution contribution = new LocationForecastContribution();
		contribution.setActive(false);
		contribution.setStartDate(new Date(112, 11, 27, 1, 55));
		contribution.setFinishDate(new Date(112, 11, 27, 2, 55));
		contribution.setWeight(3);
		contribution.setLimit(2d);
		contribution.setEstimate(new GeoPoint(2, -2));
		return contribution;
	}
	
	@Test
	public void testActUpon() throws Exception 
	{
		LocationForecastContribution contribution = (LocationForecastContribution) createContribution();
		ProblemSpace space = createTestSpace();
		contribution.actUpon(space);
		int withLocation = 0;
		Geometry expected = GeoSupport.getFactory().createPoint(new Coordinate(-2, 2)).buffer(2);
		for (BoundedState state : space.states()) 
		{
			if (state.getLocation() != null)
			{
				assertFalse(state.getLocation().getGeometry().intersection(expected).isEmpty());
				assertTrue(state.getLocation().getGeometry().difference(expected).isEmpty());
				withLocation++;
			}			
		}
		assertEquals(4, withLocation);
	}	
}
