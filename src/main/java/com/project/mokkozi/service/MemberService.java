package com.project.mokkozi.service;

import com.project.mokkozi.model.Member;
import com.project.mokkozi.dto.MemberDto;
import com.project.mokkozi.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional // DB의 일관성을 유지하기 위해 service 단에 transaction을 걸어줌
@RequiredArgsConstructor
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

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
    public List<MemberDto> readMembers() {
        return memberRepository
                .findAll()
                .stream()
                .map(this::convertMember)
                .collect(Collectors.toList());
    }

    public MemberDto readMember(Long id) {
        Optional<Member> optionalMember = memberRepository.findById(id);
        if(optionalMember.isPresent()) {
            return convertMember(optionalMember.get());
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
     * @param memberDto 수정할 정보가 담긴 member 객체
     * @return 사용자 정보가 존재하지 않을 경우 EntityNotFoundException, 존재할 경우 값 수정(set)
     */
    public Member updateMember(Long id, MemberDto memberDto) {
        Optional<Member> optionalMember = memberRepository.findById(id);
        if(!optionalMember.isPresent()) {
            throw new EntityNotFoundException("member not present, id : " + id);
        }

        Member entityMember = optionalMember.get();

        if(StringUtils.hasLength(memberDto.getLoginId())) {
            entityMember.setLoginId((memberDto.getLoginId()));
        }
        if(StringUtils.hasLength(memberDto.getName())) {
            entityMember.setName((memberDto.getName()));
        }
        if(StringUtils.hasLength(memberDto.getPassword())) {
            entityMember.setPassword((memberDto.getPassword()));
        }
        log.info("check >> " + StringUtils.hasLength(memberDto.getCategory1()));
        if(StringUtils.hasLength(memberDto.getCategory1())) {
            log.info("check 2");
            entityMember.setCategory1((memberDto.getCategory1()));
        }
        if(StringUtils.hasLength(memberDto.getCategory2())) {
            entityMember.setCategory2((memberDto.getCategory2()));
        }
        if(StringUtils.hasLength(memberDto.getCategory3())) {
            entityMember.setCategory3((memberDto.getCategory3()));
        }
        if(memberDto.getWarningCnt() != null) {
            entityMember.setWarningCnt((memberDto.getWarningCnt()));
        }

        return memberRepository.save(entityMember);
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

    /* Member -> MemberDto 형변환 */
    private MemberDto convertMember(Member member) {
        return MemberDto.builder()
                .id(member.getId())
                .loginId(member.getLoginId())
                .password(member.getPassword())
                .name(member.getName())
                .category1(member.getCategory1())
                .category2(member.getCategory2())
                .category3(member.getCategory3())
                .warningCnt(member.getWarningCnt())
                .build();
    }
    /*public ApiResponse join(JoinRequest request) {
        return new ApiResponse(200, "회원가입 성공", null);
    }*/



}
