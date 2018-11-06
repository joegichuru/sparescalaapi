package com.joseph.dao.services

import com.joseph.domain.User
import org.json.JSONObject
import org.springframework.http.{HttpEntity, HttpHeaders, ResponseEntity}
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

/**
  * general app services like sending notifications
  */
@Service
class ServiceUtils {

  private val FIREBASE_SERVER_KEY = "AAAAyGHy4yo:APA91bFzT9ZS-c54AibpTuDkG11oZtwC1RTp4iX4bwM-YVlpag-4UTpVzIOIeg8i9WGz1Xlac2MBdVK4FCqVU-oowu9fcVHrkh0G_zNgzrX3HKEWcBKQ9mnS9oBEdZqT62RpWUwiv4kF"
  private val FIREBASE_API_URL = "https://fcm.googleapis.com/v1/projects/myproject-b5ae1/messages:send"

  @Async
  def sendLikeNotification(user: User, sendUser: User): Unit = {
    val likeString = sendUser.getName + " liked your post"
    val restTemplate = new RestTemplate()


    val token = new JSONObject()
    token.put("token", sendUser.accessToken)
    val notification = new JSONObject()
    notification.put("title", "Spare App")
    notification.put("body", likeString)
    val message = new JSONObject()
    message.put("token", token)
    message.put("notification", notification)
    val data = new JSONObject()
    data.put("message", message)
    val httpHeaders=new HttpHeaders()
    httpHeaders.add(HttpHeaders.AUTHORIZATION,"Bearer "+FIREBASE_SERVER_KEY)
    httpHeaders.add(HttpHeaders.CONTENT_TYPE,"application/json")
    val httpEntity=new HttpEntity[String,String](data,httpHeaders)
    val response:ResponseEntity[String]=restTemplate.postForEntity(FIREBASE_API_URL,httpEntity,classOf[String])
    System.out.println(response.getBody.toString)


  }


}
