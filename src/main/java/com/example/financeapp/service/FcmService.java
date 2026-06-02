package com.example.financeapp.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
public class FcmService
{
    public void sendPushNotification(String targetDeviceToken, String title, String body)
    {
        if (targetDeviceToken == null || targetDeviceToken.trim().isEmpty())
        {
            System.err.println("⚠️ Bildirim gönderilemedi: Cihaz token'ı boş!");
            return;
        }

        try
        {
            Notification notification = Notification.builder().setTitle(title).setBody(body).build();

            Message message = Message.builder().setToken(targetDeviceToken).setNotification(notification).build();

            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("✨ Bildirim telefona uçuruldu. Firebase ID: " + response);
        }
        catch (Exception e)
        {
            System.err.println("❌ Firebase gönderme hatası: " + e.getMessage());
        }
    }
}
