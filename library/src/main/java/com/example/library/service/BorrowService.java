package com.example.library.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.library.entity.Book;
import com.example.library.entity.BorrowRecord;
import com.example.library.mapper.BookMapper;
import com.example.library.mapper.BorrowRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class BorrowService {

    private static final String STATUS_BORROWED = "BORROWED";
    private static final String STATUS_RETURNED = "RETURNED";
    private static final int DEFAULT_BORROW_DAYS = 14;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;

    /**
     * 获取当前用户的借阅记录（按借出时间倒序）
     */
    public List<BorrowRecord> listBorrowed(Long userId) {
        return borrowRecordMapper.selectList(
                new LambdaQueryWrapper<BorrowRecord>()
                        .eq(BorrowRecord::getUserId, userId)
                        .orderByDesc(BorrowRecord::getBorrowDate)
        );
    }

    /**
     * 获取可借阅的图书列表
     */
    public List<Book> listAvailableBooks(String keyword) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.gt(Book::getAvailableQuantity, 0);
        if (StrUtil.isNotBlank(keyword)) {
            wrapper.like(Book::getTitle, keyword)
                    .or()
                    .like(Book::getAuthor, keyword)
                    .or()
                    .like(Book::getPublisher, keyword);
        }
        return bookMapper.selectList(wrapper);
    }

    /**
     * 借书
     */
    @Transactional(rollbackFor = Exception.class)
    public void borrowBook(Long bookId, Long userId) {
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new IllegalArgumentException("图书不存在");
        }
        Integer available = book.getAvailableQuantity();
        if (available == null || available <= 0) {
            throw new IllegalStateException("该图书已无可借库存");
        }

        int borrowed = book.getBorrowedQuantity() == null ? 0 : book.getBorrowedQuantity();

        BorrowRecord record = new BorrowRecord();
        record.setBookId(bookId);
        record.setUserId(userId);
        record.setBorrowDate(LocalDateTime.now());
        record.setDueDate(LocalDateTime.now().plusDays(DEFAULT_BORROW_DAYS));
        record.setStatus(STATUS_BORROWED);
        borrowRecordMapper.insert(record);

        book.setAvailableQuantity(available - 1);
        book.setBorrowedQuantity(borrowed + 1);
        bookMapper.updateById(book);
    }

    /**
     * 还书
     */
    @Transactional(rollbackFor = Exception.class)
    public void returnBook(Long recordId, Long userId) {
        BorrowRecord record = borrowRecordMapper.selectById(recordId);
        if (record == null) {
            throw new IllegalArgumentException("借阅记录不存在");
        }
        if (!Objects.equals(record.getUserId(), userId)) {
            throw new IllegalStateException("无法归还他人的借阅记录");
        }
        if (STATUS_RETURNED.equals(record.getStatus())) {
            return;
        }

        record.setStatus(STATUS_RETURNED);
        record.setReturnDate(LocalDateTime.now());
        borrowRecordMapper.updateById(record);

        Book book = bookMapper.selectById(record.getBookId());
        if (book != null) {
            int total = book.getTotalQuantity() == null ? 0 : book.getTotalQuantity();
            int available = book.getAvailableQuantity() == null ? 0 : book.getAvailableQuantity();
            int borrowed = book.getBorrowedQuantity() == null ? 0 : book.getBorrowedQuantity();
            int newAvailable = Math.min(available + 1, total);
            int newBorrowed = Math.max(borrowed - 1, 0);
            book.setAvailableQuantity(newAvailable);
            book.setBorrowedQuantity(newBorrowed);
            bookMapper.updateById(book);
        }
    }
}
