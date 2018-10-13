package com.joseph.spareapi.dao.services

import com.joseph.spareapi.dao.repositories.UserRepository
import com.joseph.spareapi.domain.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import scala.collection.JavaConverters._
import scala.collection.mutable

@Service
class UserService @Autowired()(userRepository: UserRepository) {
  def existByEmail(email: String): Boolean = {
    userRepository.existsByEmail(email)
  }

  def register(user: User): mutable.HashMap[String,String] = {
    var status=new mutable.HashMap[String,String]()
    if(userRepository.findOneByEmail(user.getEmail)!=null){
      status.+=("status"->"failed")
      status.+=("message"->"E-mail already registered.")
    }else{
      userRepository.save(user)
      status.+=("status"->"success")
      status.+=("message"->"User successfully registered.Please log in.")

    }
    status
  }

  def addUser(user: User): Boolean = {
    userRepository.save(user) != null
  }

  def deleteUser(user: User): Unit = {
    userRepository.delete(user)
  }

  def findUser(userId: String): User = {
    userRepository.findById(userId).get()
  }
  def findAll():Set[User]={
    userRepository.findAll().asScala.toSet
  }
  def save(user: User):User={
   userRepository.save(user)
  }
  def findByEmail(email: String):User={
    userRepository.findOneByEmail(email)
  }
}
