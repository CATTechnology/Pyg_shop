app.service("seckillPayService" , function($http){

    /**
     * 生成验证码
     * @returns {*}
     */
    this.createNative = function(){
        return $http.get('/seckillOrderPay/createNative.do');
    }

    /**
     * 查询支付状态
     */
    this.queryPayStatus = function(out_trade_no){
        return $http.get('/seckillOrderPay/queryPayStatus.do?out_trade_no='+out_trade_no);
    }
})