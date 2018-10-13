package com.joseph.spareapi.dao.repositories

import com.joseph.spareapi.domain.Item
import org.springframework.data.domain.{Page, Pageable}
import org.springframework.data.geo.{Distance, Point}
import org.springframework.data.repository.PagingAndSortingRepository

trait ItemRepository extends PagingAndSortingRepository[Item,String]{
  def findAllByOrderByPostedOnDesc(pageable: Pageable): Page[Item]

  override def findAll(): java.util.List[Item] ;

  def findByPointNearOrderByPoint(point:Point,distance: Distance,pageable: Pageable):Page[Item]
  def findByPointNear(point:Point,distance: Distance):java.util.List[Item]
}
