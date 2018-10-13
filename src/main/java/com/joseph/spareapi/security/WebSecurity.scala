package com.joseph.spareapi.security


import com.joseph.spareapi.dao.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.{EnableWebSecurity, WebSecurityConfigurerAdapter}
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.cors.{CorsConfigurationSource, UrlBasedCorsConfigurationSource}

@EnableWebSecurity
class WebSecurity @Autowired ()(userDetailsServiceImp:UserDetailServiceImpl,
                                 passwordEncoder:BCryptPasswordEncoder,userService:UserService)
  extends WebSecurityConfigurerAdapter{
  override def configure(http: HttpSecurity): Unit = {
    http.cors().and()
      .csrf()
      .disable()
      .authorizeRequests()
      .antMatchers(HttpMethod.POST,Constants.SIGN_UP_URL)
      .permitAll()
       .antMatchers(HttpMethod.GET,Constants.FILE_URLS).permitAll()
      .antMatchers(HttpMethod.POST,Constants.ACTIVATE).permitAll()
      .anyRequest().authenticated()
      .and()
      .addFilter(new JWTAuthenticationFilter(authenticationManager(),userService))
      .addFilter(new JWTAuthorizationFilter(authenticationManager(),userService))
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
  }

  override def configure(auth: AuthenticationManagerBuilder): Unit = {
    auth.userDetailsService(userDetailsServiceImp).passwordEncoder(passwordEncoder)
  }

  import org.springframework.context.annotation.Bean
  import org.springframework.web.cors.CorsConfiguration

  @Bean def corsConfigurationSource: CorsConfigurationSource = {
    val source = new UrlBasedCorsConfigurationSource
    source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues)
    source
  }
}
