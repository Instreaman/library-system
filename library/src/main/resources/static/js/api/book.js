/**
 * 图书管理 API
 */
const BookAPI = {
    list() {
        return HttpUtil.get('/books');
    },
    detail(id) {
        return HttpUtil.get(`/books/${id}`);
    },
    create(payload) {
        return HttpUtil.post('/books', payload);
    },
    update(id, payload) {
        return HttpUtil.put(`/books/${id}`, payload);
    },
    remove(id) {
        return HttpUtil.delete(`/books/${id}`);
    },
    records(id) {
        return HttpUtil.get(`/books/${id}/records`);
    }
};
