package com.ustc.charles.dto;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author charles
 * @date 2020/1/25 9:42
 */
@ToString
@Getter
public class PaginationDTO {
    private Boolean showFirstPage;
    private Boolean showPrevious;
    private Boolean showNextPage;
    private Boolean showEndPage;
    private Integer currentPage;
    private Integer totalPage;
    private List<Integer> pages;

    public void setPagination(Integer totalPage, Integer currentPage, Integer size) {
        pages = new ArrayList<>();
        this.totalPage = totalPage;
        this.currentPage = currentPage;
        pages.add(currentPage);
        for (int i = 1; i <= 3; ++i) {
            if (currentPage - i > 0) {
                pages.add(0, currentPage - i);
            }
            if (currentPage + i <= totalPage) {
                pages.add(currentPage + i);
            }
        }
        //判断是否展示上一页
        showPrevious = currentPage != 1;
        //判断是否展示下一页
        showNextPage = !currentPage.equals(totalPage);
        //判断是否展示第一页
        showFirstPage = !pages.contains(1);
        //判断是否展示最后一页
        showEndPage = !pages.contains(totalPage);
    }
}
