package com.project.mokkozi.service;

import com.project.mokkozi.auth.JWTProvider;
import com.project.mokkozi.entity.Member;
import com.project.mokkozi.model.ApiResponse;
import com.project.mokkozi.model.JoinRequest;
import com.project.mokkozi.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional // DB의 일관성을 유지하기 위해 service 단에 transaction을 걸어줌
@RequiredArgsConstructor
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JWTProvider jwtProvider;

    public Member login(Member reqMember) {
        Optional<Member> optionalMember = Optional.ofNullable(memberRepository.findByLoginId(reqMember.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다.")));

        Member member = optionalMember.get();

        if(!member.getPassword().equals(reqMember.getPassword())){
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        return member;
    }

    /**
     * 사용자 생성 및 생성된 사용자 반환
     * <p>
     * @param member 생성할 사용자 정보
     * @return 생성된 사용자 정보
     */
    public Member createMember(Member member) {
       return memberRepository.save(member);
    }

    /**
     * 모든 사용자 정보를 조회
     * <p>
     * @return 사용자 정보가 존재할 경우 member, 그렇지 않을 경우 null 반환
     */
    public List<Member> readMembers() {
        return memberRepository.findAll();
    }

    public Member readMember(Long id) {
        Optional<Member> readMember = memberRepository.findById(id);
        if(readMember.isPresent()) {
            return readMember.get();
        }
        throw new EntityNotFoundException("Cannot find member id, id : " + id);
    }

    /**
     * id에 해당하는 사용자 정보 조회
     * <p>
     * @param id 조회할 사용자명
     * @return 사용자 정보가 존재할 경우 member, 그렇지 않을 경우 null 반환
     */
    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    /**
     * memberName에 해당하는 사용자 정보 조회
     * <p>
     * @param memberName 조회할 사용자명
     * @return 사용자 정보가 존재할 경우 member, 그렇지 않을 경우 null 반환
     */
    public Optional<Member> findByName(String memberName) {
        return memberRepository.findByName(memberName);
    }

    /**
     * id에 해당하는 member 정보 수정
     * <p>
     * @param id 조회할 사용자명
     * @param member 수정할 정보가 담긴 member 객체
     * @return 사용자 정보가 존재하지 않을 경우 EntityNotFoundException, 존재할 경우 값 수정(set)
     */
    public Member updateMember(Long id, Member member) {
        Optional<Member> findMember = memberRepository.findById(id);
        if(!findMember.isPresent()) {
            throw new EntityNotFoundException("member not present, id : " + id);
        }

        Member updateMember = findMember.get();
        updateMember.setPassword(member.getPassword());
        updateMember.setName((member.getName()));

        return memberRepository.save(updateMember);
    }

    /**
     * 사용자 제거
     * <p>
     * @param id 삭제할 사용자 아이디
     * @return 생성된 사용자 정보
     */
    public String deleteMember(Long id) {
        memberRepository.deleteById(id);
        return "ok";
    }

    public boolean checkLoginIdDuplicate(String loginId) {
        return memberRepository.existsByLoginId(loginId);
    }

    /*public ApiResponse join(JoinRequest request) {
        return new ApiResponse(200, "회원가입 성공", null);
    }*/

}
