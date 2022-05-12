package com.ooamo.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@MapperScan(basePackages = "com.ooamo.mapper")
@Configuration
@EnableTransactionManagement
public class MybatisPlusConfig {

    /**
     * 分页插件
     * local variable 'xxx' is redudant
     * 因为可以不使用局部变量更简单的编写此代码, 因此局部变量是多余的。
     * 如以下这段代码
     * CorsFilter corsFilter = new CorsFilter(source);
     *      return corsFilter;
     * 可改成
     * return new CorsFilter(source);
     * 要加上Bean
     * @return
     */
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        return new PaginationInterceptor();
    }

}
