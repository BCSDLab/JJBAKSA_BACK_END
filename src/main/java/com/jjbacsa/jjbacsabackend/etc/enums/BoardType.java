package com.jjbacsa.jjbacsabackend.etc.enums;


public enum BoardType {
    NOTICE("NOTICE"), FAQ("FAQ"), INQUIRY("INQUIRY");

    String boardType;

    BoardType(String boardType){ this.boardType = boardType; }

    public String getBoardType() {return this.boardType; }
}
