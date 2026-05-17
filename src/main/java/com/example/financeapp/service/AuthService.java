package com.example.financeapp.service;

import com.example.financeapp.dto.*;
import com.example.financeapp.entity.Otp;
import com.example.financeapp.entity.User;
import com.example.financeapp.repository.OtpRepository;
import com.example.financeapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;


    public AuthService(
            UserRepository userRepository,
            OtpRepository otpRepository,
            EmailService emailService,
            JwtService jwtService,
            PasswordEncoder passwordEncoder
            )
    {
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
        this.emailService = emailService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

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

    public  String sendOtp(String email)
    {
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
            return new LoginResponse(null, false);
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(request.getPassword(),user.getPassword()))
        {
            return new LoginResponse(null, false);
        }

        String token = jwtService.generateToken(user.getEmail());

        return new LoginResponse(token, true);
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

}
