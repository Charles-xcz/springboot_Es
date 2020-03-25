package com.ustc.charles.service;

import com.ustc.charles.dao.EsHouseRepository;
import com.ustc.charles.dao.QueryDao;
import com.ustc.charles.dto.FieldAttributeDTO;
import com.ustc.charles.dto.QueryParamDTO;
import com.ustc.charles.model.House;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author charles
 * @date 2020/3/25 9:34
 */
@Service
public class EsHouseService {
    @Autowired
    private EsHouseRepository esHouseRepository;
    @Autowired
    private QueryDao queryDao;

    public void saveAll(List<House> houses) {
        esHouseRepository.saveAll(houses);
    }

    public long getCount() {
        return esHouseRepository.count();
    }

    public List<House> listByPage(int current, int limit, String sortField) {
        return queryDao.listByPage(current, limit, sortField);
    }

    public Map<String, Object> searchHouse(QueryParamDTO queryParam, int offset, int limit) {
        return queryDao.searchHouse(queryParam, offset, limit);
    }

    public List<FieldAttributeDTO> getFieldAttributes() {
        return queryDao.getFieldAttribute();
    }
}
