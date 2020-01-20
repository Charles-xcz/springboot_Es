package com.ustc.charles.service;

import com.ustc.charles.Model.Es;

import java.util.List;
import java.util.Map;


/**
 * @author charles
 */
public interface QueryService {

    List<Map<String, Object>> queryListFromES(Es es, int storeId, String storeName, String startDate, String endDate);

    List<String> boolQuery(Es es, String title, String author);
}
