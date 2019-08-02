app.controller('searchController', function ($scope, $location, searchService) {

    //初始化查询条件
    $scope.searchMap = {
        keywords: '',
        category: '',
        brand: '',
        specOps: null,
        startPrice: '',
        endPrice: '',
        price: '',
        sortDirection: '',
        sort: '',
        currentPage: 1,
        pageSize: 20
    };

    $scope.watchChange = {
        rekeywords: '',
        recategory: ''
    }

    //用于分页
    $scope.pages = [];

    $scope.search = function () {
        //首先判断keyword是否为空
        var keywords = $location.search()['keywords'];
        if (keywords != null && keywords != '' && $scope.searchMap.keywords == '') {
            $scope.searchMap.keywords = keywords;
        }
        //每次查询前都清空
        $scope.pages = [];
        $scope.searchMap.currentPage = parseInt($scope.searchMap.currentPage);
        $scope.searchMap.pageSize = parseInt($scope.searchMap.pageSize);
        if ($scope.watchChange.rekeywords != $scope.searchMap.keywords ||
            $scope.watchChange.recategory != $scope.searchMap.category) {
            //修改了商品类型之后重新赋值
            $scope.watchChange.rekeywords = $scope.searchMap.keywords;
            $scope.watchChange.recategory = $scope.searchMap.category;
            $scope.searchMap.currentPage = 1;
        }

        searchService.search($scope.searchMap).success(function (response) {

            $scope.resultMap = response;
            set_Pages();
        });
    }

    set_Pages = function () {
        var start = 1;
        var end = $scope.resultMap.totalPage;

        //设置开始和结束
        $scope.isPre = 0;
        $scope.isEnd = 0;
        if ($scope.resultMap.totalPage > 5) {
            if ($scope.searchMap.currentPage <= 3) {
                $scope.isEnd = 1;
                end = 5;
            } else if ($scope.searchMap.currentPage >= $scope.resultMap.totalPage - 3) {
                $scope.isPre = 1;
                start = $scope.resultMap.totalPage - 4;
            } else {
                $scope.isPre = 1;
                $scope.isEnd = 1;
                start = $scope.searchMap.currentPage - 2;
                end = $scope.searchMap.currentPage + 2;
            }
        }
        for (var i = start; i <= end; i++) {
            $scope.pages.push(i);
        }
    }

    //搜索条件的添加
    $scope.addConditionToSearchMap = function (column, value) {
        if (column == 'category' || column == 'brand' || column == 'price') {
            //alert(value);
            $scope.searchMap[column] = value;
        } else {
            if ($scope.searchMap.specOps == null) {
                $scope.searchMap.specOps = {};
            }
            $scope.searchMap.specOps[column] = value;
        }
        //调用查询
        $scope.search();
    }

    //删除条件
    $scope.cancelSelect = function (column) {
        if (column == 'category' || column == 'brand' || column == 'price') {
            $scope.searchMap[column] = '';
        } else {
            delete $scope.searchMap.specOps[column];
        }
        $scope.search();
    }

    //按价格筛选
    $scope.filterByPrice = function (startPrice, endPrice) {

        $scope.addConditionToSearchMap('price', startPrice + '-' + endPrice);
        //价格赋值
        $scope.searchMap.startPrice = startPrice;
        $scope.searchMap.endPrice = endPrice;
        //刷新页面
        $scope.search();
    }

    //当查询的是和品牌相关的关键字是隐藏品牌列表
    $scope.isContains = function (list, key) {
        for (var i = 0; i < list.length; i++) {
            var index = key.indexOf(list[i].text);
            if (index >= 0) {
                return false;
            }
        }
        return true;
    }

    //朝向
    $scope.Direction = {
        original: '',
        comprehensive: '0',
        price: '0',
        seller: '0',
        evaluate: '0',
        newProduct: '0'
    }

    //排序
    $scope.sortByCondition = function (column) {
        //首先判断选项的状态
        if ($scope.Direction[column] != null && $scope.Direction[column] == '0') {
            if ($scope.Direction.original == '') {
                $scope.Direction.original = column;
                $scope.Direction[column] = '1';
            } else {
                $scope.Direction[$scope.Direction.original] = '0';
                $scope.Direction.original = column;
                $scope.Direction[column] = '1';
            }
        } else if ($scope.Direction[column] != null && $scope.Direction[column] == '1') {
            $scope.Direction[column] = '2';
        } else if ($scope.Direction[column] != null && $scope.Direction[column] == '2') {
            $scope.Direction[column] = '1';
        }
        //赋值于查询操作
        $scope.searchMap.sort = column;
        //升序还是降序
        $scope.searchMap.sortDirection = $scope.Direction[column] == '1' ? 'asc' : 'desc';
        //查询
        $scope.search();
    }

    //页面跳转
    $scope.jumpPage = function (page) {
        if ($scope.searchMap.currentPage == 1 && page <= 0) {
            $scope.page = 1;
            return;
        }
        if ($scope.searchMap.currentPage == $scope.resultMap.totalPage && page > $scope.resultMap.totalPage) {
            return;
        }

        if (page < 1) {
            page = 1;
        }

        if (page > $scope.resultMap.totalPage) {
            page = $scope.resultMap.totalPage;
        }
        $scope.searchMap.currentPage = page;
        $scope.page = $scope.searchMap.currentPage;
        //查询
        $scope.search();
    }

    $scope.jumpToItem = function (id) {
        location.href = "http://localhost:8089/" + id + ".html";
    }

    $scope.addToCart = function (itemId, num) {
        searchService.addToCart(itemId, num).success(function (response) {
            alert(response.info);
        });
    }
});