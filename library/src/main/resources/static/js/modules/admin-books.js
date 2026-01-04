/**
 * 管理员 - 图书管理页面逻辑
 */
function adminBooksModule() {
    return {
        loading: false,
        modalOpen: false,
        keyword: '',
        books: [],
        form: {
            id: null,
            title: '',
            author: '',
            publisher: '',
            isbn: '',
            totalQuantity: 1,
            availableQuantity: null
        },

        init() {
            this.guard();
            this.fetchBooks();
        },

        guard() {
            const role = StorageUtil.getRole();
            if (!role || role !== CONFIG.ROLES.ADMIN) {
                StorageUtil.clear();
                window.location.href = '/pages/login.html';
            }
        },

        filteredBooks() {
            const key = this.keyword.trim().toLowerCase();
            if (!key) return this.books;
            return this.books.filter(item => {
                const title = (item.title || '').toLowerCase();
                const author = (item.author || '').toLowerCase();
                const publisher = (item.publisher || '').toLowerCase();
                return title.includes(key) || author.includes(key) || publisher.includes(key);
            });
        },

        borrowedQuantity(book) {
            if (book.borrowedQuantity != null) return book.borrowedQuantity;
            const total = book.totalQuantity || 0;
            const available = book.availableQuantity || 0;
            return Math.max(total - available, 0);
        },

        openCreate() {
            this.form = {
                id: null,
                title: '',
                author: '',
                publisher: '',
                isbn: '',
                totalQuantity: 1,
                availableQuantity: null
            };
            this.modalOpen = true;
        },

        openEdit(book) {
            this.form = {
                id: book.id,
                title: book.title || '',
                author: book.author || '',
                publisher: book.publisher || '',
                isbn: book.isbn || '',
                totalQuantity: book.totalQuantity || 0,
                availableQuantity: book.availableQuantity
            };
            this.modalOpen = true;
        },

        async fetchBooks() {
            this.loading = true;
            try {
                const res = await BookAPI.list();
                if (res.code === 200) {
                    this.books = res.data || [];
                } else {
                    Swal.fire({ icon: 'error', title: '加载失败', text: res.message || '请稍后再试', confirmButtonColor: '#000' });
                }
            } catch (err) {
                console.error('加载图书失败', err);
            } finally {
                this.loading = false;
            }
        },

        async submitForm() {
            if (!this.form.title) {
                Swal.fire({ icon: 'warning', title: '提示', text: '请输入书名', confirmButtonColor: '#000' });
                return;
            }
            if (!this.form.isbn) {
                Swal.fire({ icon: 'warning', title: '提示', text: '请输入 ISBN', confirmButtonColor: '#000' });
                return;
            }
            if (this.form.totalQuantity == null || this.form.totalQuantity < 0) {
                Swal.fire({ icon: 'warning', title: '提示', text: '总库存需大于等于 0', confirmButtonColor: '#000' });
                return;
            }

            try {
                let res;
                const payload = {
                    title: this.form.title,
                    author: this.form.author,
                    publisher: this.form.publisher,
                    isbn: this.form.isbn,
                    totalQuantity: Number(this.form.totalQuantity),
                    availableQuantity: this.form.availableQuantity != null ? Number(this.form.availableQuantity) : null
                };

                if (this.form.id) {
                    res = await BookAPI.update(this.form.id, payload);
                } else {
                    res = await BookAPI.create(payload);
                }

                if (res.code === 200) {
                    await Swal.fire({ icon: 'success', title: '成功', text: res.message || '操作成功', confirmButtonColor: '#000', timer: 1200, showConfirmButton: false });
                    this.modalOpen = false;
                    await this.fetchBooks();
                } else {
                    Swal.fire({ icon: 'error', title: '操作失败', text: res.message || '请稍后再试', confirmButtonColor: '#000' });
                }
            } catch (err) {
                console.error('保存失败', err);
            }
        },

        async confirmDelete(book) {
            const result = await Swal.fire({
                icon: 'warning',
                title: '确认删除？',
                text: `删除后将无法恢复：${book.title}`,
                showCancelButton: true,
                confirmButtonColor: '#000',
                cancelButtonColor: '#9ca3af',
                confirmButtonText: '删除',
                cancelButtonText: '取消'
            });
            if (!result.isConfirmed) return;

            try {
                const res = await BookAPI.remove(book.id);
                if (res.code === 200) {
                    await Swal.fire({ icon: 'success', title: '已删除', text: res.message || '删除成功', confirmButtonColor: '#000', timer: 1200, showConfirmButton: false });
                    await this.fetchBooks();
                } else {
                    Swal.fire({ icon: 'error', title: '删除失败', text: res.message || '请稍后再试', confirmButtonColor: '#000' });
                }
            } catch (err) {
                console.error('删除失败', err);
            }
        },

        goRecords(book) {
            window.location.href = `/pages/admin/book_record.html?id=${book.id}`;
        },

        logout() {
            StorageUtil.clear();
            window.location.href = '/pages/login.html';
        }
    };
}
