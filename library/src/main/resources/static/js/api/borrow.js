/**
 * 借阅相关 API
 */
const BorrowAPI = {
    /**
     * 可借图书列表
     */
    available(keyword) {
        return HttpUtil.get('/user/available-books', { keyword });
    },

    /**
     * 当前用户借阅记录
     */
    borrowed() {
        return HttpUtil.get('/user/borrowed');
    },

    /**
     * 借书
     */
    borrow(bookId) {
        return HttpUtil.post(`/user/borrow/${bookId}`);
    },

    /**
     * 还书
     */
    returnBook(recordId) {
        return HttpUtil.post(`/user/return/${recordId}`);
    }
};
