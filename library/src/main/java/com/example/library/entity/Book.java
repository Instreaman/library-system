package com.example.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 图书实体类
 */
@Data
@TableName("book")
public class Book {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
    
    private String author;
    
    private String isbn;
    
    private String publisher;
    
    private Integer totalQuantity;
    
    private Integer availableQuantity;

    private Integer borrowedQuantity;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
