package com.ustc.charles.controller;

import com.ustc.charles.dao.QueryDao;
import com.ustc.charles.dto.FieldAttributeDTO;
import com.ustc.charles.dto.QueryParamDTO;
import com.ustc.charles.model.House;
import com.ustc.charles.model.Page;
import com.ustc.charles.service.EsHouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author charles
 * @date 2020/3/24 22:42
 */
@Controller
@Slf4j
public class HouseController {
    @Autowired
    private QueryDao queryDao;
    @Autowired
    private EsHouseService esHouseService;

    @GetMapping("/detail/{id}")
    public String getHouseDetail(@PathVariable("id") String id, Model model) {
        House house = queryDao.getById(id);
        model.addAttribute("house", house);
        return "site/house-detail";
    }

    @GetMapping("/search")
    public String searchHouse(QueryParamDTO queryParamDTO,
                              Page page, Model model, HttpServletRequest request,
                              @RequestParam(name = "orderMode", defaultValue = "default") String orderMode) {
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
        page.setPath("/search?" + sb.toString());

        List<FieldAttributeDTO> fieldAttributes = queryDao.getFieldAttribute();
        model.addAttribute("fieldAttributes", fieldAttributes);

        Map<String, Object> map = esHouseService.searchHouse(queryParamDTO, page.getOffset(), page.getLimit());
        List<House> houses = (List<House>) map.get("houses");
        long totalHits = (long) map.get("totalHits");
        page.setLimit(10);
        page.setRows((int) totalHits);
        model.addAttribute("houses", houses);
        model.addAttribute("totalHits", totalHits);
        model.addAttribute("page", page);
        model.addAttribute("orderMode", orderMode);
        model.addAttribute("keyword", queryParamDTO.getKeyword());
        return "search2";
    }

}
