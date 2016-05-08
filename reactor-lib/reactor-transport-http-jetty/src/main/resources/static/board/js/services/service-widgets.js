var WidgetsService = function($widgetsStorageService) {

    this.onWidgetAddedCallbacks = [];
    this.onWidgetRemovedCallbacks = [];
    this.onWidgetChangedCallbacks = [];

    this.getWidgets = function() {
        return $widgetsStorageService.loadWidgets();
    };

    this.addNewWidget = function($widgetData) {
        this.notifyWidgetAdded($widgetsStorageService.addNewWidget($widgetData));
    };

    this.updateWidget = function($widgetData) {
        this.notifyWidgetUpdated($widgetsStorageService.updateWidget($widgetData));
    };

    this.removeWidget = function($widgetData) {
        $widgetsStorageService.removeWidget($widgetData)
        this.notifyWidgetRemoved($widgetData);  
    };
    
    this.notifyWidgetAdded = function($newWidget) {
        for (var callbackIndex in this.onWidgetAddedCallbacks) {
            var onWidgetAddedCallback = this.onWidgetAddedCallbacks[callbackIndex];
            onWidgetAddedCallback($newWidget);
        }
    };

    this.notifyWidgetRemoved = function($removedWidget) {
        for (var callbackIndex in this.onWidgetRemovedCallbacks) {
            var onWidgetRemovedCallback = this.onWidgetRemovedCallbacks[callbackIndex];
            onWidgetRemovedCallback($removedWidget);
        }
    };

    this.notifyWidgetUpdated = function($updatedWidget) {
        for (var callbackIndex in this.onWidgetChangedCallbacks) {
            var onWidgetChangedCallback = this.onWidgetChangedCallbacks[callbackIndex];
            onWidgetChangedCallback($updatedWidget);
        }
    };

    this.onWidgetChanged = function($onWidgetChangedListener) {
        this.onWidgetChangedCallbacks.push($onWidgetChangedListener);
    }

    this.onWidgetAdded = function($callback) {
        this.onWidgetAddedCallbacks.push($callback);
    };

    this.onWidgetRemoved = function($callback) {
        this.onWidgetRemovedCallbacks.push($callback);
    };
}

app.service('$widgetsService', WidgetsService);