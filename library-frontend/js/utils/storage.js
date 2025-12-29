/**
 * 本地存储工具类
 */
const StorageUtil = {
    /**
     * 保存数据到 localStorage
     */
    set(key, value) {
        localStorage.setItem(key, value);
    },

    /**
     * 从 localStorage 获取数据
     */
    get(key) {
        return localStorage.getItem(key);
    },

    /**
     * 删除 localStorage 中的数据
     */
    remove(key) {
        localStorage.removeItem(key);
    },

    /**
     * 清空 localStorage
     */
    clear() {
        localStorage.clear();
    },

    /**
     * 保存 Token
     */
    setToken(token) {
        this.set(CONFIG.STORAGE_KEYS.TOKEN, token);
    },

    /**
     * 获取 Token
     */
    getToken() {
        return this.get(CONFIG.STORAGE_KEYS.TOKEN);
    },

    /**
     * 删除 Token
     */
    removeToken() {
        this.remove(CONFIG.STORAGE_KEYS.TOKEN);
    },

    /**
     * 保存用户角色
     */
    setRole(role) {
        this.set(CONFIG.STORAGE_KEYS.ROLE, role);
    },

    /**
     * 获取用户角色
     */
    getRole() {
        return this.get(CONFIG.STORAGE_KEYS.ROLE);
    },

    /**
     * 保存用户名
     */
    setUsername(username) {
        this.set(CONFIG.STORAGE_KEYS.USERNAME, username);
    },

    /**
     * 获取用户名
     */
    getUsername() {
        return this.get(CONFIG.STORAGE_KEYS.USERNAME);
    }
};
