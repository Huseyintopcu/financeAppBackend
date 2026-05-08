package com.example.financeapp.service;

import com.example.financeapp.dto.*;
import com.example.financeapp.entity.Otp;
import com.example.financeapp.entity.User;
import com.example.financeapp.repository.OtpRepository;
import com.example.financeapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private  JwtService jwtService;


    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // REGISTER
    public RegisterResponse register(RegisterRequest request) {

        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {
            return new RegisterResponse(false, "Email Zaten Kullanılıyor");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        userRepository.save(user);

        return new RegisterResponse(true, "Kayıt Başarılı");
    }


    public String generateCode()
    {
        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }

    public  String sendOtp(String email)
    {
        otpRepository.deleteByEmail(email);
        String code = generateCode();

        Otp otp = new Otp();
        otp.setEmail(email);
        otp.setCode(code);
        otp.setExpireTime(LocalDateTime.now().plusMinutes(5));

        otpRepository.save(otp);
        emailService.sendOtp(email,code);

        return "Kod Gönderildi";
    }

    public boolean verifyOtp(String email, String code) {


        Optional<Otp> otp = otpRepository.findByEmail(email);

        if (otp.isEmpty()) return false;

        if (otp.get().getExpireTime().isBefore(LocalDateTime.now()))
            return false;

        boolean valid = otp.get().getCode().trim().equals(code.trim());

        if (valid)
        {
            otpRepository.deleteByEmail(email);
        }


        return valid;
    }

    // LOGIN
    public LoginResponse login(LoginRequest request) {

        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            return new LoginResponse(null, false);
        }

        User user = userOpt.get();

        if (!user.getPassword().equals(request.getPassword())) {
            return new LoginResponse(null, false);
        }

        String token = jwtService.generateToken(user.getEmail());

        return new LoginResponse(token, true);
    }
}
