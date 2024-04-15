package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.dto.UsernameOnlyDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.awt.print.Pageable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    /**
     * interface 만 주입했는데 어떻게 인젝션되서 사용하는건가??
     * 스프링 데이터 JPA 가 구현체를 만들어서 주입해주는거라고...
     */
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberQueryRepository memberQueryRepository; // 필요에의해 이런식으로 레포지토리를 더 두는건 안티패턴아님

    @Test
    public void testMember() {

        System.out.println("memberRepository.getClass() = " + memberRepository.getClass());
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get(); // 실무에서는 이렇게 꺼내쓰지말것!
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

//        findMember1.setUsername("member!!!!!"); // 변경감지 테스트

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2L);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deleteCount = memberRepository.count();
        assertThat(deleteCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findHelloBy() { // 쿼리확인용 => 진짜 멤버 전체조회 쿼리가 나감
        List<Member> helloBy = memberRepository.findTop3By();
    }

    @Test
    public void testNamedQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> result = memberRepository.findUsernameList();
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.changeTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType() {
        Member m1 = new Member("AAA", 10);
//        Member m2 = new Member("BBB", 20);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

//        List<Member> aaa = memberRepository.findListByUsername("AAA");
//        Member findMember = memberRepository.findMemberByUsername("AAA");
//        System.out.println("findMember = " + findMember);
//        Optional<Member> findMember = memberRepository.findOptionalByUsername("AAA");
//        System.out.println("findMember = " + findMember);

        // 조회결과 없을시 - 컬렉션
        List<Member> aaa = memberRepository.findListByUsername("ㅁㄴㅇㅁㄴㅇ");
        System.out.println("aaa = " + aaa); // [] null 이 아닌게 보장된다!
        System.out.println("aaa = " + aaa.size()); // 0

        // 조회결과 없을시 - 단일조회
//        Member findMember = memberRepository.findMemberByUsername("aaaaasd");
//        System.out.println("findMember = " + findMember); // null 반환

        // 조회결과 없을시 - Optional 사용
//        Optional<Member> findMember = memberRepository.findOptionalByUsername("asdasd");
//        System.out.println("findMember = " + findMember); // Optional.empty => .orElse 등으로 처리

        // 단일 조회인데 두건이상 조회된다면? => IncorrectResultSizeDataAccessException 에러 발생
//        Optional<Member> findMember = memberRepository.findOptionalByUsername("AAA");
//        System.out.println("findMember = " + findMember); // Optional.empty => .orElse 등으로 처리
    }

    @Test
    public void paging() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
        // Slice는 컨텐츠 갯수를 1개 더해서 호출함. count쿼리가 나가지 않는다.
//        Slice<Member> page = memberRepository.findByAge(age, pageRequest);

        // 편하게 Dto로 변환 가능
        Page<MemberDto> toMap = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        // then
        List<Member> content = page.getContent();

        assertThat(content.size()).isEqualTo(3); // 페이지 내 컨텐츠 갯수
        assertThat(page.getTotalElements()).isEqualTo(5); // 전체 컨텐츠 갯수
        assertThat(page.getNumber()).isEqualTo(0); // 현재 페이지
        assertThat(page.getTotalPages()).isEqualTo(2); // 전체 페이지
        assertThat(page.isFirst()).isTrue(); // 첫번째 페이지인지 확인
        assertThat(page.hasNext()).isTrue(); // 다음페이지가 있는지 확인
    }

    @Test
    public void bulkUpdate() {
        // given - 여기 쿼리 먼저 나가고(flush) repository의 jpql이 나간다.
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        // when
        int resultCount = memberRepository.bulkAgePlus(20);
        // 벌크 연산시 영속성 컨텍스트를 거치지 않고 바로 DB에 업데이트가 되기 때문에 JPA는 바뀌기 전 데이터를 가지고 있다.
        // 따라서 이렇게 초기화 해주어야 데이터를 정확히 조회함
//        em.flush(); // repository의 @Modifying를 통해 생략도 가능
//        em.clear();


        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = " + member5);

        // then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() {
        // given
        // member1 -> teamA
        // member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        List<Member> members = memberRepository.findEntityGraphByUsername("member1");

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.team = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint() {
        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        // when
        // 읽기로만 쓰는 설정을 걸었기 때문에 조회에만 최적화된 기능이 제공되기 때문에 영속성 컨텍스트를 활용한 변경감지를 사용할 수 없음.
        // 드라마틱한 성능개선은 없고... 잘 판단해서 적당히 쓸만한곳에서만 쓰면 됨
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2"); // 변경쿼리가 나가지 않는다.

        em.flush();
    }

    @Test
    public void lock() {
        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        // when
        List<Member> findMember = memberRepository.findLockByUsername("member1");
    }

    @Test
    public void callCustom() {
        List<Member> result = memberRepository.findMemberCustom();
    }

    @Test
    public void specBasic() {
        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        // when
        Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
        List<Member> result = memberRepository.findAll(spec);

        // then
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void queryByExample() {
        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        // when
        // Probe?
        Member member = new Member("m1");
        Team team = new Team("teamA");
        member.setTeam(team); // join

        ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("age");

        Example<Member> example = Example.of(member, matcher);

        List<Member> result = memberRepository.findAll(example);

        assertThat(result.get(0).getUsername()).isEqualTo("m1");
    }

    @Test
    public void projections() {
        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        // when
//        List<UsernameOnlyDto> result = memberRepository.findProjectionsByUsername("m1", UsernameOnlyDto.class);
        List<NestedClosedProjections> result = memberRepository.findProjectionsByUsername("m1", NestedClosedProjections.class);

        for (NestedClosedProjections nestedClosedProjections : result) {
            String username = nestedClosedProjections.getUsername();
            String teamName = nestedClosedProjections.getTeam().getName();
            System.out.println("username = " + username);
            System.out.println("teamName = " + teamName);
        }
    }

    @Test
    public void nativeQuery() {
        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        // when
//        Member result = memberRepository.findByNativeQuery("m1");
        Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(0, 10));
        List<MemberProjection> content = result.getContent();
        for (MemberProjection memberProjection : content) {
            System.out.println("memberProjection = " + memberProjection);
            System.out.println("memberProjection = " + memberProjection.getUsername());
            System.out.println("memberProjection = " + memberProjection.getTeamName());
        }
    }
}