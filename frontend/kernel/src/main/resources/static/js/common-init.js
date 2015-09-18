(function (global, $, undefined) {
    'use strict';

    global.Module = {};

    $(function () {

        // obsolete: Load init function of specific Page.
        //var pageName = $('[data-page-name]:last').attr('id');
        //if (pageName && global.Module.hasOwnProperty(pageName)) {
        //    global.Module[pageName]();
        //}

        // load js libs for the current page
        var sz_libs = '';

        // concat all data-libs strings
        $('[data-libs]').each(function (index, element) {
            sz_libs += ' ' + $(element).data('libs')
        })
        var libs = sz_libs.trim() ? sz_libs.trim().split(/[\s]+/) : [];
        libs.push('common')    // add common module

        // do uniq
        libs = (function (libs) {
            var unique = [];
            for (var i = 0; i < libs.length; i++) {
                if (unique.indexOf(libs[i]) == -1) {
                    unique.push(libs[i]);
                }
            }
            return unique;
        })(libs);

        // load each lib
        for (var i in libs) {
            if (libs[i] && global.Module.hasOwnProperty(libs[i])) {
                global.Module[libs[i]]();
            }
        }

    });
})(window, window.jQuery);