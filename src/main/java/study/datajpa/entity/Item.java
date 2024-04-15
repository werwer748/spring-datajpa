package study.datajpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

//@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
/**
 * jpa의 save는 pk값을 확인해서 persist, merge를 진행함
 * 여타의 이유로 id를 직접 넣어주는 경우 save를 사용시 merge를 진행하게됨.(객체 생성시 id를 직접 넣었으니까...)
 * 이 때 merge 는 select를 호출하고 DB에 값이 없기 떄문에 다시 save를 날림 - 기능상 좋지못함.
 * 그럴 때 사용하는게 Persistable!
 */
public class Item implements Persistable<String> {

    @Id
//    @GeneratedValue
    private String id;

    @CreatedDate
    private LocalDateTime createdDate;

    public Item(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public boolean isNew() {
        // 직접 persist, merge를 정의한다.
        return createdDate == null;
    }
}
