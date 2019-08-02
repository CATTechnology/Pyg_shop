//控制层
app.controller('goodsController', function ($scope, $location, $controller, web_shop_goodsService, uploadService, itemCatService, typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        web_shop_goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        web_shop_goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function () {
        var id = $location.search().id;
        alert($location.port());
        //alert("您的主机ip是:"+$location.host());
        web_shop_goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                editor.html($scope.entity.goodsDesc.introduction);
                $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);
                angular.forEach($scope.entity.itemList, function (i, n) {
                    i.spec = JSON.parse(i.spec);
                });
            }
        );
    }

    //保存
    $scope.save = function () {
        $scope.entity.goodsDesc.introduction = editor.html();

        var serviceObject;//服务层对象
        if ($scope.entity.goods.id != null) {//如果有ID
            serviceObject = web_shop_goodsService.update($scope.entity); //修改
        } else {
            serviceObject = web_shop_goodsService.saveEntity($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //添加成功后
                    if (confirm('商品保存成功，你是否要清空现在的内容')) {
                        $scope.entity = {};
                        editor.html('');
                    }
                } else {
                    //添加失败
                    if (confirm('商品保存失败，你是否要清空现在的内容')) {
                        $scope.entity = {};
                        editor.html('');
                    }
                }
            }
        );
    }

    $scope.entity = {
        goods: {},
        goodsDesc: {itemImages: [], customAttributeItems: [], specificationItems: []},
        itemList: []
    };

    //商品保存设计多张表的操作
    $scope.saveEntity = function () {
        $scope.entity.goodsDesc.introduction = editor.html();
        //保存
        web_shop_goodsService.saveEntity($scope.entity).success(function (response) {
            alert(response.success);
            if (response.success) {
                //添加成功后
                if (confirm('商品添加成功，你是否要清空现在的内容')) {
                    editor.html('');
                }
            } else {
                //添加失败
                if (confirm('商品添加失败，你是否要清空现在的内容')) {
                    editor.html('');
                }
            }
        });
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        web_shop_goodsService.dele($scope.ids).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    alert(response.info);
                    $scope.ids = [];
                }
            }
        );
    }

    //批量删除
    $scope.verfify = function () {
        //获取选中的复选框
        web_shop_goodsService.verfify($scope.ids).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    alert(response.info);
                    $scope.ids = [];
                }
            }
        );
    }

    //批量删除
    $scope.shield = function () {
        //获取选中的复选框
        web_shop_goodsService.shield($scope.ids).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    alert(response.info);
                    $scope.ids = [];
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        web_shop_goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //上传图片
    //初始化图片为

    //新建
    $scope.addImage = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image);
    }

    $scope.uploadImage = function () {
        uploadService.uploadImage().success(function (response) {
            if (response.success) {
                //图片回显
                $scope.image.url = response.info;
                alert('上传成功');
            } else {
                alert('上传失败!!!');
            }
        });
    }

    //更具上级id来获取itemCat
    $scope.selectItemCat1List = function () {
        //第一个下拉选择框需要传入0
        itemCatService.findByParentIdUpdate(0).success(function (response) {
            $scope.itemCat1List = response;
        });
    }

    //第二个下拉框应该捕捉第一个下拉框的变化，当第一个下拉框有值时第一个下拉框发生变化
    $scope.$watch('entity.goods.category1Id', function (newValue, oldValue) {
        //alert(newValue);
        if (newValue != oldValue && newValue != 0) {
            //此时第一个下拉框已经选择了，所以第二个下拉框联动
            itemCatService.findByParentIdUpdate(newValue).success(function (response) {
                $scope.itemCat2List = response;
            });
        }
    });

    //第三个下拉框应该更加第二个下拉框变化
    //注意$watch里面的值必须用""或''包裹起来
    $scope.$watch('entity.goods.category2Id', function (newValue, oldValue) {
        if (newValue != oldValue && newValue != 0) {
            itemCatService.findByParentIdUpdate(newValue).success(function (response) {
                $scope.itemCat3List = response;
            });
        }
    });

    $scope.typeTemplate = {specIds: [], brandIds: [], customAttributeItems: []};

    $scope.$watch('entity.goods.category3Id', function (newValue, oldValue) {
        //alert(newValue);
        if (newValue != oldValue && newValue != 0) {
            if ($location.search().id == null) {
                angular.forEach($scope.itemCat3List, function (i, n) {
                    setTemplateValue(i.typeId);
                });
            } else {
                itemCatService.findOne(newValue).success(function (response) {
                    setTemplateValue(response.typeId);
                });
            }
        }
    });

    setTemplateValue = function (id) {
        //设置id
        typeTemplateService.findOneUpdate(id).success(function (response) {
            $scope.typeTemplate = response;
            $scope.entity.goods.typeTemplateId = id;
            $scope.typeTemplate.specIds = JSON.parse($scope.typeTemplate.specIds);
            $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);
            if ($location.search().id == null) {
                $scope.typeTemplate.customAttributeItems = JSON.parse($scope.typeTemplate.customAttributeItems);
                $scope.entity.goodsDesc.customAttributeItems = $scope.typeTemplate.customAttributeItems;
            }
        });
    }

    $scope.item = {};

    $scope.$watch('entity.goods.brandId', function (newValue, oldValue) {
        if (newValue != oldValue) {
            angular.forEach($scope.typeTemplate.brandIds, function (op, n) {
                if (op.id == $scope.entity.goods.brandId) {
                    $scope.item.brand = op.text;
                    $scope.item.category = $scope.typeTemplate.name;
                }
            });
        }
    });

    //添加规格的选项
    $scope.addSpeOptionToItems = function ($event, rowName, rowValue) {
        //首先要获取到这个对象
        var object = isContain($scope.entity.goodsDesc.specificationItems, "attributeName", rowName);
        //判断当前选择框是否选中
        if (object != null) {
            if ($event.target.checked) {
                object.attributeValue.push(rowValue);
            } else {
                var index = object.attributeValue.indexOf(rowValue);
                object.attributeValue.splice(index, 1);
                //判断是否有对象
                if (object.attributeValue.length == 0) {
                    $scope.entity.goodsDesc.specificationItems.splice(
                        $scope.entity.goodsDesc.specificationItems.indexOf(object), 1
                    );
                }
            }
        } else {
            $scope.entity.goodsDesc.specificationItems.push({
                "attributeName": rowName,
                "attributeValue": [rowValue]
            });
        }
    }
    //[{"attributeName":"网络制式","attributeValue":["移动4G"]},{"attributeName":"屏幕尺寸","attributeValue":["5.5寸","4.5寸"]}]

    //定义默认的


    //创建sku
    $scope.createItemList = function () {

        $scope.entity.itemList = [{
            spec: {},
            price: 5000,
            num: 9999,
            status: 1,
            isDefault: '0',
            seller: $scope.item.seller,
            brand: $scope.item.brand,
            category: $scope.item.category
        }];
        //首先要获取$scope.entity.goodsDesc.specificationItems
        var itemSpec = $scope.entity.goodsDesc.specificationItems;
        for (var i = 0; i < itemSpec.length; i++) {
            $scope.entity.itemList = addColumn(
                $scope.entity.itemList, itemSpec[i].attributeName, itemSpec[i].attributeValue
            );
        }
    }

    //添加行
    addColumn = function (list, specName, values) {
        var newList = [];
        for (var i = 0; i < list.length; i++) {
            var oldList = list[i];
            for (var j = 0; j < values.length; j++) {
                var object = JSON.parse(JSON.stringify(oldList));
                object.spec[specName] = values[j];
                newList.push(object);
            }
        }

        return newList;
    }

    //用于转换商品的状态
    $scope.status = ['审核中', '审核通过', '审核未通过', '驳回', '已屏蔽', '未申请'];

    //定义一个保存分类的列表
    $scope.categoryList = [];

    //加载分类数据
    $scope.loadItemCatList = function () {
        itemCatService.findAll().success(function (response) {
            angular.forEach(response, function (i, n) {
                $scope.categoryList[i.id] = i.name;
            });
        });
    }

    //全选
    $scope.selectAll = function ($event) {
        //定义一个标记
        var status = false;
        if ($event.target.checked) {
            status = true;
        }
        //设置selectOne的状态和status相同
        $scope.selectOne = status;
        if (status) {
            //遍历集合，将所有选中的id至于要删除的id列表中
            angular.forEach($scope.list, function (i, n) {
                $scope.ids.push(i.id);
            });
        } else {
            $scope.ids = [];
        }
    }

    //新建
    $scope.new = function () {
        location.href = "goods_edit.html";
    }

    //修改
    $scope.updateGoods = function (id) {
        location.href = "goods_edit.html#?id=" + id;
    }

    //是否选择
    //[{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]},{"attributeName":"屏幕尺寸","attributeValue":["5.5寸","5寸"]}]
    $scope.isSelected = function (optionText, optionName) {
        var object = isContain($scope.entity.goodsDesc.specificationItems, "attributeName", optionText);

        if (object != null) {
            for (var i = 0; i < object.attributeValue.length; i++) {
                if (object.attributeValue[i] == optionName) {
                    return true;
                }
            }
        }

        return false;
    }

    //查询商品详情
    $scope.detailGoods = function(id){
        //alert(id);
        location.href="goods_edit.html#?id="+id;
    }

    $scope.updateStatus = function(status){
        web_shop_goodsService.updateStatus($scope.ids , status).success(
            function(response){
                $scope.reloadList();
                alert(response.info);
            }
        )
    }

});	
