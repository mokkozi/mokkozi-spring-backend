package com.project.mokkozi.controller;

import com.project.mokkozi.auth.JWTProvider;
import com.project.mokkozi.auth.SHA256Util;
import com.project.mokkozi.service.LoginService;
import com.project.mokkozi.dto.ApiResponseDto;
import com.project.mokkozi.dto.MemberDto;
import com.project.mokkozi.model.Member;
import com.project.mokkozi.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/members")
/*
    Create : POST (Body O)
        - /member : 새로운 member 데이터 생성
    Read : GET (Body X)
        - /member : Post 테이블의 모든 데이터 보여주기
        - /member?id={memberId}: userId가 일치하는 member 데이터 보여주기
    Update : PUT/PATCH (Body O)
        - /member/{id}: id가 일치하는 member 데이터 수정
    Delete : DELETE (Body X)
        - /member/{id}: id가 일치하는 member 데이터 삭제
 */
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private JWTProvider jwtProvider;

    /**
     * [createMember] 사용자 생성 및 생성된 사용자 반환
     * <p>
     * @param member 생성할 사용자 정보
     * @return 생성된 사용자 정보
     */
    /*@PostMapping
    public @ResponseBody ResponseEntity<Member> createMember(@RequestBody Member member) {
        return ResponseEntity.ok(memberService.createMember(member));
    }*/

    /**
     * [readMembers] 사용자 정보 조회
     * <p>
     * @param id 조회할 사용자 id (선택)
     * @return param으로 id가 넘어올 경우 해당 사용자 조회, 없을 경우 모든 사용자 조회
     */
    @GetMapping
    public @ResponseBody ResponseEntity readMembers(@RequestParam(value = "id", required = false) Long id) {
        // 프로필 조회
        if(id != null) { // member 단일 조회
            return ResponseEntity.ok(
                ApiResponseDto.res(HttpStatus.OK, "프로필 조회 성공",
                            memberService.readMember(id))
            );
        }
        // 회원목록 조회
        return ResponseEntity.ok(
            ApiResponseDto.res(HttpStatus.OK, "회원목록 조회 성공", memberService.readMembers())
        );
    }

    /**
     * [updateMember] id에 해당하는 member 정보 수정
     * <p>
     * @param id 조회할 사용자명
     * @param memberDto 수정할 정보가 담긴 member 객체
     * @return 사용자 정보가 존재하지 않을 경우 EntityNotFoundException, 존재할 경우 값 수정(set)
     */
    @PatchMapping
    public ResponseEntity<ApiResponseDto> updateMember (@PathVariable @RequestParam(value = "id") Long id, @RequestBody MemberDto memberDto) {
        return ResponseEntity.ok(
                ApiResponseDto.res(HttpStatus.OK, "프로필 수정 성공",
                        memberService.updateMember(id, memberDto))
        );
    }

    /**
     * [deleteMember] id에 해당하는 사용자 삭제
     * <p>
     * @param id 삭제할 사용자 id
     * @return 사용자 정보가 존재할 경우 해당 id 삭제, 그렇지 않을 경우 null 반환
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteMember(@PathVariable @RequestParam(value = "id") Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Member reqMember) {
        Member entityMember = loginService.login(reqMember);    // 1. 사용자 정보 확인
        String token = jwtProvider.generateToken(entityMember);  // 2. Jwt 생성

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);    // 3. header 내 Jwt 전달

        return ResponseEntity.ok().headers(headers).body(HttpStatus.OK);
    }

    /*@PostMapping("/members")
    public ApiResponse join(@RequestBody JoinRequest request) {
        return memberService.join(request);
    }*/

    @GetMapping("/duplication/{loginId}")
    public ResponseEntity<ApiResponseDto> checkLoginIdDuplicate(@PathVariable String loginId){
        if(memberService.checkLoginIdDuplicate(loginId)) {
            return ResponseEntity.ok(
                    ApiResponseDto.res(HttpStatus.BAD_REQUEST, "중복된 아이디 입니다.", null)
            );
        }
        else {
            return ResponseEntity.ok(
                    ApiResponseDto.res(HttpStatus.OK, "사용 가능한 아이디 입니다.", loginId)
            );
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto> join(@RequestBody Member member) {
        return ResponseEntity.ok(
                ApiResponseDto.res(HttpStatus.OK, "회원가입 성공", memberService.createMember(member))
        );
    }
}
