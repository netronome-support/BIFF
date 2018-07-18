/*
 * Copyright (c) 2015 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.hansolo.enzo.gauge;

import eu.hansolo.enzo.common.Section;
import javafx.beans.property.*;
import javafx.geometry.Dimension2D;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.List;


/**
 * Created by hansolo on 22.07.14.
 */
public class RadialGaugeBuilder<B extends RadialGaugeBuilder<B>> {
    private HashMap<String, Property> properties = new HashMap<>();


    // ******************** Constructors **************************************
    protected RadialGaugeBuilder() {}


    // ******************** Methods *******************************************
    public static final RadialGaugeBuilder create() {
        return new RadialGaugeBuilder();
    }

    public final B value(final double VALUE) {
        properties.put("value", new SimpleDoubleProperty(VALUE));
        return (B) this;
    }

    public final B minValue(final double MIN_VALUE) {
        properties.put("minValue", new SimpleDoubleProperty(MIN_VALUE));
        return (B) this;
    }

    public final B maxValue(final double MAX_VALUE) {
        properties.put("maxValue", new SimpleDoubleProperty(MAX_VALUE));
        return (B) this;
    }

    public final B decimals(final int DECIMALS) {
        properties.put("decimals", new SimpleIntegerProperty(DECIMALS));
        return (B) this;
    }

    public final B title(final String TITLE) {
        properties.put("title", new SimpleStringProperty(TITLE));
        return (B)this;
    }

    public final B unit(final String UNIT) {
        properties.put("unit", new SimpleStringProperty(UNIT));
        return (B)this;
    }

    public final B animated(final boolean ANIMATED) {
        properties.put("animated", new SimpleBooleanProperty(ANIMATED));
        return (B)this;
    }

    public final B animationDuration(final double ANIMATION_DURATION) {
        properties.put("animationDuration", new SimpleDoubleProperty(ANIMATION_DURATION));
        return (B)this;
    }

    public final B startAngle(final double START_ANGLE) {
        properties.put("startAngle", new SimpleDoubleProperty(START_ANGLE));
        return (B)this;
    }

    public final B angleRange(final double ANGLE_RANGE) {
        properties.put("angleRange", new SimpleDoubleProperty(ANGLE_RANGE));
        return (B)this;
    }

    public final B autoScale(final boolean AUTO_SCALE) {
        properties.put("autoScale", new SimpleBooleanProperty(AUTO_SCALE));
        return (B)this;
    }

    public final B needleColor(final Color NEEDLE_COLOR) {
        properties.put("needleColor", new SimpleObjectProperty<>(NEEDLE_COLOR));
        return (B)this;
    }

    public final B tickLabelOrientation(final RadialGauge.TickLabelOrientation TICK_LABEL_ORIENTATION) {
        properties.put("tickLabelOrientation", new SimpleObjectProperty<>(TICK_LABEL_ORIENTATION));
        return (B)this;
    }

    public final B numberFormat(final RadialGauge.NumberFormat NUMBER_FORMAT) {
        properties.put("numberFormat", new SimpleObjectProperty<>(NUMBER_FORMAT));
        return (B)this;
    }

    public final B majorTickSpace(final double MAJOR_TICK_SPACE) {
        properties.put("majorTickSpace", new SimpleDoubleProperty(MAJOR_TICK_SPACE));
        return (B)this;
    }

    public final B minorTickSpace(final double MINOR_TICK_SPACE) {
        properties.put("minorTickSpace", new SimpleDoubleProperty(MINOR_TICK_SPACE));
        return (B)this;
    }

    public final B dropShadowEnabled(final boolean DROP_SHADOW_ENABLED) {
        properties.put("dropShadowEnabled", new SimpleBooleanProperty(DROP_SHADOW_ENABLED));
        return (B)this;
    }

    public final B tickLabelFill(final Color TICK_LABEL_FILL) {
        properties.put("tickLabelFill", new SimpleObjectProperty<>(TICK_LABEL_FILL));
        return (B)this;
    }

    public final B tickMarkFill(final Color TICK_MARKER_FILL) {
        properties.put("tickMarkFill", new SimpleObjectProperty<>(TICK_MARKER_FILL));
        return (B)this;
    }

    public final B style(final String STYLE) {
        properties.put("style", new SimpleStringProperty(STYLE));
        return (B)this;
    }

    public final B styleClass(final String... STYLES) {
        properties.put("styleClass", new SimpleObjectProperty<>(STYLES));
        return (B)this;
    }

    public final B ledColor(final Color LED_COLOR) {
        properties.put("ledColor", new SimpleObjectProperty<>(LED_COLOR));
        return (B)this;
    }

    public final B ledVisible(final boolean LED_VISIBLE) {
        properties.put("ledVisible", new SimpleBooleanProperty(LED_VISIBLE));
        return (B) this;
    }

    public final B sections(final Section... SECTIONS) {
        properties.put("sectionsArray", new SimpleObjectProperty<>(SECTIONS));
        return (B)this;
    }

    public final B sections(final List<Section> SECTIONS) {
        properties.put("sectionsList", new SimpleObjectProperty<>(SECTIONS));
        return (B)this;
    }

    public final B areas(final Section... AREAS) {
        properties.put("areasArray", new SimpleObjectProperty<>(AREAS));
        return (B)this;
    }

    public final B areas(final List<Section> AREAS) {
        properties.put("areasList", new SimpleObjectProperty<>(AREAS));
        return (B)this;
    }

    public final B sectionsVisible(final boolean SECTIONS_VISIBLE) {
        properties.put("sectionsVisible", new SimpleBooleanProperty(SECTIONS_VISIBLE));
        return (B)this;
    }

    public final B areasVisible(final boolean AREAS_VISIBLE) {
        properties.put("areasVisible", new SimpleBooleanProperty(AREAS_VISIBLE));
        return (B)this;
    }

    public final B sectionFill0(final Color SECTION_0_FILL) {
        properties.put("sectionFill0", new SimpleObjectProperty<>(SECTION_0_FILL));
        return (B)this;
    }

    public final B sectionFill1(final Color SECTION_1_FILL) {
        properties.put("sectionFill1", new SimpleObjectProperty<>(SECTION_1_FILL));
        return (B)this;
    }

    public final B sectionFill2(final Color SECTION_2_FILL) {
        properties.put("sectionFill2", new SimpleObjectProperty<>(SECTION_2_FILL));
        return (B)this;
    }

    public final B sectionFill3(final Color SECTION_3_FILL) {
        properties.put("sectionFill3", new SimpleObjectProperty<>(SECTION_3_FILL));
        return (B)this;
    }

    public final B sectionFill4(final Color SECTION_4_FILL) {
        properties.put("sectionFill4", new SimpleObjectProperty<>(SECTION_4_FILL));
        return (B)this;
    }

    public final B sectionFill5(final Color SECTION_5_FILL) {
        properties.put("sectionFill5", new SimpleObjectProperty<>(SECTION_5_FILL));
        return (B)this;
    }

    public final B sectionFill6(final Color SECTION_6_FILL) {
        properties.put("sectionFill6", new SimpleObjectProperty<>(SECTION_6_FILL));
        return (B)this;
    }

    public final B sectionFill7(final Color SECTION_7_FILL) {
        properties.put("sectionFill7", new SimpleObjectProperty<>(SECTION_7_FILL));
        return (B)this;
    }

    public final B sectionFill8(final Color SECTION_8_FILL) {
        properties.put("sectionFill8", new SimpleObjectProperty<>(SECTION_8_FILL));
        return (B)this;
    }

    public final B sectionFill9(final Color SECTION_9_FILL) {
        properties.put("sectionFill9", new SimpleObjectProperty<>(SECTION_9_FILL));
        return (B)this;
    }

    public final B areaFill0(final Color AREA_0_FILL) {
        properties.put("areaFill0", new SimpleObjectProperty<>(AREA_0_FILL));
        return (B)this;
    }

    public final B areaFill1(final Color AREA_1_FILL) {
        properties.put("areaFill1", new SimpleObjectProperty<>(AREA_1_FILL));
        return (B)this;
    }

    public final B areaFill2(final Color AREA_2_FILL) {
        properties.put("areaFill2", new SimpleObjectProperty<>(AREA_2_FILL));
        return (B)this;
    }

    public final B areaFill3(final Color AREA_3_FILL) {
        properties.put("areaFill3", new SimpleObjectProperty<>(AREA_3_FILL));
        return (B)this;
    }

    public final B areaFill4(final Color AREA_4_FILL) {
        properties.put("areaFill4", new SimpleObjectProperty<>(AREA_4_FILL));
        return (B)this;
    }

    public final B areaFill5(final Color AREA_5_FILL) {
        properties.put("areaFill5", new SimpleObjectProperty<>(AREA_5_FILL));
        return (B)this;
    }

    public final B areaFill6(final Color AREA_6_FILL) {
        properties.put("areaFill6", new SimpleObjectProperty<>(AREA_6_FILL));
        return (B)this;
    }

    public final B areaFill7(final Color AREA_7_FILL) {
        properties.put("areaFill7", new SimpleObjectProperty<>(AREA_7_FILL));
        return (B)this;
    }

    public final B areaFill8(final Color AREA_8_FILL) {
        properties.put("areaFill8", new SimpleObjectProperty<>(AREA_8_FILL));
        return (B)this;
    }

    public final B areaFill9(final Color AREA_9_FILL) {
        properties.put("areaFill9", new SimpleObjectProperty<>(AREA_9_FILL));
        return (B)this;
    }

    public final B prefSize(final double WIDTH, final double HEIGHT) {
        properties.put("prefSize", new SimpleObjectProperty<>(new Dimension2D(WIDTH, HEIGHT)));
        return (B)this;
    }
    public final B minSize(final double WIDTH, final double HEIGHT) {
        properties.put("minSize", new SimpleObjectProperty<>(new Dimension2D(WIDTH, HEIGHT)));
        return (B)this;
    }
    public final B maxSize(final double WIDTH, final double HEIGHT) {
        properties.put("maxSize", new SimpleObjectProperty<>(new Dimension2D(WIDTH, HEIGHT)));
        return (B)this;
    }

    public final B prefWidth(final double PREF_WIDTH) {
        properties.put("prefWidth", new SimpleDoubleProperty(PREF_WIDTH));
        return (B)this;
    }
    public final B prefHeight(final double PREF_HEIGHT) {
        properties.put("prefHeight", new SimpleDoubleProperty(PREF_HEIGHT));
        return (B)this;
    }

    public final B minWidth(final double MIN_WIDTH) {
        properties.put("minWidth", new SimpleDoubleProperty(MIN_WIDTH));
        return (B)this;
    }
    public final B minHeight(final double MIN_HEIGHT) {
        properties.put("minHeight", new SimpleDoubleProperty(MIN_HEIGHT));
        return (B)this;
    }

    public final B maxWidth(final double MAX_WIDTH) {
        properties.put("maxWidth", new SimpleDoubleProperty(MAX_WIDTH));
        return (B)this;
    }
    public final B maxHeight(final double MAX_HEIGHT) {
        properties.put("maxHeight", new SimpleDoubleProperty(MAX_HEIGHT));
        return (B)this;
    }

    public final B scaleX(final double SCALE_X) {
        properties.put("scaleX", new SimpleDoubleProperty(SCALE_X));
        return (B)this;
    }
    public final B scaleY(final double SCALE_Y) {
        properties.put("scaleY", new SimpleDoubleProperty(SCALE_Y));
        return (B)this;
    }

    public final B layoutX(final double LAYOUT_X) {
        properties.put("layoutX", new SimpleDoubleProperty(LAYOUT_X));
        return (B)this;
    }
    public final B layoutY(final double LAYOUT_Y) {
        properties.put("layoutY", new SimpleDoubleProperty(LAYOUT_Y));
        return (B)this;
    }

    public final B translateX(final double TRANSLATE_X) {
        properties.put("translateX", new SimpleDoubleProperty(TRANSLATE_X));
        return (B)this;
    }
    public final B translateY(final double TRANSLATE_Y) {
        properties.put("translateY", new SimpleDoubleProperty(TRANSLATE_Y));
        return (B)this;
    }

    public final RadialGauge build() {
        final RadialGauge CONTROL = new RadialGauge();

        // Make sure that sections and markers will be added first
        if (properties.keySet().contains("sectionsArray")) {
            CONTROL.setSections(((ObjectProperty<Section[]>) properties.get("sectionsArray")).get());
        }
        if(properties.keySet().contains("sectionsList")) {
            CONTROL.setSections(((ObjectProperty<List<Section>>) properties.get("sectionsList")).get());
        }
        if (properties.keySet().contains("areasArray")) {
            CONTROL.setAreas(((ObjectProperty<Section[]>) properties.get("areasArray")).get());
        }
        if(properties.keySet().contains("areasList")) {
            CONTROL.setAreas(((ObjectProperty<List<Section>>) properties.get("areasList")).get());
        }

        for (String key : properties.keySet()) {
            if ("prefSize".equals(key)) {
                Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                CONTROL.setPrefSize(dim.getWidth(), dim.getHeight());
            } else if("minSize".equals(key)) {
                Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                CONTROL.setPrefSize(dim.getWidth(), dim.getHeight());
            } else if("maxSize".equals(key)) {
                Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                CONTROL.setPrefSize(dim.getWidth(), dim.getHeight());
            } else if("prefWidth".equals(key)) {
                CONTROL.setPrefWidth(((DoubleProperty) properties.get(key)).get());
            } else if("prefHeight".equals(key)) {
                CONTROL.setPrefHeight(((DoubleProperty) properties.get(key)).get());
            } else if("minWidth".equals(key)) {
                CONTROL.setMinWidth(((DoubleProperty) properties.get(key)).get());
            } else if("minHeight".equals(key)) {
                CONTROL.setMinHeight(((DoubleProperty) properties.get(key)).get());
            } else if("maxWidth".equals(key)) {
                CONTROL.setMaxWidth(((DoubleProperty) properties.get(key)).get());
            } else if("maxHeight".equals(key)) {
                CONTROL.setMaxHeight(((DoubleProperty) properties.get(key)).get());
            } else if("scaleX".equals(key)) {
                CONTROL.setScaleX(((DoubleProperty) properties.get(key)).get());
            } else if("scaleY".equals(key)) {
                CONTROL.setScaleY(((DoubleProperty) properties.get(key)).get());
            } else if ("layoutX".equals(key)) {
                CONTROL.setLayoutX(((DoubleProperty) properties.get(key)).get());
            } else if ("layoutY".equals(key)) {
                CONTROL.setLayoutY(((DoubleProperty) properties.get(key)).get());
            } else if ("translateX".equals(key)) {
                CONTROL.setTranslateX(((DoubleProperty) properties.get(key)).get());
            } else if ("translateY".equals(key)) {
                CONTROL.setTranslateY(((DoubleProperty) properties.get(key)).get());
            } else if("styleClass".equals(key)) {
                CONTROL.getStyleClass().setAll("gauge");
                CONTROL.getStyleClass().addAll(((ObjectProperty<String[]>) properties.get(key)).get());
            } else if("minValue".equals(key)) {
                CONTROL.setMinValue(((DoubleProperty) properties.get(key)).get());
            } else if("maxValue".equals(key)) {
                CONTROL.setMaxValue(((DoubleProperty) properties.get(key)).get());
            } else if("value".equals(key)) {
                CONTROL.setValue(((DoubleProperty) properties.get(key)).get());
            } else if("decimals".equals(key)) {
                CONTROL.setDecimals(((IntegerProperty) properties.get(key)).get());
            } else if("title".equals(key)) {
                CONTROL.setTitle(((StringProperty) properties.get(key)).get());
            } else if("unit".equals(key)) {
                CONTROL.setUnit(((StringProperty) properties.get(key)).get());
            } else if("animated".equals(key)) {
                CONTROL.setAnimated(((BooleanProperty) properties.get(key)).get());
            } else if("animationDuration".equals(key)) {
                CONTROL.setAnimationDuration(((DoubleProperty) properties.get(key)).get());
            } else if("startAngle".equals(key)) {
                CONTROL.setStartAngle(((DoubleProperty) properties.get(key)).get());
            } else if("angleRange".equals(key)) {
                CONTROL.setAngleRange(((DoubleProperty) properties.get(key)).get());
            } else if ("autoScale".equals(key)) {
                CONTROL.setAutoScale(((BooleanProperty) properties.get(key)).get());
            } else if("needleColor".equals(key)) {
                CONTROL.setNeedleColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if("tickLabelOrientation".equals(key)) {
                CONTROL.setTickLabelOrientation(((ObjectProperty<RadialGauge.TickLabelOrientation>) properties.get(key)).get());
            } else if("numberFormat".equals(key)) {
                CONTROL.setNumberFormat(((ObjectProperty<RadialGauge.NumberFormat>) properties.get(key)).get());
            } else if("majorTickSpace".equals(key)) {
                CONTROL.setMajorTickSpace(((DoubleProperty) properties.get(key)).get());
            } else if("minorTickSpace".equals(key)) {
                CONTROL.setMinorTickSpace(((DoubleProperty) properties.get(key)).get());
            } else if("dropShadowEnabled".equals(key)) {
                CONTROL.setDropShadowEnabled(((BooleanProperty) properties.get(key)).get());
            } else if("tickLabelFill".equals(key)) {
                CONTROL.setTickLabelFill(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("tickMarkFill".equals(key)) {
                CONTROL.setTickMarkFill(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("style".equals(key)) {
                CONTROL.setStyle(((StringProperty) properties.get(key)).get());
            } else if("ledColor".equals(key)) {
                CONTROL.setLedColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("ledVisible".equals(key)) {
                CONTROL.setLedVisible(((BooleanProperty) properties.get(key)).get());
            } else if ("sectionsVisible".equals(key)) {
                CONTROL.setSectionsVisible(((BooleanProperty) properties.get(key)).get());
            } else if("sectionFill0".equals(key)) {
                CONTROL.setSectionFill0(((ObjectProperty<Color>) properties.get(key)).get());
            } else if("sectionFill1".equals(key)) {
                CONTROL.setSectionFill1(((ObjectProperty<Color>) properties.get(key)).get());
            } else if("sectionFill2".equals(key)) {
                CONTROL.setSectionFill2(((ObjectProperty<Color>) properties.get(key)).get());
            } else if("sectionFill3".equals(key)) {
                CONTROL.setSectionFill3(((ObjectProperty<Color>) properties.get(key)).get());
            } else if("sectionFill4".equals(key)) {
                CONTROL.setSectionFill4(((ObjectProperty<Color>) properties.get(key)).get());
            } else if("sectionFill5".equals(key)) {
                CONTROL.setSectionFill5(((ObjectProperty<Color>) properties.get(key)).get());
            } else if("sectionFill6".equals(key)) {
                CONTROL.setSectionFill6(((ObjectProperty<Color>) properties.get(key)).get());
            } else if("sectionFill7".equals(key)) {
                CONTROL.setSectionFill7(((ObjectProperty<Color>) properties.get(key)).get());
            } else if("sectionFill8".equals(key)) {
                CONTROL.setSectionFill8(((ObjectProperty<Color>) properties.get(key)).get());
            } else if("sectionFill9".equals(key)) {
                CONTROL.setSectionFill9(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("areasVisible".equals(key)) {
                CONTROL.setAreasVisible(((BooleanProperty) properties.get(key)).get());
            } else if("areaFill0".equals(key)) {
                CONTROL.setAreaFill0(((ObjectProperty<Color>) properties.get(key)).get());
            } else if("areaFill1".equals(key)) {
                CONTROL.setAreaFill1(((ObjectProperty<Color>) properties.get(key)).get());
            } else if("areaFill2".equals(key)) {
                CONTROL.setAreaFill2(((ObjectProperty<Color>) properties.get(key)).get());
            } else if("areaFill3".equals(key)) {
                CONTROL.setAreaFill3(((ObjectProperty<Color>) properties.get(key)).get());
            } else if("areaFill4".equals(key)) {
                CONTROL.setAreaFill4(((ObjectProperty<Color>) properties.get(key)).get());
            } else if("areaFill5".equals(key)) {
                CONTROL.setAreaFill5(((ObjectProperty<Color>) properties.get(key)).get());
            } else if("areaFill6".equals(key)) {
                CONTROL.setAreaFill6(((ObjectProperty<Color>) properties.get(key)).get());
            } else if("areaFill7".equals(key)) {
                CONTROL.setAreaFill7(((ObjectProperty<Color>) properties.get(key)).get());
            } else if("areaFill8".equals(key)) {
                CONTROL.setAreaFill8(((ObjectProperty<Color>) properties.get(key)).get());
            } else if("areaFill9".equals(key)) {
                CONTROL.setAreaFill9(((ObjectProperty<Color>) properties.get(key)).get());
            }
        }
        return CONTROL;
    }
}
