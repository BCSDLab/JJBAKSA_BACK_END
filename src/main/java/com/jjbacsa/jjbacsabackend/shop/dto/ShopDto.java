package com.jjbacsa.jjbacsabackend.shop.dto;

import com.jjbacsa.jjbacsabackend.etc.enums.ErrorMessage;
import com.jjbacsa.jjbacsabackend.etc.exception.CriticalException;
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

    public static ShopDto ShopDto(ShopApiDto shopApiDto) {

        if (shopApiDto.getName() == null || shopApiDto.getGeometry() == null ||
            shopApiDto.getTypes().isEmpty() || shopApiDto.getPlace_id()==null ||
                shopApiDto.getFormatted_address()==null )
            throw new CriticalException(ErrorMessage.REQUIRED_ATTRIBUTE_MISSING_EXCEPTION);

        if(shopApiDto.getOpening_hours()==null){
            return ShopDto.builder()
                    .placeName(shopApiDto.getName())
                    .address(shopApiDto.getFormatted_address())
                    .placeId(shopApiDto.getPlace_id())
                    .x(shopApiDto.getGeometry().getLocation().getLng())
                    .y(shopApiDto.getGeometry().getLocation().getLat())
                    .categoryName(shopApiDto.getTypes().get(0))
                    .phone(shopApiDto.getFormatted_phone_number())
                    .businessDay(null)
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
                    .x(shopApiDto.getGeometry().getLocation().getLng())
                    .y(shopApiDto.getGeometry().getLocation().getLat())
                    .categoryName(shopApiDto.getTypes().get(0))
                    .phone(shopApiDto.getFormatted_phone_number())
                    .businessDay(jsonArray.toJSONString())
                    .build();
        }
    }
}