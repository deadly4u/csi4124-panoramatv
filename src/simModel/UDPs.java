package simModel;
/**
 * @author mush
 */

public class UDPs 
{
	PanoramaTV model;  // for accessing the clock
	
	// Constructor
	protected UDPs(PanoramaTV model) { this.model = model; }


	public double GetManualNodeProcessTime(int manualNode){
		if (manualNode == Const.OP10) { return model.rvp.uLoadTv(); }
		else if (manualNode == Const.OP40A || 
				manualNode == Const.OP40B || 
				manualNode == Const.OP40C || 
				manualNode == Const.OP40D || 
				manualNode == Const.OP40E) {  return model.dvp.uElectronicAssemblyTime(); }
		else if (manualNode == Const.REWORK) { return model.rvp.uReworkTime(); }
		else if (manualNode == Const.OP60) { return model.rvp.uUnLoadTv(); }
		
		return -1;
	}
	
	public int GetAssociatedSegmentID(int NodeID, boolean isAutoNode){
		
		if(isAutoNode)
		{
			switch(NodeID){
				case Const.OP20:
					return Const.CS_OP20;
				case Const.OP30:
					return Const.CS_OP30;
				case Const.TEST:
					return Const.CS_TEST;
				case Const.OP50:
					return Const.CS_OP50;
				default:
					return -1;
			}
		}
		else
		{
			switch (NodeID) {
			case Const.OP10:
				return Const.CS_OP10;
			case Const.OP40A:
				return Const.CS_OP40A;
			case Const.OP40B:
				return Const.CS_OP40B;
			case Const.OP40C:
				return Const.CS_OP40C;
			case Const.OP40D:
				return Const.CS_OP40D;
			case Const.OP40E:
				return Const.CS_OP40E;
			case Const.REWORK:
				return Const.CS_REWORK;
			case Const.OP60:
				return Const.CS_OP60;
			default:
				return -1;
			}
		}
	}
	
	public int GetAssociatedNodeID(int segmentId){
		
		switch(segmentId)
		{
		case Const.CS_OP10:
			return Const.OP10;
		case Const.CS_OP20:
			return Const.OP20;
		case Const.CS_OP30:
			return Const.OP30;
		case Const.CS_OP40A:
			return Const.OP40A;
		case Const.CS_OP40B:
			return Const.OP40B;
		case Const.CS_OP40C:
			return Const.OP40C;
		case Const.CS_OP40D:
			return Const.OP40D;
		case Const.CS_OP40E:
			return Const.OP40E;
		case Const.CS_RETEST:
		case Const.CS_TEST:
			return Const.TEST;
		case Const.CS_REWORK:
			return Const.REWORK;
		case Const.CS_OP50:
			return Const.OP50;
		case Const.CS_OP60:
			return Const.OP60;
			default:
				return -1;
		}
	}
	
	public int GetAutoNodeReadyForProcessing(){		
		
		//Iterate from the end to the beginning of the array.
		for(int autoNodeId = model.autoNodes.length - 1; autoNodeId >= 0; autoNodeId--)
		{
			if(!model.autoNodes[autoNodeId].getBusy())
			{
				int segmentID = model.udp.GetAssociatedSegmentID(autoNodeId, true);
				int headOfSegment = model.conveyorSegments[segmentID].getCapacity() - 1;
				
				if(model.conveyorSegments[segmentID].positions[headOfSegment] != null)
				{
					if(model.autoNodes[autoNodeId].getTimeUntilFailure() > model.dvp.uAutomaticProcessTime(autoNodeId)
							&& model.autoNodes[autoNodeId].lastTVType == model.conveyorSegments[segmentID].positions[headOfSegment].tvType
							&& model.conveyorSegments[segmentID].positions[headOfSegment].inMotion == false
							&& model.conveyorSegments[segmentID].positions[headOfSegment].finishedProcessing == false)
					{
						return autoNodeId;
					}
				}
			}
		}
		
		return -1;
	}
	
	public int GetAutoNodeForPartialProcessing(){
		//Iterate from the end to the beginning of the array.
		for(int autoNodeId = model.autoNodes.length - 1; autoNodeId >= 0; autoNodeId--)
		{
			if(!model.autoNodes[autoNodeId].getBusy())
			{
				int segmentID = model.udp.GetAssociatedSegmentID(autoNodeId, true);
				int headOfSegment = model.conveyorSegments[segmentID].getCapacity() - 1;
				
				if(model.conveyorSegments[segmentID].positions[headOfSegment] != null)
				{
					if(model.autoNodes[autoNodeId].getTimeUntilFailure() < model.dvp.uAutomaticProcessTime(autoNodeId)
						&& model.autoNodes[autoNodeId].lastTVType == model.conveyorSegments[segmentID].positions[headOfSegment].tvType
						&& model.conveyorSegments[segmentID].positions[headOfSegment].inMotion == false
							&& model.conveyorSegments[segmentID].positions[headOfSegment].finishedProcessing == false)
					{
						return autoNodeId;
					}
				}
			}
		}
		
		return -1;
	}
	/**
	 * For every RC.AutoNode (nodeID) in the system which is not busy:
	 * segmentID ← GetAssociatedSegmentID(nodeID)
	 * capacity ← RQ.ConveyorSegment[segmentID].capacity
	 * palletID ← RQ.ConveyorSegment[segmentID].positions[capacity - 1]
	 * If (node.timeUntilFailure > RVP.uAutomaticProcessTime()
	 * 
	 * AND RQ.ConveyorSegment[SegmentID].positions[capacity - 1].lastTVType 
	 * 
	 * NOT EQUAL TO RQ.ConveyorSegment[SegmentID].positions[palletID].tvType
	 * 
	 * AND RQ.ConveyorSegment[SegmentID].positions[palletID].inMotion = FALSE
	 * 
	 * AND R.Maintenance.busy = FALSE) 
	 * 
	 * Then @return the nodeID
	 * 
	 * Else @return -1	 
	 * 
	 */
	public int GetAutoNodeRequiringRetooling(){
		for (int autoNodeId = model.autoNodes.length - 1; autoNodeId >= 0 ; autoNodeId--){
			int segmentID = GetAssociatedSegmentID(autoNodeId, true);
			int headOfSegment = model.conveyorSegments[segmentID].getCapacity() - 1;
			
			if (model.conveyorSegments[segmentID].positions[headOfSegment] != null && !model.autoNodes[autoNodeId].getBusy()) {
				if((model.autoNodes[autoNodeId].getTimeUntilFailure() > model.dvp.uAutomaticProcessTime(autoNodeId))
						&&(model.autoNodes[autoNodeId].lastTVType != model.conveyorSegments[segmentID].positions[headOfSegment].tvType)
						&&(model.conveyorSegments[segmentID].positions[headOfSegment].inMotion == false)
						&&(model.maintenance.busy == false)
						&& model.conveyorSegments[segmentID].positions[headOfSegment].finishedProcessing == false)
				{
					return autoNodeId;
				}
			}
		}
		return -1;
	}
	/**
	 * For every RC.AutoNode (nodeID) in the system which is busy:
	 * If (node.timeUntilFailure LESS THAN OR EQUAL TO 0 
	 * AND
	 * R.Maintenance.busy = FALSE)
	 * Then return the nodeID
	 * Else Return -1

	 * @return
	 */
	public int GetAutoNodeRequiringRepair(){
		for (int index = model.autoNodes.length - 1; index >= 0 ; index--){
			if (model.autoNodes[index].getTimeUntilFailure() <= 0 && 
				(model.maintenance.busy == false) && model.autoNodes[index].getBusy())
				return index;
		}
		return -1;
	}
	/**
	 * For every R.ManualNode (nodeID) in the system which is not busy:
	 * segmentID ← GetAssociatedSegmentID(nodeID, isAutoNode)
	 * capacity ← RQ.ConveyorSegment[segmentID].capacity
	 * if(RQ.ConveyorSegment[segmentID].positions[capacity - 1] is NOT NULL
	 * AND
	 * RQ.ConveyorSegment[segmentID].positions[capacity - 1].inMotion = FALSE)
	 * Return nodeID
	 * Else Return -1;
	 * @return
	 */
	public int GetManualNodeReadyForProcessing() {
		for (int manualNodeId = model.manualNodes.length - 1; manualNodeId >= 0; manualNodeId--) {

			if (model.manualNodes[manualNodeId].getBusy() == false) {

				int segmentID = this.GetAssociatedSegmentID(manualNodeId, false);
				int headOfSegment = model.conveyorSegments[segmentID].getCapacity() - 1;

				if (model.conveyorSegments[segmentID].positions[headOfSegment] != null) {

					if (model.conveyorSegments[segmentID].positions[headOfSegment].inMotion == false
							&& model.conveyorSegments[segmentID].positions[headOfSegment].finishedProcessing == false) {

						return manualNodeId;
					}
				}
			}
		}
		return -1;
	}
	/**
	 * If (autoNodeID = OP20) THEN Return RVP.uOP20RepairTime()
	 * Else If (autoNodeID = OP30) THEN Return RVP.uOP30RepairTime()
	 * Else If (autoNodeID = OP50) THEN Return RVP.uOP50RepairTime()
	 * Else If (autoNodeID = TEST) THEN Return RVP.uTESTRepairTime()

	 * @param autoNodeID
	 * @return 
	 */
	public double GetNodeRepairTime(int autoNodeID){
		if (autoNodeID == Const.OP20)
			return model.rvp.uOP20RepairTime();		
		else if (autoNodeID == Const.OP30)
			return model.rvp.uO30RepairTime();
		else if (autoNodeID == Const.OP50)
			return model.rvp.uOP50RepairTime();
		else if (autoNodeID == Const.TEST)
			return model.rvp.uTESTRepairTime();
		
		return -1;
	}
	/**
	 * 
	 * @return
	 */
	public int GetPalletReadyForMoving(){
		for(int i = 0; i < model.pallets.length; i++)
		{
			if(!model.pallets[i].inMotion)
			{
				int currConveyor = model.pallets[i].currConveyor;
				int currPosition = model.pallets[i].currPosition;
				int headOfSegment = model.conveyorSegments[currConveyor].getCapacity() - 1;
				if(currPosition < headOfSegment)
				{
					if(model.conveyorSegments[currConveyor].positions[currPosition + 1] == null
							|| model.conveyorSegments[currConveyor].positions[currPosition + 1].inMotion == true)
					{
						return i;
					}
				}
				else if (currPosition == headOfSegment && model.pallets[i].finishedProcessing)
				{
					//evaluated much deeper in this ugly if tree
					
					if(model.pallets[i].currConveyor == Const.CS_RETEST)
					{
						int headOfTest = model.conveyorSegments[Const.CS_TEST].getCapacity() - 1;
						if(model.conveyorSegments[Const.CS_TEST].positions[headOfTest] != null && model.conveyorSegments[Const.CS_TEST].positions[headOfTest].inMotion)
						{
							continue;
						}
					}
					
					if(model.pallets[i].currConveyor == Const.CS_TEST)
					{
						int headOfRetest = model.conveyorSegments[Const.CS_RETEST].getCapacity() - 1;
						if(model.conveyorSegments[Const.CS_RETEST].positions[headOfRetest] != null && model.conveyorSegments[Const.CS_RETEST].positions[headOfRetest].inMotion)
						{
							continue;
						}
					}
					
					int nextConveyor = model.conveyorSegments[model.pallets[i].currConveyor].nextConveyor;
					
					if(model.conveyorSegments[nextConveyor].positions[0] == null || 
							(model.conveyorSegments[nextConveyor].positions[0] != null && model.conveyorSegments[nextConveyor].positions[0].inMotion))
					{
						return i;
					}
				}
			}
		}
		return -1;
	}



}

