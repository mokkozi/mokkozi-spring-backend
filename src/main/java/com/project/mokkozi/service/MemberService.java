package com.project.mokkozi.service;

import com.project.mokkozi.dto.ApiResponseDto;
import com.project.mokkozi.model.Member;
import com.project.mokkozi.dto.MemberDto;
import com.project.mokkozi.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.hibernate.UnknownEntityTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * @param id 조회할 사용자
     * @param requestMemberDto 수정할 정보가 담긴 MemberDto 객체
     * @return 사용자 정보가 존재하지 않을 경우 EntityNotFoundException, 필수값 입력하지 않을 경우 BadRequestException,
     *         필수값 입력 & 정보가 존재할 경우 값 수정(set)
     */
    public ApiResponseDto updateMember(Long id, MemberDto requestMemberDto) {
        try {
            Member beforeMember = memberRepository.findById(id).orElseThrow(()->new EntityNotFoundException("회원정보를 찾을 수 없습니다. id = " + id));

            // Entity클래스의 nullable = false인 컬럼은 필수값 체크(salt는 DB에는 not null인데 entity에는 안되어있음)
            if(!StringUtils.hasLength(requestMemberDto.getLoginId())
                    || !StringUtils.hasLength(requestMemberDto.getPassword())
                    || !StringUtils.hasLength(requestMemberDto.getName())
            ) {
                throw new BadRequestException("[로그인 ID, 비밀번호, 이름]은 필수값입니다.");
            }

            // Member 변경 대상 컬럼값 세팅
            beforeMember.update(beforeMember, requestMemberDto);

            MemberDto responseMemberDto = convertMember(memberRepository.save(beforeMember));

            return ApiResponseDto.res(HttpStatus.OK, "회원정보 수정 성공", responseMemberDto);
        }
        catch (EntityNotFoundException e) {
            return ApiResponseDto.res(HttpStatus.NOT_FOUND, e.getMessage(), null);
        }
        catch (BadRequestException e) {
            return ApiResponseDto.res(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
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
}
