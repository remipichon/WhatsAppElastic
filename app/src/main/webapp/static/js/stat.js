statistique = null; //will store the StatistiqueService currently drawn


Object.size = function (obj) {
    var size = 0,
        key;
    for (key in obj) {
        if (obj.hasOwnProperty(key)) size++;
    }
    return size;
};

/**
 * ref is mandatory !
 * @param  {[type]} options [description]
 * @return {[type]}         [description]
 */
function _StatistiqueService(options) {
    //TODO
    this.betweenDate = DatetimePicker.prototype.infinityDate();
    this.betweenHours = DatetimePicker.prototype.infinityHours();

    this.enumName = null;
    this.numberMessagePerUser = null;
    this.totalContentPerUser = null;
    this.totalMessageContent = null;
    this.numberTotalMessage = null;
    this.statNumberMessagePerUser = null;
    this.statContentMessagePerUser = null;
    this.numberCharacterPerMessagePerUser = null;
    this.messagePerUserTimeline = null;
    this.messagePerUserTimelineMonth = null;
    this.fetchedRows = null;
    this.year = function(){return new Date().getFullYear()}();

    this.sorted;


    //ce sort est pourri, implementer un quick sort serait bien mieux !
    //sinon, sort les values puis reconstruire l'object à partir des values => probleme si deux values identiques
    this.sortObject = function (ob) {

        var temp = {};
        var cont = true;
        var nb = Object.size(ob)
        log.debug("StatistiqueService.sortObject object.length", nb, ob);
        while (nb !== 0) {
            cont = true;
            var min = 10000000;
            var user;
            _.each(ob, function (value, userName) {
                if (value < min) {
                    min = value;
                    user = userName;
                }
            });
            temp[user] = min;
            delete ob[user];
            nb--;
        }

        this.sorted = temp;
        return temp;
    }


    this.getEnumName = function () {
        if (this.enumName !== null) return this.enumName;

        var self = this;
        $.ajax({
            url: '/api/conversation/getauthors',
            type: 'GET',
            async: true,
            dataType: "json",
            success: function (data) {
                self.enumName = [];
                _.each(data, function (key, value) {
                    self.enumName.push(key.name);
                });
            },
            error: function (jqXHR, textStatus, errorThrown) {
                self.enumName = errorThrown;
            }

        });

        return null;
    };

    this.sortEnumName = function () {
        var names = [];
        _.each(this.numberMessagePerUser, function (value, name) {
            names.push(name);
        });
        this.enumName = names;
        return names;
    };

    /**
     * if options.refetch == true, this.numerMessagePerUser will be updated
     * @param options
     * @returns {*}
     */
    this.getContentStatAndPostCountByUser = function () {
        var self = this;
        $.ajax({
            url: '/api/stats/contentStatAndPostCountByUser',
            type: 'GET',
            async: true,
            dataType: "json",
            success: function (data) {
                var occurences = data;

                self.numberMessagePerUser = {};
                self.numberCharacterPerMessagePerUser = {};
                self.totalContentPerUser = {};
                self.enumName = [];
                _.each(occurences, function (key, value) {
                    self.numberMessagePerUser[value] = key.post_count;
                });

                self.numberMessagePerUser = self.sortObject(self.numberMessagePerUser);

                _.each(self.numberMessagePerUser, function (key, value) {
                    self.numberCharacterPerMessagePerUser[value] = parseInt(occurences[value].content_avg);
                    self.totalContentPerUser[value] = occurences[value].content_sum;
                    self.enumName.push(value);
                });

                var mapped = _.map(self.numberMessagePerUser, function(count, name){ return {count: count, name: name} });
                var sortedByMessageCount = _.sortBy(mapped,function(item){return (-1) * item.count});

                var numberMessagePerUser = {};
                var numberCharacterPerMessagePerUser = {};
                var totalContentPerUser = {};
                var enumName = [];
                sortedByMessageCount.forEach(function(userCount){
                    numberMessagePerUser[userCount.name] = userCount.count;
                    numberCharacterPerMessagePerUser[userCount.name] = parseInt(occurences[userCount.name].content_avg);
                    totalContentPerUser[userCount.name] = occurences[userCount.name].content_sum;
                    enumName.push(userCount.name);
                });

                self.numberMessagePerUser = numberMessagePerUser;
                self.numberCharacterPerMessagePerUser = numberCharacterPerMessagePerUser;
                self.totalContentPerUser = totalContentPerUser;
                self.enumName = enumName;

                highchartsService.drawUserBarChart(statistique);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                self.numberCharacterPerMessagePerUser = errorThrown;
                self.totalContentPerUser = errorThrown;
                self.enumName = errorThrown
            }
        });
    }

    this.getProportionMessageAndContent = function () {
        var self = this;
        $.ajax({
            url: '/api/stats/proportionMessageAndContentPerUser',
            type: 'GET',
            async: true,
            dataType: "json",
            success: function (data) {
                var occurences = data;

                self.statContentMessagePerUser = {};
                self.statNumberMessagePerUser = {};
                _.each(occurences, function (key, value) {
                    self.statContentMessagePerUser[value] = key.content_proportion;
                    self.statNumberMessagePerUser[value] = key.post_proportion;
                });

                self.statContentMessagePerUser = self.sortObject(self.statContentMessagePerUser);
                self.statNumberMessagePerUser = self.sortObject(self.statNumberMessagePerUser);

                highchartsService.drawMessageUserPieChart(statistique);
                highchartsService.drawContentUserPieChart(statistique);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                self.statContentMessagePerUser = errorThrown;
                self.statNumberMessagePerUser = errorThrown;
            }
        });
    };


    this.getNumberMessagePerUser = function (options) {
        if (this.numberMessagePerUser !== null) return this.numberMessagePerUser;

        var self = this;
        $.ajax({
            url: '/api/conversation/postCountByAuthors',
            type: 'GET',
            async: true,
            dataType: "json",
            success: function (data) {
                var occurences = data;
                occurences = self.sortObject(occurences);
                self.numberMessagePerUser = occurences;
            },
            error: function (jqXHR, textStatus, errorThrown) {
                self.numberMessagePerUser = errorThrown;
            }
        });
        return null;
    };

    this.getTotalContentPerUser = function () {
        if (this.totalContentPerUser !== null) return this.totalContentPerUser;

        var self = this;
        $.ajax({
            url: '/api/conversation/postLengthByAuthors', //TODO send conversation ref
            type: 'GET',
            async: true,
            dataType: "json",
            success: function (data) {
                var occurences = data;
                occurences = self.sortObject(occurences);
                self.totalContentPerUser = occurences;

            },
            error: function (jqXHR, textStatus, errorThrown) {
                self.totalContentPerUser = errorThrown;
            }
        });
        return null;
    };

    this.getStatNumberMessagePerUser = function () {
        if (this.statNumberMessagePerUser !== null) return this.statNumberMessagePerUser;
        return null;
    };

    this.getStatContentMessagePerUser = function () {
        if (this.statContentMessagePerUser !== null) return this.statContentMessagePerUser;
        return null;
    };

    this.getNumberCharacterPerMessagePerUser = function () {
        if (this.numberCharacterPerMessagePerUser !== null) return this.numberCharacterPerMessagePerUser;
        return null;
    };

    this.getMessagePerUserTimeline = function (callback) {
        if (this.messagePerUserTimeline != null) return this.messagePerUserTimeline;

        var self = this;
        $.ajax({
            url: '/api/stats/postCountPerMonthPerUser?year='+this.year,
            type: 'GET',
            async: true,
            dataType: "json",
            success: function (data) {
                var occurences = {};
                /*
                 from
                 {
                 author : {
                 monthNb: count,...
                 }
                 }
                 to
                 {
                 author: [
                 countMonth0, countMonth1,... //avec du padding s'il faut
                 ]
                 }
                 */
                _.each(data, function (monthsCount, author) {
                    var arrayMonth = [];
                    for (var i = 0; i < 12; i++) {
                        var count = monthsCount[i + 1]; //backend give month in range [1,12] Highcarts needs [0,11]
                        arrayMonth.push((count != null) ? count : 0);
                    }
                    occurences[author] = arrayMonth;
                });

                self.messagePerUserTimeline = occurences;
                highchartsService.drawMessageBarChartTimeline(statistique);

            },
            error: function (jqXHR, textStatus, errorThrown) {
                self.messagePerUserTimeline = errorThrown;
            }
        });
        return;
    };

    this.getMessagePerUserTimelineMonth = function (callback) {
        if (this.messagePerUserTimelineMonth != null) return this.messagePerUserTimelineMonth;


        var month = 1;
        if (typeof callback === "object")
            if (typeof callback.month !== "undefined")
                month = callback.month;


        var self = this;
        $.ajax({
            url: '/api/stats/postCountPerDayPerUser?year='+this.year+'&month='+month,
            type: 'GET',
            async: true,
            dataType: "json",
            success: function (data) {
                var occurences = {};
                /*
                 from
                 {
                 author : {
                 dayNb: count,...
                 }
                 }
                 to
                 {
                 author: [
                 countDay0, countDay1,... //avec du padding s'il faut
                 ]
                 }
                 */
                if(_.isEmpty(data)){
                    //TODO afficher un feedback
                    log.info("month "+month+" doesn't have any record");
                }
                _.each(data, function (dayCount, author) {
                    var arrayDay = [];
                    for (var i = 0; i < 28; i++) {
                        var count = dayCount[i + 1]; //backend give month in range [1,31] Highcarts needs [0,30]
                        arrayDay.push((count != null) ? count : 0);
                    }
                    occurences[author] = arrayDay;
                });

                self.messagePerUserTimelineMonth = occurences;
                highchartsService.drawMessageBarChartTimelineMonth(statistique);

            },
            error: function (jqXHR, textStatus, errorThrown) {
                self.messagePerUserTimelineMonth = errorThrown;
            }
        });
        return;
    };


    this.setAll = function (callback) {
        this.getContentStatAndPostCountByUser();

        this.getProportionMessageAndContent();

        this.getMessagePerUserTimeline();

        this.getMessagePerUserTimelineMonth();
    }

    /**
     * return an object of all attributes
     * @return {[type]} [description]
     */
    this.getAttributes = function () {
        var attr = {};
        var arrayProperties = Object.getOwnPropertyNames(this);
        for (var id = 0; id < arrayProperties.length; id++) {
            var property = arrayProperties[id];
            if (typeof this[property] !== "function") {
                attr[property] = this[property];
            }
        }
        delete attr.betweenDate;
        delete attr.betweenHours;
        return attr;
    };

    this.getJsonData = function () {
        return JSON.stringify(this.getAttributes());
    }


}
/*** AOP by RemiP**/
StatistiqueService = function (options) {

    var conversationName = options.conversationName;
    //add/replace conversationName to all request
    $.ajaxPrefilter(function (options, originalOptions, jqXHR) {
        if(options.url.indexOf("api/conversation") !== -1){
            return;
        }

        if (options.url.indexOf('?conversationName=') !== -1) {
            //we need to remove previous conversationName
            options.url = options.url.split("?conversationName=")[0];
        }
        var op;
        if (options.url.indexOf("?") === -1) {
            op = "?";
        } else {
            op = "&";
        }
        options.url += op + "conversationName=" + conversationName;
    });


    var statistique = new _StatistiqueService({
        conversationName: conversationName
    });

    var arrayProperties = Object.getOwnPropertyNames(statistique);
    for (var id = 0; id < arrayProperties.length; id++) {
        var property = arrayProperties[id];
        if (typeof statistique[property] === "function") {
            (function (statistique, property) {
                log.info("add AOP on", property)
                var old = statistique[property];
                statistique[property] = function () {
                    log.info(" AOPbefore StatistiqueService." + property, "called with", arguments);
                    var retour = old.apply(statistique, arguments);
                    log.info("AOPafter StatistiqueService." + property, "which returned", retour);
                    return retour;
                }
            })(statistique, property);
        }
    }

    if (options.setAll) statistique.setAll();

    return statistique;
};
