package com.project.mokkozi.service;

import com.project.mokkozi.auth.SHA256Util;
import com.project.mokkozi.model.Member;
import com.project.mokkozi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class LoginService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private SHA256Util sha256Util;

    /**
     * 로그인 프로세스
     * <p>
     * @param reqMember 입력된 사용자 정보
     * @return 입력된 사용자 정보가 올바를 경우, 해당 사용자 정보 (reqMember)
     */
    // TODO exception 통일 (ApiResponseDto)
    public Member login(Member reqMember) {
        Optional<Member> optionalMember = Optional.ofNullable(memberRepository.findByLoginId(reqMember.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다.")));

        // 통과할 경우 reqMember의 loginId 존재함
        boolean canLogin = isAuthenticated(reqMember); // 입력된 아이디, 비밀번호 전달
        if(!canLogin){
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        return reqMember;
    }

    /**
     * 입력한 비밀번호와 DB에 저장된 비밀번호가 같은지 판별
     * <p>
     * @param reqMember 입력된 사용자 정보
     * @return 로그인 가능 유무
     */
    public boolean isAuthenticated(Member reqMember) {
        boolean result = false;
        Member savedMember = memberRepository.findByLoginId(reqMember.getLoginId()).get(); // 실제 DB에 저장된 사용자 정보
        // db에 저장된 salt + 입력한 password 조합

        String savedMemberSalt = savedMember.getSalt(); // DB에 저장된 salt
        String reqMemberPassword = reqMember.getPassword(); // 화면에 입력한 password

        String encryptPassword = sha256Util.getEncryptPassword(savedMemberSalt, reqMemberPassword); // 화면에 입력한 비밀번호를 암호화
        String savedPassword = memberRepository.findByLoginId(reqMember.getLoginId()).get().getPassword(); // DB에 저장되어 있는 비밀번호를 select

        if(encryptPassword.equals(savedPassword)){ // encryptPassword 와 savedPassword 비교
            result = true;
        }

        return result;
    }





}
