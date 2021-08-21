package com.example.scrapermanager.service;

import com.example.scrapermanager.entity.RedditBot;
import com.example.scrapermanager.entity.RedditUser;
import com.example.scrapermanager.model.RequestInitializeRedditBot;
import com.example.scrapermanager.redditservice.RedditSenderRunnable;
import com.example.scrapermanager.utils.BotStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
@Qualifier("RedditService")
public class RedditService {

  @Autowired
  @Qualifier("DataBaseService")
  DataBaseService dataBaseService;

  private ScheduledExecutorService scheduledExecutorService =
      Executors.newScheduledThreadPool(1000);
  public static Queue<RedditSenderRunnable> pendingRedditBot;
  public static List<RedditSenderRunnable> redditRunningBots;
  public static HashMap<Long, Date> lastRun;

  @PostConstruct
  public void init() {
    //    pendingRedditBot = new PriorityQueue();
    //    redditRunningBots = new ArrayList<>();
    //    lastRun= new HashMap<>();
    //
    //    scheduledExecutorService.scheduleWithFixedDelay(
    //        new Runnable() {
    //          public void mainPurpose() {
    //            for (RedditSenderRunnable redditSenderRunnable : redditRunningBots) {
    //              if (redditSenderRunnable.getRedditBot().getStatus() != BotStatus.RUNNING) {
    //                if (redditSenderRunnable.getRedditBot().getStatus() == BotStatus.PENDING) {
    //                  pendingRedditBot.add(redditSenderRunnable);
    //                }
    //                redditRunningBots.remove(redditSenderRunnable);
    //              }
    //            }
    //
    //            if (redditRunningBots.size() <= 4 && pendingRedditBot.size() != 0) {
    //              boolean flag = false;
    //              RedditSenderRunnable redditSenderRunnable = pendingRedditBot.poll();
    //              Date now = new Date();
    //              System.out.println(now);
    //              if (!lastRun.keySet().contains(redditSenderRunnable.getRedditBot().getId())) {
    //                flag = true;
    //                lastRun.put(redditSenderRunnable.getRedditBot().getId(), now);
    //
    // System.out.println(lastRun.get(redditSenderRunnable.getRedditBot().getId()).getTime()-
    // now.getTime());
    //              } else {
    //                if (lastRun.get(redditSenderRunnable.getRedditBot().getId()).getTime() -
    // now.getTime()
    //                    >= new Random().nextInt() % 3 + 3) {
    //                  lastRun.replace(redditSenderRunnable.getRedditBot().getId(), now);
    //                  flag = true;
    //                } else {
    //                  pendingRedditBot.add(redditSenderRunnable);
    //                }
    //              }
    //              if (flag) {
    //                redditSenderRunnable.getRedditBot().setStatus(BotStatus.RUNNING);
    //
    // scheduledExecutorService.schedule(redditSenderRunnable,0,TimeUnit.MILLISECONDS);
    //                System.out.println(pendingRedditBot.size());
    //                redditRunningBots.add(redditSenderRunnable);
    //              }
    //            }
    //          }
    //
    //          @Override
    //          public void run() {
    //            try {
    //              mainPurpose();
    //            } catch (Exception e) {
    //              run();
    //            }
    //          }
    //        },
    //        0,
    //        5,
    //        TimeUnit.SECONDS);
  }

  public void createRedditBot(RequestInitializeRedditBot requestInitializeRedditBot) {
    RedditBot redditBot = new RedditBot();
    redditBot.setBotName(requestInitializeRedditBot.getBotName());
    redditBot.setId(DataBaseService.redditBotId++);
    redditBot.setPm(requestInitializeRedditBot.isPm());
    redditBot.setMessage(requestInitializeRedditBot.getMessage());
    redditBot.setStatus(BotStatus.STOPPED);
    redditBot.setUserNames(new ArrayList<>());
    redditBot.setCreatedDate(
        new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:sss z")
            .format(new Date(System.currentTimeMillis())));
    redditBot.setClientId(requestInitializeRedditBot.getClientId());
    redditBot.setClientSecret(requestInitializeRedditBot.getClientSecret());
    redditBot.setPassword(requestInitializeRedditBot.getRedditPassword());
    redditBot.setLoginUserName(requestInitializeRedditBot.getLoginUserName());
    redditBot.setUserAgent(requestInitializeRedditBot.getUserAgent());
    redditBot.setProjectPath(requestInitializeRedditBot.getProjectPath());
    redditBot.setTotalRequestSize(requestInitializeRedditBot.getUserNames().size());
    redditBot.setNotSent(0);
    dataBaseService.addRedditBot(redditBot);

    int count = 0;
    for (String userName : requestInitializeRedditBot.getUserNames()) {
      boolean flag = false;
      for (RedditUser redditUser : dataBaseService.getAllRedditUser()) {
        if (redditUser.getUserName().equals(userName)) {
          flag = true;
          break;
        }
      }
      if (!flag) {
        RedditUser newRedditUser =
            new RedditUser(DataBaseService.redditUserId++, userName, redditBot);
        dataBaseService.addRedditUser(newRedditUser);
        redditBot.getUserNames().add(newRedditUser);
      }
    }
    redditBot.setNotSent(
        requestInitializeRedditBot.getUserNames().size() - redditBot.getUserNames().size());
    dataBaseService.updateBot(redditBot.getId(), redditBot);
  }

  public List<Long> getAllRedditBotId() {
    List<Long> redditBotId = new ArrayList<>();

    for (RedditBot user : dataBaseService.getRedditBotList()) {
      redditBotId.add(user.getId());
    }
    return redditBotId;
  }

  public void runBot(long id) {
    RedditBot bot = null;
    for (RedditBot redditBot : dataBaseService.getRedditBotList()) {
      if (redditBot.getId() == id) {
        bot = redditBot;
      }
    }
    if (bot != null
        && (bot.getStatus() == BotStatus.STOPPED || bot.getStatus() == BotStatus.FAILED)) {
      RedditSenderRunnable redditSenderRunnable = new RedditSenderRunnable(bot, dataBaseService);
      scheduledExecutorService.execute(redditSenderRunnable);
    }
  }

  public void fillModelRedditBotData(List<Map<String, Object>> modelData) {
    for (RedditBot redditBot : dataBaseService.getRedditBotList()) {
      Map<String, Object> object = new HashMap<>();
      int sent = 0;
      object.put("id", redditBot.getId());
      object.put("botName", redditBot.getBotName());
      object.put("createdDate", redditBot.getCreatedDate());

      if(redditBot.getStatus()==BotStatus.SUCCESSED){
        object.put("sent", redditBot.getTotalRequestSize() - redditBot.getNotSent());
      }else{
        object.put("sent",0);
      }

      object.put("notSent", redditBot.getNotSent());
      object.put("total", redditBot.getTotalRequestSize());
      object.put("status", redditBot.getStatus());
      modelData.add(object);
    }
  }

  public void deleteBot(Long id) throws IOException {
    RedditBot bot = null;

    for (RedditBot redditBot : dataBaseService.getRedditBotList()) {
      if (redditBot.getId() == id) {
        List<String> eachMessaged = new ArrayList<>();
        String messaged = "";
        File file = new File(redditBot.getProjectPath() + "/messaged.txt");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String str;
        str = bufferedReader.readLine();
        while (str != null) {
          eachMessaged.add(str);
          str = bufferedReader.readLine();
        }
        bufferedReader.close();

        for (RedditUser redditUser : redditBot.getUserNames()) {
          dataBaseService.getAllRedditUser().remove(redditUser);
          for (String messagedUser : eachMessaged) {
            if (messagedUser.equals(redditUser.getUserName())) {
              eachMessaged.remove(messagedUser);
              break;
            }
          }
        }
        for (String messagedUser : eachMessaged) {
          messaged = messaged + messagedUser + "\n";
        }
        if (messaged.length() != 0) {
          messaged = messaged.substring(0, messaged.length() - 1);
        }
        Writer writer = new BufferedWriter(new FileWriter(file));
        writer.append(messaged);
        writer.close();
        dataBaseService.getRedditBotList().remove(redditBot);
        break;
      }
    }
  }
}
