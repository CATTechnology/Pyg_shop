app.service('searchService', function ($http) {

    this.search = function (searchMap) {
        return $http.post('itemsearch/search.do', searchMap);
    }

    this.addToCart =function(itemId , num){
        return $http.get('/cart/addGoodsToCartList.do?itemId='+itemId+"&num="+num);
    }
});