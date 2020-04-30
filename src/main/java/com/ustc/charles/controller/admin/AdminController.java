package com.ustc.charles.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.ustc.charles.dto.ApiDataTableResponse;
import com.ustc.charles.dto.ApiResponse;
import com.ustc.charles.dto.DatatableSearch;
import com.ustc.charles.dto.QiNiuDto;
import com.ustc.charles.entity.HouseForm;
import com.ustc.charles.entity.ServiceMultiResult;
import com.ustc.charles.entity.ServiceResult;
import com.ustc.charles.model.House;
import com.ustc.charles.model.SupportAddress;
import com.ustc.charles.service.AddressService;
import com.ustc.charles.service.HouseService;
import com.ustc.charles.service.impl.QiNiuServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author charles
 * @date 2020/3/26 20:21
 */
@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private QiNiuServiceImpl qiNiuService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private HouseService houseService;

    /**
     * 后台管理中心
     */
    @GetMapping({"/center", "/"})
    public String adminCenterPage() {
        return "admin/center";
    }

    /**
     * 欢迎页
     *
     * @return
     */
    @GetMapping("/welcome")
    public String welcomePage() {
        return "admin/welcome";
    }

    @GetMapping("/house/subscribe")
    public String subscribePage() {
        return "admin/subscribe";
    }

    /**
     * 管理员登录页
     */
    @GetMapping("/login")
    public String adminLoginPage() {
        return "admin/login";
    }

    /**
     * 房源列表页
     */
    @GetMapping("/house/list")
    public String houseListPage() {
        return "admin/house-list";
    }

    @PostMapping("/houses")
    @ResponseBody
    public ApiDataTableResponse houses(@ModelAttribute DatatableSearch searchBody) {
        ServiceMultiResult<House> result = houseService.adminQuery(searchBody);
        ApiDataTableResponse response = new ApiDataTableResponse(ApiResponse.Status.SUCCESS);
        response.setData(result.getResult());
        response.setRecordsFiltered(result.getTotal());
        response.setRecordsTotal(result.getTotal());
        response.setDraw(searchBody.getDraw());
        return response;
    }

    /**
     * 新增房源功能页
     *
     * @return
     */
    @GetMapping("/add/house")
    public String addHousePage() {
        return "admin/house-add";
    }

    /**
     * 上传图片接口
     *
     * @param file
     * @return
     */
    @PostMapping(value = "/upload/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ApiResponse uploadPhoto(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_VALID_PARAM);
        }
        try {
            InputStream inputStream = file.getInputStream();
            Response response = qiNiuService.uploadFile(inputStream);
            if (response.isOK()) {
                QiNiuDto ret = JSONObject.parseObject(response.body(), QiNiuDto.class);
                return ApiResponse.ofSuccess(ret);
            } else {
                return ApiResponse.ofMessage(response.statusCode, response.getInfo());
            }
        } catch (QiniuException e) {
            Response response = e.response;
            try {
                return ApiResponse.ofMessage(response.statusCode, response.bodyString());
            } catch (QiniuException e1) {
                e1.printStackTrace();
                return ApiResponse.ofStatus(ApiResponse.Status.INTERNAL_SERVER_ERROR);
            }
        } catch (IOException e) {
            return ApiResponse.ofStatus(ApiResponse.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 新增房源接口
     *
     * @param houseForm
     * @param bindingResult
     * @return
     */
    @PostMapping("/add/house")
    @ResponseBody
    public ApiResponse addHouse(@Valid @ModelAttribute("form-house-add") HouseForm houseForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ApiResponse(HttpStatus.BAD_REQUEST.value(), bindingResult.getAllErrors().get(0).getDefaultMessage(), null);
        }

        if (houseForm.getPhotos() == null || houseForm.getCover() == null) {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), "必须上传图片");
        }

        Map<SupportAddress.Level, SupportAddress> addressMap = addressService.findCityAndRegion(houseForm.getCityEnName(), houseForm.getRegionEnName());

        if (addressMap.keySet().size() != 2) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_VALID_PARAM);
        }

        ServiceResult<House> result = houseService.save(houseForm);
        if (result.isSuccess()) {
            return ApiResponse.ofSuccess(result.getResult());
        }
        return ApiResponse.ofSuccess(ApiResponse.Status.NOT_VALID_PARAM);
    }

    /**
     * 房源信息编辑页
     *
     * @return
     */
    @GetMapping("/house/edit")
    public String houseEditPage(@RequestParam(value = "id") Long id, Model model) {
        if (id == null || id < 1) {
            return "404";
        }
        ServiceResult<House> serviceResult = houseService.findById(id);
        if (!serviceResult.isSuccess()) {
            return "404";
        }
        House result = serviceResult.getResult();
        return "admin/house-edit";
    }

    /**
     * 编辑接口
     */
    @PostMapping("/house/edit")
    @ResponseBody
    public ApiResponse saveHouse(@Valid @ModelAttribute("form-house-edit") HouseForm houseForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ApiResponse(HttpStatus.BAD_REQUEST.value(), bindingResult.getAllErrors().get(0).getDefaultMessage(), null);
        }
        Map<SupportAddress.Level, SupportAddress> addressMap = addressService.findCityAndRegion(houseForm.getCityEnName(), houseForm.getRegionEnName());

        if (addressMap.keySet().size() != 2) {
            return ApiResponse.ofSuccess(ApiResponse.Status.NOT_VALID_PARAM);
        }
        ServiceResult result = houseService.update(houseForm);
        if (result.isSuccess()) {
            return ApiResponse.ofSuccess(null);
        }
        ApiResponse response = ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
        response.setMessage(result.getMessage());
        return response;
    }

    @GetMapping("/house/operate/{id}/{op}")
    @ResponseBody
    public ApiResponse deleteHouse(@PathVariable("id") Long id, @PathVariable("op") Integer op) {
        if (op == 3) {
            houseService.deleteHouse(id);
        }
        return ApiResponse.ofSuccess(ApiResponse.Status.SUCCESS);
    }
}
