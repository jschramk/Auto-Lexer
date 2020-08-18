import java.util.List;

public class StringDFA implements DFA<Character, Boolean> {

  private State<Character, Boolean> init;

  public StringDFA(State<Character, Boolean> init) {
    this.init = init;
  }

  @Override public Boolean output(List<Character> input) {

    State<Character, Boolean> curr = init;
    for(char c : input){
      if(curr.hasTransition(c)){
        curr = curr.nextState(c);
      } else {
        return null;
      }
    }

    return curr.output();
  }

  @Override public void print() {
    init.print();
  }


}
