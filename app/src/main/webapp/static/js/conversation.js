//Conversation = new Meteor.Collection("conversation");
ConversationSubscription = null;

conversationNameStored = null;


ConversationHelper = function () {
}


/**
 * @param  {string} conversationName
 */
ConversationHelper.prototype.setConversationName = function (conversationName) {
    $("#filename h3").html(conversationName);
    $("#header-conversation-name").html("Conversation " +conversationName);
    conversationNameStored = conversationName;
}



Aop.around("", function (f) {
    log.info(" AOPbefore ConversationHelper." + f.fnName, "called with", ((arguments[0].arguments.length == 0) ? "no args" : arguments[0].arguments));
    var retour = Aop.next(f, ConversationHelper.prototype); //mandatory
    log.info(" AOPafter ConversationHelper." + f.fnName, "which returned", retour);
    return retour; //mandatory
}, [ConversationHelper.prototype]);
