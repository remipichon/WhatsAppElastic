/**
 * Created by remi on 04/07/15.
 */
angular.module('hello', [])
    .controller('home', function ($scope, $http) {
        $http.get('/resource/').success(function (data) {
            $scope.greeting = data;
        })
    });


local = true;


$(document).ready(function () {
    log.setLevel("trace");
    //parce que les trace prennent trop de place dans la console
    log.trace = function () {
        var args = Array.prototype.slice.call(arguments);
        args.unshift("TRACE : ");
        log.info.apply(this, args);
    }
    DatetimePickerService.prototype.initDatePicker();
    DatetimePickerService.prototype.initTimePicker();
    $("#draw-button").on("click", HighchartsService.prototype.initDrawHighcharts);

    ModalControler.prototype.initModal();

    highchartsService = new HighchartsService();

    if (local) {
        if (typeof storedMessagePerUserTimeline != "undefined") {
            statistique = new StatistiqueService({
                conversationName: "sample_newFormat", //TODO get from method params
                setAll: false
            });

            allData = eval("(" + allData + ")");

            statistique.enumName = allData.enumName;
            statistique.numberMessagePerUser = allData.numberMessagePerUser;
            statistique.numberCharacterPerMessagePerUser = allData.numberCharacterPerMessagePerUser;
            statistique.totalContentPerUser = allData.totalContentPerUser;

            statistique.statContentMessagePerUser = allData.statContentMessagePerUser;

            statistique.statNumberMessagePerUser = allData.statNumberMessagePerUser;

            statistique.messagePerUserTimeline = allData.messagePerUserTimeline;

            highchartsService.drawHighcharts(statistique);


        }
    }


    //don't why I have to do this
    $("#filename").css("cursor", "pointer");
});

