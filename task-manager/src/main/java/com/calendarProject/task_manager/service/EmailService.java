package com.calendarProject.task_manager.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;


@Service
public class EmailService {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public void sendPasswordResetEmail(String userEmail, String token) {
        String resetUrl = frontendUrl + "/reset-password?token=" + token;

        // UWAGA: Adres 'from' musi być zweryfikowany w Twoim koncie SendGrid!
        Email from = new Email("roksana.zylka@studenci.collegiumwitelona.pl");
        String subject = "Reset hasła w Twojej aplikacji";
        Email to = new Email(userEmail);
        Content content = new Content("text/html", "<h1>Reset hasła</h1><p>Aby zresetować hasło, kliknij w poniższy link:</p>"
                + "<a href=\"" + resetUrl + "\">Zresetuj hasło</a>"
                + "<p>Link jest ważny przez 1 godzinę.</p>");

        Mail mail = new Mail(from, subject, to, content);
        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            // Używamy com.sendgrid.Method.POST
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            // Logowanie odpowiedzi jest bardzo pomocne przy diagnozowaniu problemów
            Response response = sg.api(request);
            System.out.println("SendGrid status code: " + response.getStatusCode());
            System.out.println("SendGrid body: " + response.getBody());

        } catch (IOException ex) {
            System.err.println("Błąd podczas wysyłania e-maila: " + ex.getMessage());
            // W prawdziwej aplikacji warto użyć loggera, np. SLF4J
        }
    }
}