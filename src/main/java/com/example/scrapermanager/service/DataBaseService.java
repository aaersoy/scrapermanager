package com.example.scrapermanager.service;

import com.example.scrapermanager.entity.RedditBot;
import com.example.scrapermanager.entity.RedditUser;
import com.example.scrapermanager.redditservice.RedditSenderRunnable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;

import com.example.scrapermanager.utils.BotStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


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

  public void updateBotStatus(Long id, BotStatus status){
    for (int i = 0; i < redditBotList.size(); i++) {
      if(redditBotList.get(i).getId()==id){
        redditBotList.get(i).setStatus(status);
        break;
      }
    }
  }

  public void updateNotSentCount(Long id, int count){
    for (int i = 0; i < redditBotList.size(); i++) {
      if(redditBotList.get(i).getId()==id){
        redditBotList.get(i).setNotSent(count);
        break;
      }
    }
  }

  public void updateBot(Long id, RedditBot redditBot){
    for (int i = 0; i < redditBotList.size(); i++) {
      if(redditBotList.get(i).getId()==id){
        redditBotList.remove(redditBotList.get(i));
        redditBotList.add(redditBot);
        break;
      }
    }
  }

}
