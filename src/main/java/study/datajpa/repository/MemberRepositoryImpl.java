package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import java.util.List;

// 간단한 기능은 jpa를 통해 해결하지만 복잡한 쿼리가 필요한 경우 이처럼 쓰는데 QueryDsl을 붙여서 쓴다고 함
// 이렇게 쓰다보면 여기 구현에 모든 기능이 집중되는 경우가 많은데 그럼 복잡도가 올라감. 용도에따라 따로 레포지토리를 더 두는것도 좋은 방법임(MemberQueryRepository 참고)
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom { // 구현체 이름은 레포지토리명 + Impl이 붙는것이 규칙

    private final EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }
}
