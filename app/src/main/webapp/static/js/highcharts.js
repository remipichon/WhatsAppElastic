HighchartsService = function () {
}

HighchartsService.prototype.drawUserBarChart = function (statistique) {

    //perharps the user or Worker will help
    var enumName = statistique.getEnumName();
    var numberMessagePerUser = statistique.getNumberMessagePerUser();
    var numberCharacterPerMessagePerUser = statistique.getNumberCharacterPerMessagePerUser();
    var totalContentPerUser = statistique.getTotalContentPerUser();

    var $chart = $('#user-bar-chart');
    if (typeof $chart.highcharts() !== "undefined") $chart.highcharts().destroy();
    $chart.highcharts({
        chart: {
            type: 'bar'
        },
        title: {
            text: 'Message and letter count per user'
        },
        xAxis: {
            categories: enumName

        },
        yAxis: [{
            min: 0,
            title: {
                text: 'Total message count'
            }
        }, {
            title: {
                text: 'Average letter count per message'
            }
        }, {
            title: {
                text: 'Total letter count'
            }
        }],
        legend: {
            shadow: false
        },
        tooltip: {
            shared: true
        },
        plotOptions: {
            bar: {
                grouping: false,
                shadow: false,
                borderWidth: 0
            },
        },
        series: [{
            name: 'Total message count',
            color: 'rgba(124, 181, 236,0.7)',
            data: _.values(numberMessagePerUser),
            pointPadding: 0.2,
            pointPlacement: -0.2,
            yAxis: 0
        },
            {
                name: 'Average letter count per message',
                color: 'rgba(209, 50, 144, 0.3)',
                data: _.values(numberCharacterPerMessagePerUser),
                pointPadding: 0.3,
                pointPlacement: -0.2,
                yAxis: 1
            }, {
                name: 'Total letter count',
                color: 'rgba(144, 237, 125,0.7);',
                data: _.values(totalContentPerUser),
                pointPadding: 0.4,
                pointPlacement: -0.2,
                yAxis: 2
            }
        ]
    });
}


HighchartsService.prototype.drawContentUserPieChart = function (statistique) {

    var statContentMessagePerUser = statistique.getStatContentMessagePerUser();

    var $chart = $('#user-content-pie-chart');
    if (typeof $chart.highcharts() !== "undefined") $chart.highcharts().destroy();

    $('#user-content-pie-chart').highcharts({
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: 1, //null,
            plotShadow: false
        },
        title: {
            text: 'Letter count per user'
        },
        tooltip: {
            pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: true,
                    format: '<span style="font-size: 15px;"><span style="font-size: 16px; font-weight:bold;">{point.name}</span>: {point.percentage:.1f} %</span>',
                    style: {
                        color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                    }
                },
                colors: statistique.colors.statContentMessagePerUser
            }
        },
        series: [{
            type: 'pie',
            name: 'Total letters count',
            data: _.map(statContentMessagePerUser, function (num, key) {
                return [key, num];
            })
        }]
    });
}


HighchartsService.prototype.drawMessageUserPieChart = function (statistique) {

    var statNumberMessagePerUser = statistique.getStatNumberMessagePerUser();

    var $chart = $('#user-message-pie-chart');
    if (typeof $chart.highcharts() !== "undefined") $chart.highcharts().destroy();

    $('#user-message-pie-chart').highcharts({
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: 1, //null,
            plotShadow: false
        },
        title: {
            text: 'Message count per user'
        },
        tooltip: {
            pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
        },
        minSize: 120,
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: true,
                    format: '<span style="font-size_: 15px;"><span style="font-size: 16px; font-weight:bold;">{point.name}</span>: {point.percentage:.1f} %</span>',
                    style: {
                        color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black',

                    }
                },
                colors: statistique.colors.statNumberMessagePerUser
            }
        },
        series: [{
            type: 'pie',
            name: 'Messages sent',
            data: _.map(statNumberMessagePerUser, function (num, key) {
                return [key, num];
            })
        }]
    });
}


HighchartsService.prototype.drawMessageBarChartTimeline = function (statistique) {
    var categories = [];

    var $chart = $('#user-bar-chart-timeline');
    if (typeof $chart.highcharts() !== "undefined") $chart.highcharts().destroy();

    var series = [];
    var messagePerUserTimeline = statistique.getMessagePerUserTimeline();
    _.each(messagePerUserTimeline, function (value, name) {
        series.push({
            name: name,
            data: value
        });
    });

    var month = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"];
    for (var i = 1; i <= 12; i++) {
        categories.push(month[i - 1])
    }

    $('#user-bar-chart-timeline').highcharts({
        title: {
            text: 'Message count per user per month on ' + statistique.year,
            x: -20 //center
        },
        xAxis: {
            categories: categories
        },
        yAxis: {
            title: {
                text: 'Nb messages'
            },
            plotLines: [{
                value: 0,
                width: 1,
                color: '#808080'
            }]
        },
        tooltip: {
            valueSuffix: ' messages'
        },
        legend: {
            layout: 'vertical',
            align: 'right',
            verticalAlign: 'middle',
            borderWidth: 0
        },
        series: series
    });
}

HighchartsService.prototype.drawMessageBarChartTimelineMonth = function (statistique) {
    var categories = [];

    var $chart = $('#user-bar-chart-timeline-month');
    if (typeof $chart.highcharts() !== "undefined") $chart.highcharts().destroy();

    var series = [];
    var messagePerUserTimelineMonth = statistique.getMessagePerUserTimelineMonth();
    _.each(messagePerUserTimelineMonth, function (value, name) {
        series.push({
            name: name,
            data: value
        });
    });

    var day = ["M", "T", "W", "T","F", "S", "S"];
    for (var i = 1; i <= 31; i++) {
        categories.push(i)
    }

    $('#user-bar-chart-timeline-month').highcharts({
        title: {
            text: 'Message count per user per day on ' + statistique.year,
            x: -20 //center
        },
        xAxis: {
            categories: categories
        },
        yAxis: {
            min: 0,
            title: {
                text: 'Nb messages'
            },
            plotLines: [{
                value: 0,
                width: 1,
                color: '#808080'
            }]
        },
        tooltip: {
            valueSuffix: ' messages'
        },
        legend: {
            layout: 'vertical',
            align: 'right',
            verticalAlign: 'middle',
            borderWidth: 0
        },
        series: series
    });
}


HighchartsService.prototype.drawHighcharts = function (statistique) {

    if (!statistique instanceof StatistiqueService) {
        return log.error("drawHighcharts : not statistique args");
    }
    var highchartsService = new HighchartsService();
    highchartsService.drawUserBarChart(statistique);
    highchartsService.drawMessageUserPieChart(statistique);
    highchartsService.drawContentUserPieChart(statistique);
    highchartsService.drawMessageBarChartTimeline(statistique);
    highchartsService.drawMessageBarChartTimelineMonth(statistique);
}


HighchartsService.prototype.initDrawHighcharts = function () {

        if(conversationNameStored == null) {
            alert("You must first upload or load a conversation");
            return;
        }

        //TODO add dates
        statistique = new StatistiqueService({
            conversationName: conversationNameStored,
            setAll: true
        });

        return statistique;


}

// must be after adding methods to prototype
Aop.around("", function (f) {
    log.info(" AOPbefore HighchartsService." + f.fnName, "called with", ((arguments[0].arguments.length == 0) ? "no args" : arguments[0].arguments));
    var retour = Aop.next(f, HighchartsService.prototype); //mandatory
    log.info(" AOPafter HighchartsService." + f.fnName, "which returned", retour);
    return retour; //mandatory
}, [HighchartsService.prototype]);
