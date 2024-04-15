package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

@SpringBootApplication
/*
* JavaConfig 설정
* @EnableJpaRepositories(basePackages = "study.datajpa.repository")
* 스프링 부트 사용시 생략이 가능하다.
* 스프링 부트 사용시 @SpringBootApplication 위치를 지정
* 만약 위치가 달라진다면 사용해야 한다.
* */
@EnableJpaAuditing // 스프링 데이터 JPA에서 자동으로 컬럼 변경사항을 기록할 수 있게 해줌.
public class DataJpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataJpaApplication.class, args);
    }

    // 등록자, 수정자 auditing
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of(UUID.randomUUID().toString());
    }

}
