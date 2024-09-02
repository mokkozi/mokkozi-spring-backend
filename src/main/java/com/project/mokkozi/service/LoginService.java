package com.project.mokkozi.service;

import com.project.mokkozi.auth.CustomUserDetails;
import com.project.mokkozi.auth.JWTProvider;
import com.project.mokkozi.auth.SHA256Util;
import com.project.mokkozi.dto.ApiResponseDto;
import com.project.mokkozi.dto.JWTDto;
import com.project.mokkozi.dto.MemberDto;
import com.project.mokkozi.model.Member;
import com.project.mokkozi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private SHA256Util sha256Util;

    @Autowired
    private JWTProvider jwtProvider;

    /**
     * 로그인 프로세스
     * <p>
     * @param loginReqMemberDto 로그인 요청한 사용자 정보
     * @return 입력된 사용자 정보가 올바를 경우, 해당 사용자 정보 (reqMember)
     */
    // TODO exception 통일 (ApiResponseDto)
    @Transactional
    public JWTDto login(MemberDto loginReqMemberDto) {
        log.info(":: LoginService >> login start ::");
        // 1. Authentication/CustomUserDetails를 통해 사용자 정보 확인
        Authentication authentication = new UsernamePasswordAuthenticationToken(loginReqMemberDto.getLoginId(), loginReqMemberDto.getPassword());
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getDetails();
        // 2. 사용자 정보 확인
        Optional<Member> optionalMember = Optional.ofNullable(
                memberRepository.findByLoginId(customUserDetails.getUsername()).orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 아이디입니다.")
                )
        );

        Member verifyingMember = optionalMember.get();
        boolean canLogin = isAuthenticated(loginReqMemberDto); // 입력된 아이디, 비밀번호 전달
        if(!canLogin){
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        // 3. 로그인한 사용자 token 생성
        JWTDto jwtDto = jwtProvider.generateAccessNRefreshToken(verifyingMember);

        // 3. refreshToken DB 저장
        saveRefreshToken(jwtDto, verifyingMember);
        log.info("::LoginService >> login end ::");


        return jwtDto;
    }

    @Transactional
    public JWTDto reissue(JWTDto reissueJwtDto) {
        if(!jwtProvider.validateToken(reissueJwtDto.getRefreshToken())) {
            throw new RuntimeException("Invalid Refresh Token");
        }

        Authentication authentication = jwtProvider.getAuthentication(reissueJwtDto.getAccessToken());

        Optional<Member> reissueOptionalMember = Optional.ofNullable(memberRepository.findByLoginId(authentication.getPrincipal().toString())
                .orElseThrow(() -> new RuntimeException("This Member status is Logout")));
        Member reissueMember = reissueOptionalMember.get();
        String currentRefreshToken = reissueMember.getRefreshToken();

        if(!currentRefreshToken.equals(reissueJwtDto.getRefreshToken())) {
            throw new RuntimeException("This Member info do not match with token");
        }

        JWTDto newJwtDto = null;
        if(!jwtProvider.validateToken(currentRefreshToken)) { // refresh Token 만료일 경우 (access + refresh)
            newJwtDto = jwtProvider.generateAccessNRefreshToken(reissueMember);
            saveRefreshToken(newJwtDto, reissueMember);
        } else { // refresh Token 만료되지 않았을 경우 (access)
            newJwtDto = jwtProvider.generateAccessToken(reissueMember); // todo check 다량의 요청이 한번에 들어올때 이슈?
        }

        return newJwtDto;
    }

    /**
     * 입력한 비밀번호와 DB에 저장된 비밀번호가 같은지 판별
     * <p>
     * @param loginReqMemberDto 입력된 사용자 정보
     * @return 로그인 가능 유무
     */
    private boolean isAuthenticated(MemberDto loginReqMemberDto) {
        boolean result = false;
        // Member savedMember = memberRepository.findByLoginId(reqMember.getLoginId()).get(); // 실제 DB에 저장된 사용자 정보

        String savedMemberSalt = memberRepository.findByLoginId(loginReqMemberDto.getLoginId()).get().getSalt(); // DB에 저장된 salt
        String reqMemberPassword = loginReqMemberDto.getPassword(); // 화면에 입력한 password (plain)

        // db에 저장된 salt + 입력한 password 조합
        String encryptPassword = sha256Util.getEncryptPassword(savedMemberSalt, reqMemberPassword); // 화면에 입력한 비밀번호를 암호화
        String savedPassword = memberRepository.findByLoginId(loginReqMemberDto.getLoginId()).get().getPassword(); // DB에 저장되어 있는 비밀번호를 select

        if(encryptPassword.equals(savedPassword)){ // encryptPassword 와 savedPassword 비교
            result = true;
        }

        return result;
    }

    private void saveRefreshToken(JWTDto jwtDto, Member verifyingMember) {
        MemberDto verifyingMemberDto = convertMember(verifyingMember);
        memberRepository.updateRefreshTokenByLoginId(jwtDto.getRefreshToken(), verifyingMemberDto.getLoginId());
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
                .refreshToken(member.getRefreshToken())
                .build();
    }

}
