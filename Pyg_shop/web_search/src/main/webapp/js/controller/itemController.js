app.controller('itemController', function ($scope, $http) {

    //初始化选择的商品默认为1
    $scope.count = 1;

    //商品数量加减操作
    $scope.addNum = function (num) {
        $scope.count += num;
        if ($scope.count <= 0) {
            $scope.count = 1;
        }
    }

    //规格选择初始化
    $scope.specificationItems = {};

    $scope.currentItem = {};

    //添加规格到规格保存中
    $scope.selectSpecification = function (spcName, specValue) {
        $scope.specificationItems[spcName] = specValue;
        //搜索sku
        searchSku();
    }

    //判断当前选择的规格里面是否含有当前规格
    $scope.isSelected = function (specName, value) {
        if ($scope.specificationItems[specName] != null && $scope.specificationItems[specName] == value) {
            return true;
        }
        return false;
    }

    //当前的item
    $scope.sku = {};

    //加载sku
    $scope.loadSKU = function () {
        //初始化为第一个商品
        $scope.sku = itemArr[0];
        $scope.specificationItems = JSON.parse(JSON.stringify(itemArr[0].spec));
    }

    //判断是否有当前的sku
    searchSku = function () {
        //判断此时是否满足任意一个sku的规格情况
        for (var i = 0; i < itemArr.length; i++) {
            if (mapEqualsTo(itemArr[i].spec, $scope.specificationItems)) {
                $scope.sku = itemArr[i];
                return;
            }

        }

        //如果没找到
        $scope.sku = {id: 0, title: '-------', price: 0.0};
    }

    //判断两个map是否全等
    mapEqualsTo = function (dict1, dict2) {

        for (var key in dict1) {
            if (dict1[key] != dict2[key]) {
                return false;
            }
        }

        for (var key in dict2) {
            if (dict2[key] != dict1[key]) {
                return false;
            }
        }

        return true;

    }

    //添加购物车
    $scope.addToItemCart = function () {
        if ($scope.sku.id == 0) {
            alert("亲，您选择的商品不存在!!!");
        } else {
            $http.get("http://localhost:8088/cart/addGoodsToCartList.do?itemId=" + $scope.sku.id + "&num=" + $scope.count ,{'withCredentials':true}).success(
                function (response) {
                    if(response.success){
                        //跳转
                    }
                }
            );
            alert('亲，您添加id为' + $scope.sku.id + '的商品到购物车');
        }
    }


})