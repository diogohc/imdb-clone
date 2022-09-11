package com.example.demo.service;

import com.example.demo.Payload.AccountSummaryResponse;
import com.example.demo.Payload.CreateAccountRequest;
import com.example.demo.Payload.MessageResponse;
import com.example.demo.entity.Account;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Rating;
import com.example.demo.entity.WatchedMovie;
import com.example.demo.security.UserPrincipal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface AccountService {

  AccountSummaryResponse getCurrentAccount(UserPrincipal currentAccount);

  Account getProfile(String username, UserPrincipal currentAccount);

  List<Comment> getCommentsByAccount(String username, UserPrincipal currentAccount);

  List<WatchedMovie> getWatchedMoviesByAccount(String username, UserPrincipal currentAccount);

  List<Rating> getRatingsByAccount(String username, UserPrincipal currentAccount);

  Account updateAccount(String username, UserPrincipal currentAccount);

  MessageResponse deleteAccount(String username, UserPrincipal currentAccount);

  Account createAccount(CreateAccountRequest request, UserPrincipal currentAccount);

  MessageResponse giveAdminRole(String username, UserPrincipal currentAccount);

  MessageResponse takeAdminRole(String username, UserPrincipal currentAccount);
}
