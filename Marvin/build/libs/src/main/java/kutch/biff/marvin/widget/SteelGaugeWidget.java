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

import eu.hansolo.enzo.common.Section;
import eu.hansolo.enzo.gauge.Gauge;
import eu.hansolo.enzo.gauge.Gauge.TickLabelOrientation;
import java.util.ArrayList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import kutch.biff.marvin.datamanager.DataManager;
import kutch.biff.marvin.utility.FrameworkNode;

/**
 *
 * @author Patrick Kutch
 */
public class SteelGaugeWidget extends BaseWidget
{
    private String UnitText;
    private double MinValue;
    private double MaxValue;
    private int DialStartAngle;
    private int DialRangeAngle;
    private double MajorTick;
    private double MinorTick;
    private TickLabelOrientation eOrientation;
    private boolean EnhancedRateText;
    private boolean Shadowed;
    private boolean ShowMeasuredMax;
    private boolean ShowMeasuredMin;
    private ArrayList<Section> Sections;
    private Gauge _Gauge;  // remember that you need to disable mouse action in gaugeskin knob.setOnMousePressed(event -> ~ line 324
    private GridPane _ParentGridPane;
    private double _InitialValue ;
    
    public SteelGaugeWidget()
    {
        UnitText = "";
        MinValue = 0;
        MaxValue = 0;
        DialStartAngle = 270;
        DialRangeAngle = 270;
        MajorTick = 0;
        MinorTick = 0;
        eOrientation = TickLabelOrientation.HORIZONTAL;
        EnhancedRateText = true;
        Shadowed = true;
        ShowMeasuredMax = true;
        ShowMeasuredMin = true;
        Sections = null;
        _Gauge = new Gauge();
        _Gauge.setAnimationDuration(400);
        //_Gauge.setAnimated(false);

    }

    @Override
    public boolean Create(GridPane pane, DataManager dataMgr)
    {
        SetParent(pane);
        _ParentGridPane = pane;
        if (false == SetupGauge())
        {
            return false;
        }
        _Gauge.setValue(_InitialValue);

        ConfigureDimentions();

        ConfigureAlignment();
        EventHandler<MouseEvent> eh = SetupTaskAction(); // special because Gauge can be interactive
        if (null == eh)
        {
                eh = new EventHandler<MouseEvent>() // create a dummy one, because we dont' want interactive
                {
                    @Override
                    public void handle(MouseEvent event)
                    {
                    }
                };
        }
        _Gauge.customKnobClickHandlerProperty().set(eh);
        SetupPeekaboo(dataMgr);

        pane.add(_Gauge, getColumn(), getRow(), getColumnSpan(), getRowSpan());

        dataMgr.AddListener(getMinionID(), getNamespace(), new ChangeListener()
        {
            @Override 
            public void changed(ObservableValue o, Object oldVal, Object newVal)
            {
                if (IsPaused())
                {
                    return;
                }

                double newDialValue = 0;
                double oldDialValue = 0;
                String strVal = newVal.toString();
                try
                {
                    newDialValue = Double.parseDouble(strVal);

                    //oldDialValue = Double.parseDouble(oldVal.toString());
                }
                catch (Exception ex)
                {
                    LOGGER.severe("Invalid data for Gauge received: " + strVal);
                    return;
                }

                _Gauge.setValue(newDialValue);
            }
        });

        return ApplyCSS();
    }

    private boolean SetupGauge()
    {
        _Gauge.setMinValue(MinValue);
        _Gauge.setMaxValue(MaxValue);
        _Gauge.setStartAngle(DialStartAngle);
        _Gauge.setAngleRange(DialRangeAngle);
        _Gauge.setTickLabelOrientation(eOrientation);
        _Gauge.setDropShadowEnabled(Shadowed);
        _Gauge.setMinMeasuredValueVisible(ShowMeasuredMin);
        _Gauge.setMaxMeasuredValueVisible(ShowMeasuredMax);
        _Gauge.setPlainValue(!EnhancedRateText);

        if (getTitle().length() > 0)
        {
            _Gauge.setTitle(getTitle());
        }

        if (MajorTick > 0)
        {
            _Gauge.setMajorTickSpace(MajorTick);
        }
        if (MinorTick > 0)
        {
            _Gauge.setMinorTickSpace(MinorTick);
        }
        if (null != getUnitsOverride())
        {
            _Gauge.setUnit(getUnitsOverride());
            LOGGER.config("Overriding Widget Units Text to " + getUnitsOverride());
        }
        else if (UnitText.length() > 0)
        {
            _Gauge.setUnit(UnitText);
        }
        if (null != Sections)
        {
            _Gauge.setSections(Sections);
        }

        _Gauge.setDecimals(getDecimalPlaces());
//        _Gauge.setInteractive(true);

        //_Gauge.setMouseTransparent(!CONFIG.isDebugMode());  // only allow in debug mode, is for displaying widget info on click
        //_Gauge.setOnMouseClicked(null);
        return true;
    }
    @Override
    public void SetInitialValue(String value)
    {
        try
        {
            _InitialValue = Double.parseDouble(value);
        }
        catch (NumberFormatException ex)
        {
            LOGGER.severe("Invalid Default Value data for SteelGauge: " + value);
        }
    }

    @Override
    public javafx.scene.Node getStylableObject()
    {
        return _Gauge;
    }

    public void setUnitText(String UnitText)
    {
        this.UnitText = UnitText;
    }

    public void setDialRangeAngle(int DialRangeAngle)
    {
        this.DialRangeAngle = DialRangeAngle;
    }

    public void setMinValue(double MinValue)
    {
        this.MinValue = MinValue;
    }

    public void setMaxValue(double MaxValue)
    {
        this.MaxValue = MaxValue;
    }

    public void setDialStartAngle(int DialStartAngle)
    {
        this.DialStartAngle = DialStartAngle;
    }

    public void setRangeAngle(int DialEndAngle)
    {
        this.DialRangeAngle = DialEndAngle;
    }

    public void setMajorTick(double MajorTick)
    {
        this.MajorTick = MajorTick;
    }

    public void setMinorTick(double MinorTick)
    {
        this.MinorTick = MinorTick;
    }

    public void setOrientation(Gauge.TickLabelOrientation eOrientation)
    {
        this.eOrientation = eOrientation;
    }

    public void setEnhancedRateText(boolean EnhancedRateText)
    {
        this.EnhancedRateText = EnhancedRateText;
    }

    public void setShadowed(boolean Shadowed)
    {
        this.Shadowed = Shadowed;
    }

    public void setShowMeasuredMax(boolean ShowMeasuredMax)
    {
        this.ShowMeasuredMax = ShowMeasuredMax;
    }

    public void setShowMeasuredMin(boolean ShowMeasuredMin)
    {
        this.ShowMeasuredMin = ShowMeasuredMin;
    }

    public void setSections(ArrayList<Section> Sections)
    {
        this.Sections = Sections;
    }

    @Override
    public ObservableList<String> getStylesheets()
    {
        return _Gauge.getStylesheets();
    }
    /**
     * Sets range for widget - not valid for all widgets
     * @param rangeNode
     * @return
     */

    @Override
    public boolean HandleValueRange(FrameworkNode rangeNode)
    {
        double Min = -1234.5678;
        double Max = -1234.5678;
        if (rangeNode.hasAttribute("Min"))
        {
            Min = rangeNode.getDoubleAttribute("Min", Min);
            if (Min == -1234.5678)
            {
                return false;
            }
            this.MinValue = Min;
        }
        if (rangeNode.hasAttribute("Max"))
        {
            Max = rangeNode.getDoubleAttribute("Max", Max);
            if (Max == -1234.5678)
            {
                return false;
            }
            this.MaxValue = Max;
        }
        for (FrameworkNode node : rangeNode.getChildNodes())
        {
            if (node.getNodeName().equalsIgnoreCase("#Text") || node.getNodeName().equalsIgnoreCase("#comment"))
            {
                continue;
            }
            if (node.getNodeName().equalsIgnoreCase("TickCount"))
            {
                double MajorTickVal = -1234;
                double MinorTickVal = -1234;
                
                if (node.hasAttribute("Major"))
                {
                    MajorTickVal = node.getDoubleAttribute("Major", MajorTickVal);
                    if (MajorTickVal != -1234)
                    {
                        setMajorTick((this.MaxValue - this.MinValue)/MajorTickVal);
                    }
                    else
                    {
                        LOGGER.severe("Invalid TickCount:Major ->" + node.getAttribute("Major"));
                        return false;
                    }
                }
                if (node.hasAttribute("Minor"))
                {
                    MinorTickVal = node.getDoubleAttribute("Minor", MinorTickVal);
                    if (MinorTickVal != -1234)
                    {
                        setMinorTick((this.MaxValue - this.MinValue)/MinorTickVal);
                    }
                    else
                    {
                        LOGGER.severe("Invalid TickCount:Minor ->" + node.getAttribute("Minor"));
                        return false;
                    }
                }
            }
            else
            {
                return false;
            }
            
        }
        return true;
    }
    @Override
    public void UpdateTitle(String strTitle)
    {
        _Gauge.setTitle(getTitle());
    }
 
}
