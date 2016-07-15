(function(window, document, Chartist) {
    'use strict';

    var defaultOptions = {
        chartId: 'line-chart',
        labels: [],
        labelClass: 'ct-label',
        labelOffset: {
            x: 5,
            y: -5
        },
        labelSpacing: '100px'
    };

    var findOrCreateDefsElement = function($svgRoot) {
        var defsElement = null;
        for (var childIndex = 0; childIndex < $svgRoot._node.childNodes.length; childIndex++) {
            var childNode = $svgRoot._node.childNodes[childIndex];
            if (childNode.localName === 'defs') {
                defsElement = childNode;
                break;
            }
        }
        if (defsElement) {
            return new Chartist.Svg(defsElement);
        }
        return $svgRoot.elem('defs', {}, null, true);
    };

    var generateSeriesLabel = function($seriesLabel) {
        var label = '';
        for (var index = 0; index < 100; index++) {
            label = label + ' ' + $seriesLabel;
        }
        return label;
    }

    Chartist.plugins = Chartist.plugins || {};
    Chartist.plugins.lineChartLabels = function(options) {
        options = Chartist.extend({}, defaultOptions, options);

        return function lineChartLabels(chart) {
            options.chartId = chart.container.id || options.chartId;

            if (chart instanceof Chartist.Line) {
                chart.on('draw', function(data) {
                    if (data.type !== 'line') {
                        return;
                    }
                    if (!data.path.pathElements || data.path.pathElements.length == 0) {
                        return;
                    }

                    var definitionsElement = findOrCreateDefsElement(data.element.root());
                    var pathDefinitionElement = definitionsElement.elem('path');
                    pathDefinitionElement.attr({
                        id: options.chartId + '_series_path_' + data.seriesIndex,
                        d: data.element.attr('d')
                    });

                    data.group.elem('text', {
                        x: options.labelOffset.x,
                        'word-spacing': options.labelSpacing
                    }, options.labelClass).elem('textPath', {
                        'xlink:href': '#' + options.chartId + '_series_path_' + data.seriesIndex
                    }).elem('tspan', {
                        dy: options.labelOffset.y
                    }).text(generateSeriesLabel(options.labels[data.seriesIndex]));
                });
            }
        };
    };

}(window, document, Chartist));