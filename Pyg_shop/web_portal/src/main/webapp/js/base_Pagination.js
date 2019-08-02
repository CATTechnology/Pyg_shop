var app = angular.module('pinyougou', ['pagination']);
/*自定义过来器
* 用于信任html
* */
app.filter('trustHtml', ['$sce', function ($sce) {
    return function (data) {
        return $sce.trustAsHtml(data);
    }
}]);