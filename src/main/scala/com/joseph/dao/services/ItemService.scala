package com.joseph.dao.services

import java.util
import java.util.{Calendar, Date}

import com.joseph.dao.repositories.{CommentRepository, ItemRepository, LikeRepository}
import com.joseph.domain.{Comment, Item, Like, User}
import com.mongodb.BasicDBObject
import com.mongodb.client.gridfs.model.GridFSFile
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.{Page, PageRequest}
import org.springframework.data.geo.{Distance, Metrics, Point}
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.{Criteria, Query}
import org.springframework.data.mongodb.gridfs.{GridFsResource, GridFsTemplate}
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

import scala.collection.JavaConverters._

@Service
class ItemService @Autowired()(itemRepository: ItemRepository, commentRepository: CommentRepository
                               , gridFsOperations: GridFsTemplate, likeRepository: LikeRepository, mongoTemplate: MongoTemplate) {
  def suspendByUser(user: User): Unit = {
    itemRepository.findAllByUser(user).asScala.foreach(item => {
      item.setIsPublished(false)
      itemRepository.save(item)
    })
  }


  //default page sizes is 20
  def findByPage(pageno: Int = 0): Page[Item] = {
    val pageRequest: PageRequest = PageRequest.of(pageno, 20)
    //todo add function to transform items to be user specific
    itemRepository.findAllByOrderByPostedOnDesc(pageRequest)

  }



  def findAll(): java.util.List[Item] = {
    itemRepository.findAll()
  }

  def findAllActive(): java.util.List[Item] = {
    itemRepository.findAllByIsPublishedTrue()
  }

  //number of likes on individual items
  def likesCount(item: Item): Long = {
    likeRepository.findAllByItemId(item.getId).size()
  }

  //transform items that are user specific
  def findAll(user: User):java.util.List[Item]={
    itemRepository.findAll().asScala.map(item=>{
      item.setIsLiked(isLiked(user.getId,item.getId))
      item.setLikes(likesCount(item))
      item
    }).asJava

  }
  def findAllActive(user: User):java.util.List[Item]={
    itemRepository.findAllByIsPublishedTrue().asScala.map(item=>{
      item.setIsLiked(isLiked(user.getId,item.getId))
      item.setLikes(likesCount(item))
      item
    }).asJava

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

  def findByUser(user: User): java.util.List[Item] = {
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
  def findNearPaged(lat: Double, lng: Double, d: Double = 100, p: Int = 0,user: User): java.util.List[Item] = {
    val pageRequest: PageRequest = PageRequest.of(p, 30)
    val distance: Distance = new Distance(d, Metrics.KILOMETERS)
    val point = new Point(lat, lng)
    val data=itemRepository.findByPointNearOrderByPoint(point = point, distance = distance, pageRequest)

    val list=data.getContent
    list.asScala.map(item=>{
      item.setLikes(likesCount(item))
      item.setIsLiked(isLiked(user.getId,item.getId))
      item
    }).asJava

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

  def updateUserItems(user: User): Unit = {
    itemRepository.saveAll(itemRepository.findAllByUserId(user.getId).asScala.map(item => {
      item.setUser(user)
      item
    }).asJava)
  }

  def deleteUserItems(user: User): Unit = {
    itemRepository.deleteAll(itemRepository.findAllByUser(user))
  }

  def searchByQuery(q: String): util.List[Item] = {
    val query = new Query()
    query.addCriteria(Criteria.where("description").regex("/.*" + q + "*/"))
    val list = mongoTemplate.find(query, classOf[Item])
    list
  }

  def searchByQ(q: String): java.util.List[Item] = {
    itemRepository.findAllBy(s".*${q.trim}*.")
  }

  def itemsCount(): Long = {
    itemRepository.count()
  }

  def likesCount(): Long = {
    likeRepository.count()
  }

  def itemBetween(start:Long,end:Long): util.ArrayList[Item] ={
    itemRepository.findAllByPostedOnBetween(start+(24*60*60),end)
  }

  def findAllThisWeek() :java.util.List[Item]={
    val time=new Date().getTime
    itemRepository.findAllByPostedOnGreaterThan(time)
  }

  def weeklyPosts():JSONObject={
    //find all this week
    //create mutable map of days
    //update days count
    val items=findAllThisWeek()
    val days=new util.HashMap[String,Long]()
    days.put("mon",0)
    days.put("tue",0)
    days.put("wed",0)
    days.put("thu",0)
    days.put("fri",0)
    days.put("sat",0)
    days.put("sun",0)

    items.asScala.foreach(i=>{
      val calender=Calendar.getInstance()
      calender.setTimeInMillis(i.getPostedOn)
       calender.get(Calendar.DAY_OF_WEEK) match{
         case Calendar.MONDAY=>days.put("mon",days.get("mon")+1)
         case Calendar.TUESDAY=>days.put("tue",days.get("tue")+1)
         case Calendar.WEDNESDAY=>days.put("wed",days.get("wed")+1)
         case Calendar.THURSDAY=>days.put("thu",days.get("thu")+1)
         case Calendar.FRIDAY=>days.put("fri",days.get("fri")+1)
         case Calendar.SATURDAY=>days.put("sat",days.get("sat")+1)
         case Calendar.SUNDAY=>days.put("sun",days.get("sun")+1)
      }
    })
    new JSONObject(days)

  }



}
