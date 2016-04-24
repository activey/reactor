var WidgetsChartsService = function() {

    this.aaa = {};

    this.updatedWidgetData = function($widgetId, $widgetData, $widgetChartSettings) {
        console.log("widget id = " + $widgetId);
        console.log($widgetData);
    };

};

app.service('$widgetsChartsService', WidgetsChartsService);