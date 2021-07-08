package com.example.scrapermanager.service;

import com.example.scrapermanager.entity.RedditBot;
import com.example.scrapermanager.entity.RedditUser;
import com.example.scrapermanager.redditservice.RedditSenderRunnable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import sun.misc.Queue;

@Service
@Qualifier("DataBaseService")
public class DataBaseService {

  public static List<RedditBot> redditBotList;
  public static List<RedditUser> redditUserList;
  public static long redditUserId=0;
  public static long redditBotId=0;


  @PostConstruct
  public void init(){
    DataBaseService.redditBotList=new ArrayList<>();
    DataBaseService.redditUserList=new ArrayList<>();
  }

  public void addRedditUser(RedditUser user){
    redditUserList.add(user);
  }

  public void addRedditBot(RedditBot bot){
    redditBotList.add(bot);
  }

  public List<RedditUser> getAllRedditUser(){
    return redditUserList;
  }

  public List<RedditBot> getRedditBotList(){
    return redditBotList;
  }

}
