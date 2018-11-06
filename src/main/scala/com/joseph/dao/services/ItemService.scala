package com.joseph.dao.services

import java.util

import com.mongodb.BasicDBObject
import com.mongodb.client.gridfs.model.GridFSFile
import com.joseph.dao.repositories.{CommentRepository, ItemRepository, LikeRepository}
import com.joseph.domain.{Comment, Item, Like, User}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.{Page, PageRequest}
import org.springframework.data.geo.{Distance, Metrics, Point}
import org.springframework.data.mongodb.core.query.{Criteria, Query}
import org.springframework.data.mongodb.gridfs.{GridFsResource, GridFsTemplate}
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

import scala.collection.JavaConverters._

@Service
class ItemService @Autowired()(itemRepository: ItemRepository, commentRepository: CommentRepository
                               , gridFsOperations: GridFsTemplate, likeRepository: LikeRepository) {


  //default page sizes is 20
  def findByPage(pageno: Int = 0): Page[Item] = {
    val pageRequest: PageRequest = PageRequest.of(pageno, 20)
    //todo add function to transform items to be user specific
    itemRepository.findAllByOrderByPostedOnDesc(pageRequest)
  }

  def findAll(): java.util.List[Item] = {
    itemRepository.findAll()
  }


  def findOne(itemId: String): Item = {
    if (itemRepository.existsById(itemId)) itemRepository.findById(itemId).get() else null
  }

  def removeItem(item: Item): Unit = {
    //delete its comments too
    commentRepository.deleteAllByItemId(item.getId)
    itemRepository.delete(item)
    //
  }

  def save(item: Item): Item = {
    itemRepository.save(item)
  }

  def exists(id: String): Boolean = {
    itemRepository.existsById(id)
  }
  def findByUser(user:User):java.util.List[Item]={
    itemRepository.findAllByUser(user)
  }

  //add user meta to list items
  def transformer(list: List[Item], email: String): java.util.ArrayList[Item] = {
    //for each item get its comments
    //for each comment find if user exists REPLACE COMMENTS WITH LIKES
    list.foreach(item => {
      // item.setComments( commentRepository.findByItemId(item.getId).size())
      //     if( .asScala.count(p=>p.getUser.getEmail==email)>0){
      //       item.set
      //     }
    })
    null
  }


  def saveWithImages(images: java.util.ArrayList[MultipartFile], item: Item): Any = {
    //save images using itemid_{1-n}
    //update item id db
    val iterator = images.iterator()
    var imageUrls = new util.ArrayList[String]()
    while (iterator.hasNext) {
      val metadata = new BasicDBObject()
      val multipartFile = iterator.next()
      metadata.put("type", multipartFile.getContentType)
      metadata.put("originalName", multipartFile.getOriginalFilename)
      val is = multipartFile.getInputStream
      val id = gridFsOperations.store(is, multipartFile.getOriginalFilename, multipartFile.getContentType, metadata)
      imageUrls.add(id.toString)
    }
    item.setImageUrls(imageUrls)
    save(item)
  }

  def findImage(imageId: String): GridFsResource = {
    val file: GridFSFile = gridFsOperations.findOne(new Query(Criteria.where("_id").is(imageId)))
    val f = new GridFsResource(file)
    f
  }

  def isLiked(userId: String, itemId: String): Boolean = {
    if (!likeRepository.existsByItemId(itemId)) return false
    val likes = likeRepository.findAllByItemId(itemId).asScala.toList
    if (likes.isEmpty) false else {

      likes.exists(_.getUser.getId == userId)
    }
  }

  def like(like: Like): Unit = {
    likeRepository.save(like)
  }

  def unlike(userId: String, itemId: String): Boolean = {
    val likes = likeRepository.findAllByItemId(itemId).asScala.toList
    if (likes.isEmpty) false else {
      likes.foreach(like => if (like.getUser.getId == userId) likeRepository.delete(like))
      true
    }
  }

  //radius of around 100 km seems ok and around 30 items too
  def findNearPaged(lat: Double, lng: Double, d: Double = 100, p: Int = 0): Page[Item] = {
    val pageRequest: PageRequest = PageRequest.of(p, 30)
    val distance: Distance = new Distance(d, Metrics.KILOMETERS)
    val point = new Point(lat, lng)
    itemRepository.findByPointNearOrderByPoint(point = point, distance = distance, pageRequest)
  }

  def findNear(lat: Double, lng: Double, d: Double = 50): java.util.List[Item] = {
    val distance: Distance = new Distance(d, Metrics.KILOMETERS)
    val point = new Point(lat, lng)
    itemRepository.findByPointNear(point, distance)
  }

  def findComments(itemId: String, page: Int = 0): Page[Comment] = {
    val pageRequest: PageRequest = PageRequest.of(page, 100)
    commentRepository.findByItemIdOrderByCreatedOnDesc(itemId, pageRequest)
  }

  def saveComment(comment: Comment): Unit = {
    commentRepository.save(comment)
  }

  def deleteComment(commentId: String): Unit = {
    if (commentRepository.existsById(commentId)) {
      commentRepository.deleteById(commentId)
    }
  }


}
