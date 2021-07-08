package com.example.scrapermanager.entity;


import com.example.scrapermanager.utils.BotStatus;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RedditBot {

  private long id;
  private String botName;
  private List<RedditUser> userNames;
  private boolean isPm;
  private BotStatus status;
  private String createdDate;
  private String message;

}
