package com.foodie.auth_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${app.name}")
    private String appName;

    /**
     * Envoie un email de réinitialisation de mot de passe
     */
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        try {
            String resetUrl = frontendUrl + "/auth/reset-password?token=" + resetToken;

            // Créer un message HTML
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Réinitialisation de votre mot de passe - " + appName);

            // Corps de l'email en HTML
            String htmlContent = buildPasswordResetEmailTemplate(resetUrl);
            helper.setText(htmlContent, true);

            // Envoyer l'email
            mailSender.send(message);

            System.out.println("========================================");
            System.out.println("✅ EMAIL ENVOYÉ AVEC SUCCÈS");
            System.out.println("========================================");
            System.out.println("À : " + toEmail);
            System.out.println("Lien : " + resetUrl);
            System.out.println("========================================");

        } catch (MessagingException e) {
            System.err.println("❌ ERREUR lors de l'envoi de l'email : " + e.getMessage());
            throw new RuntimeException("Erreur lors de l'envoi de l'email", e);
        }
    }

    /**
     * Template HTML pour l'email de réinitialisation
     */
    private String buildPasswordResetEmailTemplate(String resetUrl) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        background-color: #f4f4f4;
                        margin: 0;
                        padding: 0;
                    }
                    .container {
                        max-width: 600px;
                        margin: 50px auto;
                        background-color: white;
                        border-radius: 10px;
                        box-shadow: 0 4px 6px rgba(0,0,0,0.1);
                        overflow: hidden;
                    }
                    .header {
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: white;
                        padding: 30px;
                        text-align: center;
                    }
                    .header h1 {
                        margin: 0;
                        font-size: 28px;
                    }
                    .content {
                        padding: 40px 30px;
                    }
                    .content p {
                        color: #333;
                        line-height: 1.6;
                        margin: 15px 0;
                    }
                    .button {
                        display: inline-block;
                        padding: 15px 40px;
                        margin: 20px 0;
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: white;
                        text-decoration: none;
                        border-radius: 8px;
                        font-weight: bold;
                    }
                    .footer {
                        background-color: #f9f9f9;
                        padding: 20px;
                        text-align: center;
                        color: #666;
                        font-size: 12px;
                    }
                    .warning {
                        background-color: #fff3cd;
                        border-left: 4px solid #ffc107;
                        padding: 15px;
                        margin: 20px 0;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🍽️ """ + appName + """
            </h1>
                    </div>
                    <div class="content">
                        <h2>Réinitialisation de mot de passe</h2>
                        <p>Bonjour,</p>
                        <p>Vous avez demandé à réinitialiser votre mot de passe pour votre compte """ + appName + """
            .</p>
                        <p>Cliquez sur le bouton ci-dessous pour créer un nouveau mot de passe :</p>
                        <center>
                            <a href=\"""" + resetUrl + """
            \" class="button">Réinitialiser mon mot de passe</a>
                        </center>
                        <div class="warning">
                            <strong>⏰ Ce lien expirera dans 1 heure.</strong>
                        </div>
                        <p>Si vous n'avez pas demandé cette réinitialisation, ignorez cet email. Votre mot de passe restera inchangé.</p>
                        <p>Si le bouton ne fonctionne pas, copiez et collez ce lien dans votre navigateur :</p>
                        <p style="word-break: break-all; color: #667eea;">""" + resetUrl + """
            </p>
                    </div>
                    <div class="footer">
                        <p>© 2026 """ + appName + """
            . Tous droits réservés.</p>
                        <p>Cet email a été envoyé automatiquement, merci de ne pas y répondre.</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }
}