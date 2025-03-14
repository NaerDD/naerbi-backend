package com.naer.springbootinit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.naer.springbootinit.annotation.AuthCheck;
import com.naer.springbootinit.common.BaseResponse;
import com.naer.springbootinit.common.DeleteRequest;
import com.naer.springbootinit.common.ErrorCode;
import com.naer.springbootinit.common.ResultUtils;
import com.naer.springbootinit.constant.UserConstant;
import com.naer.springbootinit.exception.BusinessException;
import com.naer.springbootinit.exception.ThrowUtils;
import com.naer.springbootinit.model.dto.chart.ChartAddRequest;
import com.naer.springbootinit.model.dto.chart.ChartEditRequest;
import com.naer.springbootinit.model.dto.chart.ChartQueryRequest;
import com.naer.springbootinit.model.dto.chart.ChartUpdateRequest;
import com.naer.springbootinit.model.entity.Chart;
import com.naer.springbootinit.model.entity.User;
import com.naer.springbootinit.model.vo.ChartVO;
import com.naer.springbootinit.service.ChartService;
import com.naer.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 帖子接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/chart")
@Slf4j
public class ChartController {

    @Resource
    private ChartService chartService;

    @Resource
    private UserService userService;

    private final static Gson GSON = new Gson();

    // region 增删改查

    /**
     * 创建
     *
     * @param chartAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addchart(@RequestBody ChartAddRequest chartAddRequest, HttpServletRequest request) {
        if (chartAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartAddRequest, chart);
        List<String> tags = chartAddRequest.getTags();
        if (tags != null) {
            chart.setTags(GSON.toJson(tags));
        }
        chartService.validchart(chart, true);
        User loginUser = userService.getLoginUser(request);
        chart.setUserId(loginUser.getId());
        chart.setFavourNum(0);
        chart.setThumbNum(0);
        boolean result = chartService.save(chart);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newchartId = chart.getId();
        return ResultUtils.success(newchartId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deletechart(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        chart oldchart = chartService.getById(id);
        ThrowUtils.throwIf(oldchart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldchart.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = chartService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param chartUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updatechart(@RequestBody ChartUpdateRequest chartUpdateRequest) {
        if (chartUpdateRequest == null || chartUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        chart chart = new chart();
        BeanUtils.copyProperties(chartUpdateRequest, chart);
        List<String> tags = chartUpdateRequest.getTags();
        if (tags != null) {
            chart.setTags(GSON.toJson(tags));
        }
        // 参数校验
        chartService.validchart(chart, false);
        long id = chartUpdateRequest.getId();
        // 判断是否存在
        chart oldchart = chartService.getById(id);
        ThrowUtils.throwIf(oldchart == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = chartService.updateById(chart);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<ChartVO> getchartVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = chartService.getById(id);
        if (chart == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(chartService.getchartVO(chart, request));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<ChartVO>> listchartVOByPage(@RequestBody ChartQueryRequest chartQueryRequest,
            HttpServletRequest request) {
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<chart> chartPage = chartService.page(new Page<>(current, size),
                chartService.getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartService.getchartVOPage(chartPage, request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<ChartVO>> listMychartVOByPage(@RequestBody ChartQueryRequest chartQueryRequest,
            HttpServletRequest request) {
        if (chartQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        chartQueryRequest.setUserId(loginUser.getId());
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> chartPage = chartService.page(new Page<>(current, size),
                chartService.getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartService.getchartVOPage(chartPage, request));
    }

    // endregion

    /**
     * 分页搜索（从 ES 查询，封装类）
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/search/page/vo")
    public BaseResponse<Page<ChartVO>> searchchartVOByPage(@RequestBody ChartQueryRequest chartQueryRequest,
            HttpServletRequest request) {
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> chartPage = chartService.searchFromEs(chartQueryRequest);
        return ResultUtils.success(chartService.getchartVOPage(chartPage, request));
    }

    /**
     * 编辑（用户）
     *
     * @param chartEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editchart(@RequestBody ChartEditRequest chartEditRequest, HttpServletRequest request) {
        if (chartEditRequest == null || chartEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new chart();
        BeanUtils.copyProperties(chartEditRequest, chart);
        List<String> tags = chartEditRequest.getTags();
        if (tags != null) {
            chart.setTags(GSON.toJson(tags));
        }
        // 参数校验
        chartService.validchart(chart, false);
        User loginUser = userService.getLoginUser(request);
        long id = chartEditRequest.getId();
        // 判断是否存在
        chart oldchart = chartService.getById(id);
        ThrowUtils.throwIf(oldchart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldchart.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = chartService.updateById(chart);
        return ResultUtils.success(result);
    }

}
