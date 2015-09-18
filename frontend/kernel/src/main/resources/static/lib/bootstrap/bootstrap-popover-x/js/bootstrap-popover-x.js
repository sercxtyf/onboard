/*!
 * @copyright &copy; Kartik Visweswaran, Krajee.com, 2014
 * @modified by harttle, yangjvn@126.com, 2014
 * @version 1.0.0
 *
 * Bootstrap Popover Extended - Popover with modal behavior, styling enhancements and more.
 *
 * For more JQuery/Bootstrap plugins and demos visit http://plugins.krajee.com
 * For more Yii related demos visit http://demos.krajee.com
 */
! function($) {

    var PopoverX = function(element, options) {
        var self = this;
        self.options = options;
        self.$element = $(element).on('click.dismiss.popoverX', '[data-dismiss="popover-x"]', $.proxy(self.hide, self));
        self.init();
    }

    PopoverX.prototype = $.extend({}, $.fn.modal.Constructor.prototype, {
        constructor: PopoverX,
        init: function() {
            var self = this;
            self.$body = $(document.body);
            self.setTrigger(self.options.trigger);
            if (self.$element.find('.popover-footer').length) {
                self.$element
                    .removeClass('has-footer')
                    .addClass('has-footer');
            }
            if (self.options.remote) {
                self.setRemote(this.options.remote);
            }
        },
        setTrigger: function(trigger) {
            this.$trigger = $(trigger);
        },
        setRemote: function(remote) {
            var self = this;
            self.$element.find('.popover-content').load(remote, function() {
                self.$element.trigger('load.complete.popoverX');
            });
        },
        getPosition: function() {
            var $element = this.$trigger;
            return $.extend({}, ($element.offset()), {
                width: $element[0].offsetWidth,
                height: $element[0].offsetHeight
            });
        },
        refreshPosition: function() {
            var self = this,
                $dialog = self.$element,
                placement = self.options.placement,
                actualWidth = $dialog[0].offsetWidth,
                actualHeight = $dialog[0].offsetHeight,
                position, pos = self.getPosition();
            switch (placement) {
                case 'bottom':
                    position = {
                        top: pos.top + pos.height,
                        left: pos.left + pos.width / 2 - actualWidth / 2
                    }
                    break;
                case 'bottom bottom-left':
                    position = {
                        top: pos.top + pos.height,
                        left: pos.left
                    }
                    break;
                case 'bottom bottom-right':
                    position = {
                        top: pos.top + pos.height,
                        left: pos.left + pos.width - actualWidth
                    }
                    break;
                case 'top':
                    position = {
                        top: pos.top - actualHeight,
                        left: pos.left + pos.width / 2 - actualWidth / 2
                    }
                    break;
                case 'top top-left':
                    position = {
                        top: pos.top - actualHeight,
                        left: pos.left
                    }
                    break;
                case 'top top-right':
                    position = {
                        top: pos.top - actualHeight,
                        left: pos.left + pos.width - actualWidth
                    }
                    break;
                case 'left':
                    position = {
                        top: pos.top + pos.height / 2 - actualHeight / 2,
                        left: pos.left - actualWidth
                    }
                    break;
                case 'left left-top':
                    position = {
                        top: pos.top,
                        left: pos.left - actualWidth
                    }
                    break;
                case 'left left-bottom':
                    position = {
                        top: pos.top + pos.height - actualHeight,
                        left: pos.left - actualWidth
                    }
                    break;
                case 'right':
                    position = {
                        top: pos.top + pos.height / 2 - actualHeight / 2,
                        left: pos.left + pos.width
                    }
                    break;
                case 'right right-top':
                    position = {
                        top: pos.top,
                        left: pos.left + pos.width
                    }
                    break;
                case 'right right-bottom':
                    position = {
                        top: pos.top + pos.height - actualHeight,
                        left: pos.left + pos.width
                    }
                    break;
            }
            $dialog
                .css(position)
                .addClass(placement)
                .addClass('in');
        },
        show: function() {
            var self = this,
                $dialog = self.$element;
            $dialog.css({
                top: 0,
                left: 0,
                display: 'block',
                'z-index': 1050
            });
            self.refreshPosition();
            $.fn.modal.Constructor.prototype.show.call(self, arguments);
            this.$body.removeClass('modal-open');
        },
        shown: function() {
            return this.isShown;
        }
    });

    $.fn.popoverX = function(option) {
        if (typeof option == 'string' && option == 'shown') {
            var $this = $(this);
            var pp = $this.data('popover-x');
            return pp[option]();
        }
        return this.each(function() {
            var $this = $(this);
            var pp = $this.data('popover-x');

            var options = $.extend({}, $.fn.popoverX.defaults, option);

            if (!$this.data('popover-x'))
                $this.data('popover-x', (pp = new PopoverX(this, options)));

            // initialize popovers            
            if (typeof option == 'object') {
                if (options.trigger) {
                    setTrigger(options.trigger, this);

                    if (!options.manual) {
                        regTrigger(options.trigger, this);
                    }
                }
            }
            // popover operations
            else if (typeof option == 'string')
                pp[option]();
        });
    }

    $.fn.popoverX.defaults = $.extend({}, $.fn.modal.defaults, {
        placement: 'right',
        keyboard: true
    });


    // set popoverX.trigger = selector
    var setTrigger = function(selector, popover) {

        // set default selector
        $(selector || "[data-toggle='popover-x']").each(function(i, element) {

            // deduce popover dialog
            var $this = $(element),
                href = $this.attr('href'),
                $dialog = $(popover || $this.data('target') || (href && href.replace(/.*(?=#[^\s]+$)/, '')));

            var pp = $dialog.data('popover-x');
            if (!pp) return; // do only trigger, no create

            pp.setTrigger(this);
            pp.setRemote(!/#/.test(href) && href);
        });
    }

    // reg trigger.click & trigger.keyup
    var regTrigger = function(selector, popover) {
        $(selector || "[data-toggle='popover-x']")
            .on('click', function(e) {
                // get popover dialog
                var $this = $(this),
                    href = $this.attr('href'),
                    $dialog = $(popover || $this.data('target') || (href && href.replace(/.*(?=#[^\s]+$)/, '')));

                e.preventDefault();
                $dialog.trigger('click.target.popoverX');

                var pp = $dialog.data('popover-x');

                // deduce action before set trigger
                var action = (!pp.$trigger.is(this) && pp.isShown) ? 'refreshPosition' : 'toggle';

                // reset trigger when clicked! note: multiple triggers may exist for one popover-x
                pp.setTrigger(this);
                pp.setRemote(!/#/.test(href) && href);

                $dialog.popoverX(action).on('hide', function() {
                    $this.focus();
                });
            })
            .on('keyup', function(e) {
                var $this = $(this),
                    $dialog = $(popover || $this.data('target') || (href && href.replace(/.*(?=#[^\s]+$)/, ''))); //strip for ie7
                $dialog && e.which == 27 && $dialog.trigger('keyup.target.popoverX') && $dialog.popoverX('hide');
            });
    }

    $(document).on('ready', function() {
        // init popoverX
        $('.popover-x').each(function(i, ele) {
            var $ele = $(ele);
            if (!$ele.data('popover-x')) $ele.popoverX();
        });

        // init triggers
        setTrigger();
        regTrigger();
    });
}(window.jQuery);
