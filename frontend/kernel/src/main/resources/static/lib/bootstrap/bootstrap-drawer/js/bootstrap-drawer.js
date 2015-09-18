/*!
 * @copyright &copy; harttle, yangjvn@126.com, 2014
 * @version 1.0.0
 *
 * Bootstrap Drawer - Drawer with modal behavior
 */

! function($) {
    var Drawer = function(element, options) {
        var self = this;
        self.options = options;
        self.$element = $(element).on('click.dismiss.drawer', '[data-dismiss="drawer"]', $.proxy(self.hide, self));
        self.init();
    };

    Drawer.prototype = $.extend({}, $.fn.modal.Constructor.prototype, {
        constructor: Drawer,
        init: function() {
            this.$body = $(document.body);
        },
        show: function() {
            //this.$element.css('display', 'block');
            this.$element.addClass('in');
            this.$element.addClass(this.options.placement);
            this.$element.trigger($.Event('show.bs.drawer'));
        },
        hide: function(){
            this.$element.removeClass('in');
            this.$element.trigger($.Event('hide.bs.drawer'));
        }
    });

    $.fn.drawer = function(option) {

        return this.each(function(i, ele) {
            var $this = $(ele),
                d = $this.data('drawer');

            var options = $.extend({}, $.fn.drawer.defaults, $this.data(), typeof option == 'object' && option);
            delete options.drawer

            // initialize 
            if (!d || typeof option == 'object') {
                $this.data('drawer', (d = new Drawer(this, options)));
            }

            // operations
            if (typeof option == 'string') {
                d[option]();
            }
        });
    }

    $.fn.drawer.defaults = $.extend({}, $.fn.modal.defaults, {
        placement: 'right',
        keyboard: true,
        backdrop: true,    // hide on focusout
    });

    var initTriggers = function() {

        // init drawer trigger
        $('[data-toggle="drawer"]').each(function(i, ele) {
            $(ele).click(function() {
                var $this = $(this)
                var $dialog = $($this.data('target') || $this.attr('href'))
                $dialog.drawer('show')
            })
        })
    };

    $(document).on('ready', function() {
        initTriggers();
    });

}(window.jQuery);
