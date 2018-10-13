package com.joseph.spareapi.security

import java.util

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm.HMAC512
import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.gson.{ExclusionStrategy, FieldAttributes, Gson, GsonBuilder}
import com.joseph.spareapi.dao.services.UserService
import com.joseph.spareapi.domain.{Status, User}
import javax.servlet.FilterChain
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.{AuthenticationManager, UsernamePasswordAuthenticationToken}
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter


class JWTAuthorizationFilter(authenticationManager: AuthenticationManager, userService: UserService)
  extends BasicAuthenticationFilter(authenticationManager) {
  var appUser: User = _
  var active:Boolean=true


  override def doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain): Unit = {
    val header = request.getHeader(Constants.HEADER_STRING)
    if (header == null || !header.startsWith(Constants.PREFIX)) {
      chain.doFilter(request, response)
      return
    }

    val auth: UsernamePasswordAuthenticationToken = fetchAuthentication(request)
    if(!active){
      val gson:Gson=new GsonBuilder().addSerializationExclusionStrategy(new ExclusionStrategy {
        override def shouldSkipField(f: FieldAttributes): Boolean = {
          val jsonIgnore:JsonIgnore=f.getAnnotation(classOf[JsonIgnore])
          jsonIgnore!=null
        }

        override def shouldSkipClass(clazz: Class[_]): Boolean = false
      }).create()

      response.addHeader(HttpHeaders.CONTENT_TYPE,"application/json;charset=UTF-8")
      val status=new Status("error","Account not active.Please activate email or contact admin")
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
        //confirm user is active and not suspended
        appUser=userService.findByEmail(user)
        if(appUser!=null){
          if(!appUser.getActive||appUser.getSuspended){
            active=false
            return null
          }
        }
        active=true
        return new UsernamePasswordAuthenticationToken(user, null, new util.ArrayList)
      } else return null
    }
    null
  }

}
