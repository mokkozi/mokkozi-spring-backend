package com.project.mokkozi.model;

import jakarta.persistence.*;
import lombok.*;

//@Setter
@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 데이터베이스에 기본 키 생성을 위임 (PostgreSQL 내부 설정으로 돌아가도록)
    private Long id;

    @Column(nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column
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

    @Column
    private String refreshToken;

    public Member() {

    }

    public static Member of(String title, String author) {
        Member member = new Member();
        //member.title = title;
        return member;
    }

}
