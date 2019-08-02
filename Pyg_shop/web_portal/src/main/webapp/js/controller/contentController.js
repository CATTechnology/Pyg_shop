app.controller('contentController' , function($scope , contentService){

    $scope.contentList = [];

    $scope.selectContentList = function(index){
        contentService.selectContentList(index).success(function(response){
            $scope.contentList[index] = response;
        });
    }

    $scope.search = function(){
        location.href="search.html#?keywords="+$scope.keywords;
    }
})