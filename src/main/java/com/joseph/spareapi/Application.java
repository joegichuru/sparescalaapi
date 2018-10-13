package com.joseph.spareapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;


@SpringBootApplication
public class Application{
    public static void main(String[] args) {
        SpringApplication.run(com.joseph.spareapi.Application.class,args);
        SpringApplicationBuilder app = new SpringApplicationBuilder(Application.class)
                .web(WebApplicationType.NONE);
        app.build().addListeners(new ApplicationPidFileWriter("./bin/shutdown.pid"));
        app.run();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){

        return new BCryptPasswordEncoder();
    }

    @Bean
    public ByteArrayHttpMessageConverter byteArrayHttpMessageConverter(){
        ByteArrayHttpMessageConverter converter=new ByteArrayHttpMessageConverter();
        converter.setSupportedMediaTypes(supportedMediaTypes());
        return converter;
    }

    public List<MediaType> supportedMediaTypes(){
        List<MediaType> mediaTypes=new ArrayList<>();
        mediaTypes.add(MediaType.IMAGE_JPEG);
        mediaTypes.add(MediaType.IMAGE_PNG);
        mediaTypes.add(MediaType.IMAGE_GIF);
        return mediaTypes;
    }


}