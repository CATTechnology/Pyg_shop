app.service('indexService' , function($http){
    this.getName = function(){
        return $http.get('../user/getName.do');
    }
})