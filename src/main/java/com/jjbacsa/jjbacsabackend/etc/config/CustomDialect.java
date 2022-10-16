package com.jjbacsa.jjbacsabackend.etc.config;

import org.hibernate.dialect.MySQL8Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypeTemplate;
import org.hibernate.type.StandardBasicTypes;

public class CustomDialect extends MySQL8Dialect {
    public CustomDialect() {
        super();

        registerFunction("match",
                new SQLFunctionTemplate(StandardBasicTypes.DOUBLE,"match(?1, ?2) against (?3 in boolean mode)"));
    }
}
