package com.example.scrapermanager.redditservice;

import com.example.scrapermanager.entity.RedditBot;
import com.example.scrapermanager.service.DataBaseService;
import com.example.scrapermanager.utils.BotStatus;
import lombok.Getter;

import java.io.*;
import java.util.HashMap;

@Getter
public class RedditSenderRunnable implements Runnable, Comparable {

  private DataBaseService dataBaseService;
  private RedditBot redditBot;
  private static boolean flag = true;

  public RedditSenderRunnable(RedditBot redditBot, DataBaseService dataBaseService) {
    this.redditBot = redditBot;
    this.dataBaseService = dataBaseService;
  }

  public void createMessagedFile(String projectFolderPath) throws IOException {
    File file = new File(projectFolderPath + "/messaged.txt");
    Writer output = new BufferedWriter(new FileWriter(file));
    output.close();
  }

  public void createMessageFile(String projectFolderPath, String text) throws IOException {
    File file = new File(projectFolderPath + "/messages.txt");
    Writer output = new BufferedWriter(new FileWriter(file));
    output.write(text);
    output.close();
  }

  public void createEnvFile(String projectFolderPath, HashMap<String, String> map)
      throws IOException {
    File file = new File(projectFolderPath + "/.env");
    Writer output = new BufferedWriter(new FileWriter(file));
    for (String key : map.keySet()) {
      output.append(key + "=" + map.get(key) + "\n");
    }
    output.close();
  }

  public void createDataFile(String projectFolderPath) throws IOException {
    File file = new File(projectFolderPath + "/data.csv");
    Writer output = new BufferedWriter(new FileWriter(file));
    int count = 0;
    String userNames = "";

    for (int i = 0; i < redditBot.getUserNames().size(); i++) {
      userNames = userNames + redditBot.getUserNames().get(i).getUserName() + ",\n";
    }

    if (userNames.length() != 0) {
      userNames = userNames.substring(0, userNames.length() - 1);
    }
    output.append(userNames);
    output.close();
  }

  public void commandRunner(String mod) throws IOException {

    String[] failedKeywords = new String[] {"Can't", "Already", "Failed", "Chat"};
    String[] criticalError = new String[] {"File"};
    int count = 0;
    ProcessBuilder builder =
        new ProcessBuilder(
            "cmd.exe", "/c", "cd " + redditBot.getProjectPath() + " && " + redditBot.getUserAgent()+" dm_bot.py " + mod);
    Process p = builder.start();
    BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
    String line;
    while (true) {
      line = r.readLine();
      if (line == null) {
        break;
      }
      System.out.println(line);
      for (int i = 0; i < failedKeywords.length; i++) {
        if(line.contains(failedKeywords[i])){
          redditBot.setNotSent(redditBot.getNotSent()+1);
          break;
        }
      }
      for (int i = 0; i < criticalError.length; i++) {
        if(line.contains(failedKeywords[i])){
          redditBot.setNotSent(redditBot.getNotSent()+1);
          break;
        }
      }
    }
  }

  @Override
  public void run() {
    dataBaseService.updateBotStatus(redditBot.getId(), BotStatus.RUNNING);
    try {
      if (flag) {

        flag = false;
        createMessageFile(redditBot.getProjectPath(), redditBot.getMessage());
        createDataFile(redditBot.getProjectPath());
        HashMap<String, String> map = new HashMap<>();
        map.put("CLIENT_ID", redditBot.getClientId());
        map.put("CLIENT_SECRET", redditBot.getClientSecret());
        map.put("PASSWORD", redditBot.getPassword());
        map.put("USERNAME", redditBot.getLoginUserName());
        map.put("USER_AGENT", redditBot.getUserAgent());
        createEnvFile(redditBot.getProjectPath(), map);

        flag = true;
        commandRunner(redditBot.isPm() == true ? "pm" : "dm");
        dataBaseService.updateBotStatus(redditBot.getId(), BotStatus.SUCCESSED);
        Thread.currentThread().stop();

      } else {
        try {
          Thread.sleep(5000);
          run();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    } catch (IOException e) {

      dataBaseService.updateBotStatus(redditBot.getId(), BotStatus.FAILED);
      Thread.currentThread().stop();
      e.printStackTrace();
    } finally {
      flag = true;
    }
  }

  @Override
  public int compareTo(Object o) {
    return 0;
  }
}
