/**
 * Created by remi on 04/07/15.
 */
angular.module('hello', [])
    .controller('home', function ($scope, $http) {
        $http.get('/resource/').success(function (data) {
            $scope.greeting = data;
        })
    });


$(document).ready(function() {
    //log.setLevel("trace");
    ////parce que les trace prennent trop de place dans la console
    //log.trace = function() {
    //    var args = Array.prototype.slice.call(arguments);
    //    args.unshift("TRACE : ");
    //    log.info.apply(this, args);
    //}
    DatetimePickerService.prototype.initDatePicker();
    DatetimePickerService.prototype.initTimePicker();
    $("#draw-button").on("click", HighchartsService.prototype.initDrawHighcharts);

    ModalControler.prototype.initModal();

    // test.drawHightcharts("sample");

    //don't why I have to do this
    $("#filename").css("cursor","pointer");
});

log = console;