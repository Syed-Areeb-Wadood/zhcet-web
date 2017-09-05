package in.ac.amu.zhcet.data.service.token;

import in.ac.amu.zhcet.data.model.base.user.UserAuth;
import in.ac.amu.zhcet.data.model.token.VerificationToken;
import in.ac.amu.zhcet.data.repository.VerificationTokenRepository;
import in.ac.amu.zhcet.data.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.UUID;

@Slf4j
@Service
public class EmailVerificationService {

    private final UserService userService;
    private final EmailService emailService;
    private final VerificationTokenRepository verificationTokenRepository;

    @Autowired
    public EmailVerificationService(UserService userService, EmailService emailService, VerificationTokenRepository verificationTokenRepository) {
        this.userService = userService;
        this.emailService = emailService;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    private VerificationToken createVerificationToken(String email) {
        UserAuth user = userService.getLoggedInUser();
        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken(user, token, email);
        verificationTokenRepository.save(verificationToken);

        return verificationToken;
    }

    public VerificationToken generate(String email) {
        UserAuth userAuth = userService.getUserByEmail(email);
        if (userAuth != null && !userAuth.getEmail().equals(userService.getLoggedInUser().getEmail()))
            throw new DuplicateEmailException(email);

        return createVerificationToken(email);
    }

    public String validate(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);

        if (verificationToken == null)
            return "Token: "+ token +" is invalid";

        if (verificationToken.isUsed())
            return "Token: "+ token +" is already used! Please request another link!";

        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiry().getTime() - cal.getTime().getTime()) <= 0) {
            return "Token: "+token+" has expired";
        }

        return null;
    }

    public void confirmEmail(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        verificationToken.setUsed(true);
        verificationTokenRepository.save(verificationToken);

        UserAuth userAuth = verificationToken.getUser();
        userAuth.setEmail(verificationToken.getEmail());
        userAuth.setActive(true);
        userService.save(userAuth);
    }

    public void sendMail(String appUrl, VerificationToken token) {
        String url = appUrl + "/login/verify?token=" + token.getToken();
        log.info("Verification link generated : " + url);
        String message = "You requested email verification on zhcet for user ID: " + token.getUser().getUserId() + "\r\n" +
                "Please click this link to verify your email ID. \r\n" + url;

        emailService.sendMail(token.getEmail(), "ZHCET Email Verification", message);
    }

}