
LayoutController = function () {
};

Layout =  {
    "charts": "charts",
    "feedback": "feedback",
    "help": "help"
};


LayoutController.prototype.init = function(){
    this.hide(Layout.charts);
    this.hide(Layout.feedback);
    this.hide(Layout.help);

    //read anchor to get conversation name
    var conversationName = window.location.hash.replace("#","");
    if(!conversationName){
        this.show(Layout.help);
        return;
    }

    ConversationHelper.prototype.setConversationName(conversationName);

    //fetch info about conversation name
    $.ajax({
        url: '/api/conversation?conversationName='+conversationName,
        type: 'GET',
        cache: false,
        success: _.bind(function (data) {

            console.log('data from /api/conversation?conversationName='+conversationName,data);

            if (data.parsed) {
                this.drawChart();
            } else {
                this.initWebsocket(data.name)
            }

        },this),
        error: _.bind(function (jqXHR, textStatus, errorThrown) {
            log.error(textStatus, errorThrown);
            this.show(Layout.help);
        },this)
    });

};

LayoutController.prototype.initWebsocket = function(conversationName){
    this.show(Layout.feedback);

    //init websocket to update the loader
    var socket = new SockJS('/whatsappQueries'); //endpoint
    var stompClient = Stomp.over(socket);
    stompClient.connect({}, _.bind(function(frame) {
        var destination = '/parseFileFeedback/'+ conversationName;
        console.log('subscribe to channel ' + destination);
        stompClient.subscribe(destination, _.bind(function(loadProgressData){ //subscribe channel
            var loadProgress =  JSON.parse(loadProgressData.body);

            log.info(destination,loadProgress);

            var ESformat = "YYYY-MM-dd";
            var startDate = new moment(loadProgress.startDate, ESformat);
            var currentDate = new moment(loadProgress.currentParseDate, ESformat);
            var endDate = new moment();
            var lineRead = loadProgress.lineRead;

            var wholeDateSpan = endDate.diff(startDate);
            var alreadyDone = currentDate.diff(startDate);
            var percentage = alreadyDone / wholeDateSpan;


            log.info("destination progress", startDate, endDate, currentDate, lineRead, percentage);

            $("#line-read-count").html(lineRead);

            var percentageProgress = (percentage)*100+"%";


            $("#parse-file-progress-bar").css("width", percentageProgress);
            console.log(' progress bar' + (loadProgress.value/loadProgress.total)*100+"%");
            $("#parse-file-progress-bar span").html(percentageProgress);


            if(lineRead == -24){
                //this is the end

                $("#parse-file-progress-bar").css("width", "100%");
                $("#parse-file-progress-bar span").html("Complete");

                this.drawChart();

            }

        },this));
    },this));
};


LayoutController.prototype.drawChart = function(){
    var statistique = HighchartsService.prototype.initDrawHighcharts()
    HighchartsService.prototype.drawHighcharts(statistique);
    this.show(Layout.charts);
    this.hide(Layout.feedback);
};

LayoutController.prototype.hide = function(target){
    $("#"+target).hide();
};

LayoutController.prototype.show = function(target){
    $("#"+target).show();
};






Aop.around("", function (f) {
    log.info(" AOPbefore LayoutController." + f.fnName, "called with", ((arguments[0].arguments.length == 0) ? "no args" : arguments[0].arguments));
    var retour = Aop.next(f, LayoutController.prototype); //mandatory
    log.info(" AOPafter LayoutController." + f.fnName, "which returned", retour);
    return retour; //mandatory
}, [LayoutController.prototype]);
