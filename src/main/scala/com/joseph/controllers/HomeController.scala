package com.joseph.controllers

import java.security.Principal

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.{GetMapping, RequestMapping}

@Controller
@RequestMapping(Array("/jjjb"))
class HomeController {

  @GetMapping
  def index(principal: Principal):String={
    if(principal==null){
      return "login"
    }
    return "index"
  }

}
