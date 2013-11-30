package com.gtech.prefix;

import com.gtech.lib.BoundedSortedQueue;
import com.gtech.model.TermScoreProtos.TermScore;
import com.gtech.model.TermScoreProtos.TermScoreDataset;
import java.lang.Character;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
 * This is the key datastructure we use for prefix matching.
 * The general idea is: 
 *  - we maintain a prefix ordered tree. 
 *  - On each node, we maintain:
 *    - the character it matches with; the prefix till this node is represented 
 *        by characters on nodes from root to this node. 
 *    - The top 10 TermScores whose name has prefix or (_prefix) of this node.
 *    - A hashmap of next character to their respective Trie nodes.
 *
 * The set up happens in initSetup method, which takes TermScoreDataset as input.
 *  - Set up root TopTrie node. 
 *  - For each TermScore to be indexed, gathering all tokens to be indexed (split on _)
 *  - Insert a token and TermScore pair in the root TopTrie node.
 *
 *
 * The insertion in Trie node is handled in addTermScore method.
 *  - Starts by verifying that the prefix str has any remaining unmatched tokens.
 *  - Finds the next node from its subTrie, whose character matches next to be matched token.
 *  - If such a node does not exist, creates it and adds to hashmap.
 *  - Continues to insert this pair in that children subTrie. 
 *  - Also, does bookeeping -- to maintain top 10 matches on this node. 
 *  - Uses a fixed capacity priority queue for this.. see BoundedSortedQueue.java
 *
 * The lookup for a prefix happens in getPrefixMatches.
 *  - Follows similar method as insertion, taraversing tree character by character.
 *  - when all characters match up, returns the precalculated top 10 matches of that node. 
 */

public class TopTrie {
  boolean isRoot;
  Character c;
  BoundedSortedQueue<TermScore> sortedTermScores;
  HashMap<Character, TopTrie> subTrie;
  public TopTrie(TermScoreDataset dataset) {
    this.isRoot = true;
    this.init(null);
    this.initSetup(dataset);  
  } 
  public TopTrie(Character ch) {
    this.isRoot = false;
    this.init(ch);
  }
  private void init(Character ch) {
    this.c = ch;
    this.subTrie = new HashMap<Character, TopTrie>();
    // Make this capacity configurable.
    this.sortedTermScores = new BoundedSortedQueue<TermScore>(10, new TermScoreComparator());
  }

  private void initSetup(TermScoreDataset dataset) {
    for(int i=0;i<dataset.getTermScoreCount(); i++) {
      TermScore ts = dataset.getTermScore(i);
      String [] tokens = ts.getName().split("_");
      for(int j=0;j<tokens.length;j++) {
        this.addTermScore(ts, tokens[j]);
      }
      if (i % 10000 == 0) {
        System.out.println(String.format("indexed %s of %s", i, dataset.getTermScoreCount()));
      }
    }
  }

  public void addTermScore(TermScore termScore, String str) {
    addTermScore(termScore, str, 0);
  }

  public void addTermScore(TermScore termScore, String str, int charsUsed) {
    if (str.length() > charsUsed) {
      char strC = str.charAt(charsUsed);
      if (this.isRoot || strC == this.c.charValue()) {
        this.sortedTermScores.add(termScore);
        int incrementPosBy = 1;
        if (this.isRoot) incrementPosBy = 0;
        if (str.length() > charsUsed + incrementPosBy) {
          Character nextChar = new Character(str.charAt(charsUsed + incrementPosBy));
          TopTrie nextTrie = subTrie.get(nextChar);
          if (nextTrie == null) {
            nextTrie = new TopTrie(nextChar);
            subTrie.put(nextChar, nextTrie);
          }
          nextTrie.addTermScore(termScore, str, charsUsed + incrementPosBy);
        }
      }
    }
  }

  public List<TermScore> getPrefixMatches(String str, int charsMatched) {
    if (charsMatched == str.length()) {
      return sortedTermScores.getAll();
    } else if (charsMatched < str.length() && (this.isRoot || str.charAt(charsMatched - 1) == c.charValue())) {
      TopTrie nextTrie = this.subTrie.get(new Character(str.charAt(charsMatched)));
      if (nextTrie == null) {
        return new ArrayList<TermScore>();
      } else {
        return nextTrie.getPrefixMatches(str, charsMatched + 1);
      }
    } else return null;
  }
}
