package com.planetmayo.debrief.satc.model.contributions;

import java.util.Date;

import com.planetmayo.debrief.satc.model.ModelObject;
import com.planetmayo.debrief.satc.model.states.ProblemSpace;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;

public abstract class BaseContribution extends ModelObject implements
		Comparable<BaseContribution>
{
	public static final String WEIGHT = "weight";
	public static final String START_DATE = "startDate";
	public static final String NAME = "name";
	public static final String FINISH_DATE = "finishDate";
	public static final String ACTIVE = "active";
	public static final String HARD_CONSTRAINTS = "hardConstraints";
	public static final String ESTIMATE = "estimate";

	protected String _name;
	protected boolean _active;
	protected int _weight;
	protected Date _startDate;
	protected Date _finishDate;
	
	/**
	 * apply this contribution to the supplied Problem Space
	 * 
	 * @param space
	 *          the object that we're going to bound
	 */
	public abstract void actUpon(ProblemSpace space) throws IncompatibleStateException;
	public abstract ContributionDataType getDataType();

	public Date getFinishDate()
	{
		return _finishDate;
	}

	/**
	 * provide a formatted string representing the hard constraints
	 * 
	 * @return summary of constraints
	 */
	public abstract String getHardConstraints();

	public String getName()
	{
		return _name;
	}

	public Date getStartDate()
	{
		return _startDate;
	}

	public int getWeight()
	{
		return _weight;
	}

	public boolean isActive()
	{
		return _active;
	}

	public void setActive(boolean active)
	{		
		boolean oldActive = _active;
		this._active = active;
		firePropertyChange(ACTIVE, oldActive, active);
	}

	public void setFinishDate(Date finishDate)
	{		
		Date oldFinishDate = _finishDate;
		this._finishDate = finishDate;
		firePropertyChange(FINISH_DATE, oldFinishDate, finishDate);
	}

	public void setName(String name)
	{		
		String oldName = _name;
		_name = name;
		firePropertyChange(NAME, oldName, name);
	}

	public void setStartDate(Date startDate)
	{
		Date oldStartDate = _startDate;
		this._startDate = startDate;
		firePropertyChange(START_DATE, oldStartDate, startDate);
	}

	public void setWeight(int weight)
	{
		int oldWeight = _weight;
		this._weight = weight;
		firePropertyChange(WEIGHT, oldWeight, weight);
	}

	@Override
	public int compareTo(BaseContribution o)
	{
		// ok, what type am I?
		int myScore = getScore();
		int hisScore = o.getScore();
		if(myScore == hisScore) {
			// ha-they must be equal, compare the names
			return this.getClass().toString().compareTo(o.getClass().toString());
		}
		return myScore - hisScore;
	}

	private int getScore()
	{
		switch (getDataType()) {
			case MEASUREMENT:
				return 0;
			case FORECAST:
				return 1;
			default:
				return 2;
		}
	}
}
