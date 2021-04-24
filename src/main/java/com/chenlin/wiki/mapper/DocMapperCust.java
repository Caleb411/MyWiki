package com.chenlin.wiki.mapper;

import com.chenlin.wiki.domain.Test;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DocMapperCust {

    void increaseViewCount(@Param("id") Long id);
}
