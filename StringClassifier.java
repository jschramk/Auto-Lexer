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

  private DFAState<I, O> forwardInit = new DFAState<>();
  private DFAState<I, O> reverseInit = new DFAState<>();
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
    O defaultResult = forwardInit.getOutput();
    if(defaultResult != null){
      System.out.println("Default: ("+defaultResult.toString()+")");
    }
    printRecur(forwardInit, 0);
  }

  public void printReverse(){
    O defaultResult = reverseInit.getOutput();
    if(defaultResult != null){
      System.out.println("Default: ("+defaultResult.toString()+")");
    }
    printRecur(reverseInit, 0);
  }

  private void printRecur(DFAState<I, O> curr, int indent){

    for(I c : curr.getTransitions()){

      DFAState<I, O> next = curr.getDestination(c);

      O output = next.getOutput();

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

    DFAState<I, O> curr = forwardInit;
    for(int i = 0; i < input.size(); i++){
      curr = curr.addTransition(input.get(i), new DFAState<>());
    }
    curr.setOutput(result);

    if(useReverseTree){
      curr = reverseInit;
      for(int i = input.size()-1; i >= 0; i--){
        curr = curr.addTransition(input.get(i), new DFAState<>());
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

    DFAState<I, O> curr = forwardInit;

    for (I thisChar : input) {

      if (!curr.getTransitions().contains(thisChar)) {
        return null;
      }

      curr = curr.getDestination(thisChar);

    }

    return curr.getOutput();

  }

  public Classification<O> classifyFirst(List<I> input){
    return classifyFirst(input, 0);
  }

  public Classification<O> classifyFirst(List<I> input, int startIndex){

    if(startIndex > input.size()){
      throw new IllegalArgumentException("Start index: "+startIndex+", length: "+input.size());
    }

    int end = startIndex;
    O result = forwardInit.getOutput();

    DFAState<I, O> current = forwardInit;

    for (int i = startIndex; i < input.size(); i++) {

      I thisInput = input.get(i);

      current = current.getDestination(thisInput);

      if(current == null){
        return new Classification<>(result, startIndex, end);
      }

      if(current.getOutput() != null){
        end = i+1;
        result = current.getOutput();
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

    List<DFAState<I, O>> activeDFAStates = new ArrayList<>();

    int start = input.size(), end = input.size();

    for (int i = 0; i < input.size(); i++) {

      I thisInput = input.get(i);

      Iterator<DFAState<I, O>> iterator = activeDFAStates.iterator();

      int index = 0;

      while (iterator.hasNext()){

        DFAState<I, O> s = iterator.next();

        if(s.getTransitions().contains(thisInput)){
          s = s.getDestination(thisInput);
          activeDFAStates.set(index, s);
          if(index++ == 0){
            end = i+1;
          }
        } else {
          iterator.remove();
        }

      }

      if(this.forwardInit.getTransitions().contains(thisInput)){
        if(activeDFAStates.size() == 0){
          start = i;
          end = i+1;
        }
        activeDFAStates.add(this.forwardInit.getDestination(thisInput));
      }

    }


    if(activeDFAStates.size() == 0){

      return new Classification<>(this.forwardInit.getOutput(), start, end);

    } else {

      O testResult = activeDFAStates.get(0).getOutput();

      O result = testResult == null ? this.forwardInit.getOutput() : testResult;

      return new Classification<>(result, start, end);

    }

  }


  private Classification<O> classifyLastReverse(List<I> input){

    int start = input.size(), end = input.size();
    O result = reverseInit.getOutput();

    DFAState<I, O> current = reverseInit;

    for (int i = input.size()-1; i >= 0; i--) {

      I thisInput = input.get(i);

      current = current.getDestination(thisInput);

      if(current == null){
        return new Classification<>(result, start, end);
      }

      if(current.getOutput() != null){
        start = i;
        result = current.getOutput();
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
