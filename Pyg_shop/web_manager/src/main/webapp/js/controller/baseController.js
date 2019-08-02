app.controller('baseController', function ($http, $scope) {
    <!--1.重新加载页面加载页面-->
    $scope.reloadList = function () {
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    };

    //分页页面配置
    $scope.paginationConf = {
        <!--当前的页码-->
        currentPage: 1,
        <!--初始化总条数-->
        totalItems: 10,
        <!--每页的数量-->
        itemsPerPage: 10,
        <!--可选择的每页条数-->
        perPageOptions: [5, 10, 15, 20, 25],
        /*修改页码加载数据*/
        onChange: function () {
            $scope.reloadList();
        }
    };

    //多选
    $scope.ids = [];
    //将选择的id和取消的id在ids集合里面操作
    $scope.pushId = function ($event, id) {
        if ($event.target.checked) {
            $scope.ids.push(id);
        } else {
            var index = $scope.ids.indexOf(id);
            $scope.ids.splice(index, 1);//删除一个
        }
    }

    //json格式转换字符串
    $scope.parseJsonToStr = function (str, pattern) {
        var objs = JSON.parse(str);
        var result = '';
        for (var i = 0; i < objs.length; i++) {
            if (i > 0) {
                result += ',';
            }
            result += objs[i][pattern];
        }
        return result;
    }

    //判断一个集合里面是否含有当前的列
    isContain = function (list, rowName , rowValue) {
        //遍历集合
        for (var i = 0; i < list.length; i++) {
            if (list[i][rowName] == rowValue) {
                return list[i];
            }
        }
        return null;
    }
})