package com.example.financeapp.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initializedFirebase() {
        try {
            InputStream serviceAccount;

            String firebaseKeyEnv = System.getenv("FIREBASE_KEY");

            if (firebaseKeyEnv != null && !firebaseKeyEnv.trim().isEmpty())
            {
                serviceAccount = new ByteArrayInputStream(firebaseKeyEnv.getBytes(StandardCharsets.UTF_8));
                System.out.println("☁️ Firebase, sunucu çevre değişkeni (Environment Variable) ile başlatılıyor...");
            }
            else
            {
                serviceAccount = getClass().getClassLoader().getResourceAsStream("serviceAccountKey.json");
                System.out.println("💻 Firebase, yerel serviceAccountKey.json dosyası ile başlatılıyor...");
            }

            if (serviceAccount == null)
            {
                System.err.println("❌ HATA: Firebase kimlik doğrulama kaynağı bulunamadı!");
                return;
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty())
            {
                FirebaseApp.initializeApp(options);
                System.out.println("🚀 Firebase Admin SDK başarıyla başlatıldı.");
            }
        }
        catch (IOException e)
        {
            System.err.println("❌ Firebase başlatma hatası: " + e.getMessage());
        }
    }
}