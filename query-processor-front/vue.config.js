module.exports = {
    devServer: {
      proxy: {
        '/qp-api': {
          target: 'http://localhost:8080',
          changeOrigin: true,
          pathRewrite: { '^/qp-api': '' },
        },
      },
    },
  };
  