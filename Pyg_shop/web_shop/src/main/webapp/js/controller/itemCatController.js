//控制层
app.controller('itemCatController', function ($scope, itemCatService, typeTemplateService) {

    $scope.parentId = 0;

    $scope.searchEntity = {};

    // $controller('baseController',{$scope:$scope});//继承
    $scope.reloadList = function () {
        $scope.findByParentId($scope.parentId, $scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    };
    //多选
    $scope.ids = [];
    //将选择的id和取消的id在ids集合里面操作
    $scope.pushId = function ($event, id) {
        if ($event.target.checked) {
            alert(id);
            $scope.ids.push(id);
        } else {
            var index = $scope.ids.indexOf(id);
            $scope.ids.splice(index, 1);//删除一个
        }
    }
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

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        itemCatService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        itemCatService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function (id) {
        itemCatService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    }

    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        var isAdd = true;
        if ($scope.entity.id != null) {//如果有ID
            alert($scope.entity_origin_name);
            if ($scope.entity_origin_name != $scope.entity.name || $scope.entity_origin_typeId != $scope.entity.typeId) {
                serviceObject = itemCatService.update($scope.entity); //修改
                isAdd = false;
            } else {
                alert('您，没有做任何修改!!!');
            }
        } else {
            //设置parentId
            $scope.entity.parentId = $scope.parentId;
            serviceObject = itemCatService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //重新查询
                    $scope.reloadList();//重新加载
                    if (!isAdd) {
                        var info = "--已将---";
                        if ($scope.entity_origin_name != $scope.entity.name) {
                            info += $scope.entity_origin_name + '的名称改为' + $scope.entity.name + ",";
                        }
                        if ($scope.entity_origin_typeId != $scope.entity.typeId) {

                            //获取修改后的类型名
                            var name = "";
                            angular.forEach($scope.typeOption.data, function (n, i) {
                                if (n.id == $scope.entity.typeId) {
                                    name = n.text;
                                }
                            })
                            info += $scope.entity.name + '的类型改为' + name + '----';
                        }
                        alert(info);
                    } else {
                        alert(response.info);
                    }
                } else {
                    alert(response.info);
                }
                $scope.entity = {};
            }
        );
    }

    //全选
    $scope.selectAll = function($event){
        //首先定义一个状态,全选框默认未被选定
        var status = false;
        if($event.target.checked){
            status = true;
        }
        //设置所有单选框和这个全选框同步
        $scope.select_one = status;

        //将选择的元素放入列表
        if(status){
            angular.forEach($scope.list , function(i , n){
                $scope.ids.push(i.id);
            });
        }else{
            $scope.ids = [];
            alert('取消了所有的选择!!!');
        }

    }

    //批量删除
    $scope.deleteItemCat = function () {
        //获取选中的复选框
        itemCatService.dele($scope.ids).success(
            function (response) {
                if (response.success) {
                    $scope.ids = [];
                    $scope.reloadList();//刷新列表
                    alert(response.info);
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        itemCatService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //通过parentID来寻找
    $scope.findByParentId = function (parentId, page, rows) {
        //每次赋值
        $scope.parentId = parentId;
        itemCatService.findByParentId(parentId, page, rows, $scope.searchEntity).success(function (response) {
            $scope.list = response.rows;
            $scope.paginationConf.totalItems = response.total;
        });
    }
    //改变parentId
    $scope.changeParentId = function (id) {
        //改变上级parentId
        //$scope.parentId = id;
        //当点击查询下一级的时候每次从第一页开始查询
        $scope.findByParentId(id, 1, $scope.paginationConf.itemsPerPage);
    }

    //定义一个全局的level用于记录此时的等级
    $scope.level = 1;
    //设置此时等级
    $scope.setLevel = function (level) {
        $scope.level = level;
    }

    //更具等级设置面包屑
    $scope.selectList = function (entity) {
        if ($scope.level == 1) {
            $scope.entity_1 = null;
            $scope.entity_2 = null;
        }
        if ($scope.level == 2) {
            $scope.entity_1 = entity;
            $scope.entity_2 = null;
        }
        if ($scope.level == 3) {
            $scope.entity_2 = entity;
        }

        //初始化为每页十行，从第一页开始
        $scope.findByParentId(entity.id, 1, 10);
    }

    //修改
    $scope.setEntity = function (entity) {
        $scope.entity = entity;
        //记录此时entity是否发生变化
        $scope.entity_origin_name = entity.name;
        $scope.entity_origin_typeId = entity.typeId;
    }

    $scope.typeOption = {data: []}

    //初始化数据
    $scope.initTypeOption = function () {
        typeTemplateService.findAllToMap().success(function (response) {
            $scope.typeOption.data = response;
        });
    }


});
