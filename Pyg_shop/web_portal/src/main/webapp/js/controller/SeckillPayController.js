app.controller("seckillPayController", function ($scope, seckillPayService) {

    $scope.createNative = function () {
        payService.createNative().success(function (response) {
            if (response.success) {
                //金额
                $scope.money = (response.total_fee / 100).toFixed(2);
                //订单号
                $scope.out_trade_no = response.out_trade_no;
                var qr = new QRious({
                    element: document.getElementById('qrious'),
                    size: 250,
                    level: 'H',
                    value: response.code_url
                });

                //调用查询方法
                $scope.queryPayStatus(response.out_trade_no);
            }
        });
    }

    //查询支付状态，页面跳转
    $scope.queryPayStatus = function (out_trade_no) {
        payService.queryPayStatus(out_trade_no).success(function (response) {
            if(response.success){
                location.href="paysuccess.html#?money="+$scope.money;
            }else{
                if(response.info == "支付超时"){
                    //重新生成验证码
                    $scope.createNative();
                }else{
                    location.href="payfail.html";
                }
            }
        })
    }
});