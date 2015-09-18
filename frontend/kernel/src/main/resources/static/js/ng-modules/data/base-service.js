
angular.module('data')
    .service('baseService', ['$http', 'user', 'url', function($http, user, url) {

        var baseService = this;

        this.datas = {}; //所有缓存数据
        this.Items = {}; //所有Item类

        /**
         * 获取item在缓存中的id
         */
        function getItemId(item){
            if(item && item['type'] && item['id']){
                return item['type'] + "-" + item["id"];
            }
            return undefined;
        }

        /**
         * 将item添加进缓存
         * @param itemDTO
         * @returns {*}
         */
        this.addItem = function(itemDTO){
            if(itemDTO.isItem){
                return itemDTO;
            }
            var itemId = getItemId(itemDTO);
            if(!itemId){
                return itemDTO;
            }
            var item = baseService.datas[itemId];
            if(!item){
                if(baseService.Items[itemDTO['type']]){
                    item = new baseService.Items[itemDTO['type']]();
                    baseService.datas[itemId] = item;
                }else{
                    return itemDTO;
                }
            }
            item.init(itemDTO);
            return item;
        };

        /**
         * 获取缓存对象
         * @param type
         * @param id
         * @returns {*}
         */
        this.getItem = function(type, id){
            return baseService.datas[getItemId({type : type, id : id})];
        };

        /**
         * 公共的Item对象
         * @constructor
         */
        this.Item = {
            origin    : undefined, //更新前的对象，用于比较更新前后的值
            DTO       : undefined, //原始DTO，后台传来的值
            observers : undefined  //观察者们
            
        };

        /**
         * 需要自己实现
         * api地址，如：/api/iterations/
         * 最后需要加分隔符！
         */
        this.Item.getApi = function(){};

        /**
         * 获取和后台交互的DTO
         */
        this.Item.getDTO = function(){
            var DTO = {};
            for(var property in this.DTO){
                DTO[property] = this[property];
            }
            return DTO;
        };
        
        function dto2Item(itemDTO){
            if(!itemDTO){
                return itemDTO;
            }
            if(itemDTO.constructor === Object){
                return baseService.addItem(itemDTO);
            }else if(itemDTO.constructor === Array){
                for(var i = 0; i < itemDTO.length; i++){
                    itemDTO[i] = dto2Item(itemDTO[i]);
                }
                return itemDTO;
            }
            return itemDTO;
        }
        
        this.Item.afterinit = function(){}

        /**
         * 根据DTO初始化对象
         * @param DTO
         * @returns {Boardable}
         */
        this.Item.init = function(DTO){
            if(this.DTO){
                this.origin = this.DTO;
            }else{
                this.origin = DTO;
            }
            
            for(var property in DTO){
                var value = dto2Item(DTO[property]);
                if( value === undefined || value === null){
                    continue;
                }
                this[property] = value;
            }
            this.DTO = DTO;
            this.afterinit();
            this.notify(this);
            return this;
        };
        
        this.Item.isItem = true;

        /**
         * 重置对象
         */
        this.Item.reset = function(){
            $.extend(this, this.DTO);
        };

        /**
         * 向后台请求，创建对象
         */
        this.Item.create = function(){
            var self = this;
            return $http.post(this.getApi(), this.getDTO()).then(function (response) {
                self.init(response.data);
                baseService.datas[getItemId(self)] = self;
            });
        };

        /**
         * 向后台请求，删除对象
         * @returns {*}
         */
        this.Item.delete = function(){
            if(!this.id){
                return;
            }
            var self = this;
            this.deleted = true;
            return $http.delete(this.getApi()).then(function (response) {
                baseService.datas[getItemId(self)] = undefined;
                self.notify(this);
            });
        };

        /**
         * 从回收站中恢复
         * @returns {*}
         */
        this.Item.recover = function(){
            if(!this.id){
                return;
            }
            this.deleted = false;
            return $http.put(this.getApi(), { deleted : false }).then(function (response) {});
        };

        /**
         * 更新对象
         * @returns {*}
         */
        this.Item.update = function(){
            if(!this.id){
                return;
            }
            var self = this;
            return $http.put(this.getApi(), this.getDTO()).then(function (response) {
                self.init(response.data);
            });
        };
        
        /**
         * 添加observer
         */
        this.Item.addObserver = function(observer){
            if(!observer.afterUpdateItem){
                return;
            }
            if(this.observers === undefined){
                this.observers = {};
            }
            var itemId =  getItemId(observer);
            if(!itemId){
                return;
            }
            this.observers[itemId] = observer;
        } 
        
        /**
         * 删除observer
         */
        this.Item.removeObserver = function(observer){
            var itemId =  getItemId(observer);
            if(!itemId){
                return;
            }
            this.observers[itemId] = undefined;
        }
        
        /**
         * 通知observer
         */
        this.Item.notify = function(){
            for(var key in this.observers){
                if(this.observers[key] && this.observers[key].afterUpdateItem){
                    this.observers[key].afterUpdateItem(this);
                }
            }
        }
        
        /**
         * 观察者
         */
        this.Item.afterUpdateItem = function(){
        };

    }]);
