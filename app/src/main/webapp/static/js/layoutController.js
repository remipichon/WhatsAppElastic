
LayoutController = function () {

};

LayoutController.prototype.init = function(){

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
        success: function (data) {

            console.log('data from /api/conversation?conversationName='+conversationName,data);

            if(!data.parsed){

                //draw charts HighchartsService.prototype.initDrawHighcharts
                var statistique = HighchartsService.prototype.initDrawHighcharts()
                HighchartsService.prototype.drawHighcharts(statistique);
            } else {
                //show feedback and init websocket to update the loader
            }

        },
        error: function (jqXHR, textStatus, errorThrown) {
            log.error(textStatus, errorThrown);
        }
    });

};




Aop.around("", function (f) {
    log.info(" AOPbefore LayoutController." + f.fnName, "called with", ((arguments[0].arguments.length == 0) ? "no args" : arguments[0].arguments));
    var retour = Aop.next(f, LayoutController.prototype); //mandatory
    log.info(" AOPafter LayoutController." + f.fnName, "which returned", retour);
    return retour; //mandatory
}, [LayoutController.prototype]);
