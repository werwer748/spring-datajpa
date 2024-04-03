package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
/*
* JavaConfig 설정
* @EnableJpaRepositories(basePackages = "study.datajpa.repository")
* 스프링 부트 사용시 생략이 가능하다.
* 스프링 부트 사용시 @SpringBootApplication 위치를 지정
* 만약 위치가 달라진다면 사용해야 한다.
* */
public class DataJpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataJpaApplication.class, args);
    }

}
