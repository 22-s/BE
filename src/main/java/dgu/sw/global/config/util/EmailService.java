package dgu.sw.global.config.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // 일반 텍스트 메일
    public void sendEmail(String to, String subject, String text) {
        sendEmail(to, subject, text, false);
    }

    // HTML 여부 포함 메일
    public void sendEmail(String to, String subject, String content, boolean isHtml) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, isHtml); // true면 HTML로 인식

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 전송 중 오류가 발생했습니다.", e);
        }
    }
}