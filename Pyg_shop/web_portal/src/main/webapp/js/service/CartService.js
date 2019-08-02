app.service('cartService', function ($http) {

    this.findCartList = function () {
        return $http.get('cart/findCartList.do');
    }

    this.addGoodsToCartList = function (itemId, num) {
        return $http.get('cart/addGoodsToCartList.do?itemId=' + itemId + '&num=' + num);
    }

    this.sum = function(cartList){
        var totalValue = {totalMoney: 0, totalNum: 0};
        //var orderItem = {};
        for (var i = 0; i < cartList.length; i++) {
            var cart = cartList[i];
            for (var j = 0; j < cart.orderItemList.length; j++) {
                var orderItem = cart.orderItemList[j];
                //购物车明细
                totalValue.totalNum += orderItem.num;
                totalValue.totalMoney += orderItem.totalFee;
            }
        }

        return totalValue;
    }

    this.findUserListByUserId = function(){
        return $http.get('/cart/findUserListByUserId.do');
    }

    //添加
    this.add = function(entity){
        return $http.post('/cart/add.do' , entity);
    }

    //提交订单
    this.submitOrder = function(order){
        return $http.post("/order/add.do" , order);
    }
});