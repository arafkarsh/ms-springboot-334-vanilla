/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
var showControllersOnly = false;
var seriesFilter = "";
var filtersOnlySampleSeries = true;

/*
 * Add header in statistics table to group metrics by category
 * format
 *
 */
function summaryTableHeader(header) {
    var newRow = header.insertRow(-1);
    newRow.className = "tablesorter-no-sort";
    var cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Requests";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 3;
    cell.innerHTML = "Executions";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 7;
    cell.innerHTML = "Response Times (ms)";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Throughput";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 2;
    cell.innerHTML = "Network (KB/sec)";
    newRow.appendChild(cell);
}

/*
 * Populates the table identified by id parameter with the specified data and
 * format
 *
 */
function createTable(table, info, formatter, defaultSorts, seriesIndex, headerCreator) {
    var tableRef = table[0];

    // Create header and populate it with data.titles array
    var header = tableRef.createTHead();

    // Call callback is available
    if(headerCreator) {
        headerCreator(header);
    }

    var newRow = header.insertRow(-1);
    for (var index = 0; index < info.titles.length; index++) {
        var cell = document.createElement('th');
        cell.innerHTML = info.titles[index];
        newRow.appendChild(cell);
    }

    var tBody;

    // Create overall body if defined
    if(info.overall){
        tBody = document.createElement('tbody');
        tBody.className = "tablesorter-no-sort";
        tableRef.appendChild(tBody);
        var newRow = tBody.insertRow(-1);
        var data = info.overall.data;
        for(var index=0;index < data.length; index++){
            var cell = newRow.insertCell(-1);
            cell.innerHTML = formatter ? formatter(index, data[index]): data[index];
        }
    }

    // Create regular body
    tBody = document.createElement('tbody');
    tableRef.appendChild(tBody);

    var regexp;
    if(seriesFilter) {
        regexp = new RegExp(seriesFilter, 'i');
    }
    // Populate body with data.items array
    for(var index=0; index < info.items.length; index++){
        var item = info.items[index];
        if((!regexp || filtersOnlySampleSeries && !info.supportsControllersDiscrimination || regexp.test(item.data[seriesIndex]))
                &&
                (!showControllersOnly || !info.supportsControllersDiscrimination || item.isController)){
            if(item.data.length > 0) {
                var newRow = tBody.insertRow(-1);
                for(var col=0; col < item.data.length; col++){
                    var cell = newRow.insertCell(-1);
                    cell.innerHTML = formatter ? formatter(col, item.data[col]) : item.data[col];
                }
            }
        }
    }

    // Add support of columns sort
    table.tablesorter({sortList : defaultSorts});
}

$(document).ready(function() {

    // Customize table sorter default options
    $.extend( $.tablesorter.defaults, {
        theme: 'blue',
        cssInfoBlock: "tablesorter-no-sort",
        widthFixed: true,
        widgets: ['zebra']
    });

    var data = {"OkPercent": 96.29166666666667, "KoPercent": 3.7083333333333335};
    var dataset = [
        {
            "label" : "FAIL",
            "data" : data.KoPercent,
            "color" : "#FF6347"
        },
        {
            "label" : "PASS",
            "data" : data.OkPercent,
            "color" : "#9ACD32"
        }];
    $.plot($("#flot-requests-summary"), dataset, {
        series : {
            pie : {
                show : true,
                radius : 1,
                label : {
                    show : true,
                    radius : 3 / 4,
                    formatter : function(label, series) {
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
                            + label
                            + '<br/>'
                            + Math.round10(series.percent, -2)
                            + '%</div>';
                    },
                    background : {
                        opacity : 0.5,
                        color : '#000'
                    }
                }
            }
        },
        legend : {
            show : true
        }
    });

    // Creates APDEX table
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.9629166666666666, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.9575, 500, 1500, "Get All Products"], "isController": false}, {"data": [0.9683333333333334, 500, 1500, "Search Products"], "isController": false}]}, function(index, item){
        switch(index){
            case 0:
                item = item.toFixed(3);
                break;
            case 1:
            case 2:
                item = formatDuration(item);
                break;
        }
        return item;
    }, [[0, 0]], 3);

    // Create statistics table
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 2400, 89, 3.7083333333333335, 3.3154166666666645, 1, 92, 3.0, 5.0, 6.0, 12.0, 98.06325079676391, 169.85914899485167, 32.84735842118166], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["Get All Products", 1200, 51, 4.25, 3.396666666666669, 1, 92, 3.0, 5.0, 7.0, 12.0, 49.03563255966002, 84.80674342105263, 16.18558965348153], "isController": false}, {"data": ["Search Products", 1200, 38, 3.1666666666666665, 3.2341666666666717, 1, 53, 3.0, 4.0, 6.0, 11.990000000000009, 49.15413918813747, 85.2718710318273, 16.70472698971859], "isController": false}]}, function(index, item){
        switch(index){
            // Errors pct
            case 3:
                item = item.toFixed(2) + '%';
                break;
            // Mean
            case 4:
            // Mean
            case 7:
            // Median
            case 8:
            // Percentile 1
            case 9:
            // Percentile 2
            case 10:
            // Percentile 3
            case 11:
            // Throughput
            case 12:
            // Kbytes/s
            case 13:
            // Sent Kbytes/s
                item = item.toFixed(2);
                break;
        }
        return item;
    }, [[0, 0]], 0, summaryTableHeader);

    // Create error table
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": [{"data": ["The operation lasted too long: It took 92 milliseconds, but should not have lasted longer than 7 milliseconds.", 1, 1.1235955056179776, 0.041666666666666664], "isController": false}, {"data": ["The operation lasted too long: It took 8 milliseconds, but should not have lasted longer than 7 milliseconds.", 20, 22.471910112359552, 0.8333333333333334], "isController": false}, {"data": ["The operation lasted too long: It took 21 milliseconds, but should not have lasted longer than 7 milliseconds.", 2, 2.247191011235955, 0.08333333333333333], "isController": false}, {"data": ["The operation lasted too long: It took 27 milliseconds, but should not have lasted longer than 7 milliseconds.", 1, 1.1235955056179776, 0.041666666666666664], "isController": false}, {"data": ["The operation lasted too long: It took 14 milliseconds, but should not have lasted longer than 7 milliseconds.", 4, 4.49438202247191, 0.16666666666666666], "isController": false}, {"data": ["The operation lasted too long: It took 16 milliseconds, but should not have lasted longer than 7 milliseconds.", 3, 3.3707865168539324, 0.125], "isController": false}, {"data": ["The operation lasted too long: It took 12 milliseconds, but should not have lasted longer than 7 milliseconds.", 6, 6.741573033707865, 0.25], "isController": false}, {"data": ["The operation lasted too long: It took 13 milliseconds, but should not have lasted longer than 7 milliseconds.", 4, 4.49438202247191, 0.16666666666666666], "isController": false}, {"data": ["The operation lasted too long: It took 9 milliseconds, but should not have lasted longer than 7 milliseconds.", 22, 24.719101123595507, 0.9166666666666666], "isController": false}, {"data": ["The operation lasted too long: It took 10 milliseconds, but should not have lasted longer than 7 milliseconds.", 11, 12.359550561797754, 0.4583333333333333], "isController": false}, {"data": ["The operation lasted too long: It took 11 milliseconds, but should not have lasted longer than 7 milliseconds.", 10, 11.235955056179776, 0.4166666666666667], "isController": false}, {"data": ["The operation lasted too long: It took 53 milliseconds, but should not have lasted longer than 7 milliseconds.", 2, 2.247191011235955, 0.08333333333333333], "isController": false}, {"data": ["The operation lasted too long: It took 19 milliseconds, but should not have lasted longer than 7 milliseconds.", 1, 1.1235955056179776, 0.041666666666666664], "isController": false}, {"data": ["The operation lasted too long: It took 18 milliseconds, but should not have lasted longer than 7 milliseconds.", 2, 2.247191011235955, 0.08333333333333333], "isController": false}]}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 2400, 89, "The operation lasted too long: It took 9 milliseconds, but should not have lasted longer than 7 milliseconds.", 22, "The operation lasted too long: It took 8 milliseconds, but should not have lasted longer than 7 milliseconds.", 20, "The operation lasted too long: It took 10 milliseconds, but should not have lasted longer than 7 milliseconds.", 11, "The operation lasted too long: It took 11 milliseconds, but should not have lasted longer than 7 milliseconds.", 10, "The operation lasted too long: It took 12 milliseconds, but should not have lasted longer than 7 milliseconds.", 6], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": ["Get All Products", 1200, 51, "The operation lasted too long: It took 9 milliseconds, but should not have lasted longer than 7 milliseconds.", 13, "The operation lasted too long: It took 8 milliseconds, but should not have lasted longer than 7 milliseconds.", 12, "The operation lasted too long: It took 10 milliseconds, but should not have lasted longer than 7 milliseconds.", 7, "The operation lasted too long: It took 11 milliseconds, but should not have lasted longer than 7 milliseconds.", 5, "The operation lasted too long: It took 12 milliseconds, but should not have lasted longer than 7 milliseconds.", 3], "isController": false}, {"data": ["Search Products", 1200, 38, "The operation lasted too long: It took 9 milliseconds, but should not have lasted longer than 7 milliseconds.", 9, "The operation lasted too long: It took 8 milliseconds, but should not have lasted longer than 7 milliseconds.", 8, "The operation lasted too long: It took 11 milliseconds, but should not have lasted longer than 7 milliseconds.", 5, "The operation lasted too long: It took 10 milliseconds, but should not have lasted longer than 7 milliseconds.", 4, "The operation lasted too long: It took 12 milliseconds, but should not have lasted longer than 7 milliseconds.", 3], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
