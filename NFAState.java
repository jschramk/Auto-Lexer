import java.util.*;

public class NFAState<I, O> {

  private Map<I, Set<NFAState<I, O>>> transitions = new HashMap<>();
  private Set<NFAState<I, O>> epsilonTransitions = new HashSet<>();
  private O output;

  public NFAState(O output) {
    this.output = output;
  }

  public NFAState() {
    this(null);
  }

  public static class NFASegment<I, O> {

    private NFAState<I, O> start;
    private NFAState<I, O> end;

    public NFASegment(NFAState<I, O> start, NFAState<I, O> end) {
      this.start = start;
      this.end = end;
    }

    public NFAState<I, O> getStart() {
      return start;
    }

    public static NFASegment<Character, Boolean> fromString(String s) {

      NFAState<Character, Boolean> start = new NFAState<>();

      NFAState<Character, Boolean> curr = start;
      NFAState<Character, Boolean> end = start;

      for (int i = 0; i < s.length(); i++) {

        char c = s.charAt(i);

        NFAState<Character, Boolean> next = new NFAState<>();
        if (i == s.length() - 1) {
          next.setOutput(true);
          end = next;
        }

        curr.addTransition(c, next);
        curr = next;
      }

      return new NFASegment<>(start, end);

    }

    public NFASegment<I, O> addEpsilonClosure() {

      NFAState<I, O> newStart = new NFAState<>();
      NFAState<I, O> newEnd = new NFAState<>();

      newStart.addEpsilonTransition(start);
      end.addEpsilonTransition(newEnd);
      newStart.addEpsilonTransition(newEnd);
      end.addEpsilonTransition(start);

      return new NFASegment<>(newStart, newEnd);

    }

    public static <I, O> NFASegment<I, O> epsilonUnion(List<NFASegment<I, O>> segments) {
      NFAState<I, O> start = new NFAState<>();
      NFAState<I, O> end = new NFAState<>();
      for (NFASegment<I, O> segment : segments) {
        start.addEpsilonTransition(segment.start);
        segment.end.addEpsilonTransition(end);
      }
      return new NFASegment<>(start, end);
    }

    public NFASegment<I, O> concat(NFASegment<I, O> segment) {
      this.end.addEpsilonTransition(segment.start);
      return new NFASegment<>(this.start, segment.end);
    }

  }



  public void addTransition(I input, NFAState<I, O> destination) {
    if (!transitions.containsKey(input)) {
      transitions.put(input, new HashSet<>());
    }
    transitions.get(input).add(destination);
  }

  public void addEpsilonTransition(NFAState<I, O> destination) {
    if (destination != this)
      epsilonTransitions.add(destination);
  }

  public void setOutput(O output) {
    this.output = output;
  }

  public O getOutput() {
    return output;
  }

  public Set<NFAState<I, O>> getDestinations(I input) {
    return transitions.get(input);
  }

  public Map<I, Set<NFAState<I, O>>> getTransitions() {
    return transitions;
  }

  public Set<NFAState<I, O>> getEpsilonTransitions() {
    return epsilonTransitions;
  }

  @Override public String toString() {
    return "(NFA State #" + hashCode() + ")" + (output != null ? ": " + output.toString() : "");
  }

  public void print() {
    print("", new HashSet<>());
  }

  private void print(String currIndent, Set<NFAState<I, O>> visited) {

    System.out.print(currIndent + this);
    if (visited.contains(this)) {
      System.out.println(" (Shown above)");
      return;
    } else {
      System.out.println();
    }

    visited.add(this);

    String indent = currIndent + "    ";

    for (I input : transitions.keySet()) {
      System.out.println(indent + "[" + input + "] ↴");
      transitions.get(input).forEach(state -> {
        state.print(indent, visited);
      });
    }
    if (epsilonTransitions.size() > 0) {
      System.out.println(indent + "[EPSILON] ↴");
      epsilonTransitions.forEach(state -> {
        state.print(indent, visited);
      });
    }


  }

  public static <I, O> DFAState<I, O> toDFAState(Set<NFAState<I, O>> merge) {

    DFAState<I, O> merged = new DFAState<>();

    for (NFAState<I, O> state : merge) {

      for (I input : state.transitions.keySet()) {

        merged.addTransition(input, toDFAState(state.transitions.get(input)));

      }

    }

    return merged;

  }



  public DFAState<I, O> toDFAState() {

    DFAState<I, O> merged = new DFAState<>();

    for (I input : transitions.keySet()) {

      merged.addTransition(input, toDFAState(transitions.get(input)));

        /*
        for(NFAState<I, O> add: state.transitions.get(input)){
          merged.addTransition(input, add);
        }*/

    }



    return merged;

  }



}
