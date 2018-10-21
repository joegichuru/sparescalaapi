package com.joseph.dao.repositories

import com.joseph.domain.Like
import org.springframework.data.mongodb.repository.MongoRepository

trait LikeRepository extends MongoRepository[Like,String]{
  def findAllByItemId(itemId:String):java.util.List[Like]

  def existsByItemId(itemId:String):Boolean
}
