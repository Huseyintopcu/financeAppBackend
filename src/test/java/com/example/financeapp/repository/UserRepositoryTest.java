package com.example.financeapp.repository;

import com.example.financeapp.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private final String testEmail = "user@finance.com";
    private final String testPassword = "securePassword123";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        // 1. Kullanıcı: Hedef test kullanıcımız (FCM token'ı var)
        User user1 = new User();
        user1.setEmail(testEmail);
        user1.setPassword(testPassword);
        user1.setFcmToken("fcm_token_12345");

        // 2. Kullanıcı: Karışıklık kontrolü için farklı bir kullanıcı (FCM token'ı yok/null olabilir)
        User user2 = new User();
        user2.setEmail("other@finance.com");
        user2.setPassword("anotherPassword");
        user2.setFcmToken(null); // fcmToken nullable = false olmadığı için null olabilir

        userRepository.saveAll(List.of(user1, user2));
    }

    @Test
    void findByEmail_eposta_ile_kullaniciyi_dogru_bulmali() {
        // When: Test e-postası ile sorgulama yap
        Optional<User> foundUser = userRepository.findByEmail(testEmail);

        // Then: Kullanıcı bulunmalı, şifresi ve fcmToken'ı eşleşmeli
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getPassword()).isEqualTo(testPassword);
        assertThat(foundUser.get().getFcmToken()).isEqualTo("fcm_token_12345");
    }

    @Test
    void findByEmail_olmayan_eposta_icin_empty_donmeli() {
        // When: Veritabanında kayıtlı olmayan bir adresi sorgula
        Optional<User> foundUser = userRepository.findByEmail("olmayan@user.com");

        // Then: Optional nesnesi boş dönmeli
        assertThat(foundUser).isEmpty();
    }

    @Test
    void deleteByEmail_kullaniciyi_ve_ona_ait_bilgileri_silmeli() {
        // When: Özel yazılan deleteByEmail sorgusunu tetikle
        userRepository.deleteByEmail(testEmail);

        // Then: Hedef kullanıcının kaydı silinmiş olmalı
        Optional<User> deletedUser = userRepository.findByEmail(testEmail);
        assertThat(deletedUser).isEmpty();

        // Geriye sadece diğer 1 kullanıcı kalmış olmalı
        List<User> remainingUsers = userRepository.findAll();
        assertThat(remainingUsers).hasSize(1);
        assertThat(remainingUsers.get(0).getEmail()).isEqualTo("other@finance.com");
    }
}

