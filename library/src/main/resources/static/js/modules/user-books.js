/**
 * 用户端 - 我的借阅
 */
function userBooksModule() {
    return {
        loading: false,
        user: null,
        records: [],

        init() {
            this.guard();
            this.fetchUser();
            this.fetchBorrowed();
        },

        guard() {
            const role = StorageUtil.getRole();
            if (!role || role !== CONFIG.ROLES.USER) {
                StorageUtil.clear();
                window.location.href = '/pages/login.html';
            }
        },

        async fetchUser() {
            try {
                const res = await AuthAPI.me();
                if (res.code === 200) {
                    this.user = res.data;
                    StorageUtil.setUsername(res.data?.username || '');
                }
            } catch (err) {
                console.error('加载用户信息失败', err);
            }
        },

        async fetchBorrowed() {
            this.loading = true;
            try {
                const res = await BorrowAPI.borrowed();
                if (res.code === 200) {
                    this.records = res.data || [];
                } else {
                    Swal.fire({ icon: 'error', title: '加载失败', text: res.message || '请稍后再试', confirmButtonColor: '#000' });
                }
            } catch (err) {
                console.error('加载借阅记录失败', err);
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

        async handleReturn(record) {
            const confirm = await Swal.fire({
                icon: 'question',
                title: '确认归还？',
                text: `归还《${record.title || '这本书'}》`,
                showCancelButton: true,
                confirmButtonColor: '#000',
                cancelButtonColor: '#9ca3af',
                confirmButtonText: '归还',
                cancelButtonText: '取消'
            });
            if (!confirm.isConfirmed) return;

            try {
                const res = await BorrowAPI.returnBook(record.id);
                if (res.code === 200) {
                    await Swal.fire({ icon: 'success', title: '已归还', text: res.message || '操作成功', confirmButtonColor: '#000', timer: 1200, showConfirmButton: false });
                    this.fetchBorrowed();
                } else {
                    Swal.fire({ icon: 'error', title: '归还失败', text: res.message || '请稍后再试', confirmButtonColor: '#000' });
                }
            } catch (err) {
                console.error('归还失败', err);
            }
        },

        goBorrow() {
            window.location.href = '/pages/user/borrow_book.html';
        },

        logout() {
            StorageUtil.clear();
            window.location.href = '/pages/login.html';
        }
    };
}
