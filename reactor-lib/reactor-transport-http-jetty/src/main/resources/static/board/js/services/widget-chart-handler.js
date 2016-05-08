var LineChartHandlerFactory = function() {
    var LineChartHandler = function() {
        this.chartType = 'line';
        this.chart = {};
        this.chartData = {
            series: [
                []
            ],
            labels: []
        };

        var initializeChartLabels = function($chartSettings) {
            var labels = [];
            for (var labelIndex = 0; labelIndex < $chartSettings.timeSeriesSamples - 1; labelIndex++) {
                labels.push(labelIndex + 1);
            }
            return labels;
        };

        this.initializeChart = function($chartContainer, $chartSettings) {
            this.chartData.labels = initializeChartLabels($chartSettings);

            this.chart = new Chartist.Line($chartContainer, this.chartData, {
                low: 0,
                showArea: true,
                showLabel: true,
                showPoint: false,
                fullWidth: true,
                chartPadding: 0,
                axisX: {
                    showGrid: false,
                    showLabel: false,
                    offset: 0
                },
                axisY: {
                    showGrid: false,
                    showLabel: false,
                    offset: 0
                }
            });
        };

        var extendChartSeries = function($chartSeries, $length) {
            var extendedSeries = $chartSeries;
            if (extendedSeries.length == $length) {
                return extendedSeries;
            }
            for (var index = $chartSeries.length; index < $length; index++) {
                extendedSeries.push([]);
            }
            return extendedSeries;
        };

        this.updateChart = function($chartData, $chartSettings) {
            if (typeof $chartData === 'undefined' || $chartData === null || $chartData.length === 0) {
                this.chart.update(this.chartData);
                return;
            }

            this.chartData.series = extendChartSeries(this.chartData.series, $chartData.length);
            for (var index = 0; index < $chartData.length; index++) {
                this.chartData.series[index].push($chartData[index]);

                if (this.chartData.series[index].length >= $chartSettings.timeSeriesSamples) {
                    this.chartData.series[index].splice(0, 1);
                }
            }
            this.chart.update(this.chartData);
        };
    };

    return {
        getInstance: function() {
            return new LineChartHandler();
        }
    };
};
app.factory('LineChartHandlerFactory', LineChartHandlerFactory);


var DoughnutChartHandlerFactory = function() {

    var DoughnutChartHandler = function() {
        this.chartType = 'doughnut';

        this.chart = {};
        this.chartData = {
            series: []
        };

        this.initializeChart = function($chartContainer, $chartSettings) {
            this.chart = new Chartist.Pie($chartContainer, this.chartData, {
                donut: true,
                donutWidth: 80,
                showLabel: true
            });
        };

        this.updateChart = function($chartData, $chartSettings) {
            if (typeof $chartData === 'undefined' || $chartData === null || $chartData.length === 0) {
                this.chart.update(this.chartData);
                return;
            }
            this.chartData.series = $chartData;
            this.chart.update(this.chartData);
        };
    };

    return {
        getInstance: function() {
            return new DoughnutChartHandler();
        }
    };
}
app.factory('DoughnutChartHandlerFactory', DoughnutChartHandlerFactory);

app.service('$widgetChartHandlerFactoryService', function(DoughnutChartHandlerFactory, LineChartHandlerFactory) {
    this.createChartHandler = function($chartType) {
        switch ($chartType) {
            case 'doughnut': return DoughnutChartHandlerFactory.getInstance();
            case 'line': return LineChartHandlerFactory.getInstance();
        }
    };

    this.replaceChartHandler = function($oldChartHandler, $chartSettings) {
        var newChartHandler = this.createChartHandler($chartSettings.type);
        newChartHandler.initializeChart($oldChartHandler.chart.container, $chartSettings);
        return newChartHandler;
    };
});