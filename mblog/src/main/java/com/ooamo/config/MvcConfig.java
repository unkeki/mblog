package com.ooamo.config;

import com.ooamo.common.lang.Consts;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 照片上传路径配置
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    Consts consts;

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //访问路径
        registry.addResourceHandler("/upload/avatar/**")
                //真是映射路径
                .addResourceLocations("file:///" + consts.getUploadDir() + "/avatar/");
    }

}
