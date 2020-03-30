package com.ustc.charles.controller;

import com.ustc.charles.dto.FieldAttributeDto;
import com.ustc.charles.dto.Page;
import com.ustc.charles.model.House;
import com.ustc.charles.service.EsHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author charles
 * @date 2020/3/26 20:14
 */
@Controller
public class HomeController {
    @Autowired
    private EsHouseService esHouseService;

    @GetMapping("/")
    public String root() {
        return "redirect:/index";
    }

    @GetMapping("/404")
    public String notFoundPage() {
        return "404";
    }

    @GetMapping("/403")
    public String accessError() {
        return "403";
    }

    @GetMapping("/500")
    public String internalError() {
        return "500";
    }

    @GetMapping("/error")
    public String error() {
        return "500";
    }
}
