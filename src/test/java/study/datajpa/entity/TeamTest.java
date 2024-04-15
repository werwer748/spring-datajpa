package study.datajpa.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.repository.TeamRepository;

@SpringBootTest
@Transactional
@Rollback(false)
public class TeamTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    TeamRepository teamRepository;

    @Test
    public void BaseTimeEntity() throws Exception {
        // given
        Team team = new Team("teamA");
        teamRepository.save(team);

        Thread.sleep(1000);
        team.setName("teamB");

        em.flush();
        em.clear();

        // when
        Team findTeam = teamRepository.findById(team.getId()).get();

        // then
        System.out.println("findTeam.getCreatedDate() = " + findTeam.getCreatedDate()); // 어노테이션 중복문제 같은게 안생김
        System.out.println("findTeam.getLastModifiedDate() = " + findTeam.getLastModifiedDate());
//        System.out.println("findTeam.getCreatedBy = " + findTeam.getCreatedBy); // 없는게 당연
    }
}
