var WidgetController = function($scope, $widgetPopupService, $widgetContentRefreshService, $widgetsChartsService) {
    
    $scope.widgetContent = {
        response: []
    };
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
            decoration = evalWidgetDynamicColor($scope);
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

    var evalWidgetDynamicColor = function($scope) {
        if ($scope.widgetContent.response.length === 0) {
            return;
        }

        // creating local variables for each response line
        for (var $lineIndex in $scope.widgetContent.response) {
            var responseLine = $scope.widgetContent.response[$lineIndex];
            eval('var ' + responseLine.id + ' = ' + responseLine.value + ';');
        }

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
        $scope.$watch('widgetContent', function($newWidgetContent, bleble){
            $widgetsChartsService.updateWidgetData($scope.widget.id, $newWidgetContent, $scope.widget.chart);
        }, true);
    };

    var bindWidgetResizeListeners = function($scope) {
        $scope.$on('gridster-item-transition-end', function(item) {
            $widgetsChartsService.refreshWidgetChart($scope.widget.id);
        });
        $scope.$on('gridster-item-resized', function(item) {
            $widgetsChartsService.refreshWidgetChart($scope.widget.id);
        });
    };

    bindWidgetContentChangeListener($scope);
    bindWidgetChartValueUpdateListener($scope);
    bindWidgetResizeListeners($scope);
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