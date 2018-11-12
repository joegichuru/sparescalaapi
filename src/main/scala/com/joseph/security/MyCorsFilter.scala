package com.joseph.security


import javax.servlet.Filter
import javax.servlet.http.HttpServletRequest



class MyCorsFilter extends Filter{

  import javax.servlet.FilterChain
  import javax.servlet.FilterConfig
  import javax.servlet.ServletException
  import javax.servlet.ServletRequest
  import javax.servlet.ServletResponse
  import javax.servlet.http.HttpServletResponse
  import java.io.IOException

  @throws[ServletException]
  def init(filterConfig: FilterConfig): Unit = {
  }

  @throws[IOException]
  @throws[ServletException]
  def doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, filterChain: FilterChain): Unit = {
    val response = servletResponse.asInstanceOf[HttpServletResponse]
    val request = servletRequest.asInstanceOf[HttpServletRequest]
    response.setHeader("Access-Control-Allow-Origin", "*")
    response.setHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT,OPTIONS")
    response.setHeader("Access-Control-Allow-Headers", "*")
    response.setHeader("Access-Control-Allow-Credentials", "true")
    response.setHeader("Access-Control-Max-Age", "3600")
    filterChain.doFilter(servletRequest, servletResponse)
  }

  def destroy(): Unit = {
  }
}
