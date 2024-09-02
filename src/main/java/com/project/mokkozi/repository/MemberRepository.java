package com.project.mokkozi.repository;

import com.project.mokkozi.dto.MemberDto;
import com.project.mokkozi.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
// JpaRepository<User, Long> 인터페이스 : 스프링 데이터 JPA에서 제공하는 CRUD 메서드를 상속받아 사용할 수 있는 인터페이스
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByName(String memberName);
    Optional<Member> findByLoginId(String loginId);
    boolean existsByLoginId(String loginId);

    // 1차 캐시를 비워주는 설정, 1차 캐시를 사용하지 않고 DB에 직접 쿼리를 날려야 하는 작업이므로 비워줘야 함
    // 비워주지 않을 경우 1차 캐시와 DB가 동기화되지 않아 예외가 발생함
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.refreshToken = :refresh_token where m.loginId = :login_id")
    void updateRefreshTokenByLoginId(@Param("refresh_token") String refreshToken, @Param("login_id") String loginId);
}
