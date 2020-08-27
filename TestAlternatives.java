package dfa.utils;

import java.util.ArrayList;
import java.util.List;

public class TestAlternatives {

  public static void main(String[] args) {


    String input = "hello world";

    List<String> substrings = getAllSubstrings(input);

    System.out.println(getNumSubstrings(input.length()));
    substrings.forEach(System.out::println);
    System.out.println(getNumSubstrings(input.length()));

  }

  private static List<String> getAllSubstrings(String input) {

    List<String> subs = new ArrayList<>();

    for (int i = 0; i < input.length(); i++) {
      for (int j = i + 1; j <= input.length(); j++) {
        subs.add(input.substring(i, j));
      }
    }

    return subs;

  }

  private static int getNumSubstrings(int n) {
    return n * (n + 1) / 2;
  }



}
