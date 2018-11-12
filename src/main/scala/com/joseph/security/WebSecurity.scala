package com.joseph.security

import com.joseph.dao.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.{EnableWebSecurity, WebSecurityConfigurerAdapter}
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.cors.{CorsConfiguration, CorsConfigurationSource, UrlBasedCorsConfigurationSource}

@EnableWebSecurity
@Configuration
class WebSecurity @Autowired ()(userDetailsServiceImp:UserDetailServiceImpl,
                                 passwordEncoder:BCryptPasswordEncoder,userService:UserService)
  extends WebSecurityConfigurerAdapter{
  override def configure(http: HttpSecurity): Unit = {
    http.cors().configurationSource(corsConfigurationSource()).and()
      .csrf()
      .disable()
      .authorizeRequests()
      .antMatchers(HttpMethod.POST,Constants.SIGN_UP_URL,Constants.PASSWORD_RESET)
      .permitAll()
      .antMatchers(HttpMethod.GET,Constants.FILE_URLS,Constants.SWAGGER,Constants.SWAGGER2,Constants.API_DOCS,Constants.SWAGGER_UI).permitAll()
      .anyRequest()
      .authenticated()
      .and()
      .addFilter(new JWTAuthenticationFilter(authenticationManager(),userService))
      .addFilter(new JWTAuthorizationFilter(authenticationManager(),userService))
     // .antMatcher("/dashboard/**").addFilter(new DashboardAccessFilter(authenticationManager(),userService))
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

  }



  @Bean def corsConfigurationSource(): CorsConfigurationSource = {
    val configuration = new CorsConfiguration
    configuration.addAllowedHeader("*")
    configuration.addAllowedOrigin("*")
    configuration.addAllowedMethod("GET")
    configuration.addAllowedMethod("POST")
    configuration.addAllowedMethod("PUT")
    configuration.addAllowedMethod("OPTIONS")
    configuration.addAllowedMethod("OPTION")
    //configuration.addAllowedMethod("*")
    val source = new UrlBasedCorsConfigurationSource()
    source.registerCorsConfiguration("/**", configuration)
    source
  }

//  import org.springframework.boot.web.servlet.FilterRegistrationBean
//  import org.springframework.context.annotation.Bean
//  import org.springframework.web.cors.CorsConfiguration
//
//  @Bean def corsFilter: FilterRegistrationBean[CorsFilter]= {
//    val source = new UrlBasedCorsConfigurationSource
//    val config = new CorsConfiguration
//    config.setAllowCredentials(true)
//    config.addAllowedOrigin("*")
//    config.addAllowedHeader("*")
//    config.addAllowedMethod("*")
//    source.registerCorsConfiguration("/**", config)
//    val bean = new FilterRegistrationBean(new CorsFilter(source))
//    bean.setOrder(0)
//    bean
//  }



  override def configure(auth: AuthenticationManagerBuilder): Unit = {
    auth.userDetailsService(userDetailsServiceImp).passwordEncoder(passwordEncoder)
  }

}
