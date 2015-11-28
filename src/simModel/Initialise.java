package simModel;

import simulationModelling.ScheduledAction;

/**
 * action event...
 *  
 * @author mush
 *
 */
class Initialise extends ScheduledAction
{
	PanoramaTV model;
	
	// Constructor
	protected Initialise(PanoramaTV model) { this.model = model; }

	double [] ts = { 0.0, -1.0 }; // -1.0 ends scheduling
	int tsix = 0;  // set index to first entry.
	protected double timeSequence() 
	{
		return ts[tsix++];  // only invoked at t=0
	}

	protected void actionEvent() 
	{
		// System Initialisation
                // Add initilisation instructions 
	}
	

}
