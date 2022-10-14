package com.jjbacsa.jjbacsabackend.shop.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ShopSummaryResponse implements Comparable<ShopSummaryResponse>{
    private Long shopId;
    private String placeId;
    private String placeName;
    private String address;
    private String x;
    private String y;
    private double dist;
    private double score;

    @QueryProjection
    public ShopSummaryResponse(Long shopId, String placeId, String placeName, String address, String x, String y, double score){
        this.shopId=shopId;
        this.placeId=placeId;
        this.placeName=placeName;
        this.address=address;
        this.x=x;
        this.y=y;
        this.score=score;
    }

    @QueryProjection
    public ShopSummaryResponse(Long shopId, String placeId, String placeName, String address, String x, String y){
        this.shopId=shopId;
        this.placeId=placeId;
        this.placeName=placeName;
        this.address=address;
        this.x=x;
        this.y=y;
    }

    public void setDist(double x, double y){
        double y_double=Double.valueOf(this.getY());
        double x_double=Double.valueOf(this.getX());

        double theta=y-y_double;
        double dist=Math.sin(deg2rad((x)))*Math.sin(deg2rad(x_double))
                +Math.cos(deg2rad(x))*Math.cos(deg2rad(x_double))*Math.cos(deg2rad(theta));
        dist=Math.acos(dist);
        dist=rad2deg(dist);
        dist*=60*1.1515*1609.344; //meter

        this.dist=dist;
    }

    private double deg2rad(double deg){
        return (deg* Math.PI/180.0);
    }

    private double rad2deg(double rad){
        return (rad*180/Math.PI);
    }

    @Override
    public int compareTo(ShopSummaryResponse o) {
        return (int)(this.getDist()-o.getDist());
    }
}
