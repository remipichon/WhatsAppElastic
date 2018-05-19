
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
                this.drawChart(data);
            } else {
                this.initWebsocket(data.name)
            }

        },this),
        error: _.bind(function (jqXHR, textStatus, errorThrown) {
            log.error(textStatus, errorThrown);
            this.show(Layout.help);
        },this)
    });

  $(".previous-year").on("click", function () {
    LayoutController.prototype.setYear(LayoutController.prototype.enum.year.PREV)
  });
  $(".next-year").on("click", function () {
    LayoutController.prototype.setYear(LayoutController.prototype.enum.year.NEXT)
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

            var lineCount = loadProgress.lineCount;
            var lineRead = loadProgress.lineRead;


            var percentage = parseInt(lineRead / lineCount * 100);


            log.info("destination progress", lineCount, lineRead, percentage);

            $("#line-read-count").html(lineRead);

            var percentageProgress = (percentage)+"%";


            $("#parse-file-progress-bar").css("width", percentageProgress);
            console.log(' progress bar' + percentage+"%");
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


LayoutController.prototype.drawChart = function(conversation){
    HighchartsService.prototype.initDrawHighcharts(conversation); //export statistique to global scope
    this.setYear(this.enum.year.LAST);
    HighchartsService.prototype.drawHighcharts(statistique);
    this.show(Layout.charts);
    this.hide(Layout.feedback);
    $("#from-to").html("From " + new moment(statistique.startDate).format("Do MMMM YYYY") + " to " + new moment(statistique.endDate).format("Do MMMM YYYY"));

};

LayoutController.prototype.hide = function(target){
    $("#"+target).hide();
};

LayoutController.prototype.show = function(target){
    $("#"+target).show();
};

LayoutController.prototype.enum = {};
LayoutController.prototype.enum.year = {
  "LAST": 0,
  "NEXT": 1,
  "PREV": -1
};

LayoutController.prototype.setYear = function (action) {
  var selectedYear = new moment(statistique.selectedYear);
  if (!selectedYear) action = this.enum.year.LAST;

  if (action == this.enum.year.LAST) {
    statistique.selectedYear = statistique.endDate;
  } else if (action == this.enum.year.NEXT) {
    if (selectedYear.year() + 1 > statistique.endDate.year() )
      statistique.selectedYear = new moment(statistique.endDate);
    else
      statistique.selectedYear = selectedYear.add(1, 'year');
  } else if (action == this.enum.year.PREV) {
    if (selectedYear.year() -1 < statistique.startDate.year())
      statistique.selectedYear = new moment(statistique.startDate)
    else
      statistique.selectedYear = selectedYear.add(-1, 'year');
  }
  console.info("set year to",selectedYear.year());
  statistique.year = selectedYear.year();

  statistique.messagePerUserTimelineMonth = null;
  statistique.messagePerUserTimeline = null;
  $("#year-label").html("Stat for " + new moment(selectedYear).format("YYYY"));

  if(statistique.selectedYear.year() <= statistique.startDate.year())
    $(".previous-year").html("");
  else
    $(".previous-year").html(selectedYear.add(-1, 'year').year() + " ");

  if(statistique.selectedYear.year() >= statistique.endDate.year())
    $(".next-year").html("");
  else
    $(".next-year").html(" " + selectedYear.add(1, 'year').year());


  statistique.getMessagePerUserTimeline();
  statistique.getMessagePerUserTimelineMonth();
};


LayoutController.prototype.setMonthButton = function(month){
  $("#month-select").children("button").each(function(){
      $(this).removeClass("btn-info");
      $(this).addClass("btn-default");
    });
  var button = $("#month-select > button:nth-child("+month+")");
  button.addClass("btn-info");
  button.removeClass("btn-default");
};


Aop.around("", function (f) {
    log.info(" AOPbefore LayoutController." + f.fnName, "called with", ((arguments[0].arguments.length == 0) ? "no args" : arguments[0].arguments));
    var retour = Aop.next(f, LayoutController.prototype); //mandatory
    log.info(" AOPafter LayoutController." + f.fnName, "which returned", retour);
    return retour; //mandatory
}, [LayoutController.prototype]);
