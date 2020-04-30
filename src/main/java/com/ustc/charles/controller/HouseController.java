package com.ustc.charles.controller;

import com.ustc.charles.dto.*;
import com.ustc.charles.entity.ServiceMultiResult;
import com.ustc.charles.entity.ServiceResult;
import com.ustc.charles.model.House;
import com.ustc.charles.model.SupportAddress;
import com.ustc.charles.service.AddressService;
import com.ustc.charles.service.impl.EsHouseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @author charles
 * @date 2020/3/24 22:42
 */
@Controller
@Slf4j
@RequestMapping("house")
public class HouseController {

    @Autowired
    private EsHouseServiceImpl esHouseService;
    @Autowired
    private AddressService addressService;


    @GetMapping("/detail/{id}")
    public String getHouseDetail(@PathVariable("id") String id, Model model) {
        House house = esHouseService.findById(id);
        model.addAttribute("house", house);
        return "site/house-detail";
    }

    @GetMapping("/search")
    public String searchHouse(QueryParamDto queryParamDTO,
                              Page page, Model model, HttpServletRequest request,
                              @RequestParam(name = "orderMode", defaultValue = "default-desc") String orderMode) {

        Map<String, Set<String>> paramMap = new HashMap<>();
        String paramString = getParamString(request, paramMap);
        model.addAttribute("paramMap", paramMap);
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

    private String getParamString(HttpServletRequest request, Map<String, Set<String>> map) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        StringBuilder sb = new StringBuilder();
        for (String key : parameterMap.keySet()) {
            if (!key.equals("current")) {
                String[] values = parameterMap.get(key);
                Set<String> set = new HashSet<>();
                Collections.addAll(set, values);
                map.put(key, set);
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

    /**
     * 自动补全接口
     */
    @GetMapping("/autocomplete")
    @ResponseBody
    public ApiResponse autocomplete(@RequestParam(value = "prefix") String prefix) {

        if (prefix.isEmpty()) {
            return ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
        }
        ServiceResult<List<String>> result = this.esHouseService.suggest(prefix);
        return ApiResponse.ofSuccess(result.getResult());
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
