package com.project.mokkozi.auth;

import com.project.mokkozi.entity.Member;
import com.project.mokkozi.repository.MemberRepository;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private MemberRepository memberRepository;

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

            /* todo 분기 처리 필요
            if(salt == null || salt.length() == 0) { // salt가 비어있을 경우 회원가입으로 간주함
                salt = getRandomSalt();
                // todo db 저장 로직 추가
            }
            */

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

}
