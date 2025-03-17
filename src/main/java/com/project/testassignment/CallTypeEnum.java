package com.project.testassignment;

public enum CallTypeEnum {
    OUTCOMING("01"),
    INCOMING("02");
    private CallTypeEnum(String typeCode){
        this.typeCode = typeCode;
    }
    private String typeCode;
    public String getTypeCode(){
        return this.typeCode;
    }
}
