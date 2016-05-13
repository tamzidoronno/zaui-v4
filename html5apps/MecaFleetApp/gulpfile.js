var gulp = require('gulp');

var babel = require('gulp-babel');
var concat = require('gulp-concat');
var debug = require('gulp-debug');
var filter = require('gulp-filter');
var less = require('gulp-less');
var manifest = require('gulp-manifest');
var notify = require('gulp-notify');
var plumber = require('gulp-plumber');
var size = require('gulp-size');
var sourcemaps = require('gulp-sourcemaps');
var uglify = require('gulp-uglify');
var util = require('gulp-util');
var watch = require('gulp-watch');

var mainBowerFiles = require('main-bower-files');

var copyIndexHtml = function () {
  gulp.src('public_html/static/**/*')
      .pipe(gulp.dest('build'));
};

var bundleVendors = function () {
  var jsFilter = filter(['**/*.js']);

  // BUNDLE VENDORS
  var start = Date.now();

console.log(mainBowerFiles());
  gulp.src(mainBowerFiles())
      .pipe(jsFilter)
      .pipe(size({showFiles: true}))
      .pipe(sourcemaps.init())
      .pipe(uglify())
      .pipe(concat('vendors.js'))
      .pipe(sourcemaps.write('.'))
      .pipe(size({showFiles: true}))
      .pipe(gulp.dest('build'))
      .pipe(notify(function () {
        console.log('Built VENDOR.JS in ' + (Date.now() - start) + 'ms');
      }));
};

var bundleVendorCSS = function (events, done) {
  var start = Date.now();


  gulp.src(mainBowerFiles())
      .pipe(filter(['**/*.css']))
      .pipe(size({showFiles: true}))
      .pipe(concat('vendors.css'))
      .pipe(gulp.dest('build'))
      .pipe(notify(function () {
        console.log('Built VENDORS.CSS in ' + (Date.now() - start) + 'ms');
        done && done();
      }));
};

gulp.task('build', function () {
  // BUNDLE APP
    var bundleApp = function (events, done) {
    var start = Date.now();
    gulp.src(['public_html/app/**/*.js'])
        .pipe(sourcemaps.init())
//        .pipe(babel().on('error', function (a) {
//          console.log('Error compiling JavaScript', a)
//        }))
        .pipe(concat('main.js'))
        .pipe(sourcemaps.write('.'))
        .pipe(gulp.dest('build/'))
        .pipe(notify(function () {
          console.log('built APP in ' + (Date.now() - start) + 'ms');
          done && done();
        }));
  };

  var bundleCSS = function (events, done) {
    var start = Date.now();
    gulp.src(['public_html/app/**/*.css'])
        .pipe(concat('main.css'))
        .pipe(gulp.dest('build/'))
        .pipe(notify(function () {
          console.log('built main CSS in ' + (Date.now() - start) + 'ms');
          done && done();
        }));
  };

  var bundleTemplates = function (events, done) {
    var start = Date.now();
    gulp.src('public_html/app/**/*.html')
        .pipe(gulp.dest('build/'))
        .pipe(notify(function () {
          console.log('built TEMPLATES in ' + (Date.now() - start) + 'ms');
          done && done();
        }));
  };

  var bundleAdminLTE = function(events, done) {
    var start = Date.now();

    gulp.src('app/AdminLTE/AdminLTE.less')
      .pipe(less())
      .pipe(gulp.dest('build'))
      .pipe(notify(function () {
        console.log('built AdminLTE in ' + (Date.now() - start) + 'ms');
        done && done();
      }));
  };

  watch('public_html/app/**/*.js', bundleApp);
  watch('public_html/app/**/*.css', bundleCSS);
  watch('public_html/app/**/*.html', bundleTemplates);
  watch('public_html/static/**/*', copyIndexHtml);

  bundleApp();
  bundleTemplates();
  copyIndexHtml();

  // BUNDLE CSS

  bundleVendorCSS();
  bundleCSS();

  // BUNDLE VENDORS
  bundleVendors();
});

gulp.task('buildVendors', bundleVendors);
gulp.task('buildVendorCSS', bundleVendorCSS);

gulp.task('manifest', function(){
    gulp.src(['build/*'])
        .pipe(manifest({
            hash: true,
            preferOnline: true,
            network: ['*'],
            filename: 'app.manifest',
            exclude: 'app.manifest'
        }))
        .pipe(gulp.dest('build'));
});
