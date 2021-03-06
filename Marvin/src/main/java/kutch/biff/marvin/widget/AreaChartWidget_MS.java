/*
 * ##############################################################################
 * #  Copyright (c) 2016 Intel Corporation
 * # 
 * # Licensed under the Apache License, Version 2.0 (the "License");
 * #  you may not use this file except in compliance with the License.
 * #  You may obtain a copy of the License at
 * # 
 * #      http://www.apache.org/licenses/LICENSE-2.0
 * # 
 * #  Unless required by applicable law or agreed to in writing, software
 * #  distributed under the License is distributed on an "AS IS" BASIS,
 * #  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * #  See the License for the specific language governing permissions and
 * #  limitations under the License.
 * ##############################################################################
 * #    File Abstract: 
 * #
 * #
 * ##############################################################################
 */

package kutch.biff.marvin.widget;

import javafx.collections.ObservableList;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.Chart;
import javafx.scene.layout.GridPane;
import kutch.biff.marvin.datamanager.DataManager;
import kutch.biff.marvin.utility.SeriesDataSet;

/**
 *
 * @author Patrick Kutch
 */
public class AreaChartWidget_MS extends LineChartWidget_MS
{
    public void AreaChartWidget_MS()
    {
        
    }
    
    @Override
    public boolean Create(GridPane pane, DataManager dataMgr)
    {
        return _CreateMSChart(pane, dataMgr);
    }
  @Override
    protected Chart CreateChartObject()
    {
        return new AreaChart<>(getxAxis(), getyAxis());
    }
    

    @Override
    @SuppressWarnings("unchecked")
    protected void _CreateChart()
    {
        CreateChart();

        for (SeriesDataSet ds : getSeries())
        {
            ((AreaChart) (getChart())).getData().add(ds.getSeries());
        }
        
    }

    @Override
    public javafx.scene.Node getStylableObject()
    {
        return ((AreaChart) (getChart()));
    }

    @Override
    public ObservableList<String> getStylesheets()
    {
        return ((AreaChart) (getChart())).getStylesheets();
    }    
}
