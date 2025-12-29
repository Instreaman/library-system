package com.example.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.library.entity.BorrowRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 借阅记录 Mapper 接口
 */
@Mapper
public interface BorrowRecordMapper extends BaseMapper<BorrowRecord> {
}
