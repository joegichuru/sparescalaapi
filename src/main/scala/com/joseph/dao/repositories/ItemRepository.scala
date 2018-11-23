package com.joseph.dao.repositories

import com.joseph.domain.{Item, User}
import org.springframework.data.domain.{Page, Pageable}
import org.springframework.data.geo.{Distance, Point}
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository

trait ItemRepository extends PagingAndSortingRepository[Item, String] {
  def findAllByOrderByPostedOnDesc(pageable: Pageable): Page[Item]

  def findAllOrderByPostedOn():java.util.List[Item]

  override def findAll(): java.util.List[Item]

  def findAllByIsPublishedTrue(): java.util.List[Item]

  def findByPointNearOrderByPoint(point: Point, distance: Distance, pageable: Pageable): Page[Item]

  def findByPointNear(point: Point, distance: Distance): java.util.List[Item]

  def findAllByUser(user: User): java.util.List[Item]

  def findAllByUserId(id: String): java.util.List[Item]
  @Query("{$or : [{'name': { $regex: ?0, $options:'i' }}, {'description': { $regex: ?0, $options:'i' }}]}")
  def findAllBy(q:String):java.util.List[Item]

  def findAllByPostedOnBetween(start:Long,end:Long):java.util.ArrayList[Item]
}
