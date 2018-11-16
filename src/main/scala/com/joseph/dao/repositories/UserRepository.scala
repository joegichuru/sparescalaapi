package com.joseph.dao.repositories

import com.joseph.domain.User
import org.springframework.data.mongodb.repository.MongoRepository

trait UserRepository extends MongoRepository[User,String]{
  def existsByEmail(email:String): Boolean

  def findOneByEmail(email:String):User

  def findAllByCreatedOnBetween(start:Long,end:Long):java.util.ArrayList[User];
}