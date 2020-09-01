package dfa.utils;

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

  public void addTransition(I input, DFAState<I, O> next) {
    transitions.put(input, next);
  }

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