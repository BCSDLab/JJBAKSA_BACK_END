package com.jjbacsa.jjbacsabackend.google.dto.inner;

import lombok.Data;


@Data
public class Photos {
    int height;
    String[] html_attributions;
    String photo_reference; //token
    int width;
}
