// index.js
// 获取应用实例
const app = getApp()

Page({

  // 页面数据
  data: {
    programTitle: '防欺骗可视化\n加密数字货币钱包'
  },

  // 事件处理函数
  bindAccountsTap() {
    wx.navigateTo({
      url: '../accounts/accounts'
    })
  },
  bindViewTap() {
    wx.navigateTo({
      url: '../logs/logs'
    })
  }
})
