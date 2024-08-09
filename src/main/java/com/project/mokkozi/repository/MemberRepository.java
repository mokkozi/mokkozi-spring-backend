package com.project.mokkozi.repository;

import com.project.mokkozi.dto.MemberDto;
import com.project.mokkozi.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
// JpaRepository<User, Long> 인터페이스 : 스프링 데이터 JPA에서 제공하는 CRUD 메서드를 상속받아 사용할 수 있는 인터페이스
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByName(String memberName);
    Optional<Member> findByLoginId(String loginId);
    boolean existsByLoginId(String loginId);

}
