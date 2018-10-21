package com.joseph

import java.util
import java.util.Properties

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.http.converter.ByteArrayHttpMessageConverter
import org.springframework.mail.javamail.{JavaMailSender, JavaMailSenderImpl}
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@SpringBootApplication
class Application {
  @Bean
  def bCryptPasswordEncoder: BCryptPasswordEncoder = new BCryptPasswordEncoder

  @Bean
  def byteArrayHttpMessageConverter: ByteArrayHttpMessageConverter = {
    val converter: ByteArrayHttpMessageConverter = new ByteArrayHttpMessageConverter
    converter.setSupportedMediaTypes(supportedMediaTypes)
    converter
  }

  @Bean
  def javaMailSender: JavaMailSender = {
    val mailSender: JavaMailSenderImpl = new JavaMailSenderImpl
    mailSender.setHost("smtp.gmail.com")
    mailSender.setPort(587)
    mailSender.setUsername("josephgichuru40@gmail.com")
    mailSender.setPassword("afxvsomgbpyjivid")
    val props: Properties = mailSender.getJavaMailProperties
    props.put("mail.transport.protocol", "smtp")
    props.put("mail.smtp.auth", "true")
    props.put("mail.smtp.starttls.enable", "true")
    props.put("mail.debug", "false")
    mailSender
  }

  def supportedMediaTypes: util.List[MediaType] = {
    val mediaTypes: util.List[MediaType] = new util.ArrayList[MediaType]
    mediaTypes.add(MediaType.IMAGE_JPEG)
    mediaTypes.add(MediaType.IMAGE_PNG)
    mediaTypes.add(MediaType.IMAGE_GIF)
    mediaTypes
  }
}

object Application{
  def main(args: Array[String]): Unit = {
    SpringApplication.run(classOf[Application])
  }


}
