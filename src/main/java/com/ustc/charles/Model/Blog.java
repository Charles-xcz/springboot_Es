package com.ustc.charles.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author charles
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Blog {
    private Integer id;
    private String title;
    private String author;
    private String content;
    private Date create_time;
    private Date update_time;

    public Blog(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
