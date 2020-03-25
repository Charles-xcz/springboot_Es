package com.ustc.charles.controller;

import com.ustc.charles.dao.HouseDao;
import com.ustc.charles.dao.QueryDao;
import com.ustc.charles.dto.FieldAttributeDTO;
import com.ustc.charles.dto.QueryParamDTO;
import com.ustc.charles.model.House;
import com.ustc.charles.model.Page;
import com.ustc.charles.service.EsHouseService;
import com.ustc.charles.service.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author charles
 * @date 2020/3/24 21:16
 */
@Controller
public class HomeController {
    @Autowired
    private HouseDao houseDao;
    @Autowired
    private QueryDao queryDao;
    @Autowired
    private EsHouseService eshouseService;

    @GetMapping("/index")
    public String getIndex0Page(Model model, Page page, HttpServletRequest request,
                                @RequestParam(name = "orderMode", defaultValue = "default") String orderMode) {
        page.setLimit(10);
        page.setPath("/index?orderMode=" + orderMode);
        page.setRows((int) eshouseService.getCount());
        /*
        将属性聚合,返回前端作为筛选条件
         */
        List<FieldAttributeDTO> fieldAttributes = queryDao.getFieldAttribute();
        model.addAttribute("fieldAttributes", fieldAttributes);

        List<House> houses = eshouseService.listByPage(page.getCurrent(), page.getLimit(), orderMode);
        Map<String, String[]> parameterMap = request.getParameterMap();
        StringBuilder sb = new StringBuilder();
        for (String key : parameterMap.keySet()) {
            if (!key.equals("current")) {
                String[] values = parameterMap.get(key);
                for (String value : values) {
                    sb.append(key).append("=").append(value).append("&");
                }
            }
        }
        model.addAttribute("params", sb.toString());
        model.addAttribute("houses", houses);
        model.addAttribute("page", page);
        model.addAttribute("orderMode", orderMode);
        return "index";
    }

//    @PostMapping("/search")
//    public String search(QueryParamDTO queryParamDTO, Page page) {
//
//    }
}
