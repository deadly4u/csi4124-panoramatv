package simModel;
import com.sun.javafx.tk.Toolkit.PaintAccessor;
import com.sun.xml.internal.ws.dump.LoggingDumpTube.Position;

import simulationModelling.ConditionalActivity;
import simulationModelling.ScheduledActivity;
/*
 * @author: mush
 * @version : 1.0
 * @since : 21 nov
 */
public class ManualProcessing extends ConditionalActivity {
	
	private PanoramaTV model;
	private static int manualNodeId;
	
	@Override
	protected double duration() {
		manualNodeId = model.udp.ManualNodesReadyForProcessing();
		// TODO Auto-generated method stub
		return model.dvp.uManualProcessTime(manualNodeId);
	}

	@Override
	public void startingEvent() {
		manualNodeId = model.udp.ManualNodesReadyForProcessing();
		model.ManualNodes[manualNodeId].setBusy(false);
	}
	/**
	 * Event ManualProcessing
	 * @return RC.ManualNode[ID].busy = FALSE
	 */
	@Override
	protected void terminatingEvent() {
		manualNodeId = model.udp.ManualNodesReadyForProcessing();
		// TODO OP10, OP60 and CS_ID
		int CS_ID = 0;
		int OP10 = -1;
		int OP60 = -1;
		if(manualNodeId == OP10)
		{
			model.ConveyorSeg[CS_ID].last().TuType =	model.dvp.uTvType();
		}else if (manualNodeId == OP60){
			model.ConveyorSeg[CS_ID].last().TuType = null;
		}


		model.ManualNodes[manualNodeId].setBusy(false);
		
		
	}
	private void setManaulNode(PanoramaTV model){
		manualNodeId = model.udp.ManualNodesReadyForProcessing();
	}
	/**
	 * manualNodeId ← UDP.ManualNodesReadyForProcessing()
	 * if(manualNodeId != NULL) return TRUE else return FALSE;
	 */
	public static Boolean PreCondition(PanoramaTV model)
	{
		manualNodeId = model.udp.ManualNodesReadyForProcessing();
		return (manualNodeId != -1);
	}
	public void Duraiton (PanoramaTV model){
		setManaulNode(model);	
	}
	/**
	 * @return
	 */
	public void ManualProcessing(AutoNode ID){
		
	}
	
	

}