package com.joseph.security

import java.util
import java.util.Date

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm.HMAC512
import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.gson.{ExclusionStrategy, FieldAttributes, Gson, GsonBuilder}
import com.joseph.dao.services.UserService
import com.joseph.domain.User
import org.springframework.http.HttpHeaders
//import org.springframework.com.joseph.security.core.userdetails.User
import javax.servlet.FilterChain
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.springframework.security.authentication.{AuthenticationManager, UsernamePasswordAuthenticationToken}
import org.springframework.security.core.{Authentication, AuthenticationException}
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


class JWTAuthenticationFilter(authenticationManager: AuthenticationManager,userService:UserService) extends UsernamePasswordAuthenticationFilter {

  override def attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication = {
    val appUser:User=new User
    appUser.setEmail(request.getParameter("email"))
    appUser.setPassword(request.getParameter("password"))
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(appUser.getEmail, appUser.getPassword,
      new util.ArrayList
    ))
  }

  override def successfulAuthentication(request: HttpServletRequest, response: HttpServletResponse,
                                        chain: FilterChain, authResult: Authentication): Unit = {
    //fetch user
    //add user to body
    val user=authResult.getPrincipal.asInstanceOf[org.springframework.security.core.userdetails.User]
    val token:String=JWT.create()
      .withSubject(user.getUsername)
      .withExpiresAt(new Date(System.currentTimeMillis()+Constants.EXPIRATION_TIME))
      .sign(HMAC512(Constants.SECRET.getBytes()))

    response.addHeader(Constants.HEADER_STRING,Constants.PREFIX+" "+token)
    var appUser=userService.findByEmail(user.getUsername)
    appUser.setAccessToken(token)
    //write to response body
    val gson:Gson=new GsonBuilder().addSerializationExclusionStrategy(new ExclusionStrategy {
      override def shouldSkipField(f: FieldAttributes): Boolean = {
        val jsonIgnore:JsonIgnore=f.getAnnotation(classOf[JsonIgnore])
        jsonIgnore!=null
      }

      override def shouldSkipClass(clazz: Class[_]): Boolean = false
    }).create()

    response.addHeader(HttpHeaders.CONTENT_TYPE,"application/json;charset=UTF-8")
    response.getWriter.write(gson.toJson(appUser))

  }

  override def unsuccessfulAuthentication(request: HttpServletRequest, response: HttpServletResponse, failed: AuthenticationException): Unit = {
    //todo log this attempt
    val status=new util.HashMap[String,String]()
    status.put("status","failed")
    status.put("message",failed.getMessage)
    response.addHeader(HttpHeaders.CONTENT_TYPE,"application/json;charset=UTF-8")
    response.getWriter.write(new Gson().toJson(status))
   // super.unsuccessfulAuthentication(request, response, failed)
  }
}

