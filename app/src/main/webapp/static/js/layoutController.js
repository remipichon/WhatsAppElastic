
LayoutController = function () {
};

Layout =  {
    "charts": "charts",
    "feedback": "feedback"
};


LayoutController.prototype.init = function(){
    this.hide(Layout.charts);
    this.hide(Layout.feedback);

    //read anchor to get conversation name
    var conversationName = window.location.hash.replace("#","");
    if(!conversationName){
        ConversationHelper.prototype.setConversationName("missing");
        alert("No conversation name as been found in the url (after the anchor #)")
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
                this.initWebsocket()
            }

        },this),
        error: function (jqXHR, textStatus, errorThrown) {
            log.error(textStatus, errorThrown);
        }
    });

};

LayoutController.prototype.initWebsocket = function(){
    this.show(Layout.feedback);

    //init websocket to update the loader
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
