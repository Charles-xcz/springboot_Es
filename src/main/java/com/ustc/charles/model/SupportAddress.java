package com.ustc.charles.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author charles
 * @date 2020/3/27 10:39
 */
@Data
@Accessors(chain = true)
public class SupportAddress implements Serializable {
    private Long id;
    @JsonProperty("belong_to")
    private String belongTo;
    @JsonProperty("en_name")
    private String enName;
    @JsonProperty("cn_name")
    private String cnName;
    private String level;
    private double baiduMapLongitude;
    private double baiduMapLatitude;

    public enum Level {
        /**
         * 行政级别定义
         */
        CITY("city"),
        REGION("region");
        private String value;

        Level(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Level of(String value) {
            for (Level level : Level.values()) {
                if (level.getValue().equals(value)) {
                    return level;
                }
            }
            throw new IllegalArgumentException();
        }
    }
}
