var WidgetsChartsService = function($widgetsService) {

    this.chartRefreshListeners = []
    this.widgetDataChangeListeners = [];

    this.onChartRefreshRequested = function($widgetId, $listenerFunction) {
        this.chartRefreshListeners.push({
            widgetId: $widgetId,
            listener: $listenerFunction
        });
    };

    this.onWidgetDataChanged = function($widgetId, $listenerFunction) {
        this.widgetDataChangeListeners.push({
            widgetId: $widgetId,
            listener: $listenerFunction
        });
    };

    this.updateWidgetData = function($widgetId, $widgetData, $widgetChartSettings) {
        this.widgetDataChangeListeners.forEach(function($listenerEntry) {
            if ($listenerEntry.widgetId == $widgetId) {
                $listenerEntry.listener($widgetData, $widgetChartSettings);
                return;
            }
        });
    };

    this.refreshWidgetChart = function($widgetId) {
        this.chartRefreshListeners.forEach(function($listenerEntry) {
            if ($listenerEntry.widgetId == $widgetId) {
                $listenerEntry.listener();
                return;
            }
        });
    };

    var initWidgetChart = function($widgetData) {
        console.log($widgetData);
    };

    var initWidgetAddedCallback = function() {
        $widgetsService.onWidgetAdded(function($widgetData) {
            initWidgetChart($widgetData);
        });
    };

    var initWidgetsCharts = function() {
        var widgets = $widgetsService.getWidgets();
        widgets.forEach(initWidgetChart);
    };

    initWidgetAddedCallback();
    initWidgetsCharts();
};

app.service('$widgetsChartsService', WidgetsChartsService);