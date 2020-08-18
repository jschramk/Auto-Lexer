import java.util.*;

public class StringClassifier<I, O> {

  public static void main(String[] args) {

    StringClassifier<Character, String> sr = new StringClassifier<>();

    sr.putClassification(StringSection.toCharacterList("sinh"), "Hyperbolic Sine");
    sr.putClassification(StringSection.toCharacterList("sin"), "Sine");
    sr.putClassification(StringSection.toCharacterList("^"), "Exponent");

    String input = "5x^2 - sinhx";

    List<Character> sequence = StringSection.toCharacterList(input);


    List<Classification<String>> all = sr.segmentWhole(sequence);

    System.out.println("input: "+input);
    System.out.println("classifications: ");

    for(Classification<String> c : all){
      System.out.println("["+c.sectionOf(input)+": "+c.classification()+"]");
    }


  }

  private State<I, O> forwardInit = new State<>();
  private State<I, O> reverseInit = new State<>();
  private boolean useReverseTree;

  public StringClassifier(boolean useReverseTree) {
    this.useReverseTree = useReverseTree;
  }

  public StringClassifier(){
    this(false);
  }

  public static class Classification<O> extends StringSection {

    private final O result;

    public Classification(O result, int start, int end) {
      super(start, end);
      this.result = result;
    }

    @Override public String toString() {
      return "["+start+","+end+"], classification: "+result;
    }

    public O classification(){
      return result;
    }

  }

  public void print(){
    O defaultResult = forwardInit.output();
    if(defaultResult != null){
      System.out.println("Default: ("+defaultResult.toString()+")");
    }
    printRecur(forwardInit, 0);
  }

  public void printReverse(){
    O defaultResult = reverseInit.output();
    if(defaultResult != null){
      System.out.println("Default: ("+defaultResult.toString()+")");
    }
    printRecur(reverseInit, 0);
  }

  private void printRecur(State<I, O> curr, int indent){

    for(I c : curr.transitions()){

      State<I, O> next = curr.nextState(c);

      O output = next.output();

      String resultString = output == null ? "" : "("+output.toString()+")";

      String s = "  ".repeat(indent)+c+" "+resultString;

      System.out.println(s);

      printRecur(next, indent+1);

    }

  }

  public void putClassification(List<I> input, O result){

    if(input.size() == 0){
      throw new IllegalArgumentException(
          "Recognized inputs must be at least one entry long. "
              + "To set the default result, use setDefault()"
      );
    }

    State<I, O> curr = forwardInit;
    for(int i = 0; i < input.size(); i++){
      curr = curr.addTransition(input.get(i), new State<>());
    }
    curr.setOutput(result);

    if(useReverseTree){
      curr = reverseInit;
      for(int i = input.size()-1; i >= 0; i--){
        curr = curr.addTransition(input.get(i), new State<>());
      }
      curr.setOutput(result);
    }

  }

  public void setDefault(O result){
    forwardInit.setOutput(result);
    if(useReverseTree){
      reverseInit.setOutput(result);
    }
  }

  public O classifyWhole(List<I> input){

    State<I, O> curr = forwardInit;

    for (I thisChar : input) {

      if (!curr.hasTransition(thisChar)) {
        return null;
      }

      curr = curr.nextState(thisChar);

    }

    return curr.output();

  }

  public Classification<O> classifyFirst(List<I> input){
    return classifyFirst(input, 0);
  }

  public Classification<O> classifyFirst(List<I> input, int startIndex){

    if(startIndex > input.size()){
      throw new IllegalArgumentException("Start index: "+startIndex+", length: "+input.size());
    }

    int end = startIndex;
    O result = forwardInit.output();

    State<I, O> current = forwardInit;

    for (int i = startIndex; i < input.size(); i++) {

      I thisInput = input.get(i);

      current = current.nextState(thisInput);

      if(current == null){
        return new Classification<>(result, startIndex, end);
      }

      if(current.output() != null){
        end = i+1;
        result = current.output();
      }

    }

    return new Classification<>(result, startIndex, end);

  }

  public Classification<O> classifyLast(List<I> input){
    if(useReverseTree){
      return classifyLastReverse(input);
    } else {
      return classifyLastNoReverse(input);
    }
  }

  private Classification<O> classifyLastNoReverse(List<I> input){

    List<State<I, O>> activeStates = new ArrayList<>();

    int start = input.size(), end = input.size();

    for (int i = 0; i < input.size(); i++) {

      I thisInput = input.get(i);

      Iterator<State<I, O>> iterator = activeStates.iterator();

      int index = 0;

      while (iterator.hasNext()){

        State<I, O> s = iterator.next();

        if(s.hasTransition(thisInput)){
          s = s.nextState(thisInput);
          activeStates.set(index, s);
          if(index++ == 0){
            end = i+1;
          }
        } else {
          iterator.remove();
        }

      }

      if(this.forwardInit.hasTransition(thisInput)){
        if(activeStates.size() == 0){
          start = i;
          end = i+1;
        }
        activeStates.add(this.forwardInit.nextState(thisInput));
      }

    }


    if(activeStates.size() == 0){

      return new Classification<>(this.forwardInit.output(), start, end);

    } else {

      O testResult = activeStates.get(0).output();

      O result = testResult == null ? this.forwardInit.output() : testResult;

      return new Classification<>(result, start, end);

    }

  }


  private Classification<O> classifyLastReverse(List<I> input){

    int start = input.size(), end = input.size();
    O result = reverseInit.output();

    State<I, O> current = reverseInit;

    for (int i = input.size()-1; i >= 0; i--) {

      I thisInput = input.get(i);

      current = current.nextState(thisInput);

      if(current == null){
        return new Classification<>(result, start, end);
      }

      if(current.output() != null){
        start = i;
        result = current.output();
      }

    }

    return new Classification<>(result, start, end);

  }



  private List<Classification<O>> segmentWhole(List<I> input){

    List<Classification<O>> classifications = new ArrayList<>();

    int start = 0;
    int unknownStart = 0;

    while (start < input.size()){

      Classification<O> first = classifyFirst(input, start);

      if(first.classification() == null){
        start++;
      } else {
        if(start-unknownStart > 0){
          classifications.add(new Classification<>(null, unknownStart, start));
        }
        start = first.end;
        unknownStart = start;
        classifications.add(first);
      }

    }

    if(start-unknownStart > 0){
      classifications.add(new Classification<>(null, unknownStart, start));
    }

    return classifications;

  }









}
