import java.util.*;

public class RegexMatrix {

  private int size;
  private Set<Character>[][] matrix;
  private Set<Integer> acceptingStates = new HashSet<>();
  private Map<Integer, Object> outputMap = new HashMap<>();

  public RegexMatrix(int size) {
    this.size = size;
    this.matrix = new HashSet[size][size];

    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix.length; j++) {
        matrix[i][j] = new HashSet<>();
      }
    }

  }

  public static RegexMatrix from(Regex regex) {

    RegexMatrix r = null;

    switch (regex.type()) {

      case SINGLE: {
        r = base(regex.getCharacter(), false);
        break;
      }

      case SEQUENCE: {
        r = from(regex.components().get(0));
        for (int i = 1; i < regex.components().size(); i++) {
          r = r.append(from(regex.components().get(i)));
        }
        break;
      }

      case CHOOSE: {
        r = from(regex.components().get(0));
        for (int i = 1; i < regex.components().size(); i++) {
          r = r.or(from(regex.components().get(i)));
        }
        break;
      }

    }

    return regex.isStar() ? makeLoop(r) : r;

  }

  public static RegexMatrix base(char c, boolean star) {

    if (star) {
      RegexMatrix r = new RegexMatrix(1);
      r.addTransition(0, 0, c);
      r.acceptingStates.add(0);
      return r;
    } else {
      RegexMatrix r = new RegexMatrix(2);
      r.addTransition(0, 1, c);
      r.acceptingStates.add(1);
      return r;
    }

  }

  /*
  @Deprecated
  public static RegexMatrix from(String string) {
    return from(string, false);
  }

  @Deprecated
  public static RegexMatrix from(String string, boolean star) {

    if (star) {

      RegexMatrix r = new RegexMatrix(string.length());

      for (int i = 0; i < string.length(); i++) {
        r.addTransition(i, i == string.length() - 1 ? 0 : i + 1, string.charAt(i));
      }
      r.acceptingStates.add(0);

      return r;

    } else {

      RegexMatrix r = new RegexMatrix(string.length() + 1);

      int i;
      for (i = 0; i < string.length(); i++) {
        r.addTransition(i, i + 1, string.charAt(i));
      }
      r.acceptingStates.add(string.length());

      return r;

    }

  }

  public static RegexMatrix chooseFrom(String string, boolean star) {

    RegexMatrix r = new RegexMatrix(string.length());

    for (char c : string.toCharArray()) {
      r.addTransition(0, star ? 0 : 1, c);
    }
    r.acceptingStates.add(star ? 0 : 1);

    return r;

  }
  */

  public static RegexMatrix makeLoop(RegexMatrix r) {

    if (r.isLoop())
      return r;

    for (int acc : r.acceptingStates) {
      for (int from = 0; from < r.size; from++) {
        r.matrix[from][0].addAll(r.matrix[from][acc]);
        r.matrix[from][acc] = new HashSet<>();
      }
    }

    r.acceptingStates = new HashSet<>();
    r.acceptingStates.add(0);

    return r.removeUnusedStates();

  }

  public int numStates() {
    return size;
  }

  public RegexMatrix removeStates(List<Integer> states) {

    RegexMatrix trimmed = new RegexMatrix(size - states.size());

    int[] trimmedPos = purgedIndices(states);

    for (int from = 0; from < size; from++) {

      if (trimmedPos[from] == -1)
        continue;

      for (int to = 0; to < size; to++) {

        if (trimmedPos[to] == -1)
          continue;

        trimmed.matrix[trimmedPos[from]][trimmedPos[to]] = matrix[from][to];

      }
    }

    Set<Integer> newAccepting = new HashSet<>();
    for (int i : acceptingStates) {
      if (trimmedPos[i] == -1)
        continue;
      newAccepting.add(trimmedPos[i]);
    }

    trimmed.acceptingStates = newAccepting;

    return trimmed;

  }

  public RegexMatrix removeUnusedStates() {

    List<Integer> unused = new ArrayList<>();
    for (int i = 1; i < size; i++) {
      if (outgoing(i).isEmpty() && incoming(i).isEmpty()) {
        unused.add(i);
      }
    }

    return removeStates(unused);

  }

  public boolean isLoop() {
    for (int row = 0; row < size; row++) {
      if (matrix[row][0].size() > 0)
        return true;
    }
    return false;
  }

  public boolean isDeterministic() {
    Set<Character> characters = new HashSet<>();
    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix.length; j++) {
        for (char c : matrix[i][j]) {
          if (!characters.add(c))
            return false;
        }
      }
      characters.clear();
    }
    return true;
  }

  public static RegexMatrix combine(RegexMatrix r0, RegexMatrix r1) {

    Set<Integer> totalAccepting = new HashSet<>();
    if (r0.isLoop())
      r0 = r0.extendLoop(1);
    if (r1.isLoop()) {
      r1 = r1.extendLoop(1);
      totalAccepting.addAll(r0.acceptingStates);
    }

    RegexMatrix combined = new RegexMatrix(r0.size + r1.size - 1);

    // add first matrix
    for (int from = 0; from < r0.matrix.length; from++) {
      for (int to = 0; to < r0.matrix.length; to++) {
        combined.matrix[from][to] = r0.matrix[from][to];
      }
    }

    // add start of second matrix
    for (int fromAcc : r0.acceptingStates) {
      for (int to = 0; to < r1.matrix.length; to++) {
        combined.matrix[fromAcc][to + r0.size - 1].addAll(r1.matrix[0][to]);
      }
    }

    // add second matrix
    for (int from = 1; from < r1.matrix.length; from++) {
      for (int to = 0; to < r1.matrix.length; to++) {
        combined.matrix[from + r0.size - 1][to + r0.size - 1] = r1.matrix[from][to];
      }
    }

    // set accepting states for new matrix
    for (int i : r1.acceptingStates) {
      if (i > 0)
        totalAccepting.add(i + r0.size - 1);
    }
    combined.acceptingStates = totalAccepting;

    return combined;

  }

  public static RegexMatrix or(RegexMatrix r0, RegexMatrix r1) {

    if (r0.isLoop())
      r0 = r0.extendLoop(1);
    if (r1.isLoop()) {
      r1 = r1.extendLoop(1);
    }

    RegexMatrix or = new RegexMatrix(r0.size + r1.size - 1);

    // add first matrix
    for (int from = 0; from < r0.matrix.length; from++) {
      for (int to = 0; to < r0.matrix.length; to++) {
        or.matrix[from][to] = r0.matrix[from][to];
      }
    }

    // add start of second matrix
    for (int to = 0; to < r1.matrix.length; to++) {
      or.matrix[0][to + r0.size - 1].addAll(r1.matrix[0][to]);
    }

    // add second matrix
    for (int from = 1; from < r1.matrix.length; from++) {
      for (int to = 0; to < r1.matrix.length; to++) {
        or.matrix[from + r0.size - 1][to + r0.size - 1] = r1.matrix[from][to];
      }
    }

    // add accepting states
    or.acceptingStates.addAll(r0.acceptingStates);
    for (int i : r1.acceptingStates) {
      if (i == 0) {
        or.acceptingStates.add(i);
      } else {
        or.acceptingStates.add(i + r0.size - 1);
      }
    }

    return or;

  }

  public static RegexMatrix or2(RegexMatrix r0, RegexMatrix r1) {

    if (r0.isLoop())
      r0 = r0.extendLoop(1);
    if (r1.isLoop()) {
      r1 = r1.extendLoop(1);
    }

    RegexMatrix or = new RegexMatrix(r0.size + r1.size - 1);

    // add first matrix
    for (int from = 0; from < r0.matrix.length; from++) {
      for (int to = 0; to < r0.matrix.length; to++) {
        or.matrix[from][to] = r0.matrix[from][to];
      }
    }



    for(int from = 0; from < r1.size; from++){
      if(overlap(r0, from, r1, from)){

        

      } else {



      }
    }


    // add start of second matrix
    for (int to = 0; to < r1.matrix.length; to++) {
      or.matrix[0][to + r0.size - 1].addAll(r1.matrix[0][to]);
    }

    // add second matrix
    for (int from = 1; from < r1.matrix.length; from++) {
      for (int to = 0; to < r1.matrix.length; to++) {
        or.matrix[from + r0.size - 1][to + r0.size - 1] = r1.matrix[from][to];
      }
    }

    // add accepting states
    or.acceptingStates.addAll(r0.acceptingStates);
    for (int i : r1.acceptingStates) {
      if (i == 0) {
        or.acceptingStates.add(i);
      } else {
        or.acceptingStates.add(i + r0.size - 1);
      }
    }

    return or;

  }

  public RegexMatrix append(RegexMatrix r) {
    return combine(this, r);
  }

  public RegexMatrix or(RegexMatrix r) {
    return or(this, r);
  }

  public RegexMatrix extendLoop(int times) {

    if(times < 0){
      throw new IllegalArgumentException("Cannot extend loop a negative number of times");
    }

    if (!isLoop())
      throw new RuntimeException("Not star");

    RegexMatrix extended = new RegexMatrix((times+1) * size);

    // add original

    for(int time = 1; time <= times; time++){
      for (int from = 0; from < matrix.length; from++) {
        for (int to = 0; to < matrix.length; to++) {
          extended.matrix[size*(time-1)+from][to == 0 ? size*time : size*(time-1)+to] = matrix[from][to];
        }
      }
    }

    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix.length; j++) {
        extended.matrix[i + times*size][j + times*size] = matrix[i][j];
      }
    }

    for (int i : acceptingStates) {
      for(int time = 1; time <= times; time++){
        extended.acceptingStates.add(time*size + i);
      }

    }
    extended.acceptingStates.add(0);

    return extended;

  }

  private Map<Character, Integer> outgoing(int state) {
    Map<Character, Integer> transitions = new HashMap<>();
    for (int i = 0; i < size; i++) {
      for (char c : matrix[state][i]) {
        transitions.put(c, i);
      }
    }
    return transitions;
  }

  private Map<Character, Integer> incoming(int state) {
    Map<Character, Integer> transitions = new HashMap<>();
    for (int i = 0; i < size; i++) {
      for (char c : matrix[i][state]) {
        transitions.put(c, i);
      }
    }
    return transitions;
  }

  private Set<Integer> getAcceptingStates() {
    return acceptingStates;
  }

  private Set<Character> transitions(int from, int to) {
    return matrix[from][to];
  }

  private void addTransition(int from, int to, char c) {
    matrix[from][to].add(c);
  }

  private void removeTransition(int from, int to, char c) {
    matrix[from][to].remove(c);
  }

  public DFA<Character, Boolean> toDFA() {
    if (!isDeterministic())
      throw new RuntimeException("Tree is not deterministic");

    List<State<Boolean>> states = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      states.add(new State<>());
    }

    for (int i = 0; i < size; i++) {
      Map<Character, Integer> out = outgoing(i);
      for (char c : out.keySet()) {
        states.get(i).addTransition(c, states.get(out.get(c)));
      }
    }

    for (int i : acceptingStates) {
      states.get(i).setOutput(true);
    }

    return new StringDFA(states.get(0));

  }

  private static int purgedIndex(List<Integer> removing, int state) {
    int numGreater = 0;
    for (int i : removing) {
      if (state == i)
        return -1;
      if (state > i) {
        numGreater++;
      } else
        break;
    }
    return state - numGreater;
  }

  private int[] purgedIndices(List<Integer> removing) {
    int[] pos = new int[size];
    for (int i = 0; i < size; i++) {
      pos[i] = purgedIndex(removing, i);
    }
    return pos;
  }

  public static boolean overlap(RegexMatrix r0, int from0, RegexMatrix r1, int from1){
    for(int i = 0; i < r0.size; i++){
      for(int j = 0; j < r1.size; j++){
        if(!Collections.disjoint(r0.matrix[from0][i], r1.matrix[from1][j])) return true;
      }
    }
    return false;
  }

  @Override public String toString() {
    StringBuilder s = new StringBuilder();

    for (int i = 0; i < matrix.length; i++) {
      if (i > 0)
        s.append("\n\n");
      for (int j = 0; j < matrix.length; j++) {
        if (j > 0)
          s.append(",     ");
        s.append(matrix[i][j]);
      }
      if (acceptingStates.contains(i))
        s.append(" ->");
    }

    s.append("\n");
    s.append("Accepting states: ");
    s.append(acceptingStates);

    return s.toString();
  }

}
