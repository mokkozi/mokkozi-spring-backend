package com.project.mokkozi.repository;

import com.project.mokkozi.entity.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
// JpaRepository<User, Long> 인터페이스 : 스프링 데이터 JPA에서 제공하는 CRUD 메서드를 상속받아 사용할 수 있는 인터페이스
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByName(String memberName);
    Optional<Member> findByLoginId(String loginId);
    boolean existsByLoginId(String loginId);

    /*
    private final EntityManager em;

    public void createMember(Member member) {
        em.persist(member);
    }

    public void remove(Long id) {
        em.remove(findById(id));
    }

    public List<Member> findAllMember() {
        return em.createQuery("SELECT m FROM Member m", Member.class)
                .getResultList();
    }

    public Member findById(Long id) {
        return em.find(Member.class, id);
    }

    public Member findByName(String findName) {
        return em.createQuery("SELECT m FROM Member m WHERE m.name = :findName", Member.class)
                .setParameter("findName", findName)
                .getSingleResult();
    }

    public List<Member> findAll() {
        return em.createQuery("SELECT m FROM Member m", Member.class)
                .getResultList();
    }*/
}
