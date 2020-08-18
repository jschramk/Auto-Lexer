

public class Test {

  public static void main(String[] args) {

    RegexMatrix r0 = RegexMatrix.from(Regex.parse("a"));
    RegexMatrix r1 = RegexMatrix.from(Regex.parse("b"));

    System.out.println(RegexMatrix.overlap(r0, 0, r1, 0));

  }


}
