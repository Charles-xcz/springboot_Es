package com.ustc.charles.dto;

import lombok.Data;

/**
 * @author charles
 * @date 2020/4/4 21:22
 */
@Data
public class MapAddressDto {
    private int status;
    private Result result;

    @Data
    class Result {
        private Location location;
        private String formattedAddress;
        private String business;
        private AddressComponent addressComponent;

        @Data
        class Location {
            public float lat;
            public float lng;
        }

        @Data
        class AddressComponent {
            public String country;
            public String province;
            public String city;
            public String district;
            public String town;
            public String street;
            public String streetNumber;
            public String direction;
        }
    }
}
