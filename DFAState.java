import java.util.*;

public class DFAState<I, O> {

  private O output;
  private Map<I, DFAState<I, O>> transitions = new HashMap<>();

  public DFAState() {
  }

  public DFAState(O output) {
    this.output = output;
  }

  DFAState<I, O> addTransition(I input, DFAState<I, O> next) {
    transitions.put(input, next);
    return next;
  }

  boolean hasTransition(I input) {
    return transitions.containsKey(input);
  }

  boolean hasSelfLoop(I input) {
    return transitions.get(input) == this;
  }

  DFAState<I, O> nextState(I input) {
    return transitions.get(input);
  }

  O getOutput() {
    return output;
  }

  void setOutput(O output) {
    if (output != null) {
      this.output = output;
    }
  }

  Set<I> getTransitions() {
    return transitions.keySet();
  }

  @Override public String toString() {
    return "(DFA State #" + hashCode() + ")" + (output != null ? ": " + output.toString() : "");
  }

  public Map<DFAState<I, O>, Set<I>> getStateMap() {

    Map<DFAState<I, O>, Set<I>> stateMap = new HashMap<>();

    for (I input : transitions.keySet()) {

      DFAState<I, O> DFAState = transitions.get(input);

      Set<I> trans = stateMap.computeIfAbsent(DFAState, set -> new HashSet<>());

      trans.add(input);

    }

    return stateMap;

  }


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

  public void print() {
    System.out.println("[INIT] ↴");
    print("    ", new HashSet<>());
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