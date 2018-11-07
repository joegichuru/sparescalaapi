package com.joseph.security

import com.joseph.dao.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.{EnableWebSecurity, WebSecurityConfigurerAdapter}
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.cors.{CorsConfigurationSource, UrlBasedCorsConfigurationSource}

@EnableWebSecurity
@Configuration
class WebSecurity @Autowired ()(userDetailsServiceImp:UserDetailServiceImpl,
                                 passwordEncoder:BCryptPasswordEncoder,userService:UserService)
  extends WebSecurityConfigurerAdapter{
  override def configure(http: HttpSecurity): Unit = {
    http.cors().and()
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
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

    http.antMatcher("/dashboard/**")
      .addFilter(new DashboardAccessFilter(authenticationManager(),userService))
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
