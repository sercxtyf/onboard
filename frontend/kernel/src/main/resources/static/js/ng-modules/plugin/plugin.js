/**
 * Created by Nettle on 2015/7/7.
 */

angular.module('plugin', ['util', 'tab', 'status'])
    .service('pluginService', ['url', '$http', '$q', 'drawer', 'tab', 'statusService',
        function (url, $http, $q, drawer, tab, statusService) {
            var companyPlugins = [];
            var projectPlugins = [];
            var sidePlugins    = [];
            var accountPlugins = [];
            var footerPlugins  = [];

            this.getCompanyPlugins = function() {
                return companyPlugins;
            };
            this.getProjectPlugins = function() {
                return projectPlugins;
            };
            this.getSidePlugins = function() {
                return sidePlugins;
            };
            this.getAccountPlugins = function() {
                return accountPlugins;
            };
            this.getFooterPlugins = function() {
                return footerPlugins;
            };
            this.registerDrawer = function(option) {
                drawer.registerDrawer(option);
            };
            this.registerTab = function(option) {
                tab.registerTab(option);
            };

            this.registerCompanyPlugin = function(option) {
                var newPlugin = {
                    title: '',
                    'ui-sref': undefined,
                    href: undefined
                };
                for (var key in newPlugin)
                    if (option[key] != undefined)
                        newPlugin[key] = option[key];
                companyPlugins.push(newPlugin);
                return newPlugin;
            };
            this.registerProjectPlugin = function(option) {
                var newPlugin = {
                    title: '',
                    'ui-sref': undefined,
                    href: undefined
                };
                for (var key in newPlugin)
                    if (option[key] != undefined)
                        newPlugin[key] = option[key];
                projectPlugins.push(newPlugin);
                return newPlugin;
            };
            this.registerSidePlugin = function(option) {
                var newPlugin = {
                    icon: '',
                    title: '',
                    'ui-sref': undefined,
                    href: undefined
                };
                for (var key in newPlugin)
                    if (option[key] != undefined)
                        newPlugin[key] = option[key];
                sidePlugins.push(newPlugin);
                return newPlugin;
            };
            this.registerAccountPlugin = function(option) {
                var newPlugin = {
                    title: '',
                    'ui-sref': undefined
                };
                for (var key in newPlugin)
                    if (option[key] != undefined)
                        newPlugin[key] = option[key];
                accountPlugins.push(newPlugin);
                return newPlugin;
            };
            this.registerFooterPlugin = function(option) {
                var newPlugin = {
                    title: '',
                    icon: '',
                    'ui-sref': undefined
                };
                for (var key in newPlugin)
                    if (option[key] != undefined)
                        newPlugin[key] = option[key];
                footerPlugins.push(newPlugin);
                return newPlugin;
            };
            this.registerStatusPage = function(option) {
                var newPage = {
                	id: undefined,
                	name: undefined
                };
                for (var key in newPage)
                    if (option[key] != undefined)
                    	newPage[key] = option[key];
                statusService.addStatusPage(newPage);
                return newPage;
            };
        }
    ]);