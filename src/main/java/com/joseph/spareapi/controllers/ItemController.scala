package com.joseph.spareapi.controllers

import java.lang._
import java.security.Principal
import java.util
import java.util.Date

import com.joseph.spareapi.dao.services.{ItemService, UserService}
import com.joseph.spareapi.domain.{Comment, Item, _}
import javax.websocket.server.PathParam
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.geo.Point
import org.springframework.web.bind.annotation._
import org.springframework.web.multipart.MultipartFile


@RestController()
@RequestMapping(Array("/items"))
class ItemController @Autowired()(itemService: ItemService, userService: UserService) {

  /**
    * finds all items with no parameters
    * default to top 20 items ordered by time from the latest
    *
    * @param principle
    * @return
    */
  @GetMapping
  @ResponseBody
  def findAll(principle: Principal): Page[Item] = {
    itemService.findByPage()
  }

  /**
    * finds all items by page
    * each page is 20 items
    *
    * @param pageNo
    * @return
    */

  @GetMapping(params = Array("page"))
  @ResponseBody
  def findAll(@RequestParam(name = "page") pageNo: Int): Page[Item] = {
    itemService.findByPage(pageNo)
  }

  /**
    * find one item by id
    * returns status and message if the item doesnt exist
    * todo: optimise this with caching mechanisms
    *
    * @param itemId
    * @return
    */

  @GetMapping(Array("/{itemId}"))
  @ResponseBody
  def findOne(@PathVariable(value = "itemId") itemId: String): Any = {
    itemService.findOne(itemId) match {
      case item: Item => item
      case _ =>new Status("error","Item does not exist")
    }
  }

  /**
    * search items with given query parameter
    * returns a page of relevant items with default page size being 20
    * attach some form of analytics to track search keys
    * todo:can add location parameters to present user with most relevant data
    *
    * @param query
    * @return
    */
  @GetMapping(value = Array("/search"), params = Array("q"))
  @ResponseBody
  def search(@RequestParam("q") query: String, principal: Principal): Any = {
    val user: User = userService.findByEmail(principal.getName)
    val item = new Item
    item.setUser(user)
    item.setPostedOn(new Date().getTime)
    itemService.save(item)
  }

  /**
    * search items based on the given query and given filter
    * filter refers to type eg filter=office
    *
    * @param query
    * @param filter
    * @return
    */
  @GetMapping(value = Array("/search"), params = Array("q", "filter"))
  def search(@RequestParam("q") query: String, @RequestParam("filter") filter: String):Page[Item] = {
    null
  }

  /**
    * search items based on query filtered by @filter and in price #Range(priceLow,priceHigh)
    *
    * @param query
    * @param filter
    * @param priceHigh
    * @param priceLow
    * @return
    */
  @GetMapping(value = Array("/search"), params = Array("q", "filter", "priceHigh", "priceLow"))
  def search(@RequestParam("q") query: String, @RequestParam("filter") filter: String
             , @RequestParam("priceHigh") priceHigh: Long, @RequestParam("priceLow") priceLow: Long): Page[Item]= {
    null
  }

  /**
    * search items based on query and price #Range(priceHigh,priceLow)
    *
    * @param query
    * @param priceHigh
    * @param priceLow
    * @return
    */
  @GetMapping(value = Array("/search"), params = Array("q", "priceHigh", "priceLow"))
  def search(@RequestParam("q") query: String, @RequestParam("priceHigh") priceHigh: Long,
             @RequestParam("priceLow") priceLow: Long): Page[Item] = {
    null
  }

  /**
    * finds items ordered based on distance from user.
    * use geospatial data to project results to be most relevant
    *
    * @param lat
    * @param lng
    * @return
    */
  @GetMapping(value = Array("/nearby"))
  def findNearby(@RequestParam("lat") lat: Double, @RequestParam("lng") lng: Double):Page[Item] = {
    itemService.findNearPaged(lat,lng)
  }

  /**
    * finds comments for the given item
    * todo:add paging to filter results to be like 100 comments per page
    *
    * @param itemId
    * @param page
    * @return
    */
  @GetMapping(value = Array("/{itemId}/comments/{page}")) @ResponseBody
  def findCommentsPaged(@PathVariable("itemId") itemId: String,@PathVariable("page") page:Int=0): Page[Comment] = {
    itemService.findComments(itemId,page)
  }

  /**
    * finds all comments unpaged
    * @param itemId
    * @return
    */
  @GetMapping(value = Array("/{itemId}/comments")) @ResponseBody
  def findComments(@PathVariable("itemId") itemId: String): Page[Comment] = {
    itemService.findComments(itemId)
  }

  /**
    * posts comments for a given item id
    * extract the user adding comment from the @principal
    *
    * @param itemId
    * @param comment
    * @param principal
    * @return
    */
  @PostMapping(value = Array("/{itemId}/comment"))
  def comment(@PathVariable("itemId") itemId: String, @PathParam("comment") userComment: String, principal: Principal): Status = {
    //check if item exists first before adding comment
    if(!itemService.exists(itemId)) return  new Status(status = "error",message = "Item does not exist")
    val comment:Comment=new Comment
    comment.setBody(userComment)
    comment.setCreatedOn(new Date().getTime)
    comment.setItemId(itemId)
    comment.setUser(userService.findByEmail(principal.getName))
    itemService.saveComment(comment)
    new Status(status = "success","comment added")
  }

  /**
    * like a given item
    * todo: check if @principal already liked the item before updating
    * you can also unlike using same endpoint
    *
    * @param itemId
    * @param principal
    * @return
    */
  @PostMapping(value = Array("/{itemId}/like"))
  def like(@PathVariable("itemId") itemId: String, principal: Principal): Status = {
    //check if the item exists first
    if(!itemService.exists(itemId)) return new Status(status = "error",message = "item does not exist")
    val user=userService.findByEmail(principal.getName)

    if(itemService.isLiked(user.getId,itemId)){
      itemService.unlike(user.getId,itemId)
      new Status(status = "success",message = "Item unliked.")
    }else{
      //like
      val like:Like=new Like
      like.setCreatedOn(new Date().getTime)
      like.setItemId(itemId)
      like.setUser(user)
      itemService.like(like)
      new Status(status = "success",message = "Item liked.")
    }

  }

  @PostMapping(value = Array("/post"))
  @ResponseBody
  def add(
           @RequestParam(name = "name")
           name: String,
           @RequestParam(name = "price")
           price: Long = 0l,
           @RequestParam(name = "duration", required = false)
           duration: String = MONTH,
           @RequestParam(name = "type", required = false)
           itemType: String = RENT,
           @RequestParam(name = "category", required = false)
           category: Long = 0l,
           @RequestParam(name = "description")
           description: String,
           @RequestParam(name = "lat")
           lat: java.lang.Double = new java.lang.Double(0),
           @RequestParam(name = "lng")
           lng: java.lang.Double = new java.lang.Double(0),
           @RequestParam(name = "amenities", required = false)
           amenities: String = "", //comma separated list
           @RequestParam(name = "email")
           email: String,
           @RequestParam(name = "phone", required = false)
           phone: String,
           @RequestParam(name = "website", required = false)
           website: String,
           @RequestParam(name = "bathrooms")
           bathrooms: Int,
           @RequestParam(name = "bedrooms")
           bedrooms: Int,
           @RequestParam(name = "area")
           area: Long,
           @RequestParam(name = "images", required = true)
           images: java.util.ArrayList[MultipartFile],
           principal: Principal
         ): Any = {
    if (images.isEmpty) {
      return new Status(status = "error", message = "Atleast one image required")
    }

    val item: Item = new Item
    item.setName(name)
    item.setPrice(price)
    duration match {
      case MONTH => item.setDuration(MONTH)
      case YEAR => item.setDuration(YEAR)
      case _ => item.setDuration(MONTH)
    }
    itemType match {
      case RENT => item.setItemType(RENT)
      case SALE => item.setItemType(SALE)
      case _ => item.setItemType(RENT)
    }
    item.setCategory(category)
    item.setDescription(description)
    item.setLat(lat)
    item.setLon(lng)
    if (amenities != null && !amenities.isEmpty) {
      val am = amenities.split(",").toSet
      item.setAmenities(am)
    }


    item.setEmail(email)
    item.setPhone(phone)
    item.setWebsite(website)
    item.setBathrooms(bathrooms)
    item.setBedrooms(bedrooms)
    item.setArea(area)
    val user = userService.findByEmail(principal.getName)
    if (user != null) {
      item.setUser(user)
    }
    item.setPoint(new Point(lat,lng))
    item.setPostedOn(new Date().getTime)
    itemService.saveWithImages(images, item)

  }

  /**
    * deletes a given item
    * deletes its comments too
    * or should you just update the published flag? so in essance the item is never deleted
    * @param itemId
    * @return
    */
  @PostMapping(Array("/{itemId}/delete")) @ResponseBody
  def remove(@PathVariable("itemId") itemId:String,principal: Principal): Status = {
    if(!itemService.exists(itemId)) return new Status("error","Item not found")
    val item=itemService.findOne(itemId)
    if(item.getUser.getId!=userService.findByEmail(principal.getName).getId) {
      return new Status("error","You cannot delete an item you did not create")
    }
    itemService.removeItem(item)
    new Status("success","Item deleted successfully")
  }

  @PostMapping(Array("/update")) @ResponseBody
  def update(@RequestBody item: Item, principal: Principal): Status = {
    val user = userService.findByEmail(principal.getName)
    if (item.getId == null) return new Status(status = "error", message = "Item id not found")
    if (!itemService.exists(item.getId)) return new Status(status = "error", message = "Item does not exist")
    val localItem = itemService.findOne(item.getId)
    if (localItem.getUser.getId != user.getId) return new Status(status = "error", message = "You are not the owner")
    item.setUser(user)
    //this can not be modified by user
    item.setImageUrls(localItem.getImageUrls)
    item.setPostedOn(new Date().getTime)
    item.setIsLiked(localItem.isLiked)
    item.setIsPublished(localItem.isPublished)
    item.setPoint(new Point(item.getLat,item.getLon))
    itemService.save(item)
    new Status(status = "success", message = "Item updated")

  }

  /**
    * updates images of a given item
    *save the image then update the item images
    * @param itemId
    * @param images
    * @return
    */

  def updateImage(itemId: String, images: java.util.ArrayList[MultipartFile]): Status = {
    new Status(status = "success", message = "Not implemented yet")
  }

  /**
    * deletes an image of a given item
    * delete image then update the item images
    * @param itemId
    * @param imageId
    * @return
    */

  def deleteImage(itemId: String, imageId: String): Status = {
    new Status(status = "success", message = "Not implemented yet")
  }

  @GetMapping(Array("/categories"))
  def findCategories(): util.ArrayList[Category] ={
    val categorieslist=new util.ArrayList[Category]()
    null

  }


}
