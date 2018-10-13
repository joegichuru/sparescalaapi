package com.joseph.spareapi.controllers

import java.security.Principal
import java.util.Date

import com.joseph.spareapi.dao.services.UserService
import com.joseph.spareapi.domain.{Roles, Status, User}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation._
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping(Array("/auth"))
class UserController @Autowired()(userService: UserService, bCryptPasswordEncoder: BCryptPasswordEncoder) {


  @PostMapping(Array("/signup"))
  def signUp(@RequestParam("name") name: String,
             @RequestParam("email") email: String,
             @RequestParam("password") password: String): Status = {
    if(userService.existByEmail(email)) return new Status("error","E-mail already registered.Please login.")
      val user = new User
      user.setEmail(email)
      user.setName(name)
      user.setCreatedOn(new Date().getTime)
      user.setRole(Roles.REGULAR)
      user.setPassword(bCryptPasswordEncoder.encode(password))
      userService.register(user)
    new Status("success","Registration successful.Activation link sent through email.")
  }

  @PostMapping(Array("/activate/{userId}/{email}"))
  def activateAccount(@PathVariable("userId")id:String,@PathVariable("email") email:String): Status ={
    val user=userService.findUser(id)
    if(user==null) return new Status("error","User does not exists.")

    if(user.getEmail!=email) return new Status("error","Invalid request")

    user.setActive(true)
    user.setSuspended(false)
    userService.save(user)
    new Status("success","Account active")
  }
  //reset password
  def passwordReset(): Status ={
    null
  }
  //email user with reset link
  @PostMapping(Array("/password/resetlink")) @ResponseBody
  def requestPasswordResetLink(@RequestParam("email") email:String):Status={
    if(!userService.existByEmail(email)) return new Status("error","E-mail not found")
    val user=userService.findByEmail(email)
    //send email to user
    new Status("success","Password reset link send via email")
  }
  //change password this should be via a web interface not the app
  @PostMapping(Array("/password/userId"))
  def changePassword(@PathVariable("userId")userId:String):Status={
    null
  }

  //update account
  @PostMapping(Array("update"))
  def updateAccount(@RequestParam("name") name: String,
                    @RequestParam("email") email: String, avatar:MultipartFile, principal: Principal):Status={
    null

  }
}
