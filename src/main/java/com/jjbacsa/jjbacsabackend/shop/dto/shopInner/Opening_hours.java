package com.jjbacsa.jjbacsabackend.shop.dto.shopInner;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Opening_hours {
    List<String> weekday_text=new ArrayList<>();
}