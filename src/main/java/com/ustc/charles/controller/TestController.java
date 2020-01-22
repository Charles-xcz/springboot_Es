package com.ustc.charles.controller;

import com.ustc.charles.dao.HouseDao;
import com.ustc.charles.dao.QueryDao;
import com.ustc.charles.model.Es;
import com.ustc.charles.model.House;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author charles
 * @date 2020/1/20 13:37
 */
@Controller
@ResponseBody
public class TestController {

    @Autowired
    private Es es;
    @Autowired
    private HouseDao houseDao;
    @Autowired
    private QueryDao queryDao;

    @RequestMapping("/")
    public Long index() {
        return houseDao.getCount(es);
    }

    @RequestMapping("/add")
    public void add() {
        for (int i = 0; i < 10; i++) {
            House house = new House("" + i, "char" + i, "" + i, "" + i, "" + i, "" + i, "" + i, "" + i, "" + i, "" + i, "" + i, "" + i, "" + i, "" + i, i, i, "" + i, "" + i, "" + i);
            houseDao.add(es, house);
        }

    }

    @RequestMapping("/update")
    public String update() {
        House house = new House("" + 2, "char" + "i", "" + "i", "" + "i", "" + "i", "" + "i", "" + "i", "" + "i", "" + "i", "" + "i", "" + "i", "" + "i", "" + "i", "" + "i", 88, 77, "" + "i", "" + "i", "" + "i");
        return houseDao.update(es, house);
    }

    @RequestMapping("/all")
    public List<House> queryAll() {
        return queryDao.queryAll(es);
    }

    @RequestMapping("/page/{current}/{size}")
    public List<House> queryBySplitPage(@PathVariable("current") Integer current,
                                        @PathVariable("size") Integer size) {
        return queryDao.queryBySplitPage(es, current, size);
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
