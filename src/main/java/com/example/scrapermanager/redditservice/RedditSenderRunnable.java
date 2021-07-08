package com.example.scrapermanager.redditservice;

import com.example.scrapermanager.entity.RedditBot;
import com.example.scrapermanager.service.DataBaseService;
import com.example.scrapermanager.utils.BotStatus;
import lombok.Getter;

@Getter
public class RedditSenderRunnable implements Runnable,Comparable {

  private RedditBot redditBot;
  private String name;

  public RedditSenderRunnable(RedditBot redditBot,String name){
    this.redditBot=redditBot;
    this.name=name;
  }

  @Override
  public void run() {

    for(int i=0;i<=60;i++){
      System.out.println(name);
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    Thread.currentThread().destroy();
    redditBot.setStatus(BotStatus.SUCCESSED);
  }

  @Override
  public int compareTo(Object o) {
    return 0;
  }
}
