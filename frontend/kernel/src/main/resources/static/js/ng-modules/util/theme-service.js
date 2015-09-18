/**
 * Created by harttle on 12/11/14.
 */
// 主题：提供项目样式

angular.module('util')
    .service('theme', [function () {

        this.themes = [
            {
                color: '#AFAFAF',
                name: 'default'
            },
            {
                color: '#11acfa',
                name: 'primary'
            },
            {
                color: '#61B947',
                name: 'success'
            }, 
            {
                color: '#333645',
                name: 'info'
            },
            {
                color: '#F2AA47',
                name: 'warning'
            },
            {
                color: '#F96565',
                name: 'danger'
            }
        ];

    }]);