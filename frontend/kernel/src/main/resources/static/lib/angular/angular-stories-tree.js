(function(angular, undefined) {
    var module = angular.module('angularStoriesTree', []);

    module.directive('storiesTree', ['$q', function($q) {
        return {
            restrict  : 'A',
            scope     : {
                storiesTree: '=',
                completed: '='
            },
            replace   : true,
            template  : '<div stories-node="storiesTree" ng-click="clicked()"></div>',
            controller: ['$scope', function($scope, attrs) {
                $scope.clicked = function() {
                    $scope.$parent.noStory = false;
                }
            }]
        };
    }]);

    module.directive('storiesNode', ['$q', '$compile', '$timeout', 'storyService', 'utilService', 'drawer',
                                     function($q, $compile, $timeout, storyService, utilService, drawer) {
        return {
            restrict: 'A',
            require : '^storiesTree',
            link    : function(scope, element, attrs, controller) {
                scope.newStoryDescription = '';
                scope.addChild = function($event, story, way) {
                    if (story.addChild != undefined && story.addChild) return ;
                    story.addChild = true;
                    var div = $( $event.currentTarget );
                    if (way == 'title')
                        div = div.parent().parent().next().next().find('.new-story');
                    else 
                        div = div.next().next();
                    $timeout( function() {
                        div.animate({width: 'toggle'}, 500);
                        $timeout(function() {
                            div.find('input').focus();
                        }, 500);
                    }, 200);
                };
                
                scope.cancelAddChild = function($event, story) {
                    $( $event.currentTarget ).next().animate({width: 'toggle'}, 500);
                    $timeout( function() {
                        scope.newStoryDescription = '';
                        story.addChild = false;
                    }, 500);
                }
                
                scope.changeStatus = function($event, story) {
                    if (story.show == undefined) story.show = false;
                    else story.show = !story.show;

                    window.localStorage['story_' + story.id] = story.show;

                    $( $event.currentTarget ).parent().parent().next().slideToggle(200);
                    $( $event.currentTarget ).parent().parent().next().next().slideToggle(200);
                }

                scope.modifyIt = function(story) {
                    if (story.oldDescription == story.description) {
                        return;
                    }
                    story.update().then(function(updateStory){
                    }).catch(function(){
                    });
                };
                
                scope.addNewStory = function($event, story) {
                    if (scope.newStoryDescription.length < 1) return ;
                    storyService.getNew(story, scope.newStoryDescription).then(function(newStory){
                        newStory.create().then(function(){
                            if(!utilService.contains(story.childStoryDTOs, newStory)){
                                story.childStoryDTOs.push(newStory);
                            }
                            $( $event.currentTarget ).parent().hide();
                            scope.newStoryDescription = '';
                            story.addChild = false;
                            $timeout(function(){
                                drawer.open({
                                    type: 'story',
                                    params: {id:newStory.id},
                                    data: {
                                        projectId:newStory.projectId,
                                        companyId:newStory.companyId
                                    }
                                });
                                scope.story.uncompletedChildStoryCount++;
                            }, 1000);
                        }).catch(function(e){
                        });
                    });
                };

                scope.keyup = function($event, story, type) {
                    if ($event.keyCode == 13) {
                        if (type == 'new')
                            scope.addNewStory($event, story);
                        if (type == 'edit') {
                            $timeout(function() {
                                $( $event.currentTarget ).blur();
                            });
                        }
                    }
                };
                
                scope.delStory = function($event, story) {
                    if(confirm('确认删除该需求?')) {
                        story.delete().then(function(){
                            story.content = '';
                            var storyArr = scope.$parent.story.childStoryDTOs;
                            if (story.childStoryDTOs != undefined) {
                                if (story.childStoryDTOs.length > 0) return ;
                            }
                            $( $event.currentTarget ).parent().parent().animate({width: 'toggle'}, 500);
                            for (var i in storyArr)
                                if (story.id == storyArr[i].id) {
                                    $timeout(function() {
                                        storyArr.splice(i, 1);
                                        if (story.completed) scope.$parent.story.completedChildStoryCount--;
                                        else scope.$parent.story.uncompletedChildStoryCount--;
                                    }, 400);
                                    break;
                                }
                        }).catch(function(){
                        });
                    }
                };

                scope.saveOld = function(story) {
                    story.oldDescription = story.description;
                };

                scope.checkShow = function(story) {
                    story.show = !(window.localStorage['story_' + story.id] == 'false');
                    return window.localStorage['story_' + story.id] == 'false';
                };

                function render() {
                    var template = '<div class="stories-tree-node" ng-repeat="story in ' + attrs.storiesNode + ' | filter: {completed:' + scope.completed + '}">\
                        <div class="stories-tree-value" ng-class="{true: \'root-value\'}[story.id < 1]">\
                            <div class="stories-tree-switch">\
                                <i class="fa fa-plus-square-o" ng-show="story.childStoryDTOs.length > 0 && !story.show && story.show != undefined"  ng-click="changeStatus($event, story)" title="展开子需求"></i>\
                                <i class="fa fa-minus-square-o" ng-show="story.childStoryDTOs.length > 0 && (story.show || story.show == undefined)"  ng-click="changeStatus($event, story)" title="收起子需求"></i>\
                                <i class="fa fa-plus-circle" ng-show="story.childStoryDTOs.length < 1"  ng-click="addChild($event, story, \'title\')" title="添加子需求"></i>\
                            </div>\
                            <div class="stories-content">\
                                <div class="stories-progress"   ng-class="{\
                                    \'text-danger\'   : story.priority ==1,\
                                    \'text-warning\'  : story.priority ==2,\
                                    \'text-primary\'  : story.priority ==3,\
                                    \'text-info\'     : story.priority ==4,\
                                    \'text-muted\'    : story.priority ==5}"title="已完成{{story.completedChildStoryCount}}个子需求/共{{story.completedChildStoryCount + story.uncompletedChildStoryCount}}个子需求">\
                                    {{story.completedChildStoryCount}} / {{story.completedChildStoryCount + story.uncompletedChildStoryCount}}\
                                </div>\
                                <input type="text" ng-model="story.description" ng-focus="saveOld(story)" ng-blur="modifyIt(story)" ng-keyup="keyup($event, story, \'edit\')"/>\
                            </div>\
                            <div class="stories-tree-detail">\
                                <i class="fa fa-pencil-square-o fa-fw" title="编辑详情" open-drawer="story" open-drawer-type="\'story\'"\
                                    open-drawer-params="{id:story.id}"\
                                    open-drawer-data="{projectId:story.projectId,companyId:story.companyId}"></i>\
                                <i class="fa fa-trash fa-fw" ng-click="delStory($event, story)" ng-show="story.childStoryDTOs.length < 1" title="删除故事"></i>\
                    			<i class="fa fa-trash fa-fw" ng-click="" ng-hide="story.childStoryDTOs.length < 1" title="子需求尚未完成" style="cursor:no-drop; color: #cecece;"></i>\
                            </div>\
                        </div>\
                        <div ng-show="story.childStoryDTOs.length > 0 || story.addChild || story.id < 1" class="stories-tree-nodes" ng-class="{\'root-nodes\':(story.id < 1), \'not-show\':checkShow(story)}" stories-node="story.childStoryDTOs">\
                        </div>\
                        <div ng-show="(story.childStoryDTOs.length > 0 || story.addChild || story.id < 1)" class="stories-add-node" ng-class="{\'not-show\':checkShow(story)}">\
                            <i title="添加子需求" ng-show="story.addChild == undefined || story.addChild == false" class="fa fa-plus-circle fa-fw add-button" ng-click="addChild($event, story, \'button\')"></i>\
                            <i title="放弃添加" ng-show="story.addChild != undefined && story.addChild" class="fa fa-times-circle fa-fw fa-2x add-button" ng-click="cancelAddChild($event, story)"></i>\
                            <div class="new-story">\
                                <i title="确认添加" class="fa fa-check add-new-story" ng-click="addNewStory($event, story)"></i>\
                                <input type="text" ng-model="newStoryDescription" ng-keyup="keyup($event, story, \'new\')" placeholder="输入需求标题"/>\
                            <div>\
                        </div>\
                    </div>';
                    element.html('').append($compile(template)(scope));
                }

                render();
            }
        };
    }]);
})(angular);
