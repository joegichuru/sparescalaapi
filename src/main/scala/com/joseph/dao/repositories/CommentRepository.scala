package com.joseph.dao.repositories

import com.joseph.domain.Comment
import org.springframework.data.domain.{Page, Pageable}
import org.springframework.data.mongodb.repository.MongoRepository

trait CommentRepository extends MongoRepository[Comment, String] {
  def findByItemIdAndUser_Email(itemId: String, email: String): java.util.ArrayList[Comment]

  def findByItemIdOrderByCreatedOnDesc(itemId: String,pageable:Pageable): Page[Comment]

  def deleteAllByItemId(itemId:String):Unit

}
