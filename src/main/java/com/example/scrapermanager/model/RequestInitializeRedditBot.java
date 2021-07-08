package com.example.scrapermanager.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@NoArgsConstructor
public class RequestInitializeRedditBot {

  @JsonProperty("isPm")
  @NonNull
  private boolean isPm;
  @NonNull
  private String botName;
  @NonNull
  private String message;
  @NonNull
  private List<String> userNames;
  @JsonProperty("isOverWrite")
  @NonNull
  private boolean isOverWrite;

}
