/**
 * 管理员 - 图书借阅记录页面逻辑
 */
function adminBookRecordModule() {
    return {
        loading: false,
        bookId: null,
        book: null,
        records: [],

        init() {
            this.guard();
            const params = new URLSearchParams(window.location.search);
            this.bookId = params.get('id');
            if (!this.bookId) {
                Swal.fire({ icon: 'error', title: '缺少参数', text: '未找到图书 ID', confirmButtonColor: '#000' });
                return;
            }
            this.fetchBook();
            this.fetchRecords();
        },

        guard() {
            const role = StorageUtil.getRole();
            if (!role || role !== CONFIG.ROLES.ADMIN) {
                StorageUtil.clear();
                window.location.href = '/pages/login.html';
            }
        },

        async fetchBook() {
            try {
                const res = await BookAPI.detail(this.bookId);
                if (res.code === 200) {
                    this.book = res.data;
                }
            } catch (err) {
                console.error('加载图书详情失败', err);
            }
        },

        async fetchRecords() {
            this.loading = true;
            try {
                const res = await BookAPI.records(this.bookId);
                if (res.code === 200) {
                    this.records = res.data || [];
                } else {
                    Swal.fire({ icon: 'error', title: '加载失败', text: res.message || '请稍后再试', confirmButtonColor: '#000' });
                }
            } catch (err) {
                console.error('加载记录失败', err);
            } finally {
                this.loading = false;
            }
        },

        statusLabel(status) {
            switch (status) {
                case 'RETURNED':
                    return '已归还';
                case 'BORROWED':
                    return '借阅中';
                case 'OVERDUE':
                    return '已逾期';
                default:
                    return status || '未知';
            }
        },

        formatDate(value) {
            if (!value) return '-';
            return new Date(value).toLocaleString();
        },

        backToList() {
            window.location.href = '/pages/admin/books.html';
        },

        logout() {
            StorageUtil.clear();
            window.location.href = '/pages/login.html';
        }
    };
}
