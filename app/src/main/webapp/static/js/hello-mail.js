/**
 * Created by remi on 04/07/15.
 */
angular.module('hello', [])
    .controller('home', function ($scope, $http) {
        $http.get('/resource/').success(function (data) {
            $scope.greeting = data;
        })
    });


local = false;


$(document).ready(function () {
    log.setLevel("trace");
    //parce que les trace prennent trop de place dans la console
    log.trace = function () {
        var args = Array.prototype.slice.call(arguments);
        args.unshift("TRACE : ");
        log.info.apply(this, args);
    };


    highchartsService = new HighchartsService();

    LayoutController.prototype.init();

    $("#month-select > button").on("click",function(event){
        LayoutController.prototype.setMonthButton($(this).index()+1);
        statistique.messagePerUserTimelineMonth = null;
        statistique.getMessagePerUserTimelineMonth({month : $(this).index()+1});
    });

});

