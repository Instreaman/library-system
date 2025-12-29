/**
 * HTTP 请求工具类
 */
const HttpUtil = {
    /**
     * 发送请求
     */
    async request(config) {
        // 开始加载动画
        if (typeof NProgress !== 'undefined') {
            NProgress.start();
        }

        try {
            // 添加 Token 到请求头
            const token = StorageUtil.getToken();
            if (token) {
                config.headers = {
                    ...config.headers,
                    'Authorization': token
                };
            }

            // 发送请求
            const response = await axios({
                baseURL: CONFIG.API_BASE_URL,
                timeout: 10000,
                ...config
            });

            // 结束加载动画
            if (typeof NProgress !== 'undefined') {
                NProgress.done();
            }

            return response.data;
        } catch (error) {
            // 结束加载动画
            if (typeof NProgress !== 'undefined') {
                NProgress.done();
            }

            // 处理错误
            if (error.response) {
                // 服务器返回错误
                if (error.response.status === 401) {
                    // Token 失效，跳转到登录页
                    StorageUtil.clear();
                    if (typeof Swal !== 'undefined') {
                        await Swal.fire({
                            icon: 'error',
                            title: '登录已过期',
                            text: '请重新登录',
                            confirmButtonColor: '#000'
                        });
                    }
                    window.location.href = '/pages/login.html';
                } else {
                    if (typeof Swal !== 'undefined') {
                        Swal.fire({
                            icon: 'error',
                            title: '请求失败',
                            text: error.response.data.message || '服务器错误',
                            confirmButtonColor: '#000'
                        });
                    }
                }
            } else if (error.request) {
                // 请求发送失败
                if (typeof Swal !== 'undefined') {
                    Swal.fire({
                        icon: 'error',
                        title: '网络错误',
                        text: '请检查网络连接',
                        confirmButtonColor: '#000'
                    });
                }
            } else {
                // 其他错误
                if (typeof Swal !== 'undefined') {
                    Swal.fire({
                        icon: 'error',
                        title: '请求失败',
                        text: error.message,
                        confirmButtonColor: '#000'
                    });
                }
            }

            throw error;
        }
    },

    /**
     * GET 请求
     */
    get(url, params) {
        return this.request({
            method: 'GET',
            url,
            params
        });
    },

    /**
     * POST 请求
     */
    post(url, data) {
        return this.request({
            method: 'POST',
            url,
            data
        });
    },

    /**
     * PUT 请求
     */
    put(url, data) {
        return this.request({
            method: 'PUT',
            url,
            data
        });
    },

    /**
     * DELETE 请求
     */
    delete(url, params) {
        return this.request({
            method: 'DELETE',
            url,
            params
        });
    }
};
