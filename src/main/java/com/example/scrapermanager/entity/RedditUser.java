package com.example.scrapermanager.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RedditUser {

  private long id;
  private String userName;
  private RedditBot redditBot;
  private boolean isSent;

}
