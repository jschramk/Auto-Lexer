import java.util.*;

public class NFAState<I, O> {

  private O output;
  private Map<I, Set<NFAState<I, O>>> transitions = new HashMap<>();
  private Set<NFAState<I, O>> epsilonTransitions = new HashSet<>();

  public NFAState(O output) {
    this.output = output;
  }

  public NFAState() {
    this(null);
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

  public Set<NFAState<I, O>> getEpsilonStates() {
    Set<NFAState<I, O>> set = new HashSet<>();
    set.add(this);
    for (NFAState<I, O> connected : getEpsilonTransitions()) {
      set.addAll(connected.getEpsilonStates());
    }
    return set;
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

    System.out.println();

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

    public static <O> NFASegment<Character, O> fromString(String s) {
      return fromString(s, null);
    }

    public static <O> NFASegment<Character, O> fromRegex(Regex regex, O output){

      NFASegment<Character, O> r = null;

      switch (regex.type()) {

        case SINGLE: {
          r = fromCharacter(regex.getCharacter());
          break;
        }

        case SEQUENCE: {
          r = fromRegex(regex.components().get(0), null);
          for (int i = 1; i < regex.components().size(); i++) {
            r = r.concat(fromRegex(regex.components().get(i), null));
          }
          break;
        }

        case CHOOSE: {

          List<NFASegment<Character, O>> segments = new ArrayList<>();
          for (Regex regex1 : regex.components()) {
            segments.add(fromRegex(regex1, null));
          }

          r = epsilonUnion(segments);

          break;
        }

      }

      assert r != null;

      if(regex.isStar()) r = r.addEpsilonClosure();

      r.end.setOutput(output);

      return r;

    }

    public static <O> NFASegment<Character, O> fromCharacter(char c) {

      NFAState<Character, O> start = new NFAState<>();
      NFAState<Character, O> end = new NFAState<>();

      start.addTransition(c, end);

      return new NFASegment<>(start, end);

    }

    public static <O> NFASegment<Character, O> fromString(String s, O output) {

      NFAState<Character, O> start = new NFAState<>();

      NFAState<Character, O> curr = start;
      NFAState<Character, O> end = start;

      for (int i = 0; i < s.length(); i++) {

        char c = s.charAt(i);

        NFAState<Character, O> next = new NFAState<>();
        if (i == s.length() - 1) {
          next.setOutput(output);
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

      newEnd.setOutput(end.getOutput());
      end.setOutput(null);

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


}
