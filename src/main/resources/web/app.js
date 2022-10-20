const express = require('express'); // 引入express
const app = express();
const {createProxyMiddleware}= require('http-proxy-middleware');//引入反向代理的插件

app.use(express.static(__dirname)); // 设置node要托管哪一些静态资源
app.use(
    "/"
    ,createProxyMiddleware(
        {
            target: 'http://localhost:8888',
            changeOrigin: true,
            pathReWrite: {
                '^/': '/'
            }
        }
    )
);

app.listen(8889);
