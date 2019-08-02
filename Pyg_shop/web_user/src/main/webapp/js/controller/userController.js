//控制层
app.controller('userController', function ($scope, $controller, userService) {

    //$controller('baseController',{$scope:$scope});//继承

    $scope.entity = {phone: ""};

    $scope.registerCode = "";

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        userService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        userService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function (id) {
        userService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    }

    //保存
    $scope.save = function () {
        if($scope.registerCode == ""){
            alert("请输入验证码!!!");
            return ;
        }
        if($scope.entity.username == null || $scope.entity.username == "" || $scope.entity.password == null || $scope.entity.password == ""){
            alert("用户名或密码不能为空!!!");
            return ;
        }
        userService.add($scope.entity , $scope.registerCode).success(function (response) {
            alert(response.info);
        })
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        userService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        userService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }


    //发送验证码
    $scope.sendRegisterCode = function () {
        //判断是否输入手机号码
        if ($scope.entity.phone == null || $scope.entity.phone == "") {
            alert("请输入你的手机号，再点击发送验证码!!!!");
            return;
        }
        userService.sendRegisterCode($scope.entity.phone).success(
            function (response) {
            }
        )
    }

    //加载用户名
    $scope.loadUsername = function(){
        userService.loadUsername().success(function(response){
            $scope.loginUsername = response.username;
        })
    }
});
