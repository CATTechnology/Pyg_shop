app.service('uploadService' , function($http){
    this.uploadImage = function(){
        var formData = new FormData();
        formData.append('file' , file.files[0]);
        return $http({
            url:'../uploadImage.do',
            method:'POST',
            data:formData,
            headers:{'Content-Type':undefined},
            transformRequest:angular.identity
        });
    }
})