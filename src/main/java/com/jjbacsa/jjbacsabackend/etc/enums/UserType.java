package com.jjbacsa.jjbacsabackend.etc.enums;

public enum UserType {
    NORMAL("ROLE_NORMAL"),
    ADMIN("ROLE_NORMAL, ROLE_ADMIN");

    private String authority;

    UserType(String authority){
        this.authority = authority;
    }

    public String getUserType(){
        return authority;
    }
}
