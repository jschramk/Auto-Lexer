import java.util.*;

public class DFAState<I, O> {

  private O output;
  private Map<I, DFAState<I, O>> transitions = new HashMap<>();

  public DFAState(O output) {
    this.output = output;
  }

  public DFAState() {
    this(null);
  }

  public DFAState<I, O> addTransition(I input, DFAState<I, O> next) {
    transitions.put(input, next);
    return next;
  }

  /*
  boolean hasSelfLoop(I input) {
    return transitions.get(input) == this;
  }
   */

  public DFAState<I, O> getDestination(I input) {
    return transitions.get(input);
  }

  public O getOutput() {
    return output;
  }

  public void setOutput(O output) {
    if (output != null) {
      this.output = output;
    }
  }

  public Set<I> getTransitions() {
    return transitions.keySet();
  }

  @Override public String toString() {
    return "(DFA State #" + hashCode() + ")" + (output != null ? ": " + output.toString() : "");
  }

  private Map<DFAState<I, O>, Set<I>> getStateMap() {

    Map<DFAState<I, O>, Set<I>> stateMap = new HashMap<>();

    for (I input : transitions.keySet()) {

      DFAState<I, O> DFAState = transitions.get(input);

      Set<I> trans = stateMap.computeIfAbsent(DFAState, set -> new HashSet<>());

      trans.add(input);

    }

    return stateMap;

  }


  /*
  public DFAState<I, O> copy() {
    return copy(new HashMap<>());
  }

  private DFAState<I, O> copy(Map<DFAState<I, O>, DFAState<I, O>> matches) {

    DFAState<I, O> copy = new DFAState<>(this.output);

    matches.put(this, copy);

    for (I input : getTransitions()) {

      DFAState<I, O> next = nextState(input);

      if (!matches.containsKey(next)) {
        copy.addTransition(input, next.copy(matches));
      } else {
        copy.addTransition(input, matches.get(next));
      }

    }

    return copy;

  }
   */

  public void print() {
    System.out.println("[START] ↴");
    print("    ", new HashSet<>());
    System.out.println();
  }

  private void print(String indent, Set<DFAState<I, O>> visited) {

    visited.add(this);

    System.out.println(indent + toString());

    Map<DFAState<I, O>, Set<I>> stateMap = getStateMap();
    List<DFAState<I, O>> DFAStates = new ArrayList<>(stateMap.keySet());

    for (int i = 0; i < DFAStates.size(); i++) {

      String space = i < DFAStates.size() - 1 ? " |  " : "    ";

      DFAState<I, O> DFAState = DFAStates.get(i);

      System.out.println(indent + stateMap.get(DFAState).toString() + " ↴");

      if (DFAState == this) {

        System.out.println(indent + space + "(Self Loop)");

      } else if (!visited.contains(DFAState)) {

        DFAState.print(indent + space, visited);

      } else {

        System.out.println(indent + space + DFAState.toString() + " (Shown Above)");

      }

    }

  }



}