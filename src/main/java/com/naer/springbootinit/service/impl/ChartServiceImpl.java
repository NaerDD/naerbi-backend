package com.naer.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.naer.springbootinit.mapper.ChartMapper;
import com.naer.springbootinit.service.ChartService;
import com.naer.springbootinit.model.entity.Chart;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【chart(图表信息表)】的数据库操作Service实现
* @createDate 2025-03-14 11:41:57
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService{

}




