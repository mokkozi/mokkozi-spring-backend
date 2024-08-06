package com.project.mokkozi.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
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

}
