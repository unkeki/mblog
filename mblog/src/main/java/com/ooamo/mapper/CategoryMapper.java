package com.ooamo.mapper;

import com.ooamo.entity.Category;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ooamo
 * @since 2022-02-28
 */
@Component
public interface CategoryMapper extends BaseMapper<Category> {

}
