package com.project.mokkozi.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;

@Getter
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
