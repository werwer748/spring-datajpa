package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {

//    @Value("#{target.username + ' ' + target.age}") // 사용시 open projections
    String getUsername(); // @Value없이 사용시 close projections

}
