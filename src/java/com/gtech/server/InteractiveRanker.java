package com.gtech.server;

import com.gtech.model.TermScoreProtos.TermScore;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;

/*
 * This is the interactive command line demo class, asks user for queries on
 * command line, showing matches right away.
 * 
 * It extends TermRanker class, which handles parsing binary protobuf file, and
 * loading dataset in TopTrie datastructure.
 * 
 */ 
public class InteractiveRanker extends TermRanker {
  public InteractiveRanker(String filepath) {
    super(filepath);
    this.runServer();
  }
  public void runServer() {
    System.out.println("Type a prefix to see top 10 matches; Ctrl-C to exit");
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    while (true) {
      System.out.print("Enter a prefix query: ");
      String prefixString = null;
      try {
        prefixString = in.readLine().trim();
      } catch (IOException e) {
        System.out.println("An IO error occurred.. let us retry");
      }
      if (prefixString != null) { 
        List<TermScore> results = this.query(prefixString);
        if (results.size() == 0) {
          System.out.println("oops.. no matches found. Try another query");
        } else {
          System.out.println(String.format("%s matches found", results.size()));
          for(int i=0;i<results.size(); i++) {
            System.out.println(String.format("%s: \t (%s) \t %s", 
              i, results.get(i).getScore(), results.get(i).getName()));
          }
        }
      }
      System.out.println("");
    }
  }
  
  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("[USAGE] java -cp 3rdparty/protobuf-java-2.5.0.jar:./bin/. com.gtech/server/InteractiveRanker path-to-data-file");
    } else { 
      new InteractiveRanker(args[0]);
    }
  }
}

