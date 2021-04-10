// pages/accounts/accounts.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    accounts: [
      {
        name: "测试1",
        unit: "BTC",
        addr: 0x0001,
        key: 0x01234,
        amount: 0.0001
      },
      {
        name: "测试2",
        unit: "ETH",
        addr: 0x0002,
        key: 0x012345,
        amount: 0.01
      }
    ]
  },

  bindAccountTap: function (event) {
    console.log(event)
    wx.navigateTo({
      url: '../collect/collect?',
    })
  },
  addAccount: function (event) {

  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {

  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})