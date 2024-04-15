package study.datajpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class) // 엔티티에 auditing기능을 추가함.
@MappedSuperclass
@Getter
// 등록자, 수정자는 필요로하는경우만 쓸 수 있도록 상속받는 구조로 만드는게 유리하다.
public class BaseEntity extends BaseTimeEntity {

//    @CreatedDate
//    @Column(updatable = false)
//    private LocalDateTime createdDate;
//
//    @LastModifiedDate
//    private LocalDateTime lastModifiedDate;

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;
}
