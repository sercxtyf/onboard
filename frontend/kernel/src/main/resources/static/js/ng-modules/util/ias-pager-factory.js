// Pager constructor function to encapsulate HTTP and pagination logic
// arguments:
// @url - function which returns GET url, and takes 3 params:
// <items count> <last item> <page count>
// @parser - function which returns an array of parsed objects, and takes 
// <raw data from server> 
//     optional: skip this if server returns array
// @urlConfig - function returns config object passed into $http
angular.module('util')
    .factory('iasPager', ['$http', '$timeout',

        function ($http, $timeout) {
            var Pager = function (url, parser, urlConfig) {
                this.url = url;
                this.parser = parser;
                this.urlConfig = urlConfig;
                this.reset();
            };

            Pager.prototype.reset = function () {
                this.items = [];
                this.busy = false;
                this.pageCount = 0;
                this.end = false;
            };

            Pager.prototype.refresh = function () {
                this.reset();
                this.nextPage();
            };

            Pager.prototype.nextPage = function () {
                if (this.busy) return;

                var url = this.url(
                    this.items.length,
                    this.items.slice(-1)[0],
                    this.pageCount
                );
                if (!url) return;

                var config = {};
                if(typeof this.urlConfig == 'function'){
                    config = this.urlConfig(this.items.length,
                        this.items.slice(-1)[0],
                        this.pageCount
                    )
                }
                this.busy = true;
                $http.get(url, config)
                    .success(function (data){
                        if (this.parser) data = this.parser(data);
                        if(!data || !data.length) this.end = true;
                        $.merge(this.items, data);
                        this.pageCount++;
                        $timeout(function(){this.busy = false}.bind(this));
                    }.bind(this))
                    .error(function (err) {
                        console.error('ias-pager error loading', url, ':', err);
                        this.busy = false;
                    }.bind(this));
            };
            return Pager;
        }
    ]);
