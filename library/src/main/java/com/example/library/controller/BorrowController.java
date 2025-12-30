package com.example.library.controller;

import cn.hutool.core.collection.CollUtil;
import com.example.library.common.Result;
import com.example.library.entity.Book;
import com.example.library.entity.BorrowRecord;
import com.example.library.mapper.BookMapper;
import com.example.library.service.BorrowService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    @Autowired
    private BookMapper bookMapper;

    @GetMapping("/borrowed")
    public Result<List<Map<String, Object>>> listBorrowed(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录");
        }

        List<BorrowRecord> records = borrowService.listBorrowed(userId);
        Set<Long> bookIds = records.stream()
                .map(BorrowRecord::getBookId)
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toSet());

        Map<Long, Book> bookMap = CollUtil.isEmpty(bookIds) ? new HashMap<>() :
                bookMapper.selectBatchIds(bookIds).stream()
                        .collect(Collectors.toMap(Book::getId, Function.identity()));

        List<Map<String, Object>> result = records.stream().map(record -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", record.getId());
            item.put("bookId", record.getBookId());
            item.put("borrowDate", record.getBorrowDate());
            item.put("returnDate", record.getReturnDate());
            item.put("dueDate", record.getDueDate());
            item.put("status", record.getStatus());

            Book book = bookMap.get(record.getBookId());
            if (book != null) {
                item.put("title", book.getTitle());
                item.put("author", book.getAuthor());
                item.put("publisher", book.getPublisher());
                item.put("availableQuantity", book.getAvailableQuantity());
            }
            return item;
        }).collect(Collectors.toList());

        return Result.success(result);
    }

    @GetMapping("/available-books")
    public Result<List<Book>> listAvailableBooks(@RequestParam(value = "keyword", required = false) String keyword) {
        List<Book> books = borrowService.listAvailableBooks(keyword);
        return Result.success(books);
    }

    @PostMapping("/borrow/{bookId}")
    public Result<String> borrowBook(@PathVariable Long bookId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        try {
            borrowService.borrowBook(bookId, userId);
            return Result.success("借阅成功", "借阅成功");
        } catch (IllegalArgumentException e) {
            return Result.error(404, e.getMessage());
        } catch (IllegalStateException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            return Result.error(500, "服务器内部错误");
        }
    }

    @PostMapping("/return/{recordId}")
    public Result<String> returnBook(@PathVariable Long recordId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        try {
            borrowService.returnBook(recordId, userId);
            return Result.success("归还成功", "归还成功");
        } catch (IllegalArgumentException e) {
            return Result.error(404, e.getMessage());
        } catch (IllegalStateException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            return Result.error(500, "服务器内部错误");
        }
    }
}
