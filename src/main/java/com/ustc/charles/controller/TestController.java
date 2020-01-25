package com.ustc.charles.controller;

import com.ustc.charles.dao.HouseDao;
import com.ustc.charles.dao.QueryDao;
import com.ustc.charles.dto.PaginationDTO;
import com.ustc.charles.model.Es;
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
    private Es es;
    @Autowired
    private HouseDao houseDao;
    @Autowired
    private QueryDao queryDao;

    @RequestMapping("/count")
    public Long index() {
        return houseDao.getCount(es);
    }

    @RequestMapping("/index/{current}/{size}/{field}")
    public String indexSortedByField(@PathVariable("current") Integer current,
                                     @PathVariable("size") Integer size,
                                     @PathVariable("field") String field,
                                     Model model) {
        List<House> houses;
        if ("defult".equals(field)) {
            houses = queryDao.indexSplitPage(es, current, size);
        } else {
            houses = queryDao.indexSplitPage(es, current, size, field);
        }
        PaginationDTO paginationDTO = new PaginationDTO();
        int totalPage = (int) (houseDao.getCount(es) / size);
        paginationDTO.setPagination(totalPage, current, size);
        model.addAttribute("houses", houses);
        model.addAttribute("pagination", paginationDTO);
        model.addAttribute("field", field);
        return "index";
    }

    @RequestMapping("/delete/{id}")
    public String delete(@PathVariable("id") String id) {
        return houseDao.deleteById(es, id);
    }

    @RequestMapping("/search/{name}")
    public List<House> searchByName(@PathVariable("name") String name) {
        return queryDao.queryByName(es, name);
    }

    @RequestMapping("/search/{low}/{high}")
    public List<House> searchPrice(@PathVariable("low") Integer low, @PathVariable("high") Integer high) {
        return queryDao.queryByPriceRange(es, low, high);
    }
}
