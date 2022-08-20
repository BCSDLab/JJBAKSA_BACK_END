package com.jjbacsa.jjbacsabackend.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.json.simple.JSONArray;
import org.springframework.lang.Nullable;

@Getter
@Builder
@AllArgsConstructor
public class ShopDto {
    private String placeId;
    private String placeName;
    private String x;
    private String y;
    private String categoryName;
    private String address;

    @Nullable
    private String phone;

    @Nullable
    private String businessDay;

    public static ShopDto ShopDto(ShopApiDto shopApiDto){

        if(shopApiDto.getOpening_hours().getWeekday_text().isEmpty()){
            return ShopDto.builder()
                    .placeName(shopApiDto.getName())
                    .address(shopApiDto.getFormatted_address())
                    .placeId(shopApiDto.getPlace_id())
                    .x(shopApiDto.getGeometry().getLocation().getLat())
                    .y(shopApiDto.getGeometry().getLocation().getLng())
                    .categoryName(shopApiDto.getTypes().get(0))
                    .phone(shopApiDto.getFormatted_phone_number())
                    .build();
        }else {
            JSONArray jsonArray=new JSONArray();
            for(String weekday:shopApiDto.getOpening_hours().getWeekday_text()){
                jsonArray.add(weekday);
            }

           return ShopDto.builder()
                    .placeName(shopApiDto.getName())
                    .address(shopApiDto.getFormatted_address())
                    .placeId(shopApiDto.getPlace_id())
                    .x(shopApiDto.getGeometry().getLocation().getLat())
                    .y(shopApiDto.getGeometry().getLocation().getLng())
                    .categoryName(shopApiDto.getTypes().get(0))
                    .phone(shopApiDto.getFormatted_phone_number())
                    .businessDay(jsonArray.toJSONString())
                    .build();
        }
    }
}