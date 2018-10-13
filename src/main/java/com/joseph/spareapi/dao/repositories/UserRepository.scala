package com.joseph.spareapi.dao.repositories

import com.joseph.spareapi.domain.User
import org.springframework.data.mongodb.repository.MongoRepository

trait UserRepository extends MongoRepository[User,String]{
  def existsByEmail(email:String): Boolean

  def findOneByEmail(email:String):User
}