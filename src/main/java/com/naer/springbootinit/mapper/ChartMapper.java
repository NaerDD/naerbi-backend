package com.naer.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.naer.springbootinit.model.entity.Chart;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【chart(图表信息表)】的数据库操作Mapper
* @createDate 2025-03-14 11:41:57
* @Entity com.naer.springbootinit.model.entity.Chart
*/
public interface ChartMapper extends BaseMapper<Chart> {
    @Update("${sql}")
    void executeSQL(@Param("sql") String sql);

//    List<Map<String, Object>> queryChartData(String querySql);

}