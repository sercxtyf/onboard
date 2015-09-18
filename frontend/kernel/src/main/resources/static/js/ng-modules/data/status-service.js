angular.module('status', [])
    .service('statusService', [
        function () {
        	var dataOptions = [{
                id: 0,
                name: '活动数'
            }, {
                id: 1,
                name: '完成任务数'
            }, {
                id: 4,
                name: '修复Bug数'
            }];
        	
        	this.addStatusPage = function(newPage) {
        		dataOptions.push(newPage);
        	}
        	
        	this.getStatusPage = function() {
        		return dataOptions;
        	}
        }]);