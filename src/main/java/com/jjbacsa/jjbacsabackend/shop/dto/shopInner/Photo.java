package com.jjbacsa.jjbacsabackend.shop.dto.shopInner;

import lombok.Data;

import java.util.List;

@Data
public class Photo {
    int height;
    List<String> html_attributions;
    String photo_reference;
    int width;
}
