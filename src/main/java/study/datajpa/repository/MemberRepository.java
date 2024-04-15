package study.datajpa.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.dto.UsernameOnlyDto;
import study.datajpa.entity.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom, JpaSpecificationExecutor<Member> {

    // 알아서 이름에 맞는 SQL문을 작성한다... ㄷㄷㄷㄷ => entity 스펙바뀌면 런타임시 에러가난다. 함꼐 바꿔줘야함.
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    // 이름 명명 규칙 find...By - 로우 3개만 가져옴
    List<Member> findTop3By();

    // entity에 만든 NamedQuery 사용하기
//    @Query(name = "Member.findByUsername") // 이름을 지정해 놓은 경우 이렇게 생략도 가능, 이름을 못찾은 경우 위의 예제처럼 sql을 작성함
    List<Member> findByUsername(@Param("username") String username);

    @Query("select m from Member m where m.username = :username and m.age = :age") // 오타시 에러가 발생
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    // Dto로 반환시..
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    // :name 같은걸 이름기반 파라미터 바인딩이라고 부름.. ?0 ?1 처럼 위치기반 바인딩도 존재하지만 실용적이진 않을듯...
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    // 다양한 반환 타입을 지원해준다.
    List<Member> findListByUsername(String username); // 컬렉션
    Member findMemberByUsername(String username); // 단건
    Optional<Member> findOptionalByUsername(String username); // 단건 Optional

    // 카운트 쿼리 최적화 - 따로 카운팅에 대한 설정이없으면 join한 쿼리 그대로 카운팅이 들어가서 느려질 수 있기 때문에 이런식으로 최적화가 가능하다.
    // 하이버네이트6에서는 자동으로 이 최적화를 진행한다고 함.
    @Query(
            value = "select m from Member m"
//            value = "select m from Member m left join m.team t",
//            countQuery = "select count (m.username) from Member m"
    )
    Page<Member> findByAge(int age, Pageable pageable);
// Slice를 리턴하려면 여기서도 Slice로..
//    Slice<Member> findByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true) // 벌크성 update, delete 시 필요, clearAutomatically 영속성 컨텍스트를 초기화해주는 옵션
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    // * @EntityGraph를 쓰면 fetch join을 편리하게 사용할 수 있다.
    @Override // JPA 메서드를 오버라이드
    @EntityGraph(attributePaths = {"team"}) // jpql을 쓰지 않고 멤버 조회시 팀도 같이 조회함
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"}) // 이런식으로 jpql에 fetch join을 추가할 수도 있다.
    @Query("SELECT m FROM Member m")
    List<Member> findMemberEntityGraph();

//    @EntityGraph(attributePaths = {"team"}) // 메서드명 쿼리생성에도 사용 가능
    @EntityGraph("Member.all") // entity에서 만든 NamedEntityGraph 사용하는 방법
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    // JPA에 변경감지를 쓰지 않을것이라고 알림
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    // select ... for update => DB에 셀렉트시 다른 접근에 lock을 건다. => 꼭 써야하는 경우가 아니라면 다른 기능으로 풀어내는게 좋다고 함.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);

//    List<UsernameOnlyDto> findProjectionsByUsername(@Param("username") String username);
    // 제네릭을 활용해서 동적으로도 쓸 수 있다.
    <T> List<T> findProjectionsByUsername(@Param("username") String username, Class<T> type);

    @Query(value = "select * from member where username = ?", nativeQuery = true)
    Member findByNativeQuery(String username);

    @Query(
            value = "select m.member_id as id, m.username, t.name as teamName " +
                    "from member m left join team t",
            countQuery = "select count(*) from member",
            nativeQuery = true
    )
    Page<MemberProjection> findByNativeProjection(Pageable pageable);
}
