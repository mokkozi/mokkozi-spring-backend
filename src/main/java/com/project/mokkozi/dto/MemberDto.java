package com.project.mokkozi.dto;

import com.project.mokkozi.model.Member;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberDto {
    protected Long id;
    protected String loginId;
    protected String password;
    protected String name;
    protected String category1;
    protected String category2;
    protected String category3;
    protected Integer warningCnt;

}
