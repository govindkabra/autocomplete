package com.gtech.server;


import com.gtech.model.TermScoreProtos.TermScore;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

/*
 * This class provides interactive ranking on a hosted web service. 
 * It uses city.html page, with AJAX calls to interactively display matching city names.
 * The server uses TermRanker class to find best matches for given query and returns results in json format.
 */
public class WebserviceRanker  extends AbstractHandler {
  TermRanker tr;
  public WebserviceRanker(String filepath) {
    tr = new TermRanker(filepath);
  }
   
  private JSONObject createJSON(List<TermScore> termScores) {
    JSONObject results = new JSONObject();
    results.accumulate("results", new JSONArray());
    for(TermScore ts: termScores) {
      JSONObject result = new JSONObject();
      result.put("name", ts.getName());
      result.put("score", ts.getScore());
      results.accumulate("results", result);
    }
    return results;
  }

  private void sendJSON(HttpServletResponse response, JSONObject json) {
	  response.setContentType("text/x-json;charset=UTF-8");           
	  response.setHeader("Cache-Control", "no-cache");
	  try {
		  json.write(response.getWriter());
	  } catch (IOException e) {
		  e.printStackTrace();
	  }                               
  }

  public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    if (target.trim().equals("/search")) {
      baseRequest.setHandled(true);
      String query = request.getParameter("query");
      if (query == null) {
        query = "";
      }
      List<TermScore> termScores = tr.query(query);
      JSONObject responseJson = createJSON(termScores);
      System.out.println("Done processing " + responseJson.toString());
      sendJSON(response, createJSON(termScores)); 
    }
  }

  public static void main(String[] args) throws Exception {
    ResourceHandler resource_handler = new ResourceHandler();
    resource_handler.setDirectoriesListed(true);
    resource_handler.setWelcomeFiles(new String[]{ "index.html" });
    resource_handler.setResourceBase("./static/");

    WebserviceRanker ranker = new WebserviceRanker(args[0]);

    HandlerList handlers = new HandlerList();
    handlers.setHandlers(new Handler[] { resource_handler, ranker});

    Server server = new Server(8081);
    server.setHandler(handlers);
    server.start();
    server.join();
  }
}
