/**
 * Created by Nettle on 2015/5/31.
 */

angular.module('iteration')
    .controller('iterationEditCtrl', ['$scope', '$rootScope', '$http', '$state', '$timeout', 'url', 'bugService', '$q', 'storyService', 'iterationService',
        function ($scope, $rootScope, $http, $state, $timeout, url, bugService, $q, storyService, iterationService) {
            $scope.iterationId = url.iterationId();
            $scope.companyId = url.companyId();
            $scope.projectId = url.projectId();
            var attachsUrl = url.projectApiUrl() + '/iterationattachs';

            $scope.showDetail = function($event) {
                $( $event.currentTarget).next().slideToggle(200);
            };

            $('#iterationTab a').click(function (e) {
                e.preventDefault();
                $(this).tab('show');
            });

            $scope.changeChecked = function(obj) {
                if (!obj.choose) {
                    $http.post(attachsUrl, {
                        iterationId: url.iterationId(),
                        objectType: obj.type,
                        objectId: obj.id
                    }).success( function(data) {
                        obj.choose = !obj.choose;
                    });
                }   else {
                    $http.delete(attachsUrl + '?iterationId='+ url.iterationId() + '&attachType='+ obj.type + '&attachId=' + obj.id)
                        .success( function(data) {
                            obj.choose = !obj.choose;
                        });
                }
            };

            $scope.arr = function(storiesArray) {
                if (storiesArray.length == 0) return [];
                var ret = [];
                for (var i = 0; i < storiesArray.length; ++i) {
                    ret = $.merge(ret, [storiesArray[i]]);
                    if (storiesArray[i].childStoryDTOs.length > 0)
                        ret = $.merge(ret, $scope.arr(storiesArray[i].childStoryDTOs));
                }
                return ret;
            };

            $scope.stories = [{
                content: '',
                id: 0,
                position: 0,
                priority: 3,
                completed: false,
                childStoryDTOs: []
            }];

            iterationService.getActiveIterationStories(false, url.projectId(), url.companyId()).then(function(data) {
                $scope.stories[0].childStoryDTOs = data;
            });

            iterationService.getActiveIterationBugs(false, url.projectId(), url.companyId()).then( function(data) {
                $scope.bugs = data;
            });
        }
    ]).directive('iterationStoriesTree', ['$q', function($q) {
        return {
            restrict  : 'A',
            scope     : {
                iterationStoriesTree: '='
            },
            replace   : true,
            template  : '<div iteration-stories-node="iterationStoriesTree"></div>',
            controller: ['$scope', function($scope, attrs) {
            }]
        };
    }]).directive('iterationStoriesNode', ['$q', '$compile', '$http', 'url',
        function($q, $compile, $http, url) {
            return {
                restrict: 'A',
                require : '^iterationStoriesTree',
                link    : function(scope, element, attrs, controller) {
                    var attachsUrl = url.projectApiUrl() + '/iterationattachs';
                    scope.changeStatus = function($event, story) {
                        if (story.show == undefined) story.show = false;
                        else story.show = !story.show;

                        $( $event.currentTarget ).parent().parent().next().slideToggle(200);
                    };

                    scope.changeChecked = function(obj) {
                        if (!obj.choose) {
                            $http.post(attachsUrl, {
                                iterationId: url.iterationId(),
                                objectType: obj.type,
                                objectId: obj.id
                            }).success( function(data) {
                                obj.choose = !obj.choose;
                            });
                        }   else {
                            $http.delete(attachsUrl + '?iterationId='+ url.iterationId() + '&attachType='+ obj.type + '&attachId=' + obj.id)
                                .success( function(data) {
                                    obj.choose = !obj.choose;
                                });
                        }
                    };

                    function render() {
                        var template = '<div class="stories-tree-node" ng-repeat="story in ' + attrs.iterationStoriesNode + ' | filter: {completed: false}">\
                        <div class="stories-tree-value" ng-class="{true: \'root-value\'}[story.id < 1]">\
                            <div class="stories-tree-switch">\
                                <i class="fa fa-plus-square-o" ng-show="story.childStoryDTOs.length > 0 && !story.show && story.show != undefined"  ng-click="changeStatus($event, story)" title="展开子需求"></i>\
                                <i class="fa fa-minus-square-o" ng-show="story.childStoryDTOs.length > 0 && (story.show || story.show == undefined)"  ng-click="changeStatus($event, story)" title="收起子需求"></i>\
                            </div>\
                    		<div class="stories-progress" title="已完成{{story.completedChildStoryCount}}个子需求/共{{story.completedChildStoryCount + story.uncompletedChildStoryCount}}个子需求">\
                    			{{story.completedChildStoryCount}} / {{story.completedChildStoryCount + story.uncompletedChildStoryCount}}\
                    		</div>\
                            <div style="margin-right: 70px; margin-left: 83px;">\
                                <div>{{story.description}}</div>\
                            </div>\
                            <button class="ob-checkbox iteration-checkbox" ng-class="{true: \'ob-checkbox-checked\', false: \'\'}[story.choose]" ng-model="story.choose" ng-click="changeChecked(story)"></button>\
                        </div>\
                        <div ng-show="story.childStoryDTOs.length > 0 || story.id < 1" class="stories-tree-nodes stop-add" ng-class="{\'root-nodes\':(story.id < 1)}" iteration-stories-node="story.childStoryDTOs">\
                        </div>\
                    </div>';
                        element.html('').append($compile(template)(scope));
                    }
                    render();
                }
            };
        }]);