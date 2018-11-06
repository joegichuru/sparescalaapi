package com.joseph.dao.services

import com.joseph.dao.repositories.UserRepository
import com.joseph.domain.User
import com.mongodb.BasicDBObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

import scala.collection.JavaConverters._
import scala.collection.mutable

@Service
class UserService @Autowired()(userRepository: UserRepository,gridFsOperations: GridFsTemplate) {
  def existByEmail(email: String): Boolean = {
    userRepository.existsByEmail(email)
  }

  def register(user: User): mutable.HashMap[String, String] = {
    var status = new mutable.HashMap[String, String]()
    if (userRepository.findOneByEmail(user.getEmail) != null) {
      status.+=("status" -> "failed")
      status.+=("message" -> "E-mail already registered.")
    } else {
      userRepository.save(user)
      status.+=("status" -> "success")
      status.+=("message" -> "User successfully registered.Please log in.")

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
    try {
      userRepository.findById(userId).get()
    } catch {
      case exception: Exception => null
      case _ => null
    }

  }

  def findAll(): Set[User] = {
    userRepository.findAll().asScala.toSet
  }

  def save(user: User): User = {
    userRepository.save(user)
  }

  def findByEmail(email: String): User = {
    userRepository.findOneByEmail(email)
  }
  def saveImage(user:User,image:MultipartFile):Unit={
    //save images using userid
    //update item id db
    val metadata = new BasicDBObject()
    metadata.put("type", image.getContentType)
    metadata.put("originalName", image.getOriginalFilename)
    val is = image.getInputStream
    val id = gridFsOperations.store(is, image.getOriginalFilename, image.getContentType, metadata)
    user.setImageUrl(id.toString)

    save(user)
  }
}
