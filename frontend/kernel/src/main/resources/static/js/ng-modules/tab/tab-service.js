angular.module('tab')
    .service('tab', [function(){

        var tabMap = {
            activity: {
                name: "activity",
                title: "回顾", //show in tab
                templateUrl: "/tab/activity", //get template from the url
                active: false
            },
            linkedtask: {
                name: "linkedtask",
                title: "相关任务", //show in tab
                templateUrl: "/tab/linktask", //get template from the url
                active: false
            },
            linkedstory: {
                name: "linkedstory",
                title: "相关需求", //show in tab
                templateUrl: "/tab/linkstory", //get template from the url
                active: false
            },
            linkedbug: {
                name: "linkedbug",
                title: "相关Bug", //show in tab
                templateUrl: "/tab/linkbug" //get template from the url
            },
            comment: {
                name: "comment",
                title: "评论", //show in tab
                templateUrl: "/tab/comment", //get template from the url
                active: false
            },
            duration: {
            	name: "duration",
            	title: "工作记录",
            	templateUrl: "/tab/duration",
            	active: false
            }
        };

        this.getTabInfo = function(name, active) {
        	var tab = null;
            if (tabMap[name] != undefined) {
	            tab = angular.copy(tabMap[name]);
	            if (active == true) {
	                tab.active = true;
	            }
	            tab.exist = true;
            }	else {
            	tab = {exist: false};
            }
            return tab;
        };

        this.registerTab = function(tabInfo) {
            var newTab = {
                name: null,
                title: null,
                templateUrl: null,
                active: false
            };
            if (tabInfo.name == undefined || tabInfo.name == '') return ;
            if (tabInfo.title == undefined || tabInfo.title == '') return ;
            if (tabInfo.templateUrl == undefined || tabInfo.templateUrl == '') return ;
            newTab.name = tabInfo.name;
            newTab.title = tabInfo.title;
            newTab.templateUrl = tabInfo.templateUrl;
            tabMap[newTab.name] = newTab;
            return ;
        }
    }]);