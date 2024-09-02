package com.project.mokkozi.model;

import com.project.mokkozi.dto.MemberDto;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.util.StringUtils;

//@Setter
@Slf4j
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate /* 변경된 컬럼만 반영*/
@Table(name = "member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 데이터베이스에 기본 키 생성을 위임 (PostgreSQL 내부 설정으로 돌아가도록)
    private Long id;

    @Column(nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String salt;

    @Column(nullable = false)
    private String name;

    @Column
    private String category1;

    @Column
    private String category2;

    @Column
    private String category3;

    @Column
    private Integer warningCnt;

    /**
     * 회원정보 수정
     * id로 추출한 기존 Member와 수정될 정보를 가진 MemberDto를 비교하여
     * null이 아닌 경우만 업데이트
     * */
    public void update(MemberDto requestMemberDto) {
        if(requestMemberDto.getLoginId() != null) this.loginId = requestMemberDto.getLoginId();
        if(requestMemberDto.getPassword() != null) this.password = requestMemberDto.getPassword();
        if(requestMemberDto.getName() != null) this.name = requestMemberDto.getName();
        if(requestMemberDto.getCategory1() != null) this.category1 = requestMemberDto.getCategory1();
        if(requestMemberDto.getCategory2() != null) this.category2 =  requestMemberDto.getCategory2();
        if(requestMemberDto.getCategory3() != null) this.category3 =  requestMemberDto.getCategory3();
        if(requestMemberDto.getWarningCnt() != null) this.warningCnt = requestMemberDto.getWarningCnt();
    }
}
