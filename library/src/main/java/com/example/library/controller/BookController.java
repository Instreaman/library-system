package com.example.library.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.library.common.Result;
import com.example.library.entity.Book;
import com.example.library.entity.BorrowRecord;
import com.example.library.entity.User;
import com.example.library.mapper.BookMapper;
import com.example.library.mapper.BorrowRecordMapper;
import com.example.library.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 图书管理控制器
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 获取图书列表
     */
    @GetMapping
    public Result<List<Book>> listBooks() {
        List<Book> books = bookMapper.selectList(null);
        books.forEach(this::fillBorrowedQuantity);
        return Result.success(books);
    }

    /**
     * 获取单本图书详情
     */
    @GetMapping("/{id}")
    public Result<Book> getBook(@PathVariable Long id) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            return Result.error(404, "图书不存在");
        }
        fillBorrowedQuantity(book);
        return Result.success(book);
    }

    /**
     * 新增图书
     */
    @PostMapping
    public Result<String> createBook(@RequestBody Book book) {
        if (StrUtil.isBlank(book.getTitle())) {
            return Result.error(400, "书名不能为空");
        }
        if (StrUtil.isBlank(book.getIsbn())) {
            return Result.error(400, "ISBN 不能为空");
        }
        if (book.getTotalQuantity() == null || book.getTotalQuantity() < 0) {
            return Result.error(400, "总库存必须大于等于 0");
        }

        // 初始化可借数量与已借数量
        book.setAvailableQuantity(book.getTotalQuantity());
        book.setBorrowedQuantity(0);

        bookMapper.insert(book);
        return Result.success("添加成功", "添加成功");
    }

    /**
     * 编辑图书
     */
    @PutMapping("/{id}")
    public Result<String> updateBook(@PathVariable Long id, @RequestBody Book payload) {
        Book existing = bookMapper.selectById(id);
        if (existing == null) {
            return Result.error(404, "图书不存在");
        }

        // 计算当前已借出数量
        int borrowed = safeSubtract(existing.getTotalQuantity(), existing.getAvailableQuantity());

        Integer newTotal = payload.getTotalQuantity() != null ? payload.getTotalQuantity() : existing.getTotalQuantity();
        if (newTotal == null || newTotal < 0) {
            return Result.error(400, "总库存必须大于等于 0");
        }
        if (borrowed > newTotal) {
            return Result.error(400, "总库存不能小于已借出数量");
        }

        Integer newAvailable;
        if (payload.getAvailableQuantity() != null) {
            newAvailable = payload.getAvailableQuantity();
            if (newAvailable < 0 || newAvailable > newTotal) {
                return Result.error(400, "可借数量必须在 0 和总库存之间");
            }
            borrowed = safeSubtract(newTotal, newAvailable);
        } else {
            newAvailable = newTotal - borrowed;
        }

        existing.setTitle(StrUtil.isNotBlank(payload.getTitle()) ? payload.getTitle() : existing.getTitle());
        existing.setAuthor(Objects.nonNull(payload.getAuthor()) ? payload.getAuthor() : existing.getAuthor());
        existing.setIsbn(StrUtil.isNotBlank(payload.getIsbn()) ? payload.getIsbn() : existing.getIsbn());
        existing.setPublisher(Objects.nonNull(payload.getPublisher()) ? payload.getPublisher() : existing.getPublisher());
        existing.setTotalQuantity(newTotal);
        existing.setAvailableQuantity(newAvailable);
        existing.setBorrowedQuantity(borrowed);

        bookMapper.updateById(existing);
        return Result.success("更新成功", "更新成功");
    }

    /**
     * 删除图书
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteBook(@PathVariable Long id) {
        Book existing = bookMapper.selectById(id);
        if (existing == null) {
            return Result.error(404, "图书不存在");
        }

        // 未归还记录检查
        Long unreturnedCount = borrowRecordMapper.selectCount(
                new LambdaQueryWrapper<BorrowRecord>()
                        .eq(BorrowRecord::getBookId, id)
                        .ne(BorrowRecord::getStatus, "RETURNED")
        );
        if (unreturnedCount != null && unreturnedCount > 0) {
            return Result.error(400, "存在未归还的借阅记录，无法删除");
        }

        // 已有历史记录时避免外键冲突
        Long historyCount = borrowRecordMapper.selectCount(
                new LambdaQueryWrapper<BorrowRecord>()
                        .eq(BorrowRecord::getBookId, id)
        );
        if (historyCount != null && historyCount > 0) {
            return Result.error(400, "存在借阅历史记录，暂不支持删除该图书");
        }

        bookMapper.deleteById(id);
        return Result.success("删除成功", "删除成功");
    }

    /**
     * 按图书 ID 查询借阅记录
     */
    @GetMapping("/{id}/records")
    public Result<List<Map<String, Object>>> getBookRecords(@PathVariable Long id) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            return Result.error(404, "图书不存在");
        }

        List<BorrowRecord> records = borrowRecordMapper.selectList(
                new LambdaQueryWrapper<BorrowRecord>()
                        .eq(BorrowRecord::getBookId, id)
                        .orderByDesc(BorrowRecord::getBorrowDate)
        );

        List<Map<String, Object>> result = records.stream().map(record -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", record.getId());
            map.put("bookId", record.getBookId());
            map.put("userId", record.getUserId());
            map.put("borrowDate", record.getBorrowDate());
            map.put("returnDate", record.getReturnDate());
            map.put("dueDate", record.getDueDate());
            map.put("status", record.getStatus());

            User user = userMapper.selectById(record.getUserId());
            if (user != null) {
                map.put("username", user.getUsername());
                map.put("realName", user.getRealName());
            }
            return map;
        }).collect(Collectors.toList());

        return Result.success(result);
    }

    private void fillBorrowedQuantity(Book book) {
        if (book == null) {
            return;
        }
        int total = book.getTotalQuantity() == null ? 0 : book.getTotalQuantity();
        int available = book.getAvailableQuantity() == null ? 0 : book.getAvailableQuantity();
        // 如果数据库已有 borrowedQuantity 列，则以存储值为准，否则回填计算值
        Integer storedBorrowed = book.getBorrowedQuantity();
        int borrowed = storedBorrowed != null ? storedBorrowed : safeSubtract(total, available);
        // 校正借出数量不要为负
        borrowed = Math.max(borrowed, 0);
        book.setBorrowedQuantity(borrowed);
    }

    private int safeSubtract(Integer total, Integer available) {
        int t = total == null ? 0 : total;
        int a = available == null ? 0 : available;
        int result = t - a;
        return Math.max(result, 0);
    }
}
