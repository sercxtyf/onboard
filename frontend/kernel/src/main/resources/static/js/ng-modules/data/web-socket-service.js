/**
 * Created by harttle on 12/17/14.
 */

angular.module('data')
    .service('webSocketService', ['$rootScope', 'todoWebsocketService', 'todolistWebsocketService', 'commentWebSocketService',
        'documentWebSocketService', 'discussionWebSocketService', 'uploadsWebsocketService', 'bugWebsocketService','storyWebsocketService',
        'iterationWebsocketService',
        function($rootScope, todoWebsocketService, todolistWebsocketService, commentWebSocketService, documentWebSocketService,
                 discussionWebSocketService, uploadsWebsocketService, bugWebsocketService,storyWebsocketService, iterationWebsocketService) {

            // service mapping，映射 attachType 到数据 service
            var serviceMap = {
                'todo'      : todoWebsocketService,
                'todolist'  : todolistWebsocketService,
                'comment'   : commentWebSocketService,
                'document'  : documentWebSocketService,
                'discussion': discussionWebSocketService,
                'upload'    : uploadsWebsocketService,
                'bug'       : bugWebsocketService,
                'story'     : storyWebsocketService,
                'iteration' : iterationWebsocketService
            };

            // action mapping, 映射 action 到数据 service 的方法
            var actionMap = {
                'delete' : 'delete',
                'discard': 'delete',
                'archive': 'delete',
                'create' : 'add',
                'update' : 'update',
                'reply'  : 'add',
                'default': 'update',
                'recover': 'add',
                'copy'   : 'copy'
            };

            // 处理一个 web socket 
            //action
            this.activityHandler = function(activity) {

                var s = serviceMap[activity.attachType],
                    a = actionMap[activity.action] || actionMap["default"];

                if(s && s[a]) {
                    s[a](activity.attachObject, activity);
                    if (!$rootScope.$$phase) $rootScope.$apply();
                }
            }
        }]);