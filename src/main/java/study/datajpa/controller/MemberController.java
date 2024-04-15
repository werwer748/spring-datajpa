package study.datajpa.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }

    /**
     * ! 도메인 클래스 컨버터로 엔티티를 파라미터로 받으면, 이 엔티티는 단순 조회용으로만 사용해야 한다.
     * ! (트랜잭션이 없는 범위에서 엔티티를 조회했으므로, 엔티티를 변경해도 DB에 반영되지 않는다.)
     */
    @GetMapping("/members2/{id}") // 도메인 클래스 컨버터라는 기능 ㄷㄷㄷ
    public String findMember2(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    /**
     * /members?page=0&size=3&sort=id,desc
     * Pageable 을 파라미터로 받으면 위처럼 요청을 받아서 정렬된 데이터를 내려줄 수 있다. - 기본사이즈 20개.
     * yaml에서 글로벌로 Pageable의 기본 세팅을 바꿀수있다.
     * @PageableDefault 를 통해 해당 요청에서만 기본설정을 바꿀 수 있다. - 글로벌 설정보다 우선순위가 높음
     * yaml 에서 one-indexed-parameters: true 옵션을 키면 page 0, 1 값이 첫번째 페이지를 반환하는데 이경우 pageable내의 데이터가 꼬임.. 앵간하면 안쓰는게 좋을듯.
     */
    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5) Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
//        return page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
        return page.map(MemberDto::new); // dto에서 바로 member를 받아 처리하면 이렇게도 사용이 가능함.
    }

//    @PostConstruct
    public void init() {
        for (int i = 0; i < 100; i++) {
            memberRepository.save(new Member("user" + i, i));
        }

    }
}
