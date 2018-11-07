package com.joseph.security

import java.util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm.HMAC512
import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.gson.{ExclusionStrategy, FieldAttributes, Gson, GsonBuilder}
import com.joseph.dao.services.UserService
import com.joseph.domain.{Roles, Status, User}
import javax.servlet.FilterChain
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.{AuthenticationManager, UsernamePasswordAuthenticationToken}
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

/**
  *will filter out user not admin that attempt to log in admin panel
  * todo consider adding logging mechanisms
  * @param authenticationManager
  * @param userService
  */

class DashboardAccessFilter(authenticationManager: AuthenticationManager, userService: UserService)
  extends BasicAuthenticationFilter(authenticationManager) {

  var appUser: User = _

  var admin:Boolean=true


  override def doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain): Unit = {
    val header = request.getHeader(Constants.HEADER_STRING)
    if (header == null || !header.startsWith(Constants.PREFIX)) {
      chain.doFilter(request, response)
      return
    }

    val auth: UsernamePasswordAuthenticationToken = fetchAuthentication(request)
    if(!admin){
      val gson:Gson=new GsonBuilder().addSerializationExclusionStrategy(new ExclusionStrategy {
        override def shouldSkipField(f: FieldAttributes): Boolean = {
          val jsonIgnore:JsonIgnore=f.getAnnotation(classOf[JsonIgnore])
          jsonIgnore!=null
        }

        override def shouldSkipClass(clazz: Class[_]): Boolean = false
      }).create()

      response.addHeader(HttpHeaders.CONTENT_TYPE,"application/json;charset=UTF-8")
      val status=new Status("error","You are not an admin")
      response.getWriter.write(gson.toJson(status))
    }else{
      SecurityContextHolder.getContext.setAuthentication(auth)
      chain.doFilter(request, response)
    }

  }

  private def fetchAuthentication(request: HttpServletRequest): UsernamePasswordAuthenticationToken = {
    val token = request.getHeader(Constants.HEADER_STRING)
    if (token != null) { // parse the token.
      val user = JWT.require(HMAC512(Constants.SECRET.getBytes)).build.verify(token.replace(Constants.PREFIX, "").trim).getSubject
      if (user != null) {
        //confirm user is admin
        appUser=userService.findByEmail(user)
        if(appUser!=null){
          if(!(appUser.getActive||appUser.getRole.contentEquals(Roles.ADMIN))){
            admin=false
            return null
          }
        }
        admin=true
        return new UsernamePasswordAuthenticationToken(user, null, new util.ArrayList)
      } else return null
    }
    null
  }
}
