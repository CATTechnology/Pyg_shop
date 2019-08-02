app.service('brandService', function ($http) {
    //分页
    this.findPage = function (page, size) {
        return $http.get('../brand/findPage.do?page=' + page + "&size=" + size);
    }

    //更新操作
    this.update = function (entity) {
        return $http.post('../brand/update.do', entity);
    }

    this.save = function (entity) {
        return $http.post('../brand/save.do', entity);
    }

    //数据回显
    this.findOne = function (id) {
        return $http.get('../brand/findOne.do?id=' + id);
    }

    //批量删除
    this.delDetach = function (ids) {
        return $http.get('../brand/delDetach.do?ids=' + ids);
    }

    //搜索品牌
    this.search = function (page, size, brandentity) {
        return $http.post('../brand/search.do?page=' + page + '&size=' + size, brandentity);
    }
})