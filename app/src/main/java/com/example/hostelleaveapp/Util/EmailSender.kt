package com.example.hostelleaveapp.Util

import com.example.hostelleaveapp.Util.Constants.email
import com.example.hostelleaveapp.Util.Constants.password
import javax.mail.Session
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object EmailSender {
    fun sendEmail(
        toEmail: String,
        subject: String,
        messageBody: String,
        callback: (Boolean, String) -> Unit
    )     {
    Thread {
            try {
                val props = Properties().apply {
                    put("mail.smtp.auth", "true")
                    put("mail.smtp.starttls.enable", "true")
                    put("mail.smtp.host", "smtp.gmail.com")
                    put("mail.smtp.port", "587")
                }
                val session = Session.getInstance(props, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(email, password)
                    }
                })
                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(email))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail))
                    setSubject(subject)
                    setText(messageBody)
                }

                Transport.send(message)
                callback(true, "Email sent successfully.")
            } catch (e: Exception) {
                callback(false, "Failed to send email: ${e.message}")
            }
        }.start()
    }
}
