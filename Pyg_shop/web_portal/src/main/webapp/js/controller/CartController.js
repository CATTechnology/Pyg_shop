app.controller('cartController', function ($scope, cartService) {

    $scope.selectRow = 0;

    $scope.address = {};

    $scope.entity = {alias: '家里'};

    $scope.order = {paymentType: '1'};

    $scope.totalValue = {
        totalMoney: 0,
        totalNum: 0
    };

    $scope.findCartList = function () {
        cartService.findCartList().success(function (response) {
            $scope.cartList = response;
            $scope.totalValue = cartService.sum($scope.cartList);
        });
    }

    //商品数量增减
    $scope.changeNum = function (itemId, num) {
        cartService.addGoodsToCartList(itemId, num).success(function (response) {
            if (response.success) {
                $scope.findCartList();
            } else {
                //抛出异常
                alert(response.info);
            }
        })
    }

    //选择支付方式
    $scope.selectPayType = function (payType) {
        $scope.order.paymentType = payType;
    }

    //删除指定的商品
    $scope.deleteGoods = function (itemId, num) {
        $scope.changeNum(itemId, -num);
        $scope.findCartList();
    }

    $scope.findUserListByUserId = function () {
        cartService.findUserListByUserId().success(
            function (response) {
                $scope.addressList = response;
                if ($scope.addressList.length > 0) {
                    $scope.address = $scope.addressList[0];
                    setOrderValue($scope.addressList[0]);
                }
            }
        );
    }


    //赋值处理
    setOrderValue = function(addr){
        $scope.order['receiverAreaName'] = addr.alias;
        $scope.order['receiver'] = addr.contact;
        $scope.order['receiverMobile'] = addr.mobile;
    }


    $scope.selectThis = function (index) {
        $scope.selectRow = index;
        $scope.address = $scope.addressList[index];
        setOrderValue($scope.address);
    }

    $scope.selectAlias = function (alias) {
        $scope.entity.alias = alias;
    }

    $scope.add = function () {
        cartService.add($scope.entity).success(function (response) {
            if (response.success) {
                $scope.findUserListByUserId();
                $scope.entity = {};
            } else {
                alert(response.info);
            }
        });
    }


    $scope.submitOrder = function(){
        cartService.submitOrder($scope.order).success(function(response){
            if(response.success){
                //页面跳转
                if($scope.order.paymentType=='1'){//如果是微信支付，跳转到支付页面
                    location.href="pay.html";
                }else{//如果货到付款，跳转到提示页面
                    location.href="paysuccess.html";
                }
            }else{
                alert(response.info);
            }
        });
    }
});