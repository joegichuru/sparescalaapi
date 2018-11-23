package com.joseph.controllers

import java.security.Principal
import java.util.Date

import com.joseph.dao.services.{EmailService, ItemService, UserService}
import com.joseph.domain.{Roles, Status, User}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation._
import org.springframework.web.multipart.MultipartFile

import scala.util.Random

/**
  * All services related to account activity
  * @param userService
  * @param bCryptPasswordEncoder
  * @param emailService
  */

@RestController
@RequestMapping(Array("/auth"))
class UserController @Autowired()(userService: UserService, bCryptPasswordEncoder: BCryptPasswordEncoder
                                  , emailService: EmailService,itemService: ItemService) {

  /**
    * Register user
    * @param name
    * @param email
    * @param password
    * @return
    */

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
    user.setActive(true)
    userService.register(user)
    // val user1=userService.findByEmail(user.getEmail)

    //sent email to user with userId/email
    // emailService.sendMail(user1)

    new Status("success", "Registration successful")
  }

  /**
    * activates account
    * thism link is sent via email
    * @param id
    * @param email
    * @return
    */
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


  /**
    * email user with temporary password 6 characters long
    * @param email
    * @return
    */
  @PostMapping(Array("/password/reset"))
  @ResponseBody
  def requestPasswordResetLink(@RequestParam("email") email: String): Status = {
    if (!userService.existByEmail(email)) return new Status("error", "E-mail not found")
    val user = userService.findByEmail(email)
    //send email to user
    val random = new Random()
    val password = random.nextInt(6)
    try {
      emailService.sendMail(user, password)
    } catch {
      case _: Throwable => return new Status("error", "Password could not be reset")
    }
    new Status("success", "Password reset link send via email")
  }

  /**
    * Change user password
    * @param oldPassword
    * @param newPassword
    * @param principal
    * @return
    */
  @PostMapping(Array("/password/update"))
  @RequestBody
  def changePassword(@RequestParam("oldPassword") oldPassword: String,
                     @RequestParam("newPassword") newPassword: String, principal: Principal): Status = {
    val user = userService.findByEmail(principal.getName)
    val encodedPassword = bCryptPasswordEncoder.encode(oldPassword)
    if (bCryptPasswordEncoder.matches(oldPassword,user.getPassword )) {
      val encodedNew = bCryptPasswordEncoder.encode(newPassword)
      user.setPassword(encodedNew)
      userService.save(user)
      new Status("success", "Password updated.")
    } else {
      new Status("error", "Old password validation failed.")
    }
  }

  /**
    * Update user account
    * the image is optional
    * @param name
    * @param email
    * @param avatar
    * @param principal
    * @return
    */
  @PostMapping(Array("/update/all"))
  @ResponseBody
  def updateAccount(@RequestParam("name") name: String,
                    @RequestParam("email") email: String,
                    @RequestParam(name="avatar", required = false) avatar: MultipartFile,
                    principal: Principal): User = {
    val user = userService.findByEmail(principal.getName)
    user.setEmail(email)
    user.setName(name)
    //save profile picture
   userService.saveImage(user,avatar)

    //update user items
    itemService.updateUserItems(user)

    user

  }
  @PostMapping(Array("/update"))
  @ResponseBody
  def updateAccount(@RequestParam("name") name: String,
                    @RequestParam("email") email: String,
                    principal: Principal): User = {
    val user = userService.findByEmail(principal.getName)
    user.setEmail(email)
    user.setName(name)
    userService.save(user)

    //update user items
    itemService.updateUserItems(user)
    user

  }

  @PostMapping(Array("/admin"))
  @ResponseBody
  def makeAdmin(
                    principal: Principal): User = {
    val user = userService.findByEmail(principal.getName)
    user.setRole(Roles.ADMIN)
    userService.save(user)

    user

  }

}
