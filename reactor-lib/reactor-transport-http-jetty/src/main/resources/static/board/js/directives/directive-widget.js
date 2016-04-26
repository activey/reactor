var WidgetController = function($scope, $widgetPopupService, $widgetContentRefreshService, $widgetsChartsService) {
    
    $scope.widgetContent = "";
    $scope.processing = true;
    $scope.error = false;

    $scope.validateShowDimmer = function() {
        return $scope.widget.visual.showDimmerLoading === true;
    };

    $scope.editWidget = function() {
        $widgetPopupService.updateWidgetWindow($scope.widget);
    };

    $scope.removeWidget = function() {
        $widgetPopupService.removeWidgetWindow($scope.widget);
    };

    $scope.getWidgetDecoration = function() {
        var decoration = $scope.widget.visual.colorSettings.staticModel.color;
        if ($scope.widget.visual.colorSettings.dynamic) {
            decoration = evalWidgetDynamicColor();
        }
        if ($scope.widget.visual.colorSettings.inverted === true) {
            decoration = decoration + ' inverted';
        }
        if ($scope.widget.visual.dropShadow === true) {
            decoration = decoration + ' shadow';
        }
        decoration = decoration + ' ' + $scope.widget.visual.textAlign;
        return decoration;
    };

    var evalWidgetDynamicColor = function() {
        var response = $scope.widgetContent;
        var widgetDynamicColorModel = $scope.widget.visual.colorSettings.dynamicModel;

        switch (true) {
            case eval(widgetDynamicColorModel.blue): return 'blue';
            case eval(widgetDynamicColorModel.orange): return 'orange';
            case eval(widgetDynamicColorModel.green): return 'green';
            case eval(widgetDynamicColorModel.red): return 'red';
            case eval(widgetDynamicColorModel.purple): return 'purple';
            case eval(widgetDynamicColorModel.teal): return 'teal';
        }
    }

    var bindWidgetContentChangeListener = function($scope) {
        $widgetContentRefreshService.addDataRefreshListener($scope.widget, {
            onDataRefreshStarted: function() {
                $scope.$apply(function() {
                    $scope.processing = true;
                });
            },
            onDataRefreshFinished: function($widgetData) {
                $scope.widgetContent = $widgetData;
                $scope.error = false;

                $scope.processing = false;
            },
            onDataRefreshFailed: function() {
                $scope.error = true;
            }
        });
    };

    var bindWidgetChartValueUpdateListener = function($scope) {
        $widgetContentRefreshService.addDataRefreshListener($scope.widget, {
            onDataRefreshFinished: function($widgetData) {
                if (!$scope.widget.visual.chart.show) {
                    return;
                }
                $widgetsChartsService.updatedWidgetData($scope.widget.id, $widgetData, $scope.widget.visual.chart);
            },
            onDataRefreshStarted: function() {},
            onDataRefreshFailed: function() {}
        });
    };

    bindWidgetContentChangeListener($scope);
    bindWidgetChartValueUpdateListener($scope);
};

var WidgetLinker = function($scope, $element, $attrs) {
    var initWidgetDimmer = function($element) {
        $($element).dimmer({
            on: 'hover'
        });
    };

    initWidgetDimmer($element);
};

app.directive('widget', function() {
    return {
        restrict: 'A',
        link: WidgetLinker,
        controller: WidgetController,
        templateUrl: 'js/directives/directive-widget.html',
        scope: {
            widget: '='
        },
        replace: true
    };
});