package com.jjbacsa.jjbacsabackend.etc.enums;


public enum BoardType {
    NOTICE("NOTICE"), POWER_NOTICE("POWER_NOTICE");

    String boardType;

    BoardType(String boardType) {
        this.boardType = boardType;
    }

    public String getBoardType() {
        return this.boardType;
    }
}
