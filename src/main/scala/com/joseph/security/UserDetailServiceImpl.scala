package com.joseph.security

import java.util

import com.joseph.dao.services.UserService
import com.joseph.domain.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.{UserDetails, UserDetailsService, UsernameNotFoundException}
import org.springframework.stereotype.Service

@Service
class UserDetailServiceImpl @Autowired()(userService:UserService) extends UserDetailsService{
  override def loadUserByUsername(username: String): UserDetails = {
    val user:User=userService.findByEmail(username)
    if(user==null){
      throw new UsernameNotFoundException(username)
    }
    new org.springframework.security.core.userdetails.User(user.getEmail,user.getPassword,new util.ArrayList)
  }
}
