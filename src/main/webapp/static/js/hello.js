/**
 * Created by remi on 04/07/15.
 */
angular.module('hello', [])
    .controller('home', function ($scope, $http) {
        $http.get('/resource/').success(function (data) {
            $scope.greeting = data;
        })
    });