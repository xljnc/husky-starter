package com.wt.husky.mybatis.plus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Collection;

/**
 * @author 一贫
 * @date 2021/1/23
 */
public interface BatchBaseMapper<T> extends BaseMapper<T> {

    /**
     * 批量插入 仅适用于mysql
     *
     * @param entityList 实体列表
     * @return 影响行数
     */
    Integer insertBatchSomeColumn(Collection<T> entityList);
}
