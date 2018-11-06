package com.joseph.controllers

import java.security.Principal

import com.joseph.dao.services.UserService
import com.joseph.domain.Status
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.{PostMapping, RequestMapping, RequestParam, RestController}

/**
  * This controller serves general requests e.g registering fcm device tokens
  */
@RestController
@RequestMapping(Array("/service"))
class ServiceController @Autowired()(userService: UserService) {


  @PostMapping(Array("/fcm-token"))
  def registerFcmToken(@RequestParam("fcmtoken") fcmToken: String,principal: Principal):Status={
    //save/update user token
    val user=userService.findByEmail(principal.getName)

    user.setFcmToken(fcmToken)
    userService.save(user)
    new Status("success",message = "token updated")
  }
}
