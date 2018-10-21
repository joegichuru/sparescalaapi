package com.joseph.controllers

import java.security.Principal
import java.util.Date

import com.joseph.dao.services.{EmailService, UserService}
import com.joseph.domain.{Roles, Status, User}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation._
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping(Array("/auth"))
class UserController @Autowired()(userService: UserService, bCryptPasswordEncoder: BCryptPasswordEncoder
                                  , emailService: EmailService) {


  @PostMapping(Array("/signup"))
  def signUp(@RequestParam("name") name: String,
             @RequestParam("email") email: String,
             @RequestParam("password") password: String): Status = {
    if (userService.existByEmail(email)) return new Status("error", "E-mail already registered.Please login.")
    val user = new User
    user.setEmail(email)
    user.setName(name)
    user.setCreatedOn(new Date().getTime)
    user.setRole(Roles.REGULAR)
    user.setPassword(bCryptPasswordEncoder.encode(password))
    userService.register(user)
    val user1=userService.findByEmail(user.getEmail);
    //sent email to user with userId/email
    emailService.sendMail(user1)

    new Status("success", "Registration successful.Activation link sent through email.")
  }

  @GetMapping(Array("/activate/{userId}/{email}"))
  def activateAccount(@PathVariable("userId") id: String, @PathVariable("email") email: String): String = {
    val user = userService.findUser(id)
    if (user == null) return "User does not exist."

    if (user.getEmail != email) return "Invalid request"

    user.setActive(true)
    user.setSuspended(false)
    userService.save(user)
    "Account activated.Please login"
  }

  //reset password
  def passwordReset(): Status = {
    null
  }

  //email user with reset link
  @PostMapping(Array("/password/resetlink"))
  @ResponseBody
  def requestPasswordResetLink(@RequestParam("email") email: String): Status = {
    if (!userService.existByEmail(email)) return new Status("error", "E-mail not found")
    val user = userService.findByEmail(email)
    //send email to user
    new Status("success", "Password reset link send via email")
  }

  //change password this should be via a web interface not the app
  @PostMapping(Array("/password/userId"))
  def changePassword(@PathVariable("userId") userId: String): Status = {
    null
  }

  //update account
  @PostMapping(Array("update"))
  def updateAccount(@RequestParam("name") name: String,
                    @RequestParam("email") email: String, avatar: MultipartFile, principal: Principal): Status = {
    null

  }
}
