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
    }
};
