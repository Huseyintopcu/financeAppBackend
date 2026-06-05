package com.example.financeapp.service;

import com.example.financeapp.dto.*;
import com.example.financeapp.entity.Otp;
import com.example.financeapp.entity.User;
import com.example.financeapp.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;
    private final BillRepository billRepository;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    // REGISTER
    public RegisterResponse register(RegisterRequest request) {

        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {
            return new RegisterResponse(false, "Email Zaten Kullanılıyor");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        return new RegisterResponse(true, "Kayıt Başarılı");
    }


    public String generateCode()
    {
        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }

    @Transactional
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

    // VERIFY OTP
    @Transactional
    public VerifyOtpResponse verifyOtp(VerifyOtpRequest request)
    {
        Optional<Otp> otp = otpRepository.findByEmail(request.getEmail());

        if (otp.isEmpty()) return new VerifyOtpResponse(false,"Kullanıcı Bulunamadı");

        if (otp.get().getExpireTime().isBefore(LocalDateTime.now()))
            return new VerifyOtpResponse(false,"Kodun Süresi Geçmiş");

        boolean valid = otp.get().getCode().trim().equals(request.getCode().trim());


        if (valid)
        {
            otpRepository.deleteByEmail(request.getEmail());
            return new VerifyOtpResponse(true,"Kod Doğrulandı");
        }

        return new VerifyOtpResponse(false,"Kod Yanlış");
    }

    // LOGIN
    public LoginResponse login(LoginRequest request) {

        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            throw new org.springframework.security.authentication.BadCredentialsException("E-posta veya şifre hatalı");
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(request.getPassword(),user.getPassword()))
        {
            throw new org.springframework.security.authentication.BadCredentialsException("E-posta veya şifre hatalı");
        }

        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        return new LoginResponse(accessToken, refreshToken,true);
    }

    // Save FCM token to data table
    public boolean  updateFcmToken(FcmTokenRequest request)
    {
        var userOptinal = userRepository.findByEmail(request.getEmail());

        if (userOptinal.isPresent()) {
            var user = userOptinal.get();
            user.setFcmToken(request.getToken());
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // Access token refresh
    public LoginResponse refresh(RefreshRequest request)
    {

        if (request.getRefreshToken() != null && jwtService.validateRefreshToken(request.getRefreshToken()))
        {

            String email = jwtService.extractEmail(request.getRefreshToken());


            String newAccessToken = jwtService.generateAccessToken(email);
            String newRefreshToken = jwtService.generateRefreshToken(email);

            return new LoginResponse(newAccessToken, newRefreshToken, true);
        }

        return new LoginResponse(null, null, false);
    }

    // RESET PASSWORD
    public ResetPasswordResponse resetPassword(ResetPasswordRequest request)
    {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty())
        {
            return new ResetPasswordResponse(false,"Kullanıcı Bulunamadı");
        }

        User user = userOpt.get();

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        return new ResetPasswordResponse(true,"Şifre Güncellendi");
    }

    // Delete account
    @Transactional
    public void deleteAccount(String email)
    {
        System.out.println("DELETING USER: " + email);
        expenseRepository.deleteByUserEmail(email);
        incomeRepository.deleteByUserEmail(email);
        billRepository.deleteByUserEmail(email);

        userRepository.deleteByEmail(email);
    }
}
