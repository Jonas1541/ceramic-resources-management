package com.jonasdurau.ceramicmanagement.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordResetEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Redefinição de Senha");
        message.setFrom("gestorceramico@gmail.com");
        // TODO: A URL do frontend deve ser configurável
        message.setText("Para redefinir sua senha, clique no link: "
                + "http://localhost:4200/reset-password/" + token);
        mailSender.send(message);
    }
}
