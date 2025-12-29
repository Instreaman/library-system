/**
 * 登录页面业务逻辑
 */
function loginModule() {
    return {
        username: '',
        password: '',
        loading: false,

        /**
         * 初始化
         */
        init() {
            // 检查是否已登录
            const token = StorageUtil.getToken();
            if (token) {
                this.redirectByRole();
            }
        },

        /**
         * 提交登录
         */
        async submitLogin() {
            // 表单验证
            if (!this.username) {
                Swal.fire({
                    icon: 'warning',
                    title: '提示',
                    text: '请输入用户名',
                    confirmButtonColor: '#000'
                });
                return;
            }

            if (!this.password) {
                Swal.fire({
                    icon: 'warning',
                    title: '提示',
                    text: '请输入密码',
                    confirmButtonColor: '#000'
                });
                return;
            }

            this.loading = true;

            try {
                // 调用登录 API
                const result = await AuthAPI.login(this.username, this.password);

                if (result.code === 200) {
                    // 保存登录信息
                    StorageUtil.setToken(result.data.token);
                    StorageUtil.setRole(result.data.role);
                    StorageUtil.setUsername(result.data.username);

                    // 显示成功提示
                    await Swal.fire({
                        icon: 'success',
                        title: '登录成功',
                        text: `欢迎回来，${result.data.realName || result.data.username}`,
                        confirmButtonColor: '#000',
                        timer: 1500,
                        showConfirmButton: false
                    });

                    // 根据角色跳转
                    this.redirectByRole();
                } else {
                    Swal.fire({
                        icon: 'error',
                        title: '登录失败',
                        text: result.message,
                        confirmButtonColor: '#000'
                    });
                }
            } catch (error) {
                console.error('登录失败:', error);
            } finally {
                this.loading = false;
            }
        },

        /**
         * 根据角色跳转页面
         */
        redirectByRole() {
            const role = StorageUtil.getRole();
            if (role === CONFIG.ROLES.ADMIN) {
                window.location.href = '/pages/admin/books.html';
            } else if (role === CONFIG.ROLES.USER) {
                window.location.href = '/pages/user/books.html';
            }
        }
    };
}
