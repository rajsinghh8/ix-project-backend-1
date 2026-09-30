package com.eventforge.common;

import com.eventforge.domain.User;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CurrentUser {
 public UUID id(){
  Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
  if(authentication==null || !authentication.isAuthenticated()) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Authentication required");
  Object principal=authentication.getPrincipal();
  if(principal instanceof User user) return user.getId();
  try{return UUID.fromString(authentication.getName());}catch(IllegalArgumentException ex){throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Authenticated user id is unavailable");}
 }
}
