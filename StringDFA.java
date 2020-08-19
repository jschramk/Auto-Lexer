import java.util.List;

public class StringDFA implements DFA<Character, Boolean> {

  private DFAState<Character, Boolean> init;

  public StringDFA(DFAState<Character, Boolean> init) {
    this.init = init;
  }

  @Override public Boolean output(List<Character> input) {

    DFAState<Character, Boolean> curr = init;
    for(char c : input){
      if(curr.hasTransition(c)){
        curr = curr.nextState(c);
      } else {
        return null;
      }
    }

    return curr.getOutput();
  }

  @Override public void print() {
    init.print();
  }


}
