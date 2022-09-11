package com.example.demo.service.impl;

import com.example.demo.Payload.MessageResponse;
import com.example.demo.Payload.PasswordResetRequest;
import com.example.demo.entity.Account;
import com.example.demo.entity.VerificationToken;
import com.example.demo.enums.VerificationTypeEnum;
import com.example.demo.exceptions.BadRequestException;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.VerificationTokenRepository;
import com.example.demo.service.AuthenticationService;
import com.example.demo.service.EmailService;
import com.example.demo.util.PasswordValidation;
import com.example.demo.util.TokenValidation;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

  private final AccountRepository accountRepository;
  private final VerificationTokenRepository verificationTokenRepository;
  private final EmailService emailService;
  private final PasswordEncoder passwordEncoder;

  public AuthenticationServiceImpl(
      AccountRepository accountRepository,
      VerificationTokenRepository verificationTokenRepository,
      EmailService emailService,
      PasswordEncoder passwordEncoder) {
    this.accountRepository = accountRepository;
    this.verificationTokenRepository = verificationTokenRepository;
    this.emailService = emailService;
    this.passwordEncoder = passwordEncoder;
  }

  public String createAndSendEmailConfirmationToken(Account account) {
    String token = UUID.randomUUID().toString();

    VerificationToken verificationToken =
        new VerificationToken(
            VerificationTypeEnum.EMAIL_CONFIRMATION,
            token,
            Instant.now().plus(30, ChronoUnit.MINUTES),
            account);
    verificationTokenRepository.save(verificationToken);

    // how to replace localhost by other addresses?
    String link = "http://localhost:8080/api/auth/confirm-email-address?token=" + token;
    String confirmationEmail = emailService.buildConfirmationEmail(account.getUsername(), link);
    emailService.sendEmail(account.getEmail(), "Confirming Email Address", confirmationEmail);
    return "Confirmation email was send";
  }

  public MessageResponse confirmEmailAddress(String token) {
    if (!TokenValidation.isValid(token)) {
      throw new BadRequestException(TokenValidation.rules());
    }
    VerificationToken verificationToken =
        verificationTokenRepository
            .findByToken(token)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "Email Confirmation Token [" + token + "] not found in database."));
    Account account = verificationToken.getAccount();
    account.setEnabled(true);
    accountRepository.save(account);
    verificationToken.setConfirmedAtInUtc(Instant.now());
    verificationTokenRepository.save(verificationToken);
    return new MessageResponse("Email was confirmed and therefore account was activated");
  }

  public MessageResponse resetPassword(String email) {
    Account account =
        accountRepository
            .findByEmail(email)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "Account with email address [" + email + "] not found in database."));
    return createAndSendPasswordResetToken(account);
  }

  public MessageResponse createAndSendPasswordResetToken(Account account) {
    String token = UUID.randomUUID().toString();
    VerificationToken verificationToken =
        new VerificationToken(
            VerificationTypeEnum.PASSWORD_RESET,
            token,
            Instant.now().plus(30, ChronoUnit.MINUTES),
            account);
    verificationToken.setConfirmedAtInUtc(Instant.now());
    verificationTokenRepository.save(verificationToken);

    // how to replace localhost by other addresses? send to frontend form! which saves token in post
    // and adds new password to post request
    String link = "http://localhost:3000/api/auth/reset-password?token=" + token;
    String PasswordResetEmail = emailService.buildPasswordResetEmail(account.getUsername(), link);
    emailService.sendEmail(account.getEmail(), "Reset Password", PasswordResetEmail);
    return new MessageResponse("Email was send successfully");
  }

  public MessageResponse saveNewPassword(PasswordResetRequest request) {
    if (!PasswordValidation.isValid(request.newPassword())) {
      throw new BadRequestException(PasswordValidation.rules());
    }
    VerificationToken verificationToken =
        verificationTokenRepository
            .findByToken(request.token())
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "Password Reset Token [" + request.token() + "] not found in database."));
    Account account = verificationToken.getAccount();
    account.setPassword(passwordEncoder.encode(request.newPassword()));
    accountRepository.save(account);
    return new MessageResponse("New Password was saved");
  }
}
