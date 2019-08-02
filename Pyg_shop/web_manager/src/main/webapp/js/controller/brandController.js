//控制层
app.controller('brandController', function ($scope, $http, $controller, brandService) {

    //通过继承父类，实现共有属性
    $controller('baseController', {$scope: $scope});
    /*重新加载页面*/
    /*$scope.reloadList = function () {
        <!--加载页面-->
        //$scope.findPage($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    };*/

    $scope.findPage = function (page, size) {
        brandService.findPage(page, size).success(
            function (response) {
                //返回的数据集合List<Brand>
                $scope.list = response.rows;
                //查询到的总记录数
                $scope.paginationConf.totalItems = response.total;//更新总记录数
                //alert(response.total);
            }
        );
    };

    /*分页控制*/
    /*$scope.paginationConf = {
        <!--当前的页码-->
        currentPage: 1,
        <!--初始化总条数-->
        totalItems: 10,
        <!--每页的数量-->
        itemsPerPage: 10,
        <!--可选择的每页条数-->
        perPageOptions: [5, 10, 15, 20, 25],
        /!*修改页码加载数据*!/
        onChange: function () {
            $scope.reloadList();
        }
    };*/

    /*保存一个品牌*/
    $scope.save = function () {
        //判断此时的id是否有主键如果没有主键就保存到数据库
        if ($scope.entity != null) {
            if ($scope.entity.id != null) {
                $scope.update();
            } else {
                $scope.add();
            }
        } else {
            alert("对不起你没有输入要保存的品牌....");
        }

    }

    //更新品牌
    $scope.update = function () {
        brandService.update($scope.entity).success(function (response) {
            $scope.reloadList();
            if (response.success) {
                alert(response.info);
            } else {
                alert(response.info);
            }
        });
    }

    $scope.add = function () {
        brandService.save($scope.entity).success(function (response) {
            //点击保存之后重新加载页面
            $scope.reloadList();
            if (response.success) {
                alert(response.info);
            } else {
                alert(response.info);
            }
        });
    }
    //修改品牌信息
    $scope.findOne = function (id) {
        //将之前的缓存信息清除掉
        $scope.entity = {};
        //查询品牌根据id
        brandService.findOne(id).success(function (response) {
            $scope.entity = response;
            //添加品牌修改功能
        });
    }

    /*$scope.ids = [];
    //将选择的id和取消的id在ids集合里面操作
    $scope.pushId = function ($event, id) {
        if ($event.target.checked) {
            $scope.ids.push(id);
        } else {
            var index = $scope.ids.indexOf(id);
            $scope.ids.splice(index, 1);//删除一个
        }
    }*/
    //批量删除品牌信息
    $scope.delDetach = function () {
        brandService.delDetach($scope.ids).success(function (response) {
            alert(response.info);
            $scope.reloadList();//重新加载页面
        });
    }

    $scope.brand = {};
    //搜索功能实现
    $scope.search = function (page, size) {
        brandService.search(page, size, $scope.brand).success(function (response) {
            //商品
            $scope.list = response.rows;
            //总条数
            $scope.paginationConf.totalItems = response.total;//更新总记录数
        })
    }
});