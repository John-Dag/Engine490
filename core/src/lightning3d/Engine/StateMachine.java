package lightning3d.Engine;

import java.util.LinkedList;
import java.util.List;

import lightning3d.DynamicEntities.Enemy;

public class StateMachine {
	public List<State> States=new LinkedList<State>();	//List of states
	public State Current;								//Current state
	
	//Update the state.  This would be called during enemies update cycle.Once updated it would check for current state and act on it.
	public void UpdateStates(Enemy enemy){
		for(State state:States)
		{
			for(Condition condition:state.LinkedStates.keySet())
			{
				if(condition.IsSatisfied(enemy))
				{
					Current=state.LinkedStates.get(condition);//Get state associated with the condition
				}
			}
		}
	}
}
