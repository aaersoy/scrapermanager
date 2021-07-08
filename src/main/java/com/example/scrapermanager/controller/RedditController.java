package com.example.scrapermanager.controller;


import com.example.scrapermanager.model.RequestInitializeRedditBot;
import com.example.scrapermanager.service.DataBaseService;
import com.example.scrapermanager.service.RedditService;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.*;
import javax.validation.Valid;
import jdk.internal.util.xml.impl.ReaderUTF8;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class RedditController {


  @Autowired
  @Qualifier("RedditService")
  RedditService redditService;

  @Autowired
  @Qualifier("DataBaseService")
  DataBaseService dataBaseService;


  @RequestMapping("/")
  public ModelAndView index(){
    return currentValues();
  }


  public ModelAndView currentValues(){

    try {
      ModelAndView model = new ModelAndView("index");
      Map<String,Object> modelData=new HashMap<>();
      List<Map<String,Object>> bots=new ArrayList<>();
      redditService.fillModelRedditBotData(bots);

      modelData.put("redditBots",bots);
      model.addObject("modelData",modelData);

      return model;
    } catch (Exception e) {
      return errorPage("An error was occured.");
    }
  }

  public ModelAndView errorPage(String message){
    ModelAndView errorModel = new ModelAndView("error");
    errorModel.addObject("message", message);
    return errorModel;
  }

  @PostMapping(value="/initializeredditbot")
  public ResponseEntity<Object> initializeRedditBot(@Valid@RequestBody RequestInitializeRedditBot requestInitializeRedditBot){
    redditService.createRedditBot(requestInitializeRedditBot);
    return new ResponseEntity<>(HttpStatus.ACCEPTED);
  }

  @GetMapping(value="/getallredditbotid")
  public ResponseEntity<Object> getAllRedditBotId() throws IOException {
    Runtime runtime = Runtime.getRuntime();
    String[] commands  = {"python","devices"};
    Process process = runtime.exec(commands);


    System.out.println("****INFO*****");
    BufferedReader lineReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    lineReader.lines().forEach(System.out::println);




    System.out.println("****ERROR*****");
    BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
    errorReader.lines().forEach(System.out::println);

    return new ResponseEntity<Object>(redditService.getAllRedditBotId(),HttpStatus.ACCEPTED);
  }

  @RequestMapping("/runredditbot")
  public void runBot(@RequestParam Long id,String name){
    redditService.runBot(id,name);
  }

}
