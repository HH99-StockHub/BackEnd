package com.sparta.stockhub.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public BCryptPasswordEncoder encodePassword() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) {
        web
                .ignoring()
                .antMatchers("/h2-console/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/**").permitAll()
                // 어떤 요청이든 '인증'
                .anyRequest().authenticated()
                .and()
//                // 로그인 기능 허용
//                .formLogin()
//                .loginPage("/user/login") // 로그인 뷰
//                .loginProcessingUrl("/user/login") // 로그인 처리
//                .defaultSuccessUrl("/") // 로그인 성공
//                .failureUrl("/user/login?error") // 로그인 실패
//                .permitAll()
//                .and()
                // 로그아웃 기능 허용
                .logout()
                .logoutUrl("/user/logout") // 로그아웃
                .permitAll();
    }
}