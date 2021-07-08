package com.example.scrapermanager.service;

import com.example.scrapermanager.entity.RedditBot;
import com.example.scrapermanager.entity.RedditUser;
import com.example.scrapermanager.model.RequestInitializeRedditBot;
import com.example.scrapermanager.redditservice.RedditSenderRunnable;
import com.example.scrapermanager.utils.BotStatus;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("RedditService")
public class RedditService {

  @Autowired
  @Qualifier("DataBaseService")
  DataBaseService dataBaseService;

  private ScheduledExecutorService scheduledExecutorService= Executors.newScheduledThreadPool(4);
  public static Queue<RedditBot> pendingRedditBot;
  public static List<RedditSenderRunnable> redditRunningBots;

  @PostConstruct
  public void init(){
    pendingRedditBot=new PriorityQueue();
    redditRunningBots=new ArrayList<>();
    scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
      @Override
      public void run() {
        for(RedditSenderRunnable redditSenderRunnable:redditRunningBots){
          if(redditSenderRunnable.getRedditBot().getStatus()!=BotStatus.RUNNING){
            redditRunningBots.remove(redditSenderRunnable);
          }
        }

        if(redditRunningBots.size()<=4 && redditRunningBots.size()!=0){
          RedditSenderRunnable redditSenderRunnable=new RedditSenderRunnable(pendingRedditBot.poll(),"aaaa");
          redditRunningBots.add(redditSenderRunnable);
          scheduledExecutorService.scheduleWithFixedDelay(redditSenderRunnable,0,1,TimeUnit.SECONDS);
        }
      }
    },0,5, TimeUnit.SECONDS);
  }

  public void createRedditBot(RequestInitializeRedditBot requestInitializeRedditBot) {
    RedditBot redditBot = new RedditBot();
    redditBot.setBotName(requestInitializeRedditBot.getBotName());
    redditBot.setId(DataBaseService.redditBotId++);
    redditBot.setPm(requestInitializeRedditBot.isPm());
    redditBot.setMessage(requestInitializeRedditBot.getMessage());
    redditBot.setStatus(BotStatus.STOPPED);
    redditBot.setUserNames(new ArrayList<>());
    redditBot.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:sss z").format(new Date(System.currentTimeMillis())));
    dataBaseService.addRedditBot(redditBot);
    if (requestInitializeRedditBot.isOverWrite() || dataBaseService.getAllRedditUser().size()!=0) {
      for (String userName : requestInitializeRedditBot.getUserNames()) {
        RedditUser redditUser = new RedditUser(DataBaseService.redditUserId++, userName, redditBot,
            false);
        dataBaseService.addRedditUser(redditUser);
        redditBot.getUserNames().add(redditUser);
      }
    } else {
      for (String userName : requestInitializeRedditBot.getUserNames()) {
        for (RedditUser redditUser : dataBaseService.getAllRedditUser()) {
          if (redditUser.getUserName() != userName){
            RedditUser newRedditUser = new RedditUser(DataBaseService.redditUserId++, userName, redditBot,
                false);
            dataBaseService.addRedditUser(newRedditUser);
            redditBot.getUserNames().add(newRedditUser);

          }else{
            redditBot.getUserNames().add(redditUser);
          }
        }
      }
    }

  }

  public List<Long> getAllRedditBotId(){
    List<Long> redditBotId= new ArrayList<>();

    for(RedditBot user : dataBaseService.getRedditBotList()){
      redditBotId.add(user.getId());
    }
    return redditBotId;
  }

  //success, failed, stopped, pending

  public void runBot(long id,String name){
    RedditBot bot=null;
    for (RedditBot redditBot: dataBaseService.getRedditBotList()){
      if(redditBot.getId()==id){
        bot=redditBot;
      }
    }
    if(bot!=null && (bot.getStatus()==BotStatus.STOPPED||bot.getStatus()==BotStatus.FAILED)){
      if(redditRunningBots.size()<=4){
        RedditSenderRunnable redditSenderRunnable=new RedditSenderRunnable(bot,name);
        redditRunningBots.add(redditSenderRunnable);
        scheduledExecutorService.scheduleWithFixedDelay(redditSenderRunnable,0,1, TimeUnit.SECONDS);
      }
    }
  }


  public void fillModelRedditBotData(List<Map<String, Object>> modelData) {
    for(RedditBot redditBot : dataBaseService.getRedditBotList()){
      Map<String,Object> object=new HashMap<>();
      int sent=0;
      object.put("id",redditBot.getId());
      object.put("botName",redditBot.getBotName());
      object.put("createdDate",redditBot.getCreatedDate());


      for(RedditUser user: redditBot.getUserNames()){
        if(user.isSent()){
          sent++;
        }
      }
      object.put("sent",sent);
      object.put("notSent",redditBot.getUserNames().size()-sent);
      object.put("total",redditBot.getUserNames().size());
      object.put("status",redditBot.getStatus());
      modelData.add(object);
    }
  }
}
