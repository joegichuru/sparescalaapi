package com.joseph.spareapi.dao.repositories

import com.joseph.spareapi.domain.Like
import org.springframework.data.mongodb.repository.MongoRepository

trait LikeRepository extends MongoRepository[Like,String]{
  def findAllByItemId(itemId:String):java.util.List[Like]

  def existsByItemId(itemId:String):Boolean
}
