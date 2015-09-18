var gulp = require('gulp'),
    usemin = require('gulp-usemin'),
    rev = require('gulp-rev'),
    uglify = require('gulp-uglify'),
    less = require('gulp-less'),
    minifyCSS = require('gulp-minify-css'),
    gulpFilter = require('gulp-filter'),
    flatten = require('gulp-flatten');

var config = {
    lessMain: 'static/less/onboard/onboard.less',
    lessDist: 'static/css',
    cssDist: 'static/css',
    jsDist: 'static/js',
    htmlHead: 'templates/fragments/CommonHTMLHead.html',
    htmlHeadDist: 'templates/fragments/build',
    fontFiles: ['static/lib/bootstrap/fonts/*', 'static/lib/fontawesome/fonts/*'],
    fontDist: 'static/fonts'
};

var jsFilter = gulpFilter('**/*.js');
var cssFilter = gulpFilter('**/*.css');
var htmlFilter = gulpFilter('**/*.html');

gulp.task('less', function() {
    gulp.src(config.lessMain)
        .pipe(less())
        .pipe(gulp.dest(config.lessDist));
});

gulp.task('fonts', function() {
    gulp.src(config.fontFiles).pipe(gulp.dest(config.fontDist));
});

gulp.task('default', ['less', 'fonts'], function() {
    return gulp.src(config.htmlHead)
        .pipe(usemin({
            css: [minifyCSS(), rev()],
            js: [uglify(), rev()]
        }))
        .pipe(flatten())
        .pipe(jsFilter)
        .pipe(gulp.dest(config.jsDist))
        .pipe(jsFilter.restore())
        .pipe(cssFilter)
        .pipe(gulp.dest(config.cssDist))
        .pipe(cssFilter.restore())
        .pipe(htmlFilter)
        .pipe(gulp.dest(config.htmlHeadDist));
});

