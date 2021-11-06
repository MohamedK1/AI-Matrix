package code;

import java.util.ArrayList;


public abstract class GenericSearch {
GenericState initialState;
public abstract ArrayList<GenericState> successors(GenericState state);
public abstract boolean isGoal(GenericState state);
//we assume path cost function depends on the current state only.
public abstract int pathCostFunction(GenericState stateSequence);


}
