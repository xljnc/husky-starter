package com.wt.husky.mybatis.plus.sevice.impl;


import com.wt.husky.mybatis.plus.mapper.BatchBaseMapper;
import com.wt.husky.mybatis.plus.sevice.BaseService;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * mybatis plus 基础service实现类
 *
 * @author 一贫
 * @date 2020/12/28
 */
public class BaseServiceImpl<M extends BatchBaseMapper<T>, T> implements BaseService<T> {

    @Autowired
    protected M baseMapper;

    @Override
    public M getBaseMapper() {
        return baseMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insert(T entity) {
        return SqlHelper.retBool(getBaseMapper().insert(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer insertBatchSomeColumn(Collection<T> entityList) {
        return baseMapper.insertBatchSomeColumn(entityList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(T entity) {
        return SqlHelper.retBool(getBaseMapper().updateById(entity));

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteById(Serializable id) {
        return SqlHelper.retBool(getBaseMapper().deleteById(id));
    }

    @Override
    public T getById(Serializable id) {
        return getBaseMapper().selectById(id);
    }

    @Override
    public List<T> listByIds(Collection<? extends Serializable> ids) {
        return getBaseMapper().selectBatchIds(ids);
    }


}
