package com.ustc.charles.controller;

import com.ustc.charles.dto.FieldAttributeDto;
import com.ustc.charles.dto.HouseBucketDto;
import com.ustc.charles.dto.Page;
import com.ustc.charles.dto.QueryParamDto;
import com.ustc.charles.entity.ServiceMultiResult;
import com.ustc.charles.entity.ServiceResult;
import com.ustc.charles.model.House;
import com.ustc.charles.model.SupportAddress;
import com.ustc.charles.service.AddressService;
import com.ustc.charles.service.EsHouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author charles
 * @date 2020/3/24 22:42
 */
@Controller
@Slf4j
@RequestMapping("house")
public class HouseController {

    @Autowired
    private EsHouseService esHouseService;
    @Autowired
    private AddressService addressService;


    @GetMapping("/detail/{id}")
    public String getHouseDetail(@PathVariable("id") String id, Model model) {
        House house = esHouseService.findById(id);
        model.addAttribute("house", house);
        return "site/house-detail";
    }
//
//    @GetMapping("/index")
//    public String getIndex0Page(Model model, Page page,
//                                @RequestParam(name = "orderMode", defaultValue = "default") String orderMode) {
//        page.setLimit(10);
//        page.setPath("/index?orderMode=" + orderMode);
//        page.setRows((int) esHouseService.getCount());
//        /*
//        将属性聚合,返回前端作为筛选条件
//         */
//        List<FieldAttributeDto> fieldAttributes = esHouseService.getFieldAttributes();
//        model.addAttribute("fieldAttributes", fieldAttributes);
//
//        List<House> houses = esHouseService.listByPage(page.getCurrent(), page.getLimit(), orderMode);
//        String sb = "";
//
//        model.addAttribute("params", sb);
//        model.addAttribute("houses", houses);
//        model.addAttribute("page", page);
//        model.addAttribute("orderMode", orderMode);
//        return "index";
//    }


    @GetMapping("/search")
    public String searchHouse(QueryParamDto queryParamDTO,
                              Page page, Model model, HttpServletRequest request,
                              @RequestParam(name = "orderMode", defaultValue = "default") String orderMode) {

        String paramString = getParamString(request);

        model.addAttribute("params", paramString);

        page.setPath("/house/search?" + paramString);

        ServiceMultiResult<FieldAttributeDto> fieldAttributes = esHouseService.getFieldAttributes("0");
        model.addAttribute("fieldAttributes", fieldAttributes.getResult());

        ServiceMultiResult<House> result = esHouseService.searchHouse(queryParamDTO, orderMode, page.getOffset(), page.getLimit());
        page.setLimit(10);
        page.setRows((int) result.getTotal());
        model.addAttribute("houses", result.getResult());
        model.addAttribute("totalHits", result.getTotal());
        model.addAttribute("page", page);
        model.addAttribute("orderMode", orderMode);
        model.addAttribute("keyword", queryParamDTO.getKeyword());
        return "search";
    }

    private String getParamString(HttpServletRequest request) {
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
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    @GetMapping("/map")
    public String rentMapPage(@RequestParam(value = "cityEnName") String cityEnName,
                              Model model,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        ServiceResult<SupportAddress> city = addressService.findCity(cityEnName);
        if (!city.isSuccess()) {
            redirectAttributes.addAttribute("msg", "must_chose_city");
            return "redirect:/index";
        } else {
            session.setAttribute("cityName", cityEnName);
            model.addAttribute("city", city.getResult());
        }
        ServiceMultiResult<SupportAddress> regions = addressService.findAllRegionsByCityName(cityEnName);
        ServiceMultiResult<HouseBucketDto> serviceResult = esHouseService.mapAggregate(cityEnName);
        model.addAttribute("aggData", serviceResult.getResult());
        model.addAttribute("total", serviceResult.getTotal());
        model.addAttribute("regions", regions.getResult());
        return "rent-map";
    }

}
