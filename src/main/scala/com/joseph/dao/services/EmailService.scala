package com.joseph.dao.services

import com.joseph.domain.User
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

@Component
class EmailService @Autowired()(jms: JavaMailSender) {
  @Value("${adr}")
  var serverAddress: String = _

  def sendMail(user: User): Unit = {

    val simpleMailMessage = new SimpleMailMessage
    simpleMailMessage.setTo(user.getEmail)
    simpleMailMessage.setSubject("Spare Email Confirmation")


    var text = "Hello " + user.getName + ",\n" + "Please click the link below to confirm your email address. Ignore if you did not request.\n"
    //http://ip/userId/email
//    var link = ""
//    try
//      link = "http://" + InetAddress.
//    catch {
//      case e: UnknownHostException =>
//        e.printStackTrace()
//        link = serverAddress
//    }

    serverAddress = serverAddress + "/auth/activate/" + user.getId + "/" + user.getEmail
    text = text + serverAddress
    simpleMailMessage.setText(text)
    jms.send(simpleMailMessage)
  }
  @throws[Exception]
  def sendMail(user: User,password:Int): Unit = {

    val simpleMailMessage = new SimpleMailMessage
    simpleMailMessage.setTo(user.getEmail)
    simpleMailMessage.setSubject("Spare Password Reset")


    var text = "Hello " + user.getName + ",\n" + "Please use "+password+" as your temporary password.Ignore if you did not request.\n"

    simpleMailMessage.setText(text)
    jms.send(simpleMailMessage)
  }
}
