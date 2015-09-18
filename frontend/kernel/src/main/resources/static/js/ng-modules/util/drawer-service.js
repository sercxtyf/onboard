/**
 * Created by harttle on 12/2/14.
 */

angular.module('util')
    .service('drawer', ['$location', function($location) {

        // type -> template url
        this.tpls = {
            'upload'            : 'uploadDetail.html',
            'todolist'          : 'todolist.html',
            'new-todo'          : 'newTodo.html',
            'todo'              : 'todo.html',
            'discussion'        : 'discussion-show-drawer.html',
            'discussion-edit'   : 'discussion-edit-drawer.html',
            'new-bug'           : 'newBug.html',
            'bug'               : 'viewBug.html',
            'story'             : 'storyDetail.html',
            'step'              : 'stepDetail.html'
        };

        this.size = {
        };

        // 打开抽屉
        // * 任何抽屉都需要指定 option.type，用来定位模板
        // * 需要路由的参数放到 option.params 中
        // * 不需要路由的参数放到 option.data 中；如果该字段非空，则不提供路由
        this.open = function(option) {
            // invoke delegate
            delegate.open($.extend({}, option,
                {
                    templateUrl: this.tpls[option.type],
                    sizeClass  : this.size[option.type] || ''
                }
            ));
        };

        // 关闭抽屉
        this.close = function() {
            delegate.close();
        };

        // 抽屉委托
        var delegate = null;
        this.setDelegate = function(d) {
            delegate = d;

            // 恢复抽屉
            var params = $location.search();
            var type = params.type;
            delete params.type;
            if(type) this.open({ type: type, params: params });
        };

        this.registerDrawer = function(option) {
            if (option.name == undefined || option.name == '') return ;
            if (option.template == undefined || option.template == '') return ;
            this.tpls[option.name] = option.template;
            if (option.size != undefined)
            this.size[option.name] = option.size;
            return ;
        }
    }])
;
