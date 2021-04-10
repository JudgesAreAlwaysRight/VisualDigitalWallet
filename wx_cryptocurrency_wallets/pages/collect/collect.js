// pages/collect/collect.js
Page({

  /**
   * 页面的初始数据
   */
  data: {
    picList: [
      {
        abs: "分存图片概述1",
        data: 0x1234
      },
      {
        abs: "分存图片概述2",
        data: 0x1234
      }
    ],
    accName: "",
    addr: 0x0,
    key: 0x0
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    this.setData({
      accName: options.accName,
      addr: options.addr,
      key: options.key
    })
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

  },

  bindScanTap: function () {
    wx.scanCode({
      success(res) {
        console.log(res)
      }
    })
  }
})