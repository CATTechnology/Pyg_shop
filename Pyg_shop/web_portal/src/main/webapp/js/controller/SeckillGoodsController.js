app.controller('seckillGoodsController', function ($scope, $location, $interval, seckillGoodsService) {

    $scope.findList = function () {
        seckillGoodsService.findList().success(function (response) {
            $scope.list = response;
        });
    }

    $scope.findOneFromRedis = function () {
        var id = $location.search()['id'];
        seckillGoodsService.findOneFromRedis(id).success(function (response) {
            $scope.item = response;
            //计算当前时间到产品结束还剩多少时间
            var allSeconds = Math.floor((new Date($scope.item.endTime).getTime() - new Date().getTime()) / 1000);
            var time = $interval(function () {
                if (allSeconds > 0) {
                    allSeconds = allSeconds - 1;
                    //将秒转化为时间
                    $scope.timeStr = $scope.TimeToString(allSeconds);
                } else {
                    $interval.cancel(time);
                }
            }, 1000);
        });
    }


    $scope.TimeToString = function (seconds) {
        //首先获取天数
        var days = Math.floor(seconds / (60 * 60 * 24));
        var hours = Math.floor((seconds - days * (24 * 60 * 60)) / (60 * 60));
        var minutes = Math.floor((seconds - days * (24 * 60 * 60) - hours * (60 * 60)) / 60);
        var second = Math.floor(seconds - days * (24 * 60 * 60) - hours * (60 * 60) - minutes * 60);
        var timeStr = "";
        if (days > 0) {
            timeStr = days + "天"
        }
        if (hours > 0) {
            if (hours <= 9) {
                timeStr = timeStr + "0" + hours + ":"
            } else {
                timeStr = timeStr + hours + ":"
            }
        }
        /*if(minutes > 0 ){
            timeStr = timeStr + minutes + "分钟";
        }
        if(second > 0){
            timeStr = timeStr + second + "秒";
        }*/

        return timeStr + minutes + ":" + second;
    }

    $scope.submitOrder = function(){
        seckillGoodsService.submitOrder($scope.item.id).success(function(response){
            if(response.success){
                alert("订单生成成功,请在一分钟之内完成支付...");
                location.href="seckillPay.html";
            }else{
                alert(response.info);
            }
        });
    }

})