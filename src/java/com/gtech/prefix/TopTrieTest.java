package com.gtech.prefix;

import java.util.*;
import com.gtech.model.TermScoreProtos.TermScore;
import com.gtech.model.TermScoreProtos.TermScoreDataset;

/*
 * A sample program to test TopTrie and PriorityQueue data structurs.
 * To run this, execute following command:
 * $ java -cp 3rdparty/protobuf-java-2.5.0.jar:./bin/. com.gtech/prefix/TopTrieTest
 *
 * A stop-gap solution for now, until we add proper unit tests here.
 */
public class TopTrieTest {
  public void printMatches(TopTrie tt, String query) {
    List<TermScore> res1 = tt.getPrefixMatches(query, 0);
    System.out.println("Found " + Integer.toString(res1.size()) + " results for " + query);
    for(int i=0;i<res1.size();i++)
      System.out.println(String.format("%s: %s", res1.get(i).getScore(), res1.get(i).getName()));
  }
  public void indexingHelper(TermScoreDataset.Builder dt, String str, int score) {
    TermScore ts = TermScore.newBuilder().setName(str).setScore(score).build();
    dt.addTermScore(ts);
  }
  public void simpleSetup() {
    TermScoreDataset.Builder dt = TermScoreDataset.newBuilder();
    indexingHelper(dt, "awesome_amicable", 5);
    indexingHelper(dt, "amazing_boxing", 50);
    indexingHelper(dt, "amazball_ridi_boxer", 500);
    TopTrie tt = new TopTrie(dt.build());
    
    printMatches(tt, "boy");
    printMatches(tt, "box");
    printMatches(tt, "am");
    printMatches(tt, "ami");
    printMatches(tt, "amaz");
    printMatches(tt, "amazb");
    printMatches(tt, "a");
    printMatches(tt, "");
  }
  public void basicSetup() {
    TermScoreDataset.Builder dt = TermScoreDataset.newBuilder();
    indexingHelper(dt, "a", 10);
    indexingHelper(dt, "ab", 30);

    TopTrie tt = new TopTrie(dt.build());
    printMatches(tt, "a");
    printMatches(tt, "ab");
    printMatches(tt, "abc");
    printMatches(tt, "");
  }
  public static void main(String[] args) {
    new TopTrieTest().simpleSetup();
    new TopTrieTest().basicSetup();
  }
}

