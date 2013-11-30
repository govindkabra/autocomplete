package com.gtech.server;

import com.gtech.model.TermScoreProtos.TermScore;
import com.gtech.model.TermScoreProtos.TermScoreDataset;
import com.gtech.prefix.TopTrie;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/*
 * This is the main server class. It can be extended for different demos:
 * interactive command line demo, or jetty server for web service, or including
 * in another larger search product. 
 * 
 * The constructor taks as input a binary file containing protobuf serialized
 * TermScoreDataset.  It parses the serialized data and creates a TopTrie data
 * structure.
 * 
 */ 
public class TermRanker {
  // This is the root node of our trie data structure.
  private TopTrie topTrie;
  public TermRanker(String filepath) {
    this.initSetup(filepath);
  }
  public List<TermScore> query(String prefix) {
    return topTrie.getPrefixMatches(prefix, 0);
  }
 
  public void initSetup(String filepath) {
    TermScoreDataset dataset = loadDataset(filepath);
    this.topTrie = new TopTrie(dataset);
    System.out.println(String.format("Setup complete.. %s term scores loaded.", dataset.getTermScoreCount()));
  }
  private TermScoreDataset loadDataset(String filepath) {
    TermScoreDataset.Builder dataBuilder = TermScoreDataset.newBuilder();
    try {
      dataBuilder.mergeFrom(new FileInputStream(filepath));
    } catch (IOException e) {
      System.out.println("Error in reading file: " + filepath);
    }
    return dataBuilder.build();
  } 
}

