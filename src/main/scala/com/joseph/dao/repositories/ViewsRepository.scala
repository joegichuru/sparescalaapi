package com.joseph.dao.repositories

import com.joseph.domain.View
import org.springframework.data.mongodb.repository.MongoRepository

trait ViewsRepository extends MongoRepository[View, String] {
  def findAllByCreatedGreaterThanOrderByCreatedDesc(long: Long): java.util.List[View]
}
