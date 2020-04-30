package com.ustc.charles.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author charles
 * @date 2020/3/26 20:14
 */
@Controller
public class HomeController {

    @GetMapping({"/", "/index"})
    public String root() {
        return "redirect:/house/search";
    }

    @GetMapping("/admin")
    public String admin() {
        return "redirect:/admin/center";
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
