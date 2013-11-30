package com.gtech.prefix;

import com.gtech.model.TermScoreProtos.TermScore;
import java.util.Comparator;

// Provide custom ordering for TermScore datastructure 
class TermScoreComparator implements Comparator<TermScore> {
  public int compare(TermScore t1, TermScore t2) {
    return Integer.compare(t1.getScore(), t2.getScore());
  }
}
