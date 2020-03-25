package com.ustc.charles.controller;

import com.ustc.charles.dao.HouseDao;
import com.ustc.charles.dao.QueryDao;
import com.ustc.charles.dto.FieldAttributeDTO;
import com.ustc.charles.dto.PaginationDTO;
import com.ustc.charles.dto.QueryParamDTO;
import com.ustc.charles.model.House;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author charles
 * @date 2020/1/20 13:37
 */
@Controller
public class TestController {
    @Autowired
    private HouseDao houseDao;
    @Autowired
    private QueryDao queryDao;

    @RequestMapping("/count")
    public Long count() {
        return houseDao.getCount();
    }

    @RequestMapping("/index/{current}/{size}/{field}")
    public String indexSortedByField(@PathVariable("current") Integer current,
                                     @PathVariable("size") Integer size,
                                     @PathVariable("field") String field,
                                     Model model) {
        /*
        将属性聚合,返回前端作为筛选条件
         */
        List<FieldAttributeDTO> fieldAttributes = queryDao.getFieldAttribute();
        model.addAttribute("fieldAttributes", fieldAttributes);
        List<House> houses;
        if ("default".equals(field)) {
            houses = queryDao.listByPage(current, size);
        } else {
            houses = queryDao.listByPage(current, size, field);
        }
        PaginationDTO paginationDTO = new PaginationDTO();
        int totalPage = (int) (houseDao.getCount() / size);
        paginationDTO.setPagination(totalPage, current, size);
        model.addAttribute("houses", houses);
        model.addAttribute("pagination", paginationDTO);
        model.addAttribute("field", field);
        return "index2";
    }

    @RequestMapping("/house/detail/{id}")
    public String detail(@PathVariable("id") String id, Model model) {
        List<House> houses = queryDao.queryById(id);
        House house = houses.get(0);
        model.addAttribute("house", house);
        return "detail";
    }

    @RequestMapping("/delete/{id}")
    public String delete(@PathVariable("id") String id) {
        return houseDao.deleteById(id);
    }

    @RequestMapping("/search/{name}")
    public List<House> searchByName(@PathVariable("name") String name) {
        return queryDao.queryByName(name);
    }

    @RequestMapping("/search/{low}/{high}")
    public List<House> searchPrice(@PathVariable("low") Integer low, @PathVariable("high") Integer high) {
        return queryDao.queryByPriceRange(low, high);
    }

    @RequestMapping("/query/{current}/{size}")
    public String boolQuery(@PathVariable("current") Integer current,
                            @PathVariable("size") Integer size,
                            QueryParamDTO queryParam, Model model) {
        List<House> houses = (List<House>) queryDao.searchHouse(queryParam, current, size).get("houses");
        PaginationDTO paginationDTO = new PaginationDTO();
        int totalPage = (int) (houseDao.getCount() / size);
        paginationDTO.setPagination(totalPage, current, size);
        model.addAttribute("houses", houses);
        model.addAttribute("pagination", paginationDTO);
        model.addAttribute("keyword", queryParam.getKeyword());
        return "search";
    }
}
