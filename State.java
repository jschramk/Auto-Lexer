import java.util.*;

public class State<I, O> {

  private O output;
  private Map<I, State<I, O>> transitions = new HashMap<>();

  public State() {
  }

  public State(O output) {
    this.output = output;
  }

  State<I, O> addTransition(I input, State<I, O> next) {
    transitions.put(input, next);
    return next;
  }

  boolean hasTransition(I input) {
    return transitions.containsKey(input);
  }

  boolean hasSelfLoop(I input) {
    return transitions.get(input) == this;
  }

  State<I, O> nextState(I input) {
    return transitions.get(input);
  }

  O output() {
    return output;
  }

  void setOutput(O output) {
    if (output != null) {
      this.output = output;
    }
  }

  Set<I> transitions() {
    return transitions.keySet();
  }

  @Override public String toString() {
    return "(State #" + hashCode() + ")";
  }

  public Map<State<I, O>, Set<I>> getStateMap() {

    Map<State<I, O>, Set<I>> stateMap = new HashMap<>();

    for (I input : transitions.keySet()) {

      State<I, O> state = transitions.get(input);

      Set<I> trans = stateMap.computeIfAbsent(state, set -> new HashSet<>());

      trans.add(input);

    }

    return stateMap;

  }


  public State<I, O> copy() {
    return copy(new HashMap<>());
  }

  private State<I, O> copy(Map<State<I, O>, State<I, O>> matches) {

    State<I, O> copy = new State<>(this.output);

    matches.put(this, copy);

    for (I input : transitions()) {

      State<I, O> next = nextState(input);

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

  private void print(String indent, Set<State<I, O>> visited) {

    visited.add(this);

    System.out.print(indent + toString());

    if (output != null) {
      System.out.print(": " + output);
    }
    System.out.println();

    Map<State<I, O>, Set<I>> stateMap = getStateMap();
    List<State<I, O>> states = new ArrayList<>(stateMap.keySet());

    for (int i = 0; i < states.size(); i++) {

      String space = i < states.size() - 1 ? " |  " : "    ";

      State<I, O> state = states.get(i);

      System.out.println(indent + stateMap.get(state).toString() + " ↴");

      if (state == this) {

        System.out.println(indent + space + "(Self Loop)");

      } else if (!visited.contains(state)) {

        state.print(indent + space, visited);

      } else {

        System.out.println(indent + space + state.toString() + " (Shown Above)");

      }

    }

  }



}
