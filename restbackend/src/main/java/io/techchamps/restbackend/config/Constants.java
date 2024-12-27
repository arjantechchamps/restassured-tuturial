package io.techchamps.restbackend.config;

import java.util.List;

public class Constants {

  private Constants(){
    // hide default constructor
  }

  public static final String DEFAULT_PASSWORD ="Welkom001";

  public static final List<String> ALLOWED_ORIGINS = List.of("http://localhost:3000");

  public static final List<String> ALLOWED_METHODS = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS");

  public static final List<String> ALLOWED_HEADERS = List.of("*");

  public static final List<String> ALLOWED_EXPOSED_HEADERS = List.of("Authorization", "Content-Type");
}
