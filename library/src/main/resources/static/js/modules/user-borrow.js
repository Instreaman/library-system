/**
 * 用户端 - 借书页
 */
function userBorrowModule() {
    return {
        loading: false,
        keyword: '',
        books: [],
        user: null,

        init() {
            this.guard();
            this.fetchUser();
            this.fetchBooks();
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

        async fetchBooks() {
            this.loading = true;
            try {
                const res = await BorrowAPI.available(this.keyword || undefined);
                if (res.code === 200) {
                    this.books = res.data || [];
                } else {
                    Swal.fire({ icon: 'error', title: '加载失败', text: res.message || '请稍后再试', confirmButtonColor: '#000' });
                }
            } catch (err) {
                console.error('加载可借图书失败', err);
            } finally {
                this.loading = false;
            }
        },

        async handleBorrow(book) {
            const confirm = await Swal.fire({
                icon: 'question',
                title: '确认借阅？',
                text: `借阅《${book.title || '这本书'}》`,
                showCancelButton: true,
                confirmButtonColor: '#000',
                cancelButtonColor: '#9ca3af',
                confirmButtonText: '借阅',
                cancelButtonText: '取消'
            });
            if (!confirm.isConfirmed) return;

            try {
                const res = await BorrowAPI.borrow(book.id);
                if (res.code === 200) {
                    await Swal.fire({ icon: 'success', title: '借阅成功', text: res.message || '操作成功', confirmButtonColor: '#000', timer: 1200, showConfirmButton: false });
                    this.fetchBooks();
                } else {
                    Swal.fire({ icon: 'error', title: '借阅失败', text: res.message || '请稍后再试', confirmButtonColor: '#000' });
                }
            } catch (err) {
                console.error('借阅失败', err);
            }
        },

        goMyBorrow() {
            window.location.href = '/pages/user/books.html';
        },

        logout() {
            StorageUtil.clear();
            window.location.href = '/pages/login.html';
        }
    };
}
