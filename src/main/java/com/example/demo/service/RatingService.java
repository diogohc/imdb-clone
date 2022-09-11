package com.example.demo.service;

import com.example.demo.Payload.MessageResponse;
import com.example.demo.entity.Rating;
import com.example.demo.security.UserPrincipal;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface RatingService {

  Rating rateMovie(UserPrincipal currentAccount, Long movieId, BigDecimal score);

  List<Rating> getRatingsByAccount(UserPrincipal currentAccount);

  MessageResponse deleteRating(UserPrincipal currentAccount, Long movieId);
}
