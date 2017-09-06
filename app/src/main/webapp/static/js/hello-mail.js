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


    LayoutController.prototype.init();



    $("#month-select > button").on("click",function(event){
        $(this).parent().children().each(function(child){
            $(this).removeClass("btn-info");
            $(this).addClass("btn-default");
        });
        $(this).addClass("btn-info");
        $(this).removeClass("btn-default");
        statistique.messagePerUserTimelineMonth = null;
        statistique.getMessagePerUserTimelineMonth({month : $(this).index()+1});
    });

});

