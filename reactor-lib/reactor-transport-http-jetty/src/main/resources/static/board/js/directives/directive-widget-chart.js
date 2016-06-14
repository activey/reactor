var WidgetChartController = function($scope, $widgetsChartsService, $widgetChartHandlerFactoryService) {

    $scope.widgetChartHandler = {};

    var createWidgetChartHandler = function($scope) {
        $scope.widgetChartHandler = $widgetChartHandlerFactoryService.createChartHandler($scope.widgetChartSettings.type);
    };

    var initializeWidgetChartListeners = function($scope) {
        $widgetsChartsService.onWidgetDataChanged($scope.widgetId, $scope.onUpdateWidgetData);
        $widgetsChartsService.onChartRefreshRequested($scope.widgetId, $scope.updateChart);

        $scope.$watch('widgetChartSettings', function($newWidgetChartSettings) {
            $scope.widgetChartHandler = $widgetChartHandlerFactoryService.replaceChartHandler($scope.widgetChartHandler, $newWidgetChartSettings);
        }, true);
    };

    $scope.onUpdateWidgetData = function($widgetData, $widgetChartSettings) {
        var chartData = [];
        var widgetData = $widgetData;

        $widgetChartSettings.mappings.forEach(function($chartMappingDefinition) {
            $widgetData.response.forEach(function($widgetDataEntry) {
                if ($widgetDataEntry.id === $chartMappingDefinition.property) {
                    chartData.push($widgetDataEntry.value);
                }
            });
        });
        $scope.widgetChartHandler.updateChart(chartData, $widgetChartSettings);
    };

    $scope.updateChart = function() {
        $scope.widgetChartHandler.updateChart();
    };

    createWidgetChartHandler($scope);
    initializeWidgetChartListeners($scope);
}

var WidgetChartLinker = function($scope, $element, $attrs) {

    var initializeChart = function($element) {
        $scope.widgetChartHandler.initializeChart($element[0], $scope.widgetChartSettings);
    };
    initializeChart($element);
};

app.directive('widgetChart', function() {
    return {
        restrict: 'A',
        link: WidgetChartLinker,
        controller: WidgetChartController,
        scope: {
            widgetId: '=',
            widgetChartSettings: '='
        }
    };
});