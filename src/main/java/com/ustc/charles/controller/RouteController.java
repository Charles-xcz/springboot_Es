package com.ustc.charles.controller;

import com.ustc.charles.Model.Es;
import com.ustc.charles.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author charles
 * @date 2020/1/20 13:37
 */
@Controller
@ResponseBody
public class RouteController {

    @Autowired
    private QueryService queryService;
    @Autowired
    private Es es;

    @RequestMapping("/")
    public List<String> index() {

        return queryService.boolQuery(es, "苍凉", "*les");
    }
}
