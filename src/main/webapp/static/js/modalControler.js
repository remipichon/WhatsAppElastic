//Template.containerCharts.getCurrentConversationName = function() {
//  if (typeof Conversation.findOne({}) === "undefined") {
//    return "Feel free to click me !";
//  }
//  return Conversation.findOne({}).name;
//}

ModalControler = function () {
    this.existingConversation = null;
}

/**
 * check if input are logically filled
 * @param  {string} conv    conversation name to load
 * @param  {file} file    binary file of the file to parse
 * @param  {string} newConv new uniqueme name for the file to be parsed
 * @return {boolean}         true if modal is fine
 */
ModalControler.prototype._modalRightlyFilled = function (conv, file, newConv) {
    if (!newConv && file || !newConv && !conv || !conv && !file && !newConv) {
        log.error("there is no conversation name");
        $("#modal-file-continue").one("click", ModalControler.prototype.loadFileFromModal);
        return false;
    }

    if (!file && newConv && !conv) {
        log.error("there is no file to upload");
        $("#modal-file-continue").one("click", ModalControler.prototype.loadFileFromModal);
        return false;
    }

    return true;
}


/**
 * switch validation state of $input.
 * display none all other state
 * @param  {jQuery object} $input input within a form-group with has-feedback
 * @param  {string} state sucess, loading, error
 */
ModalControler.prototype.switchStateInput = function ($input, state) {
    var $formGroup = $input.parent(".has-feedback");
    if ($formGroup.length === 0) {
        log.error("ModalControler.prototype.switchStateInput : no form group found");
        return;
    }
    if ($formGroup.data("stateInput") === state) {
        log.debug("ModalControler.prototype.switchStateInput : skipped");
        return;
    }

    $formGroup.removeClass("has-success").removeClass("has-warning").removeClass("has-error");
    $formGroup.children(".sucess-icon").css("display", "none");
    $formGroup.children(".loading-icon").css("display", "none");
    $formGroup.children(".error-icon").css("display", "none");

    switch (state) {
        case "sucess":
            $formGroup.addClass("has-success");
            $formGroup.children(".sucess-icon").css("display", "block");
            break;
        case "loading":
            $formGroup.addClass("has-warning");
            $formGroup.children(".loading-icon").css("display", "block");
            break;
        case "error":
            $formGroup.addClass("has-error");
            $formGroup.children(".error-icon").css("display", "block");
            break;
    }
    $formGroup.data("stateInput", state);
}


ModalControler.prototype.loadFileFromModal = function (event) {
    $(this).off(event);

    var conv = $("#conversation-name").val() !== "";
    var file = $("#fileToUpload").val() !== "";
    var newConv = $("#new-conversation-name").val() !== "";
    //var getDataSub = ($("#getDataSub:checked").length === 1) ? true : false;

    if (!this._modalRightlyFilled(conv, file, newConv)) return;


    if (conv) {
        //asked to server
        var conversationName = $("#conversation-name").val();
        log.info("loadFromModal : conversation asked to server");
        $("#modal-file").one("click", ModalControler.prototype.resetModal);
        ConversationHelper.prototype.setConversationName(conversationName);
        return;
    }

    var conversationName = $("#new-conversation-name").val();
    var self = this;

    $("#modal-file-continue").attr("disabled", "disabled");
    $("#modal-file-close").attr("disabled", "disabled");
    $("#panel-parse-file-progress-bar").fadeIn(300);

    var file = document.getElementById('fileToUpload').files[0];
    var formData = new FormData();
    formData.append("file",file);
    formData.append("conversationName",conversationName);


    var data = JSON.stringify({ 'file': file,'conversationName': conversationName})
    $.ajax({
        url: '/api/conversation/uploadFile',
        type: 'POST',
        data: formData,
        async: true,
        cache: false,
        contentType: false,
        processData: false,
        success: function (data) {

            $("#parse-file-progress-bar").css("width", "100%");
            $("#parse-file-progress-bar span").html("Complete");
            $("#modal-file-continue").removeAttr("disabled");

            $("#modal-file").one("click", ModalControler.prototype.resetModal);

            ConversationHelper.prototype.setConversationName(conversationName);

        },
        error: function (jqXHR, textStatus, errorThrown) {
            log.error("loadFileFromModal " + textStatus);
           // alert("Error while parsing " + errorThrown);
            $("#parse-file-progress-bar").css("width", "100%");
            $("#parse-file-progress-bar span").html("Complete");
            $("#modal-file-continue").removeAttr("disabled");

            $("#modal-file").one("click", ModalControler.prototype.resetModal);
        }
    });
}

ModalControler.prototype.initModal = function () {
    var self = this;
    $("#modal-file-close").on("click", this.resetModal);
    $("#new-conversation-name").on("click", function () {
        log.debug("new on click ");
        $("#conversation-name").val("");
        self.switchStateInput($("#conversation-name"), "rien");
    });
    $("#conversation-name").on("click", function () {
        $("#new-conversation-name").val("");
        self.switchStateInput($("#new-conversation-name"), "rien");
    });
    this.resetModal();

}

ModalControler.prototype.resetModal = function () {
    this.switchStateInput($("#conversation-name"), "rien");
    this.switchStateInput($("#new-conversation-name"), "rien");

    $("#conversation-name").val("");
    $("#new-conversation-name").val("");
    $("#new-conversation-name").off("change");
    $("#new-conversation-name").off("keyup");
    $("#fileToUpload").val("");

    $("#parse-file-progress-bar").css("width", "0%");
    $("#parse-file-progress-bar span").html("0%");
    $("#panel-parse-file-progress-bar").fadeOut(200);
    $("#modal-file-close").removeAttr("disabled");

    $("#modal-file-continue").one("click", ModalControler.prototype.loadFileFromModal);
    $("#modal-file #conversation-name").one("click", ModalControler.prototype.initAutocompleteConversationName);

    $(this).off(event);
    $(this).modal('hide');
    $("#modal-file-continue").one("click", self.loadFileFromModal);

    $("#new-conversation-name").one("click", ModalControler.prototype.initInputNewConversationName);

    try {
        $("#modal-file #conversation-name").autocomplete("destroy");
    } catch (err) {
        log.error(err);
    }
}

ModalControler.prototype.isNameAvailable = function (coll, name) {
    return coll.indexOf(name) !== -1;
}

ModalControler.prototype.stateOfLoadName = function (result) {
    var $input = $("#modal-file #conversation-name");
    if (this.isNameAvailable(result, $input.val())) {
        this.switchStateInput($input, "sucess");
        $("#modal-file-continue").removeAttr("disabled");
    } else {
        this.switchStateInput($input, "error");
        $("#modal-file-continue").attr("disabled", "disabled");
    }

}


ModalControler.prototype.stateOfCreateName = function (result) {
    var $input = $("#modal-file #new-conversation-name");
    if (!this.isNameAvailable(result, $input.val()) && $input.val() !== "") {
        this.switchStateInput($input, "sucess");
        $("#modal-file-continue").removeAttr("disabled");
    } else {
        this.switchStateInput($input, "error");
        $("#modal-file-continue").attr("disabled", "disabled");
    }

}


ModalControler.prototype.initAutocompleteConversationName = function () {
    this.switchStateInput($("#modal-file #conversation-name"), "loading");
    var self = this;
    $("#modal-file #conversation-name").removeAttr("disabled");

    var self = this;
    $.ajax({
        url: '/api/conversation/getExistingConversationName',
        type: 'GET',
        async: true,
        dataType: "json",
        success: function (result) {

            self.switchStateInput($("#modal-file #conversation-name"), "rien");

            var data = [];
            _.each(result, function (conversation) {
                data.push(conversation.name);
            });

            log.info("getExistingConversationName init autocompelte with", data);
            // var resultForAutoComp = result.pop(""); //"" pose des soucis à jQuery.ui.autocomplete
            $("#modal-file #conversation-name").autocomplete({
                source: data,//resultForAutoComp,
                position: {
                    my: "right top",
                    at: "right bottom"
                },
                search: function (event, ui) {
                    self.stateOfLoadName(data);
                },
                change: function (event, ui) {
                    self.stateOfLoadName(data);
                },  //==> pose des soucis lorsqu'on reset le val
                select: function (event, ui) {
                    self.stateOfLoadName(data);
                }
            });

        },
        error: function (jqXHR, textStatus, errorThrown) {
            self.switchStateInput($("#modal-file #conversation-name"), "error");
            $("#modal-file #conversation-name").attr("disabled", "disabled");
            log.error("getExistingConversationName :", errorThrown);
        }
    });
}


ModalControler.prototype.initInputNewConversationName = function () {
    var $input = $("#modal-file #new-conversation-name");
    this.switchStateInput($input, "loading");
    var self = this;
    $input.removeAttr("disabled");

    var self = this;
    $.ajax({
        url: '/api/conversation/getExistingConversationName',
        type: 'GET',
        async: true,
        dataType: "json",
        success: function (result) {

            self.switchStateInput($input, "rien");

            var data = [];
            _.each(result, function (conversation) {
                data.push(conversation.name);
            });

            log.info("initInputNewConversationName init input with", data);
            $input.on("change", function () {
                self.stateOfCreateName(data);
            });
            $input.on("keyup", function () {
                self.stateOfCreateName(data);
            });

        },
        error: function (jqXHR, textStatus, errorThrown) {
            self.switchStateInput($("#modal-file #conversation-name"), "error");
            $("#modal-file #conversation-name").attr("disabled", "disabled");
            $input.attr("disabled", "disabled");
            log.error("getExistingConversationName :", errorThrown);
        }
    });
}


Aop.around("", function (f) {
    log.info(" AOPbefore ModalControler." + f.fnName, "called with", ((arguments[0].arguments.length == 0) ? "no args" : arguments[0].arguments));
    var retour = Aop.next(f, ModalControler.prototype); //mandatory
    log.info(" AOPafter ModalControler." + f.fnName, "which returned", retour);
    return retour; //mandatory
}, [ModalControler.prototype]);
