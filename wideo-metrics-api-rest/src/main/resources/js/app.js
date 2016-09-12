'use strict';

// Declare app level module which depends on views, and components
var controllers = angular.module('wideoMetrics.controllers', []);
var directives = angular.module('wideoMetrics.directives', []);
var filters = angular.module('wideoMetrics.filters', []);
var wideoMetricsApp = angular.module('wideoMetricsApp', [
  'ngRoute',
  'ngSanitize',
  'ngAnimate',
//  'cgBusy',
  'wideoMetricsApp.controllers',
  'wideoMetricsApp.services',
  'wideoMetricsApp.directives',
  'wideoMetricsApp.filters'
  ]);

angular.isUndefinedOrNull = function(val) {
    return angular.isUndefined(val) || val === null 
}