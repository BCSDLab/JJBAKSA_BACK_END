package com.jjbacsa.jjbacsabackend.etc.enums;

import lombok.Getter;

@Getter
public enum TokenType {
    ACCESS(12, true), REFRESH(24*14, false);

    private int tokenRemainTime;
    private boolean isAccess;

    TokenType(int tokenRemainTime, boolean isAccess){
        this.tokenRemainTime = tokenRemainTime;
        this.isAccess = isAccess;
    }
}
