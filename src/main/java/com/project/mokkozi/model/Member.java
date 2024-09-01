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
        // 요청본문에 있는 값만 변경, 없으면 기존 값 세팅
        // null(값 변경사항 없음)과 빈문자열(값 지움)구분
        if(requestMemberDto.getLoginId() != null) this.loginId = requestMemberDto.getLoginId();
        //log.info("this. >> " + this.name);
        // todo. 비밀번호는 변경 보류 => this.password = (requestMemberDto.getPassword() != null) ? requestMemberDto.getPassword() : beforeMember.getPassword();
        // todo. 회원정보 수정대상이면 DTO에 추가 필요 => this.salt = (requestMemberDto.get() != null) ? requestMemberDto.getLoginId() : beforeMember.getLoginId();
        if(requestMemberDto.getName() != null) this.name = requestMemberDto.getName();
        if(requestMemberDto.getCategory1() != null) this.category1 = requestMemberDto.getCategory1();
        if(requestMemberDto.getCategory2() != null) this.category2 =  requestMemberDto.getCategory2();
        if(requestMemberDto.getCategory3() != null) this.category3 =  requestMemberDto.getCategory3();
        if(requestMemberDto.getWarningCnt() != null) this.warningCnt = requestMemberDto.getWarningCnt();
    }

    public static Member of(String title, String author) {
        Member member = new Member();
        //member.title = title;
        return member;
    }

}
