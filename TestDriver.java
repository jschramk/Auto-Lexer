import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TestDriver {

  public static List<Character> getSequence(String input){
    List<Character> inputSequence = new ArrayList<>();
    for(char c : input.toCharArray()){
      inputSequence.add(c);
    }
    return inputSequence;
  }

  public static void main(String[] args) {

    Regex regex = Regex.parse("(a|b|c|d)*ef");
    DFA<Character, Boolean> dfa = RegexMatrix.from(regex).toDFA();

    Scanner sc = new Scanner(System.in);

    while (true){

      System.out.print("Input: ");

      String input = sc.nextLine();


      System.out.println("Output: "+dfa.output(getSequence(input)));
      System.out.println();

    }



  }

}
