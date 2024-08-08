package com.project.mokkozi.auth;

import com.project.mokkozi.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Optional;

@Component
@Slf4j
@Repository
public class SHA256Util {

    @Autowired
    private MemberService memberService;

    // random salt 값 생성
    public String getRandomSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[20];

        sr.nextBytes(salt); // 난수 생성

        StringBuffer sb = new StringBuffer();
        for(byte b : salt) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

    // salt + pwd 암호화 처리
    public String getEncryptPassword(String salt, String pwd) {
        String encodePassword = "";
        try {
            // SHA-256 알고리즘 변환을 위한 객체 생성
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // salt 와 pwd 를 합친 암호문자 생성
            md.update((salt + pwd).getBytes());
            byte[] saltPwd = md.digest();

            StringBuffer sb = new StringBuffer();
            for (byte b : saltPwd) {
                sb.append(String.format("%02x", b));
            }

            encodePassword = sb.toString();

        } catch (NoSuchAlgorithmException nsae) {
            nsae.printStackTrace();
        }

        return encodePassword;
    }

    /* // 분기 처리 했으나.. 필요없다고 판단됨 (우선 주석처리)
    public void saveSalt(String salt) {
        if(salt == null || salt.length() == 0) {
            salt = getRandomSalt();
            memberService.updateMember();

            //  db 저장 로직 추가
        }
    }*/

}
