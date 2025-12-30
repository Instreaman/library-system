/**
 * 认证相关 API
 */
const AuthAPI = {
    /**
     * 用户登录
     */
    login(username, password) {
        return HttpUtil.post('/login', {
            username,
            password
        });
    },

    /**
     * 当前登录用户信息
     */
    me() {
        return HttpUtil.get('/me');
    }
};
