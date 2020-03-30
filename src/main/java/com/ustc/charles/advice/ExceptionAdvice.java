package com.ustc.charles.advice;

import com.ustc.charles.entity.CommonConstant;
import com.ustc.charles.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author charles
 * @date 2020/3/26 20:57
 */
@Slf4j
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    @ExceptionHandler({Exception.class})
    public void handlerException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.error("服务器发生异常:{}", e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            log.error(element.toString());
        }
        String xRequestWith = request.getHeader("x-requested-with");
        if (CommonConstant.X_REQUESTED_WITH.equals(xRequestWith)) {
            //这是一个异步请求
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommonUtil.getJsonString(500, "服务器异常"));
        } else {
            //普通请求
            response.sendRedirect(request.getContextPath() + "/500");
        }
    }
}
