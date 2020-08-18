import java.util.*;

public class State<O> {

  private O output;
  private Map<Character, State<O>> transitions = new HashMap<>();

  public State() {
  }

  public State(O output) {
    this.output = output;
  }

  State<O> addTransition(char c, State<O> next) {
    transitions.put(c, next);
    return next;
  }

  boolean hasTransition(char c) {
    return transitions.containsKey(c);
  }

  boolean hasSelfLoop(char c) {
    return transitions.get(c) == this;
  }

  State<O> nextState(char input) {
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

  void removeOutput() {
    this.output = null;
  }

  Set<Character> transitions() {
    return transitions.keySet();
  }

  @Override public String toString() {
    return "(State #" + hashCode() + ")";
  }

  public Map<State<O>, Set<Character>> getStateMap() {

    Map<State<O>, Set<Character>> stateMap = new HashMap<>();

    for (char c : transitions.keySet()) {

      State<O> state = transitions.get(c);

      Set<Character> trans = stateMap.computeIfAbsent(state, set -> new HashSet<>());

      trans.add(c);

    }

    return stateMap;

  }


  public State<O> copy() {
    return copy(new HashMap<>());
  }

  private State<O> copy(Map<State<O>, State<O>> matches) {

    State<O> copy = new State<>(this.output);

    matches.put(this, copy);

    for (char c : transitions()) {

      State<O> next = nextState(c);

      if (!matches.containsKey(next)) {
        copy.addTransition(c, next.copy(matches));
      } else {
        copy.addTransition(c, matches.get(next));
      }

    }

    return copy;

  }

  public void print() {
    System.out.println("[INIT] ↴");
    print("    ", new HashSet<>());
  }

  private void print(String indent, Set<State<O>> visited) {

    visited.add(this);

    System.out.print(indent + toString());

    if (output != null) {
      System.out.print(": " + output);
    }
    System.out.println();

    Map<State<O>, Set<Character>> stateMap = getStateMap();
    List<State<O>> states = new ArrayList<>(stateMap.keySet());

    for (int i = 0; i < states.size(); i++) {

      String space = i < states.size() - 1 ? " |  " : "    ";

      State<O> state = states.get(i);

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
