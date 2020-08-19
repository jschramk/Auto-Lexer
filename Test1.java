public class Test1 {

  public static void main(String[] args) {

    Regex r0 = Regex.parse("a");
    Regex r1 = Regex.parse("(a)*");

    RegexMatrix rm0 = RegexMatrix.from(r0);
    RegexMatrix rm1 = RegexMatrix.from(r1);
    RegexMatrix combined = RegexMatrix.concatenate(rm0, rm1);//.purgeUnused();

    System.out.println(rm0);
    System.out.println();
    System.out.println(rm1);
    System.out.println();
    System.out.println(combined);
    System.out.println();
    combined.toDFA().print();

  }

}
