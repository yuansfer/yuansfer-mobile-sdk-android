package com.yuansfer.pay.api;

/**
 * @author fly
 * @desc API环境和超时时间配置
 */
public class APIConfig {

    /**
     * 默认超时时间
     */
    public static final int DEFAULT_TIMEOUT = 20;
    /**
     * 连接超时
     */
    private int connectTimeout;
    /**
     * 写入超时
     */
    private int writeTimeout;
    /**
     * 读取超时
     */
    private int readTimeout;
    /**
     * 是否沙箱环境
     */
    private boolean sandboxEnv;

    /**
     * 超时后的重试次数
     */
    private int retryTimes;

    private APIConfig() {
    }

    public int getConnectTimeout() {
        return connectTimeout <= 0 ? DEFAULT_TIMEOUT : connectTimeout;
    }

    public int getWriteTimeout() {
        return writeTimeout <= 0 ? DEFAULT_TIMEOUT : writeTimeout;
    }

    public int getReadTimeout() {
        return readTimeout <= 0 ? DEFAULT_TIMEOUT : readTimeout;
    }

    public boolean isSandboxEnv() {
        return sandboxEnv;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public static class Builder {
        private int connectTimeout;
        private int writeTimeout;
        private int readTimeout;
        private int retryTimes;
        private boolean sandboxEnv;

        public Builder setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder setWriteTimeout(int writeTimeout) {
            this.writeTimeout = writeTimeout;
            return this;
        }

        public Builder setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public Builder setSandboxEnv(boolean sandboxEnv) {
            this.sandboxEnv = sandboxEnv;
            return this;
        }

        public Builder setRetryTimes(int retryTimes) {
            this.retryTimes = retryTimes;
            return this;
        }

        public APIConfig build() {
            APIConfig config = new APIConfig();
            config.connectTimeout = this.connectTimeout;
            config.writeTimeout = this.writeTimeout;
            config.readTimeout = this.readTimeout;
            config.sandboxEnv = this.sandboxEnv;
            config.retryTimes = this.retryTimes;
            return config;
        }
    }
}
