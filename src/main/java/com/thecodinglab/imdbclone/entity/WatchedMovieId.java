package com.thecodinglab.imdbclone.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import org.hibernate.Hibernate;

@Embeddable
public class WatchedMovieId implements Serializable {

  @NotNull private Long movieId;

  @NotNull private Long accountId;

  public WatchedMovieId() {}

  public WatchedMovieId(long movieId, long accountId) {
    this.movieId = movieId;
    this.accountId = accountId;
  }

  public Long getMovieId() {
    return movieId;
  }

  public void setMovieId(Long movieId) {
    this.movieId = movieId;
  }

  public Long getAccountId() {
    return accountId;
  }

  public void setAccountId(Long accountId) {
    this.accountId = accountId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    WatchedMovieId entity = (WatchedMovieId) o;
    return Objects.equals(this.accountId, entity.accountId)
        && Objects.equals(this.movieId, entity.movieId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(accountId, movieId);
  }

  @Override
  public String toString() {
    return "WatchlistId{" + "movieId=" + movieId + ", accountId=" + accountId + '}';
  }
}
